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
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
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
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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

    public ParseTree parse(String input) {
        String[] split = input.split("\\r?\\n");

        var iterator = Arrays.stream(split).iterator();
        List<Operation> list = new ArrayList<>();

        Set<String> mechanics = new HashSet<>();

        while (iterator.hasNext()) {
            list.addAll(parse(iterator.next(), iterator, mechanics));
        }

        return new ParseTree(list, mechanics);
    }

    private List<Operation> parse(String input, Iterator<String> iterator, Set<String> mechanics) {
        List<Operation> ops = new ArrayList<>();

        regexp(ASSIGN, input, matcher -> parseAssign(input, iterator, mechanics, matcher, ops));
        regexp(RETURN, input, matcher -> parseReturn(input, iterator, mechanics, matcher, ops));
        regexp(MECHANIC_CALL,input, pattern -> parseMechanicCall(input, iterator, mechanics, pattern, ops));
        regexp(IF, input, matcher -> parseIf(input, iterator, mechanics, matcher, ops));
        regexp(DELAY, input, matcher -> parseDelay(input, iterator, mechanics, matcher, ops));
        return ops;
    }


    private void parseDelay(String input, Iterator<String> iterator, Set<String> mechanics, Matcher matcher, List<Operation> ops) {
        String delay = matcher.group(1);

        int ifIdx = 0;

        List<Operation> enclosed = new ArrayList<>();
        List<Operation> delayParam = parse(delay, iterator, mechanics);
        delayParam.add(new Operation() {
            @Override
            public List<StackManipulation> getStack(TokenizerContext context) {
                return Arrays.asList(
                        MethodVariableAccess.loadThis(),
                        LongConstant.forValue(1000)
                );
            }
        });
        while (iterator.hasNext()) {
            String next = iterator.next();
            enclosed.addAll(parse(next, iterator, mechanics));
            if (END.matcher(next).matches()) {
                ifIdx--;
                if (ifIdx <= 0) {
                    enclosed.add(new ReturnVoid());
                    break;
                }
            }
            if (IF.matcher(next).matches()) {
                ifIdx++;
            }
        }
        ops.add(new Delay(nextLambdaName(), enclosed));
    }

    private void parseReturn(String input, Iterator<String> iterator, Set<String> mechanics, Matcher matcher, List<Operation> ops) {
        String returnVal = matcher.group(1);
        ops.add(new ReturnEnum(returnVal.toUpperCase(Locale.ROOT)));
    }

    private void parseIf(String input, Iterator<String> iterator, Set<String> mechanics, Matcher matcher, List<Operation> ops) {
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

    private void parseAssign(String input, Iterator<String> iterator, Set<String> mechanics, Matcher matcher, List<Operation> ops) {
        String rightPart = matcher.group(2);
        ops.addAll(parse(rightPart, iterator, mechanics));

        String leftPart = matcher.group(1);
        ops.add(new AssignValue(leftPart));
    }

    private void parseMechanicCall(String input, Iterator<String> iterator, Set<String> mechanics, Matcher matcher, List<Operation> ops) {
        String mechanicName = matcher.group(1);
        String mechanicArgs = matcher.group(2);

        String[] args = mechanicArgs.split(",");

        List<Pair<String, String>> argList = new ArrayList<>();

        for (String arg : args) {
            String[] split = arg.split("=");
            String leftSide = split[0];
            String rightSide = split[1];
            argList.add(new Pair<>(leftSide.trim(), rightSide.trim()));
        }

        mechanics.add(mechanicName);
        ops.add(new CallMechanic(mechanicName, argList));
    }

    int lambdaIdx = 0;
    private String nextLambdaName() {
        String s = "lambda$a$"+lambdaIdx;
        lambdaIdx++;
        return s;
    }

    public boolean regexp(Pattern regexp, String input, Consumer<Matcher> p) {
        Matcher matcher = regexp.matcher(input);
        if (matcher.matches()) {
            p.accept(matcher);
            return true;
        }
        return false;
    }

    public interface Operation {

        List<StackManipulation> getStack(TokenizerContext context);

        default Map<String, MethodVariableAccess> skillSettingsVarsRequired(TokenizerContext context) {
            return Collections.emptyMap();
        }

        default Map<String, List<Operation>> additonalMethods(TokenizerContext context) {
            return Collections.emptyMap();
        }

        default List<Pair<String, String>> variables() {
            return Collections.emptyList();
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
            RunnableLambdaCall runnableLambdaCall = new RunnableLambdaCall(context, methodname, enclosed);
            return Collections.singletonList(
                runnableLambdaCall
            );
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

    private static record ReturnEnum(String value) implements Operation {

        @Override
        public List<StackManipulation> getStack(TokenizerContext context) {
            return Arrays.asList(
                    FieldAccess.forEnumeration(new EnumerationDescription.ForLoadedEnumeration(Enum.valueOf(SkillResult.class, value))),
                    MethodReturn.REFERENCE
            );
        }
    }

    private static record ReturnVoid() implements Operation {
        @Override
        public List<StackManipulation> getStack(TokenizerContext context) {
            return Collections.singletonList(MethodReturn.VOID);
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

        @Override
        public Map<String, List<Operation>> additonalMethods(TokenizerContext context) {
            Map<String, List<Operation>> map = new HashMap();
            for (Operation operation : enclosed) {
                map.putAll(operation.additonalMethods(context));
            }
            return map;
        }
    }

    public static record ParseTree(List<Operation> operations, Collection<String> requiredMechanics) {}


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
            String k = "";
            for (Operation operation : enclosed) {
                List<Pair<String, String>> variables = operation.variables();


                for (Pair<String, String> entrya : variables) {
                    ScriptSkillBytecodeAppenter.RefData refData = ctx.localVariables().get(entrya.value);
                    k += new TypeDescription.ForLoadedType(refData.aClass).getDescriptor();
                }
            }
            descriptor = descriptor.replaceAll("%s",k);

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
