package cz.neumimto.rpg.common.utils;

public class Wildcards {

    public static boolean matches(String text, String pattern) {
        return text.matches(pattern.replace("?", ".?").replace("*", ".*?"));
    }
}
