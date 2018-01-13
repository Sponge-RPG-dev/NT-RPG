package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.IGlobalEffect;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 26.2.2017.
 */
public class CustomItem {

	private int slot;
	private int level;
	protected Map<IGlobalEffect, EffectParams> effects = new HashMap<>();
	protected ItemStackSnapshot itemStack;
	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	//todo move to some kind of builder/service
	public CustomItem(ItemStack itemStack) {
		this.itemStack = itemStack.createSnapshot();
		this.effects = NtRpgPlugin.GlobalScope.inventorySerivce.getItemEffects(itemStack);
		this.level = NtRpgPlugin.GlobalScope.inventorySerivce.getItemLevel(itemStack);
	}

	public int getLevel() {
		return level;
	}

	public Map<IGlobalEffect, EffectParams> getEffects() {
		return effects;
	}
}
