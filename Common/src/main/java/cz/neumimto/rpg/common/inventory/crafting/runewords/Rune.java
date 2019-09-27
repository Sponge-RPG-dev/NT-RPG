package cz.neumimto.rpg.common.inventory.crafting.runewords;

import cz.neumimto.rpg.api.items.sockets.SocketType;
import cz.neumimto.rpg.api.items.sockets.SocketTypes;

/**
 * Created by NeumimTo on 29.10.2015.
 */
public class Rune extends ItemUpgrade {

    @Override
    public SocketType getSocketType() {
        return SocketTypes.RUNE;
    }
}
