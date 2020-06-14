package cz.neumimto.rpg.common.bytecode;

import com.google.inject.Injector;
import com.google.inject.Key;
import cz.neumimto.rpg.common.skills.scripting.*;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;

public class SimpleMethodVisitor extends MethodVisitor implements Opcodes {

    private Injector injector;

    public SimpleMethodVisitor(int i, Injector injector) {
        super(i);
        this.injector = injector;
    }


    protected void visitMechanicInvokeInst(MethodVisitor mv, int this_index, MethodInvocationHelper mih) {
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
        return c.isAnnotationPresent(SkillMechanic.class) || c.isAnnotationPresent(TargetSelector.class);
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
