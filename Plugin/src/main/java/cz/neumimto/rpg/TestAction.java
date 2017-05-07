package cz.neumimto.rpg;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.effects.*;
import cz.neumimto.rpg.effects.common.stacking.IntegerEffectStackingStrategy;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.inventory.data.CustomItemData;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by NeumimTo on 29.10.2016.
 * When unit tests are not enough.
 * @see cz.neumimto.rpg.commands.CommandAdmin - test.action
 */
@Singleton
public class TestAction implements IEffectSourceProvider {

	@Inject
	private EffectService effectService;

	@Inject
	private InventoryService inventoryService;

	public void testEnchantAdd(IActiveCharacter character) {
		ItemStack itemStack = character.getPlayer().getItemInHand(HandTypes.MAIN_HAND).get();
		IGlobalEffect globalEffect = effectService.getGlobalEffect("Elemental Resistance");
		IGlobalEffect globalEffect1 = effectService.getGlobalEffect("All skills");

		CustomItemData itemData = inventoryService.getItemData(itemStack);
		itemData.enchantements().put(globalEffect.getName(), "+10%");
		itemData.enchantements().put(globalEffect1.getName(), "+5");
		itemStack.offer(itemData);
		inventoryService.updateLore(itemStack);
		character.getPlayer().setItemInHand(HandTypes.MAIN_HAND, itemStack);
	}

	public void testAddEffect(IActiveCharacter character) {
		effectService.addEffect(new Test(character), character, this);
		effectService.addEffect(new Test(character), character, this);
		effectService.addEffect(new Test(character), character, this);
		IEffectContainer<Integer, Test> k = character.getEffect("test");
		if (k.getStackedValue() != 30) {
			throw new AssertionError();
		}
	}

	@Override
	public IEffectSource getType() {
		return EffectSourceType.COMMAND;
	}

	private static class Test extends EffectBase<Integer> {

		public Test(IEffectConsumer consumer) {
			super("test", consumer);
			setStackable(true, new IntegerEffectStackingStrategy());
			setDuration(10000L);
		}

		@Override
		public void onApply() {
			getConsumer().sendMessage("added");
		}

		@Override
		public void onRemove() {
			getConsumer().sendMessage("removed");
		}

		@Override
		public Integer getValue() {
			return 10;
		}
	}
}
