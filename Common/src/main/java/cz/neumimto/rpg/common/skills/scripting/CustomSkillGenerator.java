package cz.neumimto.rpg.common.skills.scripting;

import com.electronwill.nightconfig.core.Config;
import com.google.inject.Injector;
import com.google.inject.Key;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import jdk.internal.org.objectweb.asm.Type;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;

@Singleton
public class CustomSkillGenerator implements Opcodes {

    @Inject
    private Injector injector;

    public void generate(ScriptSkillModel scriptSkillModel) {
        if (scriptSkillModel == null || scriptSkillModel.getSpell() == null) {
            return;
        }

        String className = "cz.neumimto.skills.scripts.Custom" + System.currentTimeMillis();

        DynamicType.Builder<ActiveSkill> builder = new ByteBuddy()
                .subclass(ActiveSkill.class)
                .name(className)
                .annotateType(AnnotationDescription.Builder.ofType(ResourceLoader.Skill.class)
                        .define("value", scriptSkillModel.getId())
                        .build())
                .annotateType(AnnotationDescription.Builder.ofType(Singleton.class).build());

        Set<Object> mechanics = getRelevantMechanics(getMechanics(), scriptSkillModel.getSpell());

        for (Object mechanic : mechanics) {
            builder = builder.defineField(mechanic.getClass().getSimpleName(), mechanic.getClass(), Modifier.PRIVATE)
                    .annotateField(AnnotationDescription.Builder.ofType(Inject.class).build());
        }


        builder = builder
                .defineMethod("cast", SkillResult.class, Ownership.MEMBER, Visibility.PUBLIC)
                .withParameters(IActiveCharacter.class, PlayerSkillContext.class)
                .intercept(MethodDelegation.to(new Interceptor(mechanics, scriptSkillModel.getSpell(), className.replaceAll("\\.", "/"))));


        Class<? extends ActiveSkill> skillClass = builder.make()
                .load(getClass().getClassLoader())
                .getLoaded();
    }

    private Set<Object> getRelevantMechanics(Set<Object> mechanics, List<Config> spell) {
        Set finalSet = new HashSet();
        for (Object mechanic : mechanics) {
            for (Config config : spell) {
                String type = config.get("Target-Selector");
                if (mechanic.getClass().getAnnotation(TargetSelector.class).value().equalsIgnoreCase(type)) {
                    finalSet.add(mechanic);
                    continue;
                }
                List<Config> list = config.get("Mechanics");
                for (Config config1 : list) {
                    type = config1.get("Type");
                    if (mechanic.getClass().getAnnotation(SkillMechanic.class).value().equalsIgnoreCase(type)) {
                        finalSet.add(mechanic);
                    }
                }
            }
        }
        return finalSet;
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

        private final Set<Object> mechanics;
        private final Helper helper;
        private final String internalClassName;

        public Interceptor(Set<Object> mechanics, List<Config> spell, String internalClassName) {
            this.mechanics = mechanics;
            this.helper = Helper.parse(spell);
            this.internalClassName = internalClassName;
        }

        @Override
        public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            methodVisitor.visitCode();

            Map<String, String> localVars = findRequiredLocalVars(mechanics);

            int index_this = 0;
            int index_caster = 1;
            int index_context = 2;

            Label label = new Label();

            methodVisitor.visitLocalVariable("this", "L" + internalClassName + ";", null, label, label, index_this);
            methodVisitor.visitLocalVariable("character", getObjectTypeDescriptor(IActiveCharacter.class), null, label, label, index_caster);
            methodVisitor.visitLocalVariable("context", getObjectTypeDescriptor(PlayerSkillContext.class), null, label, label, index_context);

            Map<String, LocalVariableHelper> localVariables = new HashMap<>();
            localVariables.put("caster", new LocalVariableHelper(index_caster, ALOAD));
            localVariables.put("context", new LocalVariableHelper(index_context, ALOAD));
            localVariables.put("this", new LocalVariableHelper(index_this, ALOAD));

            int localVariableId = 3;
            for (Map.Entry<String, String> e : localVars.entrySet()) {
                String path = e.getKey();
                String type = e.getValue();
                if (isSkillSettingsSkillNode(path)) {
                    path = getSkillSettingsNodeName(path);
                    visitReadSkillSettingsVariable(methodVisitor, path, path, localVariableId, index_context, label);
                    localVariables.put(path, new LocalVariableHelper(localVariableId, FLOAD));
                    localVariableId++;
                }
            }

            for (Map.Entry<String, List<? extends Config>> entry : helper.targetSelectors.entrySet()) {
                String targetSelectorId = entry.getKey();
                List<? extends Config> mechanics = entry.getValue();

                Class<?> targetSelector = filterMechanicById(targetSelectorId).getClass();
                String fieldName = targetSelector.getSimpleName();

                for (Config mechanic : mechanics) {
                    visitMechanicInvokeInst(methodVisitor, index_this, new MethodInvocationHelper(internalClassName, mechanic, localVariables));
                }

            }

            StackManipulation.Size operandStackSize = new StackManipulation.Compound().apply(methodVisitor, implementationContext);
            return new Size(operandStackSize.getMaximalSize(), instrumentedMethod.getStackSize());
        }
    }


    private void visitMechanicInvokeInst(MethodVisitor mv, int this_index, MethodInvocationHelper mih) {
        mv.visitVarInsn(ALOAD, this_index);
        mv.visitFieldInsn(GETFIELD, mih.owner, mih.fieldName, mih.fieldDescriptor);
        for (LocalVariableHelper value : mih.visitVarInst) {
            mv.visitVarInsn(value.opCodeLoadInst, value.fieldIndex);
        }
        mv.visitMethodInsn(INVOKEVIRTUAL, mih.methodOwner, mih.methodName, mih.methodDescriptor, false);
    }


    private class MethodInvocationHelper {
        final String owner;
        final String fieldName;
        final String fieldDescriptor;
        final String methodOwner;
        final String methodName;
        final String methodDescriptor;
        final List<LocalVariableHelper> visitVarInst;

        public MethodInvocationHelper(String parent, Object mechanic, Map<String, LocalVariableHelper> localVars) {
            this.visitVarInst = new ArrayList<>();
            this.owner = parent;
            this.fieldName = mechanic.getClass().getSimpleName();
            this.fieldDescriptor = getObjectTypeDescriptor(mechanic.getClass());
            this.methodOwner = getInternalName(mechanic.getClass());
            Method method = getRelevantMethod(mechanic.getClass()).get();
            this.methodName = method.getName();
            this.methodDescriptor = getMethodDescriptor(method);

            List<Annotation> methodParameterAnnotations = getMethodParameterAnnotations(method);
            for (Annotation annotation : methodParameterAnnotations) {
                if (is(annotation, Caster.class)) {
                    visitVarInst.add(localVars.get("caster"));
                } else if (is(annotation, Target.class)) {
                    visitVarInst.add(localVars.get("target"));
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

        private LocalVariableHelper(int fieldIndex, int opCodeLoadInst) {
            this.fieldIndex = fieldIndex;
            this.opCodeLoadInst = opCodeLoadInst;
        }
    }

    private String getInternalName(Class<?> c) {
        return Type.getInternalName(c);
    }

    private String getMethodDescriptor(Method method) {
        try {
            return MethodHandles.lookup().unreflect(method).type().toMethodDescriptorString();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getObjectTypeDescriptor(Class<?> c) {
        return "L" + getInternalName(c) + ";";
    }

    private void visitReadSkillSettingsVariable(MethodVisitor mv, String settingsNode, String variableName, int localVariableId, int index_context, Label label) {
        mv.visitVarInsn(ALOAD, index_context);
        mv.visitMethodInsn(INVOKEVIRTUAL, "cz/neumimto/rpg/api/skills/PlayerSkillContext", "getCachedComputedSkillSettings", "()Lit/unimi/dsi/fastutil/objects/Object2FloatOpenHashMap;", false);
        mv.visitLdcInsn(settingsNode);
        mv.visitMethodInsn(INVOKEVIRTUAL, "it/unimi/dsi/fastutil/objects/Object2FloatOpenHashMap", "getFloat", "(Ljava/lang/Object;)F", false);
        mv.visitVarInsn(FSTORE, localVariableId);
        //todo labels
        mv.visitLocalVariable(variableName, "F", null, label, label, localVariableId);
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

    private Map<String, String> findRequiredLocalVars(Set<Object> mechanics) {
        Map<String, String> map = new HashMap<>();
        for (Object mechanic : mechanics) {
            Method relevantMethod = getRelevantMethod(mechanic.getClass()).get();
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

    protected Object filterMechanicById(String id) {
        for (Object mechanic : getMechanics()) {
            String annotationId = getAnnotationId(mechanic.getClass());
            if (id.equalsIgnoreCase(annotationId)) {
                return null;
            }
        }
        return null;
    }

    protected boolean hasAnnotation(Class<?> c) {
        return getAnnotationId(c) != null;
    }

    protected String getAnnotationId(Class<?> rawType) {
        return rawType.isAnnotationPresent(SkillMechanic.class) ?
                rawType.getAnnotation(SkillMechanic.class).value() : rawType.getAnnotation(TargetSelector.class).value();
    }

    protected Optional<Method> getRelevantMethod(Class<?> rawType) {
        return Stream.of(rawType.getMethods())
                .filter(AccessibleObject::isAccessible)
                .filter(method -> !Modifier.isStatic(rawType.getModifiers()))
                .filter(method -> Modifier.isPublic(rawType.getModifiers()))
                .filter(this::hasAnnotatedArgument)
                .findFirst();

    }

    private boolean hasAnnotatedArgument(Method method) {
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

    private boolean isOneOf(Annotation a, Class<?>... c) {
        for (Class<?> aClass : c) {
            if (!is(a, aClass)) {
                return false;
            }
        }
        return true;
    }

    private boolean is(Annotation a, Class<?> c) {
        return a.getClass() == c;
    }
}
