package cz.neumimto.rpg.api.configuration.adapters;


import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.logging.Log;

import java.util.function.Predicate;

/**
 * Created by NeumimTo on 6.1.2019.
 */
public class ClassTypeAdapter implements Predicate<Object> {

    @Override
    public boolean test(Object string) {
        if (string == null) {
            return false;
        }
        if (string.getClass() == String.class) {
            return false;
        }
        if (!Rpg.get().getPluginConfig().CLASS_TYPES.containsKey(string)) {
            String all = String.join(", ", Rpg.get().getPluginConfig().CLASS_TYPES.keySet());
            Log.error("Unknown class type \"" + string + "\", must be one of " + all);
            return false;
        }
        return true;
    }
}
