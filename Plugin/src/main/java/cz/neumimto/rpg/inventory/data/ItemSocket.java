package cz.neumimto.rpg.inventory.data;

import cz.neumimto.rpg.inventory.SocketType;
import cz.neumimto.rpg.inventory.runewords.ItemUpgrade;

public class ItemSocket {
    private SocketType type;
    private ItemUpgrade content;

    public ItemSocket() {
    }

    public ItemSocket(SocketType type, ItemUpgrade content) {
        this.type = type;
        this.content = content;
    }

    public SocketType getType() {
        return type;
    }

    public void setType(SocketType type) {
        this.type = type;
    }

    public ItemUpgrade getContent() {
        return content;
    }

    public void setContent(ItemUpgrade content) {
        this.content = content;
    }
}
