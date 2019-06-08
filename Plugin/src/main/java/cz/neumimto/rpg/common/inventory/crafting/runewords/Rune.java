package cz.neumimto.rpg.common.inventory.crafting.runewords;

import cz.neumimto.rpg.common.inventory.sockets.SocketType;
import cz.neumimto.rpg.common.inventory.sockets.SocketTypes;

/**
 * Created by NeumimTo on 29.10.2015.
 */
public class Rune extends ItemUpgrade {


    @Override
    public SocketType getSocketType() {
        return SocketTypes.RUNE;
    }
}
