package cz.neumimto.rpg.common.inventory.crafting.runewords;

import cz.neumimto.rpg.common.items.sockets.SocketType;
import cz.neumimto.rpg.common.items.sockets.SocketTypes;

public class Gem extends ItemUpgrade {

    @Override
    public SocketType getSocketType() {
        return SocketTypes.GEM;
    }
}
