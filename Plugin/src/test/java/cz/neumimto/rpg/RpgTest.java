package cz.neumimto.rpg;

import cz.neumimto.rpg.api.Rpg;

public class RpgTest extends Rpg {

    public RpgTest() {
        impl = new TestApiImpl();
    }
}