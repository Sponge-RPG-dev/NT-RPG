package cz.neumimto.rpg.common.skills.scripting;

import com.electronwill.nightconfig.core.Config;
import com.google.inject.Injector;
import com.google.inject.Key;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.common.skills.mech.NearbyEnemies;
import cz.neumimto.rpg.common.skills.mech.TargetSelectorSelf;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Stream;

@Singleton
public class CustomSkillGenerator implements Opcodes {

    @Inject
    private Injector injector;

    @Inject
    private NearbyEnemies nearbyEnemies;

    private static Map<String, InlineMechanic> inlineMechanic = new HashMap<>();

    static {
        inlineMechanic.put("apply_effect", (methodVisitor, config) -> {

        });
    }

    public Class<? extends ISkill> generate(ScriptSkillModel scriptSkillModel) {
        if (scriptSkillModel == null || scriptSkillModel.getSpell() == null) {
            return null;
        }

        String className = "cz.neumimto.skills.scripts.Custom" + System.currentTimeMillis();

        DynamicType.Builder<ActiveSkill> builder = new ByteBuddy()
                .subclass(ActiveSkill.class)
                .visit(new AsmVisitorWrapper.ForDeclaredMethods().writerFlags(ClassWriter.COMPUTE_MAXS))
                .name(className)
                .annotateType(AnnotationDescription.Builder.ofType(ResourceLoader.Skill.class)
                        .define("value", scriptSkillModel.getId())
                        .build())
                .annotateType(AnnotationDescription.Builder.ofType(Singleton.class).build());

        SpellData data = getRelevantMechanics(getMechanics(), scriptSkillModel.getSpell());
        if (data.targetSelector == null) {
            data.targetSelector = injector.getInstance(TargetSelectorSelf.class);
        }
        List<Object> futureFields = data.getAll();
        for (Object mechanic : futureFields) {
            builder = builder.defineField(fieldName(mechanic.getClass().getSimpleName()), mechanic.getClass(), Modifier.PRIVATE)
                    .annotateField(AnnotationDescription.Builder.ofType(Inject.class).build());
        }


        builder = builder
                .defineMethod("cast", SkillResult.class, Ownership.MEMBER, Visibility.PUBLIC)
                .withParameters(IActiveCharacter.class, PlayerSkillContext.class)
                .intercept(new Implementation.Simple(new Interceptor(data, scriptSkillModel.getSpell(), className.replaceAll("\\.", "/"))));


        DynamicType.Unloaded<ActiveSkill> make = builder.make();

        try {
            make.saveIn(new File("."));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return make.load(getClass().getClassLoader())
                .getLoaded();
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
                List<Config> list = config.get("Mechanics");
                for (Config config1 : list) {
                    type = config1.get("Type");
                    if (mechanic.getClass().isAnnotationPresent(SkillMechanic.class) && mechanic.getClass().getAnnotation(SkillMechanic.class).value().equalsIgnoreCase(type)) {
                        spellData.mechanics.add(mechanic);
                    }
                }
            }
        }
        return spellData;
    }

    private class SpellData {
        Object targetSelector;
        List<Object> mechanics = new ArrayList<>();

        private List<Object> getAll() {
            return new ArrayList<Object>() {{
                addAll(mechanics);
                add(targetSelector);
            }};
        }
    }

    private static class Helper {
        protected final Map<String, List<? extends Config>> targetSelectors;

        private Helper(Map<String, List<? extends Config>> targetSelectors) {
            this.targetSelectors = targetSelectors;
        }

        @SuppressWarnings("unchecked")
        static Helper parse(List<Config> spell) {
            Map map = new HashMap();
            for (Config config : spell) {
                String targetSelectorId = config.get("Target-Selector");
                List<? extends Config> mechanics = config.get("Mechanics");
                map.put(targetSelectorId, mechanics);
            }
            return new Helper(map);
        }
    }

    protected class Interceptor implements ByteCodeAppender {

        private final SpellData mechanics;
        private final Helper helper;
        private final String internalClassName;

        private Map<String, String> localVars;

        private int lineNumber = 19;

        public Interceptor(SpellData mechanics, List<Config> spell, String internalClassName) {
            this.mechanics = mechanics;
            this.helper = Helper.parse(spell);
            this.internalClassName = internalClassName;
            this.localVars = findRequiredLocalVars(mechanics);
        }

        @Override
        public Size apply(MethodVisitor m, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            Map<String, LocalVariableHelper> settingsVariables = new HashMap<>();

            int index_this = 0;
            int index_caster = 1;
            int index_context = 2;
            int index_hashmap = 3;

            settingsVariables.put("$caster", new LocalVariableHelper(index_caster, ALOAD));
            settingsVariables.put("$context", new LocalVariableHelper(index_context, ALOAD));
            settingsVariables.put("$settingsMap", new LocalVariableHelper(index_hashmap, ALOAD));

            Label first = newLine(m);
            methodCall(m, ALOAD, index_context,
                    PlayerSkillContext.class, "getCachedComputedSkillSettings", "()L" + getInternalName(Object2FloatOpenHashMap.class) + ";",
                    ASTORE, index_hashmap);

            int localVariableId = 4;
            for (Map.Entry<String, String> e : localVars.entrySet()) {
                String path = e.getKey();
                if (isSkillSettingsSkillNode(path)) {
                    path = getSkillSettingsNodeName(path);

                    newLine(m);
                    visitSettingsF(m, index_hashmap, path, localVariableId);
                    settingsVariables.put(path, new LocalVariableHelper(localVariableId, FLOAD, path, "F"));

                    localVariableId++;
                }
            }
            newLine(m);

            for (Map.Entry<String, List<? extends Config>> entry : helper.targetSelectors.entrySet()) {
                String targetSelectorId = entry.getKey();
                newLine(m);

                Object o = filterMechanicById(targetSelectorId);
                MethodInvocationHelper helper = new MethodInvocationHelper(internalClassName, o, settingsVariables);
                Class targetSelectorReturnType = getRelevantMethod(o.getClass()).get().getReturnType();

                helper.invoke(m, index_this, internalClassName, o);
                m.visitVarInsn(ASTORE, localVariableId); //store return value


                if (Iterable.class.isAssignableFrom(targetSelectorReturnType)) {

                    localVariableId++;
                    Label iteratorStartOffset = newLine(m);
                    m.visitVarInsn(ALOAD, localVariableId - 1); //previous variable was a list
                    m.visitMethodInsn(INVOKEINTERFACE, getInternalName(Iterable.class), "iterator", "()Ljava/util/Iterator;", true);
                    m.visitVarInsn(ASTORE, localVariableId);
                    settingsVariables.put("$targets", new LocalVariableHelper(localVariableId, ALOAD, "targets", Type.getDescriptor(targetSelectorReturnType), iteratorStartOffset, null));

                    //****************************************
                    //while(iterator.hasnext)
                    Label frameStart = newLine(m);
                    m.visitFrame(Opcodes.F_FULL, 8, new Object[]{internalClassName, "cz/neumimto/rpg/api/entity/players/IActiveCharacter", "cz/neumimto/rpg/api/skills/PlayerSkillContext", "it/unimi/dsi/fastutil/objects/Object2FloatOpenHashMap", Opcodes.FLOAT, Opcodes.FLOAT, "java/util/List", "java/util/Iterator"}, 0, new Object[]{});
                    m.visitVarInsn(ALOAD, localVariableId);
                    m.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);

                    Label label6 = new Label();
                    m.visitJumpInsn(IFEQ, label6);
                    m.visitVarInsn(ALOAD, localVariableId);
                    m.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
                    //       m.visitTypeInsn(CHECKCAST, "cz/neumimto/rpg/api/entity/IEntity");
                    localVariableId++;
                    m.visitVarInsn(ASTORE, localVariableId); //store iterator.next()

                    Label label = newLine(m);

                    java.lang.reflect.Type[] actualTypeArguments = ((ParameterizedType) getRelevantMethod(o.getClass()).get().getGenericReturnType()).getActualTypeArguments();
                    settingsVariables.put("$target", new LocalVariableHelper(localVariableId, ALOAD, "target", Type.getDescriptor((Class) actualTypeArguments[0]), label, null));

                    visitMechanicsCall(m, settingsVariables, index_this, entry);

                    Label lastFramelabel = newLine(m);
                    m.visitJumpInsn(GOTO, frameStart);
                    m.visitLabel(label6);
                    m.visitLineNumber(33, label6);
                    m.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);

                    settingsVariables.get("$target").lastLabel = lastFramelabel;
                } else {
                    settingsVariables.put("$target", new LocalVariableHelper(localVariableId, ALOAD, "target", ""));

                    visitMechanicsCall(m, settingsVariables, index_this, entry);
                }


            }

            newLine(m);
            visitReturn(m, SkillResult.OK);

            Label last = new Label();
            m.visitLabel(last);
            m.visitLocalVariable("this", "L" + internalClassName + ";", null, first, last, index_this);
            m.visitLocalVariable("character", "L" + getInternalName(IActiveCharacter.class) + ";", null, first, last, index_caster);
            m.visitLocalVariable("info", "L" + getInternalName(PlayerSkillContext.class) + ";", null, first, last, index_context);
            m.visitLocalVariable("settings", "L" + getInternalName(Object2FloatOpenHashMap.class) + ";", "Lit/unimi/dsi/fastutil/objects/Object2FloatOpenHashMap<Ljava/lang/String;>;", first, last, index_hashmap);

            for (Map.Entry<String, LocalVariableHelper> e : settingsVariables.entrySet()) {
                LocalVariableHelper value = e.getValue();
                if (value.fieldIndex > index_hashmap) {
                    m.visitLocalVariable(value.path,
                            value.descriptor,
                            null,
                            value.firstLabel == null ? first : value.firstLabel,
                            value.lastLabel == null ? last : value.lastLabel,
                            value.fieldIndex);
                }
            }
            //    m.visitLocalVariable("targets", "Ljava/util/List;", "Ljava/util/List<Lcz/neumimto/rpg/api/entity/IEntity;>;", label7, last, 6);
            //    m.visitLocalVariable("iEntity", "Lcz/neumimto/rpg/api/entity/IEntity;", null, first, label8, 8);

            return new Size(0, 0);
        }

        public void visitMechanicsCall(MethodVisitor m, Map<String, LocalVariableHelper> settingsVariables, int index_this, Map.Entry<String, List<? extends Config>> entry) {
            List<? extends Config> mechanics = entry.getValue();
            for (Config c : mechanics) {
                newLine(m);

                if (isInlineMechanic(c)) {

                } else {
                    Object mechanic = filterMechanicById(c);
                    MethodInvocationHelper helper1 = new MethodInvocationHelper(internalClassName, mechanic, settingsVariables);
                    m.visitVarInsn(ALOAD, index_this);
                    m.visitFieldInsn(GETFIELD, internalClassName, fieldName(mechanic.getClass().getSimpleName()), getObjectTypeDescriptor(mechanic.getClass()));
                    for (LocalVariableHelper toVisit : helper1.visitVarInst) {
                        m.visitVarInsn(toVisit.opCodeLoadInst, toVisit.fieldIndex);
                    }
                    m.visitMethodInsn(INVOKEVIRTUAL, helper1.methodOwner, helper1.methodName, helper1.methodDescriptor, false);
                }
            }
        }

        private Label newLine(MethodVisitor m) {
            Label label = new Label();
            m.visitLabel(label);
            m.visitLineNumber(lineNumber, label);
            lineNumber++;
            return label;
        }
    }

    private String fieldName(String string) {
        return Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }

    private void methodCall(MethodVisitor m, int load_inst, int load_index, Class methodOwner, String methodName, String descriptor, int store_inst, int store_index) {
        m.visitVarInsn(load_inst, load_index);
        m.visitMethodInsn(INVOKEVIRTUAL, getInternalName(methodOwner), methodName, descriptor, false);
        m.visitVarInsn(store_inst, store_index);
    }

    private void visitSettingsF(MethodVisitor m, int hashmap_index, String variableName, int var_index) {
        m.visitVarInsn(ALOAD, hashmap_index);
        m.visitLdcInsn(variableName);
        m.visitMethodInsn(INVOKEVIRTUAL, getInternalName(Object2FloatOpenHashMap.class), "getFloat", "(Ljava/lang/Object;)F", false);
        m.visitVarInsn(FSTORE, var_index);
    }

    private void visitReturn(MethodVisitor m, SkillResult skillResult) {
        m.visitFieldInsn(GETSTATIC, getInternalName(skillResult.getDeclaringClass()), skillResult.name(), "L" + getInternalName(skillResult.getDeclaringClass()) + ";");
        m.visitInsn(ARETURN);
    }

    private class MethodInvocationHelper {
        final String owner;
        final String fieldName;
        final String fieldDescriptor;
        final String methodOwner;
        final String methodName;
        final String methodDescriptor;
        final List<LocalVariableHelper> visitVarInst;
        final String returnTypeDescriptor;

        public MethodInvocationHelper(String parent, Object mechanic, Map<String, LocalVariableHelper> localVars) {
            this.visitVarInst = new ArrayList<>();
            this.owner = parent;
            this.fieldName = mechanic.getClass().getSimpleName();
            this.fieldDescriptor = getObjectTypeDescriptor(mechanic.getClass());
            this.methodOwner = getInternalName(mechanic.getClass());
            Method method = getRelevantMethod(mechanic.getClass()).get();
            this.methodName = method.getName();
            this.methodDescriptor = getMethodDescriptor(method);
            this.returnTypeDescriptor = Type.getReturnType(method).toString();
            List<Annotation> methodParameterAnnotations = getMethodParameterAnnotations(method);
            for (Annotation annotation : methodParameterAnnotations) {
                if (is(annotation, Caster.class)) {
                    visitVarInst.add(localVars.get("$caster"));
                } else if (is(annotation, Target.class)) {
                    visitVarInst.add(localVars.get("$target"));
                } else if (is(annotation, SkillArgument.class)) {
                    SkillArgument a = (SkillArgument) annotation;
                    if (isSkillSettingsSkillNode(a.value())) {
                        String skillSettingsNodeName = getSkillSettingsNodeName(a.value());
                        LocalVariableHelper variableHelper = localVars.get(skillSettingsNodeName);
                        visitVarInst.add(variableHelper);
                    }
                }
            }
        }

        public void invoke(MethodVisitor m, int index_this, String internalClassName, Object mechanic) {
            //**********************************************
            //var target = targetMechanic.getTarget(params)
            //visit field
            m.visitVarInsn(ALOAD, index_this); //injected field to stack
            m.visitFieldInsn(GETFIELD, internalClassName, fieldName(mechanic.getClass().getSimpleName()), getObjectTypeDescriptor(mechanic.getClass()));
            //       //invoke method
            for (LocalVariableHelper toVisit : visitVarInst) {
                m.visitVarInsn(toVisit.opCodeLoadInst, toVisit.fieldIndex);
            }
            m.visitMethodInsn(INVOKEVIRTUAL, methodOwner, methodName, methodDescriptor, false);

        }
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

        public LocalVariableHelper(int localVariableId, int aload, String path, String descriptor, Label f, Label l) {
            this(localVariableId, aload, path, descriptor);
            this.firstLabel = f;
            this.lastLabel = l;
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

    protected boolean isInlineMechanic(Config config) {
        String type = config.get("Type");
        return inlineMechanic.containsKey(type);
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

    protected Optional<Method> getRelevantMethod(Class<?> rawType) {
        return Stream.of(rawType.getDeclaredMethods())
                .filter(this::isHandlerMethod)
                .findFirst();

    }

    private boolean isHandlerMethod(Method method) {
        return Modifier.isPublic(method.getModifiers())
                && !Modifier.isStatic(method.getModifiers())
                && (method.isAnnotationPresent(Handler.class) || hasAnnotatedArgument(method));
    }

    private boolean hasAnnotatedArgument(Method method) {
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

    private boolean isOneOf(Annotation a, Class<?>... c) {
        for (Class<?> aClass : c) {
            if (is(a, aClass)) {
                return true;
            }
        }
        return false;
    }

    private boolean is(Annotation a, Class<?> c) {
        return a.annotationType() == c || a.getClass() == c;
    }


    @FunctionalInterface
    private interface InlineMechanic {
        void apply(MethodVisitor methodVisitor, Config config);
    }

    /**
     * So far we cannot do else branch
     */
    private static class Condition {

        private final Config config;
        private MethodVisitor methodVisitor;
        private Object boolmechanic;
        private final String internalClassName;
        private final int idx;

        private Label label_if;
        public Condition(Config config, Object boolmechanic, String internalClassName, int idx) {
            this.config = config;
            this.boolmechanic = boolmechanic;
            this.internalClassName = internalClassName;
            this.idx = idx;
        }

        public void _if(MethodInvocationHelper methodInvocationHelper) {
            methodInvocationHelper.invoke(methodVisitor, idx, internalClassName, boolmechanic);
            label_if = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label_if);
        }

        public void _then() {
            //do shit here
            methodVisitor.visitLabel(label_if);
            methodVisitor.visitLineNumber(99999, label_if);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
//xxxx if mechanics called in current frame had return value that i push on the stack then
            methodVisitor.visitFrame(Opcodes.F_CHOP, 0, null, 0, null);
        }

        public void _else() {
       }
    }

}
