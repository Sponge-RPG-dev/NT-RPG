package cz.neumimto.rpg.inventory.runewords;

import cz.neumimto.rpg.inventory.sockets.SocketType;
import cz.neumimto.rpg.inventory.sockets.SocketTypes;

public class Gem extends ItemUpgrade {

    @Override
    public SocketType getSocketType() {
        return SocketTypes.GEM;
    }
}
