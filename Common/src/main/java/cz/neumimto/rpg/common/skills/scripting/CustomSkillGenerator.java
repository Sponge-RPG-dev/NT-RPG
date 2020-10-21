package cz.neumimto.rpg.common.skills.scripting;

import com.electronwill.nightconfig.core.Config;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.squareup.javapoet.*;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.utils.DebugLevel;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

public abstract class CustomSkillGenerator {

    private static Logger logger = LoggerFactory.getLogger(CustomSkillGenerator.class);

    @Inject
    private Injector injector;

    @Inject
    private DamageService damageService;

    public Class<? extends ISkill> generate(ScriptSkillModel scriptSkillModel) {
        if (scriptSkillModel == null || scriptSkillModel.getSpell() == null) {
            return null;
        }

        String packagee = "cz.neumimto.skills.scripts";
        String className = "Custom" + System.currentTimeMillis();

        ParsedScript ps = findLocalVarsAndFields(scriptSkillModel.getSpell());
        TypeSpec.Builder type = TypeSpec.classBuilder(className)
                .addAnnotation(AnnotationSpec.builder(ResourceLoader.Skill.class).addMember("value", "$S", scriptSkillModel.getId()).build());

        if (scriptSkillModel.getSuperType() == null) {
                    type.superclass(ParameterizedTypeName.get(ClassName.get(ActiveSkill.class), TypeVariableName.get("T")))
                    .addTypeVariable(TypeVariableName.get("T", TypeName.get(IActiveCharacter.class)))
                    .addModifiers(PUBLIC);

            type.addMethod(MethodSpec.methodBuilder("cast").addModifiers(PUBLIC)
                    .addParameter(IActiveCharacter.class, "caster", FINAL)
                    .addParameter(PlayerSkillContext.class, "context", FINAL)
                    .returns(SkillResult.class)
                    .addCode(parseModel(scriptSkillModel))
                    .build());

        } else if ("Targeted".equalsIgnoreCase(scriptSkillModel.getSuperType())){
            type.superclass(ParameterizedTypeName.get(ClassName.get(targeted()), TypeVariableName.get("T"))).addModifiers(PUBLIC);
            type.addMethod(MethodSpec.methodBuilder("castOn").addModifiers(PUBLIC)
                    .addParameter(IEntity.class, "target", FINAL)
                    .addParameter(characterClassImpl(), "caster", FINAL)
                    .addParameter(PlayerSkillContext.class, "context", FINAL)
                    .returns(SkillResult.class)
                    .addCode(parseModel(scriptSkillModel))
                    .build());
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
            sc.cook(jfile.toString());
            Class<? extends ISkill> x = (Class<? extends ISkill>) sc.getClassLoader().loadClass(packagee + "." + className);
            return x;
        } catch (CompileException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CodeBlock parseSkillMeta(ScriptSkillModel scriptSkillModel) {
        CodeBlock.Builder builder = CodeBlock.builder();
        if (scriptSkillModel.getDamageType() != null) {
            builder.add("setDamageType($S)", scriptSkillModel.getDamageType());
        }
        if (scriptSkillModel.getSkillTypes() != null) {
            for (String skillType : scriptSkillModel.getSkillTypes()) {
                builder.add("addSkillType(SkillType.$L)", skillType.toUpperCase());
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
                .addStatement("$T<$T> map = context.getCachedComputedSkillSettings()", Object2FloatOpenHashMap.class, String.class);

        ParsedScript ps = findLocalVarsAndFields(scriptSkillModel.getSpell());

        for (Variable variable : ps.variables) {
            if (!"float".equals(variable.type)) {
                builder.addStatement(variable.type + " $L = ($L) map.getFloat($S)", getSkillSettingsNodeName(variable.name), variable.type, getSkillSettingsNodeName(variable.name));
            } else {
                builder.addStatement(variable.type + " $L = map.getFloat($S)", getSkillSettingsNodeName(variable.name), getSkillSettingsNodeName(variable.name));
            }
        }


        for (Config config : scriptSkillModel.getSpell()) {
            MethodHandler methodHandler = MethodHandler.of(filterMechanicById(config));

            List<Config> spell = scriptSkillModel.getSpell();
            List<Config> mechs = spell.get(0).get("Mechanics");

            Object mechanic1 = filterMechanicById(config);

            if (Iterable.class.isAssignableFrom(methodHandler.returnType)) {

                List<String> params = config.get("Params");
                List<String> list = parseMethodCall(mechanic1, params);

                java.lang.reflect.Type actualTypeArgument = ((ParameterizedType) methodHandler.relevantMethod.getGenericReturnType()).getActualTypeArguments()[0];
                Object[] objects = {actualTypeArgument, methodHandler.fieldName, methodHandler.methodName};
                builder.beginControlFlow("for ($T target : $L.$L(" + String.join(", ", list) + "))", objects);

                for (Config mechanic : mechs) {
                    writeCallMechanic(mechanic, builder);
                }

                builder.endControlFlow();

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
        List params = config.get("Params");
        if (params == null) {
            params = new ArrayList<>();
        }
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
                params = parseMethodCall(mechanic, params);
                Method relevantMethod = getRelevantMethod(mechanic.getClass()).get();
                if (relevantMethod.getReturnType() != boolean.class) {
                    throw new IllegalArgumentException("Conditional requires return type boolean, got " + mechanic.getClass().getSimpleName());
                }

                MethodHandler methodHandler = MethodHandler.of(mechanic);
                Object[] objects = {methodHandler.fieldName, methodHandler.methodName};

                builder.beginControlFlow("if ($L.$L(" + String.join(", ", params) + "))", objects);
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
            params = parseMethodCall(mechanic, params);

            MethodHandler methodHandler = MethodHandler.of(mechanic);
            Object[] objects = {methodHandler.fieldName, methodHandler.methodName};

            builder.add(CodeBlock.of("$L.$L(" + String.join(", ", (List<String>) params.stream().map(a->a.toString()).collect(Collectors.toList())) + ");", objects));
        }

    }

    private static String fieldName(String string) {
        return Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }



    public List parseMethodCall(Object call, List<String> configParams) {
        List params = new ArrayList<>();

        Method relevantMethod = getRelevantMethod(call.getClass()).get();
        Iterator<String> iterator = configParams.iterator();

        for (Parameter parameter : relevantMethod.getParameters()) {

            if (parameter.isAnnotationPresent(Caster.class)) {
                params.add("caster");
            } else if (parameter.isAnnotationPresent(Target.class)) {
                params.add("target");
            } else if (parameter.isAnnotationPresent(SkillArgument.class)) {
                SkillArgument a = parameter.getAnnotation(SkillArgument.class);
                if (isSkillSettingsSkillNode(a.value())) {
                    String skillSettingsNodeName = getSkillSettingsNodeName(a.value());
                    if (iterator.hasNext()) {
                        skillSettingsNodeName = iterator.next();
                    }
                    params.add(skillSettingsNodeName);
                }
            } else if (IEffect.class.isAssignableFrom(parameter.getType())) {
                String next = iterator.next();
                if (next.startsWith("Effect")) {
                    EffectMacro em = parseEffectMacro(next);
                    params.add(em);
                    //params.add("new " + classNameFq + "(" + collect + ")");;
                }
            } else if (ISkill.class.isAssignableFrom(parameter.getType())) {
                params.add("this");
            }
        }

        return params;
    }

    private static boolean isSkillSettingsSkillNode(String value) {
        return value.startsWith("settings.");
    }

    private static String getSkillSettingsNodeName(String value) {
        return value.replace("settings.", "");
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

        parseLocalVarsAndFields(config, parsedScript);

        if (config.contains("Mechanics")) {
            List<Config> list = config.get("Mechanics");
            for (Config config1 : list) {
                ParsedScript ps = (findLocalVarsAndFields(config1));
                parsedScript.mechanics.addAll(ps.mechanics);
                parsedScript.variables.addAll(ps.variables);
            }
        }

        return parsedScript;
    }

    private void parseLocalVarsAndFields(Config config, ParsedScript parsedScript) {
        Object o = filterMechanicById(config);
        if (o == null)
            return;
        Method relevantMethod = getRelevantMethod(o).orElseThrow(() ->
                new IllegalArgumentException("Mechanic " + o.getClass().getCanonicalName() + " has no handler method")
        );

        parsedScript.mechanics.add(o);

        for (int i = 0; i < relevantMethod.getParameterCount(); i++) {
            Parameter parameter = relevantMethod.getParameters()[i];
            Annotation[] annotations = relevantMethod.getParameterAnnotations()[i];
            for (Annotation a : annotations) {
                if (is(a, SkillArgument.class)) {
                    Variable variable = new Variable();
                    variable.name = ((SkillArgument) a).value();
                    variable.type = parameter.getType().toString();
                    parsedScript.variables.add(variable);
                }
            }
        }

        if (config.contains("Params")) {
            List<String> params = config.get("Params");
            for (String param : params) {
                if (param.startsWith("Effect")) {
                    EffectMacro em = parseEffectMacro(param);
                    for (Map.Entry<String, Class<?>> e : em.args.entrySet()) {
                        Variable variable = new Variable();
                        variable.name = e.getKey();
                        variable.type = e.getValue().getTypeName();
                        parsedScript.variables.add(variable);
                    }
                }
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
                effectMacro.effectClass = getDefaultEffectPackage() + effectMacro.effectClass;
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

    private static class EffectMacro {
        Constructor<?> ctr;
        String effectClass;
        String modelType;
        Map<String, Class<?>> args = new HashMap<>();
        List<String> params = new ArrayList<>();

        @Override
        public String toString() {

            long count = Stream.of(ctr.getParameters()).map(a -> IEffectContainer.class.isAssignableFrom(a.getType())).count();

            StringBuilder sb = new StringBuilder();
            sb.append("new ").append(effectClass).append("(");
            boolean first = true;
            for (int i = 0; i < ctr.getParameterCount(); i++) {
                Parameter parameter = ctr.getParameters()[i];
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
        if (type.startsWith("#")) {
            return null;
        }
        return filterMechanicById(type);
    }

    protected Object filterMechanicById(String id) {
        for (Object mechanic : getMechanics()) {
            String annotationId = getAnnotationId(mechanic.getClass());
            if (id != null && id.equalsIgnoreCase(annotationId)) {
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
                if (isOneOf(a, new Class[]{Caster.class, Target.class, SkillArgument.class})) {
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
