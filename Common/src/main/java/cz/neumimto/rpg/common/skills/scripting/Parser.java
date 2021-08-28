package cz.neumimto.rpg.common.skills.scripting;

import cz.neumimto.rpg.api.utils.Pair;
import cz.neumimto.rpg.common.skills.scripting.Operations.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cz.neumimto.rpg.common.skills.scripting.Operations.*;

public class Parser {

    private static final Pattern ASSIGN = Pattern.compile(" *(@[a-zA-Z_-]*) *= *(.*)");
    private static final Pattern MECHANIC_CALL = Pattern.compile(" *([a-zA-Z_-]*)\\{(.*)}");
    private static final Pattern RETURN = Pattern.compile(" *RETURN *([a-zA-Z]*)");
    private static final Pattern NUMBER_CONSTANT = Pattern.compile("(^[-+]?([0-9]+)(\\.[0-9]+)?)$");
    private static final Pattern IF = Pattern.compile(" *IF (.*)");
    private static final Pattern IFNE = Pattern.compile(" *IF *NOT *(.*)");
    private static final Pattern END = Pattern.compile(" *END *");
    private static final Pattern DELAY = Pattern.compile(" *DELAY *([0-9]*) *");

    private Set<Object> mechanics;
    public Parser(Set<Object> mechanics) {
        this.mechanics = mechanics;
    }


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
        ops.add(new Delay(nextLambdaName(), enclosed, delay));
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



    public static record ParseTree(List<Operation> operations, Collection<String> requiredMechanics) {}


}
