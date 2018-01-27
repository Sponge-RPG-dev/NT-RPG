package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.TextHelper;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.effects.EffectSourceType;
import cz.neumimto.rpg.effects.IEffectSource;
import cz.neumimto.rpg.inventory.runewords.ItemUpgrade;
import cz.neumimto.rpg.inventory.runewords.Rune;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;

/**
 * Created by NeumimTo on 2.1.2016.
 */
public class HotbarRune extends HotbarObject {

	protected ItemUpgrade itemUpgrade;

	public HotbarRune(ItemStack itemStack) {
		super(itemStack);
		type = HotbarObjectTypes.RUNE;
	}

	@Override
	public void onRightClick(IActiveCharacter character) {
		character.sendMessage(ChatTypes.SYSTEM, TextHelper.parse(Localization.SOCKET_HELP));
	}

	@Override
	public void onLeftClick(IActiveCharacter character) {
		onRightClick(character);
	}


	public ItemUpgrade getRune() {
		return itemUpgrade;
	}

	public void setRune(Rune r) {
		this.itemUpgrade = itemUpgrade;
	}

	@Override
	public IEffectSource getType() {
		return EffectSourceType.CHARM;
	}
}
