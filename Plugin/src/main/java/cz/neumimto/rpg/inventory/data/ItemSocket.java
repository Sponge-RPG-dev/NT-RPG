package cz.neumimto.rpg.inventory.data;

import cz.neumimto.rpg.inventory.runewords.ItemUpgrade;
import cz.neumimto.rpg.inventory.sockets.SocketTypes;

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
