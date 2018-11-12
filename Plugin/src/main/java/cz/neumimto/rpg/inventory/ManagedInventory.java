package cz.neumimto.rpg.inventory;

import java.util.HashMap;

public class ManagedInventory {

	private final Class<?> type;
	private final HashMap<Integer, SlotEffectSource> slotEffectSourceHashMap;

	public ManagedInventory(Class<?> type, HashMap<Integer, SlotEffectSource> slotEffectSourceHashMap) {

		this.type = type;
		this.slotEffectSourceHashMap = slotEffectSourceHashMap;
	}

	public Class<?> getType() {
		return type;
	}

	public HashMap<Integer, SlotEffectSource> getSlotEffectSourceHashMap() {
		return slotEffectSourceHashMap;
	}
}
