package cz.neumimto.rpg;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.RpgApi;

public class RpgTest extends Rpg {

    public RpgTest(RpgApi impl) {
        super();
        Rpg.impl = impl;
    }
}