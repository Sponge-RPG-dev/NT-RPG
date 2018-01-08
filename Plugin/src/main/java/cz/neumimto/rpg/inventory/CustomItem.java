package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.inventory.data.CustomItemData;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 26.2.2017.
 */
public class CustomItem {

	private int slot;
	protected CustomItemData customItemData;
	protected Map<IGlobalEffect, EffectParams> effects = new HashMap<>();

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public CustomItemData getCustomItemData() {
		return customItemData;
	}

	//todo move to some kind of builder/service
	public CustomItem(ItemStack itemStack) {
		customItemData = NtRpgPlugin.GlobalScope.inventorySerivce.getItemData(itemStack);
		effects = NtRpgPlugin.GlobalScope.inventorySerivce.getItemEffects(customItemData);
	}

	public Map<IGlobalEffect, EffectParams> getEffects() {
		return effects;
	}
}
