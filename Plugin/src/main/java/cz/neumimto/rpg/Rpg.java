package cz.neumimto.rpg;

import cz.neumimto.rpg.api.RpgApi;

public class Rpg {

    protected static RpgApi impl;

    public static RpgApi get() {
        return impl;
    }
}
