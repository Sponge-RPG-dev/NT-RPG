package cz.neumimto.rpg.common.skills.scripting;

import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.utils.Pair;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.DoubleConstant;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.*;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class Parser {

    private static final Pattern ASSIGN = Pattern.compile(" *(@[a-zA-Z_-]*) *= *(.*)");
    private static final Pattern MECHANIC_CALL = Pattern.compile(" *([a-zA-Z_-]*)\\{(.*)}");
    private static final Pattern MECHANIC_CALL_ARGS = Pattern.compile(" *(([a-zA-Z0-9_-]*) *= *([@$a-zA-Z0-9]*)) *");
    private static final Pattern RETURN = Pattern.compile(" *RETURN *([a-zA-Z]*)");
    private static final Pattern NUMBER_CONSTANT = Pattern.compile("(^[-+]?([0-9]+)(\\.[0-9]+)?)$");
    private static final Pattern IF = Pattern.compile(" *IF (.*)");
    private static final Pattern IFNE = Pattern.compile(" *IF *NOT *(.*)");
    private static final Pattern END = Pattern.compile(" *END *");
    private static final Pattern DELAY = Pattern.compile(" *DELAY *([0-9]*) *");

    private static final Pattern SETTINGS_VAR = Pattern.compile("(?<=\\$settings\\.)([a-zA-Z-_0-9]*)(?=[} ,])");

    public ParserOutput parse(String input) {
        String[] split = input.split("\\r?\\n");

        var iterator = Arrays.stream(split).iterator();
        List<Operation> list = new ArrayList<>();

        Set<String> mechanics = new HashSet<>();

        while (iterator.hasNext()) {
            list.addAll(parse(iterator.next(), iterator, mechanics));
        }


        return new ParserOutput(list, mechanics);
    }

    private List<Operation> parse(String input, Iterator<String> iterator, Set<String> mechanics) {
        List<Operation> ops = new ArrayList<>();

        Matcher matcher = ASSIGN.matcher(input);
        if (matcher.find()) {
            String rightPart = matcher.group(2);
            ops.addAll(parse(rightPart, iterator, mechanics));

            String leftPart = matcher.group(1);
            ops.add(new AssignValue(leftPart));
        } else {
            matcher = MECHANIC_CALL.matcher(input);
            if (matcher.matches()) {
                String mechanicName = matcher.group(1);
                String mechanicArgs = matcher.group(2);
                
                Matcher matcher1 = MECHANIC_CALL_ARGS.matcher(mechanicArgs);
                List<Pair<String, String>> argList = new ArrayList<>();
                if (matcher1.find()) {
                    int id = 1;
                    while (id < matcher1.groupCount()) {
                        String expr = matcher1.group(id);
                        id++;
                        String leftSide = matcher1.group(id);
                        id++;
                        String rightSide = matcher1.group(id);
                        argList.add(new Pair<>(leftSide, rightSide));
                    }
                }
                mechanics.add(mechanicName);
                ops.add(new CallMechanic(mechanicName, argList));
            } else {
                matcher = RETURN.matcher(input);
                if (matcher.matches()) {
                    String returnVal = matcher.group(1);
                    ops.add(new Return(returnVal.toUpperCase(Locale.ROOT)));
                } else {
                    matcher = IF.matcher(input);
                    if (matcher.matches()) {
                        String value = matcher.group(1);
                        ops.addAll(parse(value, iterator, mechanics)); // IF <EXPR>

                        int ifIdx = 0;

                        //IF
                        // <body>
                        //END
                        List<Operation> enclosed = new ArrayList<>();
                        while (iterator.hasNext()) {
                            String next = iterator.next();
                            enclosed.addAll(parse(next, iterator, mechanics));
                            if (END.matcher(next).matches()) {
                                ifIdx--;
                                if (ifIdx <= 0) {
                                    break;
                                }
                            }
                            if (IF.matcher(next).matches()) {
                                ifIdx++;
                            }

                        }
                        ops.add(new IF(enclosed, IFNE.matcher(input).matches()));

                    }
                    //DELAY 1000
                    // <body>
                    //END
                    matcher = DELAY.matcher(input);
                    if (matcher.matches()) {
                        String delay = matcher.group(1);
                        int ifIdx = 0;

                        List<Operation> enclosed = new ArrayList<>();
                        while (iterator.hasNext()) {
                            String next = iterator.next();
                            enclosed.addAll(parse(next, iterator, mechanics));
                            if (END.matcher(next).matches()) {
                                ifIdx--;
                                if (ifIdx <= 0) {
                                    break;
                                }
                            }
                            if (IF.matcher(next).matches()) {
                                ifIdx++;
                            }
                        }
                        ops.add(new Delay(nextLambdaName(), enclosed));
                    }
                }
            }
        }

        return ops;
    }

    int lambdaIdx = 0;
    private String nextLambdaName() {
        String s = "lambda$a$"+lambdaIdx;
        lambdaIdx++;
        return s;
    }

    public interface Operation {

        List<StackManipulation> getStack(TokenizerContext context);

        default Map<String, MethodVariableAccess> skillSettingsVarsRequired(TokenizerContext context) {
            return Collections.emptyMap();
        }

        default Map<String, List<Operation>> additonalMethods(TokenizerContext context) {
            return Collections.emptyMap();
        }
    }



    private record AssignValue(String variableName) implements Operation {

        @Override
        public List<StackManipulation> getStack(TokenizerContext context) {
            ScriptSkillBytecodeAppenter.RefData refData = context.localVariables().get(variableName);
            return Arrays.asList(
                    refData.type.storeAt(refData.offset)
            );
        }
    }

    //damage=$settings.damage
    private record CallMechanic(String mechanic, List<Pair<String, String>> variables) implements Operation {

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
                                && var.value.equals("$settings"))
                        .findFirst();
                first.ifPresent(parameter -> map.put(var.key, MethodVariableAccess.of(new TypeDescription.ForLoadedType(parameter.getType()))));
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
                    list.add(MethodVariableAccess.loadThis()); //alod_0
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
                    if (value.equals("$settings")) { //param = $settings.xxx
                        value = next.key;
                        ScriptSkillBytecodeAppenter.RefData refData = context.localVariables().get(value);
                        list.add(refData.type.loadFrom(refData.offset));

                    } else if (NUMBER_CONSTANT.matcher(value).matches()) { //param = 20
                        list.add(DoubleConstant.forValue(Double.parseDouble(value)));

                    } else {
                        //variable ref
                        ScriptSkillBytecodeAppenter.RefData refData = context.localVariables().get(value);
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

    private static record Delay(String methodname, List<Operation> enclosed) implements Operation {

        @Override
        public List<StackManipulation> getStack(TokenizerContext context) {

            MethodDescription.InDefinedShape targetCall = context.thisType().getDeclaredMethods().filter(named(methodname)).getOnly();
            //InvokeDynamic methodRef = InvokeDynamic.lambda(targetCall, new TypeDescription.ForLoadedType(type)).withoutArguments();

            RunnableLambdaCall runnableLambdaCall = new RunnableLambdaCall(context.thisType(), methodname);
            return Collections.singletonList(
                runnableLambdaCall
            );
        }

        @Override
        public Map<String, List<Operation>> additonalMethods(TokenizerContext context) {
            Map<String, List<Operation>> map = new HashMap<>();
            map.put(methodname, enclosed);
            return map;
        }
    }

    private static record Return(String value) implements Operation {

        @Override
        public List<StackManipulation> getStack(TokenizerContext context) {
            return Arrays.asList(
                    FieldAccess.forEnumeration(new EnumerationDescription.ForLoadedEnumeration(Enum.valueOf(SkillResult.class, value))),
                    MethodReturn.REFERENCE
            );
        }
    }

    private static class IF implements Operation {
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
    }

    public static record ParserOutput(List<Operation> operations, Collection<String> requiredMechanics) {}


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

    static class IfNEq implements StackManipulation {
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

    static class GoTo implements StackManipulation {
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

    static class Mark implements StackManipulation {
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
        private String methodName;

        public RunnableLambdaCall(TypeDescription typeDefinitions,
                                  String methodName) {
            this.typeDefinitions = typeDefinitions;
            this.methodName = methodName;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {


            methodVisitor.visitInvokeDynamicInsn("run",
                    "(L"+typeDefinitions.getDescriptor()+";)Ljava/lang/Runnable;",
                    new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false),
                    Type.VOID_TYPE,
                    new Handle(Opcodes.H_INVOKESPECIAL, typeDefinitions.getDescriptor(), methodName, Type.VOID_TYPE.getDescriptor(), false),
                    Type.VOID_TYPE);
            return new Size(1,0);
        }
    }
}
