package cz.neumimto.rpg.common.utils;

import java.util.Set;
import java.util.stream.Collectors;

public class Wildcards {

    public static boolean matches(String text, String pattern) {
        return text.matches(pattern.replace("?", ".?").replace("*", ".*?"));
    }

    public static Set<String> substract(String itemId, Set<String> allItemIds) {
        return allItemIds.stream()
                .filter(a -> matches(a, itemId))
                .collect(Collectors.toSet());
    }
}
