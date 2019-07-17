package cz.neumimto.rpg.api;

public class Rpg {

    static RpgApi impl;

    protected Rpg(RpgApi rpgApi) {
        impl = rpgApi;
    }

    public static RpgApi get() {
        return impl;
    }
}
