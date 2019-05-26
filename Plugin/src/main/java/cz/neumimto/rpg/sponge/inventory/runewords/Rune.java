package cz.neumimto.rpg.sponge.inventory.runewords;

import cz.neumimto.rpg.sponge.inventory.sockets.SocketType;
import cz.neumimto.rpg.sponge.inventory.sockets.SocketTypes;

/**
 * Created by NeumimTo on 29.10.2015.
 */
public class Rune extends ItemUpgrade {


    @Override
    public SocketType getSocketType() {
        return SocketTypes.RUNE;
    }
}
