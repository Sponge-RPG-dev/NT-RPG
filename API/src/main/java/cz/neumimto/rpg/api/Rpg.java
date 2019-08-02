package cz.neumimto.rpg.api;

public class Rpg {

    protected static RpgApi impl;

    public static RpgApi get() {
        return impl;
    }

}
