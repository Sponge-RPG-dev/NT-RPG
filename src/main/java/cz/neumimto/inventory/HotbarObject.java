package cz.neumimto.inventory;

import cz.neumimto.players.IActiveCharacter;

/**
 * Created by NeumimTo on 31.12.2015.
 */
public abstract class HotbarObject {

    public static HotbarObject EMPTYHAND_OR_CONSUMABLE = null;

    private int slot;
    protected IHotbarObjectType type;

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public IHotbarObjectType getType() {
        return type;
    }

    public abstract void onRightClick(IActiveCharacter character);

    public abstract void onLeftClick(IActiveCharacter character);
}
