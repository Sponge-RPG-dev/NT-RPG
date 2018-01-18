package cz.neumimto.rpg.inventory.runewords;

import cz.neumimto.rpg.inventory.SocketType;

public class Gem extends ItemUpgrade {

    @Override
    public SocketType getSocketType() {
        return SocketType.GEM;
    }
}
