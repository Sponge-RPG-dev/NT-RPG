package cz.neumimto.rpg;

import cz.neumimto.core.ioc.IoC;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.ItemStackUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by NeumimTo on 29.10.2016.
 * When unit tests are not enough.
 * @see cz.neumimto.rpg.commands.CommandAdmin - test.action
 */
public class TestAction {


	public void testEnchant(IActiveCharacter character) {
		ItemStack itemStack = character.getPlayer().getItemInHand(HandTypes.MAIN_HAND).get();
		EffectService effectService = IoC.get().build(EffectService.class);
		IGlobalEffect globalEffect = effectService.getGlobalEffect("Fire damage over time");

		List<Text> texts = ItemStackUtils.addItemEffect(itemStack, globalEffect, 10);
		itemStack.offer(Keys.ITEM_LORE, texts);
		character.getPlayer().setItemInHand(HandTypes.MAIN_HAND, itemStack);
	}
}
