package cz.neumimto.rpg;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 16.12.2017.
 */
public class Arg {
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

        public Arg with(String s, Object o) {
            map.put("%" + s + "%", o);
            return this;
        }

    public Map<String, Object> getParams() {
        return map;
    }
}
