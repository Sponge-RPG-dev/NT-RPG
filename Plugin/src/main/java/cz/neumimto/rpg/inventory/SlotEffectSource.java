package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.effects.IEffectSource;

public class SlotEffectSource implements IEffectSource {

    private int slotId;

    public SlotEffectSource(int slotId) {
        this.slotId = slotId;
    }

    public int getSlotId() {
        return slotId;
    }

    @Override
    public boolean multiple() {
        return false;
    }
}
