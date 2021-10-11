package cz.neumimto.rpg.common;

public class Rpg {

    protected static RpgApi impl;

    public static RpgApi get() {
        return impl;
    }

}
