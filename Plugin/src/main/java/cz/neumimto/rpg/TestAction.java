package cz.neumimto.rpg;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.ItemStackUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.List;

/**
 * Created by NeumimTo on 29.10.2016.
 * When unit tests are not enough.
 * @see cz.neumimto.rpg.commands.CommandAdmin - test.action
 */
@Singleton
public class TestAction {

	@Inject
	private EffectService effectService;

	@Inject
	private InventoryService inventoryService;

	public void testEnchantAdd(IActiveCharacter character) {
		ItemStack itemStack = character.getPlayer().getItemInHand(HandTypes.MAIN_HAND).get();
		IGlobalEffect globalEffect = effectService.getGlobalEffect("Elemental Resistance");
		IGlobalEffect globalEffect1 = effectService.getGlobalEffect("All skills");

		ItemStack i = ItemStackUtils.addEchantments(itemStack, new HashMap<IGlobalEffect, String>() {{
			put(globalEffect, "10%");
			put(globalEffect1, "+1");
		}}

		);
		List<Text> texts = i.get(Keys.ITEM_LORE).get();
		texts.add(Text.of("asd"));
		i.offer(Keys.ITEM_LORE, texts);
		character.getPlayer().setItemInHand(HandTypes.MAIN_HAND, i);
	}
}
