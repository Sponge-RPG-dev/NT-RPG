package cz.neumimto.rpg.common.skills.scripting;

import com.electronwill.nightconfig.core.Config;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.squareup.javapoet.*;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

@Singleton
public class CustomSkillGenerator implements Opcodes {

    private static Logger logger = LoggerFactory.getLogger(CustomSkillGenerator.class);

    @Inject
    private Injector injector;

    public Class<? extends ISkill> generate(ScriptSkillModel scriptSkillModel) {
        if (scriptSkillModel == null || scriptSkillModel.getSpell() == null) {
            return null;
        }

        String packagee = "cz.neumimto.skills.scripts";
        String className = "Custom" + System.currentTimeMillis();

        SpellData data = getRelevantMechanics(getMechanics(), scriptSkillModel.getSpell());
        if (data.targetSelector == null) {
            throw new UnknownMechanicException(scriptSkillModel.getId());
        }
        List<Object> futureFields = data.getAll();

        TypeSpec.Builder type = TypeSpec.classBuilder(className)
                .addAnnotation(AnnotationSpec.builder(ResourceLoader.Skill.class).addMember("value", "$S", scriptSkillModel.getId()).build())
                .superclass(ParameterizedTypeName.get(ClassName.get(ActiveSkill.class), TypeVariableName.get("T")))
                .addTypeVariable(TypeVariableName.get("T", TypeName.get(IActiveCharacter.class)))
                .addModifiers(PUBLIC);

        for (Object mechanic : futureFields) {
            type.addField(FieldSpec.builder(mechanic.getClass(), fieldName(mechanic.getClass().getSimpleName())).addAnnotation(Inject.class).build());
        }

        type.addMethod(MethodSpec.methodBuilder("cast").addModifiers(PUBLIC)
                .addParameter(IActiveCharacter.class, "caster", FINAL)
                .addParameter(PlayerSkillContext.class, "context", FINAL)
                .returns(SkillResult.class)
                .addCode(parseModel(scriptSkillModel, data))
                .build());

        TypeSpec build = type.build();
        JavaFile jfile = JavaFile.builder(packagee, build).build();


        String code = jfile.toString();
        System.out.println(code);
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

    private CodeBlock parseModel(ScriptSkillModel scriptSkillModel, SpellData data) {
        Map<String, String> requiredLocalVars = findRequiredLocalVars(data);
        CodeBlock.Builder builder = CodeBlock.builder()
                .addStatement("$T<$T> map = context.getCachedComputedSkillSettings()", Object2FloatOpenHashMap.class, String.class);
        for (Map.Entry<String, String> en : requiredLocalVars.entrySet()) {
            builder.addStatement("float $L = map.getFloat($S)", getSkillSettingsNodeName(en.getKey()), getSkillSettingsNodeName(en.getKey()));
        }


        MethodHandler methodHandler = MethodHandler.of(data.targetSelector);

        List<Config> spell = scriptSkillModel.getSpell();
        List<Config> mechs = spell.get(0).get("Mechanics");



        if (Iterable.class.isAssignableFrom(methodHandler.returnType)) {

            List<String> list = parseMethodCall(data.targetSelector, data.paramsTs);

            Object[] args = parseMethodArguments(list);

            java.lang.reflect.Type actualTypeArgument = ((ParameterizedType) methodHandler.relevantMethod.getGenericReturnType()).getActualTypeArguments()[0];
            Object[] objects = {actualTypeArgument, methodHandler.fieldName, methodHandler.methodName};
            builder.beginControlFlow("for ($T target : $L.$L("+ String.join(", ", list) +"))", objects);

            for (Config mechanic : mechs) {
                writeCallMechanic(mechanic, builder);
            }

            builder.endControlFlow();

        }

        builder.addStatement("return $T.OK", SkillResult.class);
        return builder.build();
    }

    private Object[] parseMethodArguments(List<String> list) {
        list.stream().map(a -> "$L").collect(Collectors.toList());
        Object[] args = list.toArray();
        return args;
    }


    private void writeCallMechanic(Config config, CodeBlock.Builder builder) {
        List<String> params = config.get("Params");
        if (params == null) {
            params = new ArrayList<>();
        }
        if (config.contains("If")) {

            Object mechanic = filterMechanicById((String)config.get("If"));
            params = parseMethodCall(mechanic, params);
            Method relevantMethod = getRelevantMethod(mechanic.getClass()).get();
            if (relevantMethod.getReturnType() != boolean.class) {
                throw new IllegalArgumentException("Conditional requires return type boolean, got " + mechanic.getClass().getSimpleName());
            }
            if (!config.contains("Then")) {
                throw new IllegalArgumentException("Conditional requires positive branch");
            }
            List<? extends Config> then = config.get("Then");


            MethodHandler methodHandler = MethodHandler.of(mechanic);
            Object[] args = parseMethodArguments(params);
            Object[] objects = {methodHandler.fieldName, methodHandler.methodName};

            builder.beginControlFlow("if ($L.$L("+String.join(", ", params)+"))", objects);

            for (Config posB : then) {
                writeCallMechanic(posB, builder);
            }

            builder.endControlFlow();
        } else if (config.contains("Type")) {
            String type = config.get("Type");
            Object mechanic = filterMechanicById(type);
            params = parseMethodCall(mechanic, params);

            MethodHandler methodHandler = MethodHandler.of(mechanic);
            Object[] args = parseMethodArguments(params);
            Object[] objects = {methodHandler.fieldName, methodHandler.methodName};
            objects = Stream.concat(Arrays.stream(objects), Arrays.stream(args)).toArray();

            builder.add(CodeBlock.of("$L.$L("+String.join(", ", params)+")",objects));
        }

    }

    private SpellData getRelevantMechanics(Set<Object> mechanics, List<Config> spell) {
        SpellData spellData = new SpellData();
        for (Object mechanic : mechanics) {
            for (Config config : spell) {
                String type = config.get("Target-Selector");

                if (mechanic.getClass().isAnnotationPresent(TargetSelector.class) && mechanic.getClass().getAnnotation(TargetSelector.class).value().equalsIgnoreCase(type)) {
                    spellData.targetSelector = mechanic;
                    continue;
                }

                spellData.paramsTs = config.getOrElse("Params", new ArrayList<>());


                List<Config> list = config.get("Mechanics");
                for (Config config1 : list) {
                    type = config1.get("Type");
                    if (type == null) {
                        type = config1.get("If");
                    }
                    if (mechanic.getClass().isAnnotationPresent(SkillMechanic.class) && mechanic.getClass().getAnnotation(SkillMechanic.class).value().equalsIgnoreCase(type)) {
                        spellData.mechanics.add(mechanic);
                        spellData.paramsM.add(config1.getOrElse("Params", new ArrayList<>()));
                    }
                }
            }
        }
        return spellData;
    }

    private class SpellData {
        Object targetSelector;
        List<String> paramsTs = new ArrayList<>();

        List<Object> mechanics = new ArrayList<>();
        List<List<String>> paramsM = new ArrayList<>();

        private List<Object> getAll() {
            return new ArrayList<Object>() {{
                addAll(mechanics);
                add(targetSelector);
            }};
        }
    }

    private static String fieldName(String string) {
        return Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }


    public List<String> parseMethodCall(Object call, List<String> configParams) {
        List<String> params = new ArrayList<>();

        Method relevantMethod = getRelevantMethod(call.getClass()).get();
        List<Annotation> methodParameterAnnotations = getMethodParameterAnnotations(relevantMethod);
        Iterator<String> iterator = configParams.iterator();
        for (Annotation annotation : methodParameterAnnotations) {
            if (is(annotation, Caster.class)) {
                params.add("caster");
            } else if (is(annotation, Target.class)) {
                params.add("target");
            } else if (is(annotation, SkillArgument.class)) {
                SkillArgument a = (SkillArgument) annotation;
                if (isSkillSettingsSkillNode(a.value())) {
                    String skillSettingsNodeName = getSkillSettingsNodeName(a.value());
                    if (iterator.hasNext()) {
                        skillSettingsNodeName = iterator.next();
                    }
                    params.add(skillSettingsNodeName);
                }
            }
        }


        return params;
    }


    private boolean isSkillSettingsSkillNode(String value) {
        return value.startsWith("settings.");
    }

    private String getSkillSettingsNodeName(String value) {
        return value.replace("settings.", "");
    }

    private static class LocalVariableHelper {
        final int fieldIndex;
        final int opCodeLoadInst;
        Label firstLabel;
        Label lastLabel;
        String path;
        String descriptor;

        private LocalVariableHelper(int fieldIndex, int opCodeLoadInst) {
            this.fieldIndex = fieldIndex;
            this.opCodeLoadInst = opCodeLoadInst;
        }

        public LocalVariableHelper(int localVariableId, int fload, String path, String descriptor) {
            this(localVariableId, fload);
            this.path = path;
            this.descriptor = descriptor;
        }


    }

    private String getInternalName(Class<?> c) {
        return Type.getInternalName(c);
    }

    private String getMethodDescriptor(Method method) {
        return Type.getMethodDescriptor(method);
    }

    private String getObjectTypeDescriptor(Class<?> c) {
        return Type.getDescriptor(c);//"L" + getInternalName(c) + ";";
    }

    private List<Annotation> getMethodParameterAnnotations(Method method) {
        List<Annotation> list = new ArrayList<>();
        outer:
        for (Annotation[] parameterAnnotation : method.getParameterAnnotations()) {
            for (Annotation annotation : parameterAnnotation) {
                if (isOneOf(annotation, SkillArgument.class, Caster.class, Target.class)) {
                    list.add(annotation);
                    continue outer;
                }
            }
        }
        return list;
    }

    private Map<String, String> findRequiredLocalVars(SpellData data) {

        Map<String, String> map = new HashMap<>();
        for (Object mechanic : data.getAll()) {
            Method relevantMethod = getRelevantMethod(mechanic.getClass()).orElseThrow(() ->
                    new IllegalArgumentException("Mechanic " + mechanic.getClass().getCanonicalName() + " has no handler method")
            );
            for (int i = 0; i < relevantMethod.getParameterCount(); i++) {
                Parameter parameter = relevantMethod.getParameters()[i];
                Annotation[] annotations = relevantMethod.getParameterAnnotations()[i];
                for (Annotation a : annotations) {
                    if (is(a, SkillArgument.class)) {
                        map.put(((SkillArgument) a).value(), parameter.getType().toString());
                    }
                }
            }
        }
        return map;
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

    private class UnknownMechanicException extends RuntimeException {
        public UnknownMechanicException(String id) {
            super(id);
        }
    }

    private class IllegalReturnTypeException extends RuntimeException {
        public IllegalReturnTypeException(String message) {
            super(message);
        }
    }
}
