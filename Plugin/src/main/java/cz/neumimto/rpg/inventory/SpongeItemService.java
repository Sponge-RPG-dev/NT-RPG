package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.items.WeaponClass;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.configuration.ItemString;
import cz.neumimto.rpg.common.items.AbstractItemService;
import cz.neumimto.rpg.items.SpongeRpgItemType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import javax.inject.Singleton;
import java.util.Optional;


/**
 * Created by NeumimTo on 29.4.2018.
 */
@Singleton
public class SpongeItemService extends AbstractItemService {

	public Optional<RpgItemType> getRpgItemType(ItemStack itemStack) {
		Optional<Text> text = itemStack.get(Keys.DISPLAY_NAME);
		return getRpgItemType(itemStack.getType().getId(), text.map(Text::toPlain).orElse(null));
	}

	@Override
	protected Optional<RpgItemType> createRpgItemType(ItemString parsed, WeaponClass wClass) {
		Optional<ItemType> type = Sponge.getRegistry().getType(ItemType.class, parsed.itemId);
		if (!type.isPresent()) {
			Log.error(" - Unknown itemtype " + parsed.itemId);
			return Optional.empty();
		}
		ItemType itemType = type.get();
		return Optional.of(new SpongeRpgItemType(parsed.itemId, parsed.model, wClass, parsed.damage, parsed.armor, itemType));
	}

}
