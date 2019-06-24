package cz.neumimto.rpg.api.localization;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Arg {

    public static Arg EMPTY;

    static {
        EMPTY = arg(Collections.emptyMap());
    }

    private Map<String, Object> map = new HashMap<>();

    public static Arg arg(final Map<String, Object> map) {
        Arg args = new Arg();
        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
            args.with(stringObjectEntry.getKey(), stringObjectEntry.getValue());
        }
        return args;
    }

    public static Arg arg(String s, Object o) {
        Arg args = new Arg();
        args.with(s, o);
        return args;
    }

    private static String START_TAG = "{{";
    private static String END_TAG = "}}";
    public Arg with(String s, Object o) {
        map.put(START_TAG + s + END_TAG, o);
        return this;
    }

    public Map<String, Object> getParams() {
        return map;
    }
}
