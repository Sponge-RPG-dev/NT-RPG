package cz.neumimto.rpg.common.skills.scripting;

import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.utils.Pair;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.DoubleConstant;
import net.bytebuddy.implementation.bytecode.constant.LongConstant;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.*;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Operations {

    record AssignValue(String variableName, MethodVariableAccess access) implements Operation {

        @Override
        public List<StackManipulation> getStack(TokenizerContext context) {
            RefData refData = context.localVariables().get(variableName);
            if (refData == null) {
                refData = new RefData(access, )
                context.localVariables().put(variableName, refData);
            }
            return Arrays.asList(
                    refData.type.storeAt(refData.offset)
            );
        }
    }

    //damage=$settings.damage
    record CallMechanic(String mechanic, List<Pair<String, String>> variables) implements Operation {

        public Class<?> getReturnType() {
            Object mechObj = context.mechanics()
                    .stream()
                    .filter(a -> a.getClass().getAnnotation(SkillMechanic.class).value().equalsIgnoreCase(mechanic))
                    .findFirst().get();
            Method method = Stream.of(mechObj.getClass().getDeclaredMethods())
                    .filter(a->a.isAnnotationPresent(Handler.class))
                    .findFirst()
                    .get();

            return method.getReturnType();
        }


        @Override
        public Map<String, MethodVariableAccess> skillSettingsVarsRequired(TokenizerContext context) {
            Map<String, MethodVariableAccess> map = new HashMap<>();
            Object mechObj = context.mechanics()
                    .stream()
                    .filter(a -> a.getClass().getAnnotation(SkillMechanic.class).value().equalsIgnoreCase(mechanic))
                    .findFirst().get();
            Method method = Stream.of(mechObj.getClass().getDeclaredMethods())
                    .filter(a->a.isAnnotationPresent(Handler.class))
                    .findFirst()
                    .get();

            for (Pair<String, String> var : variables) {
                Optional<Parameter> first = Stream.of(method.getParameters())
                        .filter(a -> a.isAnnotationPresent(SkillArgument.class)
                                && a.getAnnotation(SkillArgument.class).value().equals(var.key)
                                && var.value.contains("$settings"))
                        .findFirst();
                first.ifPresent(parameter -> map.put(var.key.trim(), MethodVariableAccess.of(new TypeDescription.ForLoadedType(parameter.getType()))));
            }

            return map;
        }

        @Override
        public List<StackManipulation> getStack(TokenizerContext context) {
            List<StackManipulation> list = new ArrayList<>();

            list.add(MethodVariableAccess.loadThis());
            list.add(FieldAccess.forField(mechanicField(context)).read());

            Method method = getMethod(context);
            Parameter[] parameters = method.getParameters();

            for (Parameter parameter : parameters) {
                if (parameter.getType() == ISkill.class) {
                    list.add(MethodVariableAccess.loadThis()); //aload_0
                    continue;
                }
                SkillArgument annotation = parameter.getAnnotation(SkillArgument.class);
                String functionParamName = annotation.value();
                Iterator<Pair<String, String>> iterator = new ArrayList<>(variables).iterator();
                boolean found = false;
                while (iterator.hasNext()) {
                    Pair<String, String> next = iterator.next();
                    if (!next.key.equalsIgnoreCase(functionParamName)) {
                        continue;
                    }
                    String value = next.value;
                    //settings ref
                    if (value.startsWith("$settings")) { //param = $settings.xxx
                        value = next.key;
                        RefData refData = context.localVariables().get(value);
                        list.add(refData.type.loadFrom(refData.offset));

                    } else if (NUMBER_CONSTANT.matcher(value).matches()) { //param = 20
                        list.add(DoubleConstant.forValue(Double.parseDouble(value)));

                    } else {
                        //variable ref
                        RefData refData = context.localVariables().get(value);
                        list.add(refData.type.loadFrom(refData.offset));
                    }
                    found = true;
                    break;
                }
                if (!found) {
                    list.add(NullConstant.INSTANCE);
                }
            }

            list.add(MethodInvocation.invoke(new MethodDescription.ForLoadedMethod(method)));
            return list;
        }

        private FieldDescription mechanicField(TokenizerContext context) {
            return context.thisType().getDeclaredFields()
                    .filter(ElementMatchers.named(mechanicObj(context).getClass().getSimpleName()))
                    .getOnly();
        }

        private Object mechanicObj(TokenizerContext context) {
            return context.mechanics().stream()
                    .filter(a -> a.getClass().getAnnotation(SkillMechanic.class).value().equalsIgnoreCase(mechanic))
                    .findFirst().get();
        }

        private Method getMethod(TokenizerContext context) {
            return Stream.of(mechanicObj(context).getClass().getDeclaredMethods()).filter(a -> a.isAnnotationPresent(Handler.class)).findFirst().get();
        }

    }

    record Delay(String methodname, List<Operation> enclosed, String delayArg) implements Operation {

        @Override
        public List<StackManipulation> getStack(TokenizerContext context) {
            StackManipulation delay = null;
            if (delayArg.startsWith("$settings.")) {
                RefData refData = context.localVariables().get(delayArg.replaceAll("\\$settings\\.", ""));
                delay = refData.type.loadFrom(refData.offset);
            } else {
                delay = LongConstant.forValue(Long.parseLong(delayArg));
            }

            RunnableLambdaCall runnableLambdaCall = new RunnableLambdaCall(context, methodname, enclosed);
            Method method = null;
            try {
                method = ISkill.class.getDeclaredMethod("delay", long.class, Runnable.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            List<StackManipulation> list = new ArrayList<>();
            list.addAll(Arrays.asList(
                    MethodVariableAccess.loadThis(),
                    delay,
                    MethodVariableAccess.loadThis())
            );
            for (RefData value : context.localVariables().values()) {
                list.add(value.type.loadFrom(value.offset));
            }
            var s0 = Arrays.asList(
                    MethodVariableAccess.loadThis(),
                    delay,
                    MethodVariableAccess.loadThis()
            );
            var s1 = context.localVariables().values().stream().map(a->a.type.loadFrom(a.offset)).collect(Collectors.toList());
            var s2 = Arrays.asList(
                    runnableLambdaCall,
                    MethodInvocation.invoke(new MethodDescription.ForLoadedMethod(method))
            );
            var result = new ArrayList<StackManipulation>();
            result.addAll(s0);
            result.addAll(s1);
            result.addAll(s2);
            return result;
        }

        @Override
        public Map<String, List<Operation>> additonalMethods(TokenizerContext context) {
            Map<String, List<Operation>> map = new HashMap<>();
            for (Operation operation : enclosed) {
                map.putAll(operation.additonalMethods(context));
            }
            map.put(methodname, enclosed);
            return map;
        }
    }

    record ReturnEnum(String value) implements Operation {

        @Override
        public List<StackManipulation> getStack(TokenizerContext context) {
            return Arrays.asList(
                    FieldAccess.forEnumeration(new EnumerationDescription.ForLoadedEnumeration(Enum.valueOf(SkillResult.class, value))),
                    MethodReturn.REFERENCE
            );
        }
    }

    record ReturnVoid() implements Operation {
        @Override
        public List<StackManipulation> getStack(TokenizerContext context) {
            return Collections.singletonList(MethodReturn.VOID);
        }
    }

    class IF implements Operation {
        private List<Operation> enclosed;
        private boolean negate;

        public IF(List<Operation> enclosed, boolean negate) {
            this.enclosed = enclosed;
            this.negate = negate;
        }

        @Override
        public List<StackManipulation> getStack(TokenizerContext context) {
            var list = new ArrayList<StackManipulation>();
            Label label1 = new Label();
            Label label = label1;
            list.add(negate ? new IfNEq(label) : new IfEq(label));

            list.add(new Mark(label1));
            for (Operation operation : enclosed) {
                list.addAll(operation.getStack(context));
            }
            list.add(new Mark(label));
            //todo APPEND FRAME or ref SAME depending on enclosed or just tell MethodVisitor to compute frames?
            return list;
        }

        @Override
        public Map<String, List<Operation>> additonalMethods(TokenizerContext context) {
            Map<String, List<Operation>> map = new HashMap();
            for (Operation operation : enclosed) {
                map.putAll(operation.additonalMethods(context));
            }
            return map;
        }
    }

    static class IfEq implements StackManipulation {
        private final Label label;

        public IfEq(Label label) {
            this.label = label;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public StackManipulation.Size apply(MethodVisitor mv, Implementation.Context ctx) {
            mv.visitJumpInsn(Opcodes.IFEQ, label);
            return new StackManipulation.Size(0, 0);
        }
    }

    class IfNEq implements StackManipulation {
        private final Label label;

        public IfNEq(Label label) {
            this.label = label;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public StackManipulation.Size apply(MethodVisitor mv, Implementation.Context ctx) {
            mv.visitJumpInsn(Opcodes.IFNE, label);
            return new StackManipulation.Size(0, 0);
        }
    }

    class GoTo implements StackManipulation {
        private final Label label;

        public GoTo(Label label) {
            this.label = label;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public StackManipulation.Size apply(MethodVisitor mv, Implementation.Context ctx) {
            mv.visitJumpInsn(Opcodes.GOTO, label);
            return new StackManipulation.Size(0, 0);
        }
    }

    class Mark implements StackManipulation {
        private final Label label;

        public Mark(Label label) {
            this.label = label;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public StackManipulation.Size apply(MethodVisitor mv, Implementation.Context ctx) {
            mv.visitLabel(label);
            return new StackManipulation.Size(0, 0);
        }
    }

    static class RunnableLambdaCall implements StackManipulation {

        private TypeDescription typeDefinitions;
        private TokenizerContext ctx;
        private String methodName;
        private List<Operation> enclosed;

        public RunnableLambdaCall(TokenizerContext ctx,
                                  String methodName, List<Operation> enclosed) {
            this.typeDefinitions = ctx.thisType();
            this.ctx = ctx;
            this.methodName = methodName;
            this.enclosed = enclosed;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {

            String descriptor = "(%s)Ljava/lang/Runnable;";
            String k = ctx.localVariables().values().stream().map(a-> new TypeDescription.ForLoadedType(a.aClass).getDescriptor()).collect(Collectors.joining());
            descriptor = descriptor.replaceAll("%s",ctx.thisType().getDescriptor() + k);

            methodVisitor.visitInvokeDynamicInsn("run",
                    descriptor,
                    new Handle(Opcodes.H_INVOKESTATIC,
                            new TypeDescription.ForLoadedType(LambdaMetafactory.class).getInternalName(),
                            "metafactory",
                            MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class, Object[].class).toMethodDescriptorString(),
                            false),
                    Type.VOID_TYPE,
                    new Handle(Opcodes.H_INVOKESPECIAL,
                            this.ctx.thisType().getInternalName(),
                            methodName,
                            "("+k+")V",
                            false),
                    Type.VOID_TYPE);
            return new Size(1,0);
        }
    }
}
