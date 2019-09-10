package cz.neumimto.rpg.spigot;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.RpgApi;

public class RpgImpl extends Rpg {

    public RpgImpl(RpgApi rpgApi) {
        super();
        impl = rpgApi;
    }
}
