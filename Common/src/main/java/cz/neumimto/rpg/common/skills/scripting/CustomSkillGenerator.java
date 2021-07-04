package cz.neumimto.rpg.common.skills.scripting;

import com.electronwill.nightconfig.core.Config;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.squareup.javapoet.*;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.utils.DebugLevel;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import org.codehaus.janino.SimpleCompiler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.lang.model.element.Modifier.PUBLIC;

public abstract class CustomSkillGenerator {

    @Inject
    private Injector injector;

    @Inject
    private DamageService damageService;

    public Class<? extends ISkill> generate(ScriptSkillModel scriptSkillModel, ClassLoader classLoader) throws Exception {
        if (scriptSkillModel == null || scriptSkillModel.getSpell() == null) {
            return null;
        }

        String packagee = "cz.neumimto.skills.scripts";
        String className = "Custom" + System.currentTimeMillis();

        ParsedScript ps = findLocalVarsAndFields(scriptSkillModel.getSpell());
        TypeSpec.Builder type = TypeSpec.classBuilder(className)
                .addAnnotation(AnnotationSpec.builder(Singleton.class).build())
                .addAnnotation(AnnotationSpec.builder(ResourceLoader.Skill.class).addMember("value", "$S", scriptSkillModel.getId()).build());

        if (scriptSkillModel.getSuperType() == null) {
            type.superclass(ParameterizedTypeName.get(ClassName.get(ActiveSkill.class), ClassName.get(characterClassImpl())))
                    .addModifiers(PUBLIC);

            type.addMethod(MethodSpec.methodBuilder("cast").addModifiers(PUBLIC)
                    .addParameter(IActiveCharacter.class, "caster0")
                    .addParameter(PlayerSkillContext.class, "context")
                    .returns(SkillResult.class)
                    .addCode(parseModel(scriptSkillModel))
                    .build());

        } else if ("Targeted".equalsIgnoreCase(scriptSkillModel.getSuperType())) {
            type.superclass(TypeName.get(targeted())).addModifiers(PUBLIC);
            type.addMethod(MethodSpec.methodBuilder("castOn").addModifiers(PUBLIC)
                    .addParameter(IEntity.class, "target")
                    .addParameter(IActiveCharacter.class, "caster0")
                    .addParameter(PlayerSkillContext.class, "context")
                    .returns(SkillResult.class)
                    .addCode(parseModel(scriptSkillModel))
                    .build());
        } else {
            throw new IllegalAccessException("Unknown SuperType " + scriptSkillModel.getSuperType());
        }

        for (Object mechanic : ps.mechanics) {
            type.addField(FieldSpec.builder(mechanic.getClass(), fieldName(mechanic.getClass().getSimpleName())).addAnnotation(Inject.class).build());
        }

        type.addMethod(MethodSpec.methodBuilder("init").addModifiers(PUBLIC).addCode(parseSkillMeta(scriptSkillModel)).build());

        TypeSpec build = type.build();
        JavaFile jfile = JavaFile.builder(packagee, build).build();


        String code = jfile.toString();
        Log.info(code, DebugLevel.DEVELOP);
        SimpleCompiler sc = new SimpleCompiler();
        try {
            sc.setParentClassLoader(classLoader);
            sc.cook(jfile.toString());

            Class<? extends ISkill> x = (Class<? extends ISkill>) sc.getClassLoader().loadClass(packagee + "." + className);
            return x;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private CodeBlock parseSkillMeta(ScriptSkillModel scriptSkillModel) {
        CodeBlock.Builder builder = CodeBlock.builder();
        if (scriptSkillModel.getDamageType() != null) {
            builder.addStatement("setDamageType($S)", translateDamageType(scriptSkillModel.getDamageType()).toString());
        }
        if (scriptSkillModel.getSkillTypes() != null) {
            for (String skillType : scriptSkillModel.getSkillTypes()) {
                Stream.of(SkillType.values()).filter(a -> a.getId().equalsIgnoreCase(skillType)).findFirst()
                        .ifPresent(a -> builder.addStatement("addSkillType($T.$L)", SkillType.class, a.name().toUpperCase()));

            }
        }
        return builder.build();
    }

    private static class MethodHandler {

        final Class mechanic;
        final String fieldName;
        final Method relevantMethod;
        final Class returnType;
        final String methodName;

        private MethodHandler(Class mechanic, String fieldName, Method relevantMethod, Class returnType, String methodName) {
            this.mechanic = mechanic;
            this.fieldName = fieldName;
            this.relevantMethod = relevantMethod;
            this.returnType = returnType;
            this.methodName = methodName;
        }

        static MethodHandler of(Object mechanic) {
            Class targetSelector = mechanic.getClass();
            String fieldName = fieldName(targetSelector.getSimpleName());
            Method relevantMethod = getRelevantMethod(targetSelector).get();
            Class<?> returnType = relevantMethod.getReturnType();
            String method = relevantMethod.getName();
            return new MethodHandler(
                    mechanic.getClass(),
                    fieldName,
                    relevantMethod,
                    returnType,
                    method
            );
        }

    }

    private CodeBlock parseModel(ScriptSkillModel scriptSkillModel) {
        CodeBlock.Builder builder = CodeBlock.builder()
                .addStatement("$T caster = ($T) caster0", characterClassImpl(), characterClassImpl()) //janino cant handle generics
                .addStatement("$T<$T> map = context.getCachedComputedSkillSettings()", Object2DoubleOpenHashMap.class, String.class);

        ParsedScript ps = findLocalVarsAndFields(scriptSkillModel.getSpell());

        for (Variable variable : ps.variables) {
            if (!"double".equals(variable.type)) {
                builder.addStatement(variable.type + " $L = ($L) map.getDouble($S)", getSkillSettingsNodeName(variable.name), variable.type, getSkillSettingsNodeName(variable.name));
            } else {
                builder.addStatement(variable.type + " $L = map.getDouble($S)", getSkillSettingsNodeName(variable.name), getSkillSettingsNodeName(variable.name));
            }
        }


        for (Config config : scriptSkillModel.getSpell()) {
            List<Config> mechs = config.get("Mechanics");
            try {
                MethodHandler methodHandler = MethodHandler.of(filterMechanicById(config));

                if (Iterable.class.isAssignableFrom(methodHandler.returnType)) {

                    MechanicCallDescriptor mcd = getMechanicCallDescriptor(config);
                    Set<String> strings = mcd.params.methodArgs.keySet();

                    java.lang.reflect.Type actualTypeArgument = ((ParameterizedType) methodHandler.relevantMethod.getGenericReturnType()).getActualTypeArguments()[0];
                    Object[] objects = {actualTypeArgument, methodHandler.fieldName, methodHandler.methodName};
                    builder.beginControlFlow("for ($T target : $L.$L(" +
                            strings.stream().filter(a -> mcd.params.available(a)).map(CustomSkillGenerator::getSkillSettingsNodeName).collect(Collectors.joining(", ")) + "))", objects);

                    for (Config mechanic : mechs) {
                        writeCallMechanic(mechanic, builder);
                    }

                    builder.endControlFlow();

                }

            } catch (Exception e) {
                //no target selector
                for (Config mechanic : mechs) {
                    writeCallMechanic(mechanic, builder);
                }
            }

        }

        builder.addStatement("return $T.OK", SkillResult.class);
        return builder.build();
    }

    private static class Variable {
        String name;
        String type;
    }

    private static class ParsedScript {
        Set<Variable> variables = new HashSet<>();
        Set<Object> mechanics = new HashSet<>();
    }

    private void writeCallMechanic(Config config, CodeBlock.Builder builder) {
        if (config.contains("If")) {
            String anIf = (String) config.get("If");
            if (anIf.startsWith("#")) {
                String[] s = anIf.split(" ");
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < s.length; i++) {
                    String s1 = s[i];
                    if (s1.contains(".") && !s1.startsWith("settings")) {
                        //todo mechanic call within expr
                    } else {
                        //just trim "settings."
                        sb.append(getSkillSettingsNodeName(s1) + " ");
                    }
                }
                builder.beginControlFlow("if (" + sb.toString() + ")");
            } else {
                Object mechanic = filterMechanicById(anIf);
                MechanicCallDescriptor mcd = getMechanicCallDescriptor(config);
                Set<String> params = mcd.params.methodArgs.keySet();
                Method relevantMethod = getRelevantMethod(mechanic.getClass()).get();
                if (relevantMethod.getReturnType() != boolean.class) {
                    throw new IllegalArgumentException("Conditional requires return type boolean, got " + mechanic.getClass().getSimpleName());
                }

                MethodHandler methodHandler = MethodHandler.of(mechanic);
                Object[] objects = {methodHandler.fieldName, methodHandler.methodName};

                builder.beginControlFlow("if ($L.$L(" + params.stream().filter(a -> mcd.params.available(a)).map(CustomSkillGenerator::getSkillSettingsNodeName).collect(Collectors.joining(", ")) + "))", objects);
            }
            if (!config.contains("Then")) {
                throw new IllegalArgumentException("Conditional requires positive branch");
            }
            List<? extends Config> then = config.get("Then");


            for (Config posB : then) {
                writeCallMechanic(posB, builder);
            }

            builder.endControlFlow();
        } else if (config.contains("Type")) {
            String type = config.get("Type");
            Object mechanic = filterMechanicById(type);

            MechanicCallDescriptor mcd = getMechanicCallDescriptor(config);

            Set<String> params = mcd.params.methodArgs.keySet();

            MethodHandler methodHandler = MethodHandler.of(mechanic);


            Object[] objects = {methodHandler.fieldName, methodHandler.methodName};

            builder.add(CodeBlock.of("$L.$L(" + params.stream().filter(a -> mcd.params.available(a)).map(CustomSkillGenerator::getSkillSettingsNodeName).collect(Collectors.joining(", ")) + ");", objects));
        }

    }

    private static String fieldName(String string) {
        return Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }

    private static boolean isSkillSettingsSkillNode(String value) {
        return value.startsWith("settings.");
    }

    private static String getSkillSettingsNodeName(String value) {
        return value.replace("settings.", "");
    }

    private ParsedScript findLocalVarsAndFields(Config config) {
        ParsedScript parsedScript = new ParsedScript();

        if (config.contains("If")) {
            ParsedScript ps = new ParsedScript();
            parseLocalVarsAndFields(config, ps);
            parsedScript.mechanics.addAll(ps.mechanics);
            parsedScript.variables.addAll(ps.variables);
        }

        if (config.contains("Then")) {
            List<Config> list = config.get("Then");
            for (Config config1 : list) {
                ParsedScript ps = (findLocalVarsAndFields(config1));
                parsedScript.mechanics.addAll(ps.mechanics);
                parsedScript.variables.addAll(ps.variables);
            }
        }

        if (config.contains("Else")) {
            List<Config> list = config.get("Else");
            for (Config config1 : list) {
                ParsedScript ps = (findLocalVarsAndFields(config1));
                parsedScript.mechanics.addAll(ps.mechanics);
                parsedScript.variables.addAll(ps.variables);
            }
        }

        try {
            parseLocalVarsAndFields(config, parsedScript);
        } catch (Exception ignored) {
        }

        if (config.contains("Mechanics")) {
            List<Config> list = config.get("Mechanics");
            for (Config config1 : list) {
                ParsedScript ps = findLocalVarsAndFields(config1);
                parsedScript.mechanics.addAll(ps.mechanics);
                parsedScript.variables.addAll(ps.variables);
            }
        }

        return parsedScript;
    }

    private ParsedScript findLocalVarsAndFields(List<Config> configs) {
        ParsedScript parsedScript = new ParsedScript();
        for (Config config : configs) {

            ParsedScript localVarsAndFields = findLocalVarsAndFields(config);
            parsedScript.mechanics.addAll(localVarsAndFields.mechanics);
            parsedScript.variables.addAll(localVarsAndFields.variables);
        }
        return parsedScript;
    }

    private void parseLocalVarsAndFields(Config config, ParsedScript parsedScript) {
        Object o = filterMechanicById(config);
        if (o == null) {
            return;
        }

        parsedScript.mechanics.add(o);

        MechanicCallDescriptor mechanicCallDescriptor = getMechanicCallDescriptor(config);

        for (Map.Entry<String, Type> e : mechanicCallDescriptor.params.methodArgs.entrySet()) {
            if (isSkillSettingsSkillNode(e.getKey())) {
                Variable variable = new Variable();
                variable.name = e.getKey();
                variable.type = e.getValue().getTypeName();
                parsedScript.variables.add(variable);
            }
        }
    }

    private EffectMacro parseEffectMacro(String macro) {
        EffectMacro effectMacro = new EffectMacro();
        Matcher m = Pattern.compile("\\((.*?)\\)").matcher(macro);
        if (m.find()) {
            String group = m.group(1);
            String[] split = group.split(",");

            try {
                effectMacro.effectClass = split[0].trim();
                Class.forName(effectMacro.effectClass);
            } catch (ClassNotFoundException e) {
                effectMacro.effectClass = getDefaultEffectPackage() + "." + effectMacro.effectClass;
            }

            for (int i = 1; i < split.length; i++) {
                effectMacro.params.add(split[i]);
            }
            try {
                Class<?> c = Class.forName(effectMacro.effectClass);

                Constructor<?>[] declaredConstructors = c.getDeclaredConstructors();
                if (declaredConstructors.length == 1) {
                    effectMacro.ctr = declaredConstructors[0];
                } else {
                    outer:
                    for (Constructor<?> ctr : declaredConstructors) {
                        for (Parameter parameter : ctr.getParameters()) {
                            if (parameter.isAnnotationPresent(Generate.Model.class)) {
                                effectMacro.ctr = ctr;
                                break outer;
                            }
                        }
                    }
                    if (effectMacro.ctr == null) {
                        effectMacro.ctr = declaredConstructors[0];
                    }
                }

                int i = 0;
                Parameter[] parameters = effectMacro.ctr.getParameters();
                for (Parameter parameter : parameters) {
                    Class<?> type = parameter.getType();
                    if (parameter.isAnnotationPresent(Generate.Model.class)) {
                        effectMacro.modelType = type.toString();

                        for (Field declaredField : type.getDeclaredFields()) {
                            if (declaredField.isAccessible()) {
                                if (!parameter.getType().isPrimitive()) {
                                    i++;
                                    continue;
                                }
                                effectMacro.args.put(effectMacro.params.get(i).trim(), declaredField.getType());
                            }
                        }

                    } else {
                        if (!parameter.getType().isPrimitive()) {
                            i++;
                            continue;
                        }
                        effectMacro.args.put(effectMacro.params.get(i).trim(), type);
                    }
                    i++;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return effectMacro;
    }

    protected abstract String getDefaultEffectPackage();

    protected abstract Object translateDamageType(String damageType);

    private static class EffectMacro {

        Constructor<?> ctr;
        String effectClass;
        String modelType;
        Map<String, Class<?>> args = new HashMap<>();
        List<String> params = new ArrayList<>();

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("new ").append(effectClass).append("(");
            boolean first = true;
            for (int i = 0; i < ctr.getParameterCount(); i++) {
                if (!first) {
                    sb.append(",");
                }
                sb.append(getSkillSettingsNodeName(params.get(i)));
                first = false;
            }
            return sb.append(")").toString();
        }
    }

    protected Set<Object> getMechanics() {
        Set<Object> skillMechanics = new HashSet<>();
        for (Key<?> key : injector.getAllBindings().keySet()) {
            Class<?> rawType = key.getTypeLiteral().getRawType();
            if (hasAnnotation(rawType)) {
                skillMechanics.add(injector.getInstance(rawType));
            }
        }
        return skillMechanics;
    }

    protected Object filterMechanicById(Config config) {
        String type = config.get("Type");

        if (type == null) {
            type = config.get("If");
        }
        if (type == null) {
            type = config.get("Id");
        }
        if (type == null) {
            type = config.get("Target-Selector");
        }
        if (type != null && type.startsWith("#")) {
            return null;
        }

        return filterMechanicById(type);
    }

    protected static class MechanicParams {
        public Map<String, Type> methodArgs = new LinkedHashMap<>();
        public List<String> consumed = new ArrayList<>();

        public boolean available(String l) {
            return !consumed.contains(l);
        }
    }

    protected MechanicParams filterMechanicParams(Object mechanic, String str) {
        Pattern compile = Pattern.compile("(([a-zA-Y]*)=([a-zA-Y]*)?((\\([^)]+\\))|'(.*?)')|([a-zA-Z=.]*))");
        Matcher matcher = compile.matcher(str);

        List<String> w = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group(0);
            if (!group.isEmpty()) {
                w.add(group);
            }
        }

        w.remove(0);

        MechanicParams params = new MechanicParams();

        Optional<Method> relevantMethod = getRelevantMethod(mechanic);
        Method method = relevantMethod.get();

        for (Parameter parameter : method.getParameters()) {

            if (parameter.isAnnotationPresent(Caster.class)) {
                params.methodArgs.put("caster", parameter.getType());
            } else if (parameter.isAnnotationPresent(Target.class)) {
                params.methodArgs.put("target", parameter.getType());
            } else if (parameter.isAnnotationPresent(SkillArgument.class)) {
                SkillArgument sa = parameter.getAnnotation(SkillArgument.class);

                Optional<String> first = w.stream()
                        .filter(q -> q.startsWith(sa.value() + "="))
                        .findFirst();

                if (first.isPresent()) {
                    if (isSkillSettingsSkillNode(sa.value())) {
                        String s1 = first.get();
                        params.methodArgs.put(s1.split("=")[1], parameter.getType());
                    } else if (IEffect.class.isAssignableFrom(parameter.getType())) {
                        String s = first.get().split("=")[1];
                        EffectMacro em = parseEffectMacro(s);

                        params.methodArgs.put(em.toString(), parameter.getType());
                        params.consumed.addAll(em.args.keySet());
                        for (Map.Entry<String, Class<?>> e : em.args.entrySet()) {
                            if (isSkillSettingsSkillNode(e.getKey())) {
                                params.methodArgs.put(e.getKey(), e.getValue());
                            }
                        }
                    }
                    continue;
                }
                params.methodArgs.put(sa.value(), parameter.getType());
            } else if (ISkill.class.isAssignableFrom(parameter.getType())) {
                params.methodArgs.put("this", parameter.getType());
            } else if (parameter.isAnnotationPresent(StaticArgument.class)) {
                StaticArgument sa = parameter.getAnnotation(StaticArgument.class);
                Optional<String> first = w.stream()
                        .filter(q -> q.startsWith(sa.value() + "="))
                        .findFirst();
                if (first.isPresent()) {
                    params.methodArgs.put("\"" + first.get().split("=")[1] + "\"", parameter.getType());
                } else {
                    params.methodArgs.put("\"" + sa.value() + "\"", parameter.getType());
                }
            }
        }

        return params;
    }

    protected MechanicCallDescriptor getMechanicCallDescriptor(Config config) {
        MechanicCallDescriptor mcd = new MechanicCallDescriptor();
        String type = config.get("Type");

        if (type == null) {
            type = config.get("If");
        }
        if (type == null) {
            type = config.get("Id");
        }
        if (type == null) {
            type = config.get("Target-Selector");
        }
        if (type.startsWith("#")) {
            return null;
        }
        mcd.mechanic = filterMechanicById(config);
        mcd.params = filterMechanicParams(mcd.mechanic, type);
        mcd.plain = type;
        return mcd;
    }

    private static class MechanicCallDescriptor {
        String plain;
        Object mechanic;
        MechanicParams params;
    }

    protected Object filterMechanicById(String id) {
        for (Object mechanic : getMechanics()) {
            String annotationId = getAnnotationId(mechanic.getClass());
            if (id != null && id.split(" ")[0].equalsIgnoreCase(annotationId)) {
                return mechanic;
            }
        }
        throw new IllegalStateException("Unknown mechanic id " + id);
    }

    protected boolean hasAnnotation(Class<?> c) {
        return c.isAnnotationPresent(SkillMechanic.class) || c.isAnnotationPresent(TargetSelector.class);
    }

    protected String getAnnotationId(Class<?> rawType) {
        return rawType.isAnnotationPresent(SkillMechanic.class) ?
                rawType.getAnnotation(SkillMechanic.class).value() : rawType.getAnnotation(TargetSelector.class).value();
    }

    protected static Optional<Method> getRelevantMethod(Class<?> rawType) {
        return Stream.of(rawType.getDeclaredMethods())
                .filter(CustomSkillGenerator::isHandlerMethod)
                .findFirst();

    }

    protected static Optional<Method> getRelevantMethod(Object rawType) {
        return getRelevantMethod(rawType.getClass());
    }

    private static boolean isHandlerMethod(Method method) {
        return Modifier.isPublic(method.getModifiers())
                && !Modifier.isStatic(method.getModifiers())
                && (method.isAnnotationPresent(Handler.class) || hasAnnotatedArgument(method));
    }

    private static boolean hasAnnotatedArgument(Method method) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            for (Annotation a : parameterAnnotation) {
                if (isOneOf(a, Caster.class, Target.class, SkillArgument.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isOneOf(Annotation a, Class<?>... c) {
        for (Class<?> aClass : c) {
            if (is(a, aClass)) {
                return true;
            }
        }
        return false;
    }

    private static boolean is(Annotation a, Class<?> c) {
        return a.annotationType() == c || a.getClass() == c;
    }

    protected abstract Type characterClassImpl();

    protected abstract Class<?> targeted();
}
