package cz.neumimto.rpg.spigot;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.RpgApi;

public class RpgImpl extends Rpg {

    public RpgImpl(RpgApi rpgApi) {
        super();
        impl = rpgApi;
    }
}
