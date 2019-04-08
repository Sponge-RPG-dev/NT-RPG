package cz.neumimto.rpg.inventory;

import java.util.HashMap;

public class ManagedInventory {

	private final Class<?> type;
	private final HashMap<Integer, SlotEffectSource> slots;

	public ManagedInventory(Class<?> type, HashMap<Integer, SlotEffectSource> slots) {

		this.type = type;
		this.slots = slots;
	}

	public Class<?> getType() {
		return type;
	}

	public HashMap<Integer, SlotEffectSource> getSlots() {
		return slots;
	}
}
