package cz.neumimto.rpg.sponge.inventory.data;

import cz.neumimto.rpg.common.inventory.crafting.runewords.ItemUpgrade;
import cz.neumimto.rpg.sponge.inventory.sockets.SocketTypes;

public class ItemSocket {

    private SocketTypes type;
    private ItemUpgrade content;

    public ItemSocket() {
    }

    public ItemSocket(SocketTypes type, ItemUpgrade content) {
        this.type = type;
        this.content = content;
    }

    public SocketTypes getType() {
        return type;
    }

    public void setType(SocketTypes type) {
        this.type = type;
    }

    public ItemUpgrade getContent() {
        return content;
    }

    public void setContent(ItemUpgrade content) {
        this.content = content;
    }
}
