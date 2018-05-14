package cz.neumimto.rpg.inventory.items.types;

import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.IEffectSource;
import cz.neumimto.rpg.effects.IEffectSourceProvider;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.inventory.RPGItemType;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.items.ItemMetaType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 26.2.2017.
 */
public class CustomItem implements IEffectSourceProvider {

	private int slot;
	private int level;
	protected Map<IGlobalEffect, EffectParams> effects = new HashMap<>();
	protected ItemStackSnapshot itemStack;
	private final IEffectSource effectSource;
	private RPGItemType rpgItemType;

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public CustomItem(ItemStack itemStack, IEffectSource effectSource, RPGItemType rpgItemType) {
		this.itemStack = itemStack.createSnapshot();
		this.effectSource = effectSource;
		this.rpgItemType = rpgItemType;
	}

	public int getLevel() {
		return level;
	}

	public Map<IGlobalEffect, EffectParams> getEffects() {
		return effects;
	}


	public void setLevel(int level) {
		this.level = level;
	}

	public void setEffects(Map<IGlobalEffect, EffectParams> effects) {
		this.effects = effects;
	}

	@Override
	public IEffectSource getType() {
		return effectSource;
	}

	public ItemMetaType getItemMetaType() {
		return itemStack.get(NKeys.ITEM_META_TYPE).orElse(null);
	}


}
