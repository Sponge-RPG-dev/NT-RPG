package cz.neumimto.rpg.api;

public class Rpg {

    static RpgApi impl;

    protected Rpg() {

    }

    public static RpgApi get() {
        return impl;
    }
}
