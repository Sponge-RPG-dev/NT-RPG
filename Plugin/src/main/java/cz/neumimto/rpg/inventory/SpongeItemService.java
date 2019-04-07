package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.common.items.ItemServiceImpl;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import javax.inject.Singleton;
import java.util.Optional;


/**
 * Created by NeumimTo on 29.4.2018.
 */
@Singleton
public class SpongeItemService extends ItemServiceImpl {

	public Optional<RpgItemType> getRpgItemType(ItemStack itemStack) {
		Optional<Text> text = itemStack.get(Keys.DISPLAY_NAME);
		return getRpgItemType(itemStack.getType().getId(), text.map(Text::toPlain).orElse(null));
	}

}
