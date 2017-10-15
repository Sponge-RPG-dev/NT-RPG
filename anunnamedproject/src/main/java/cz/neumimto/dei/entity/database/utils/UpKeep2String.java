package cz.neumimto.dei.entity.database.utils;

import cz.neumimto.dei.UpKeep;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import javax.persistence.AttributeConverter;
import java.util.Optional;

/**
 * Created by NeumimTo on 6.7.2016.
 */
public class UpKeep2String implements AttributeConverter<UpKeep, String> {

	@Override
	public String convertToDatabaseColumn(UpKeep u) {
		StringBuilder b = new StringBuilder();
		for (ItemStack itemStack : u.getUpkeep()) {
			int quantity = itemStack.getQuantity();
			ItemType item = itemStack.getItem();
			String text = itemStack.get(Keys.DISPLAY_NAME).get().toPlain();
			b.append(item.getName() + "/" + quantity + "/" + text + ";");
		}
		return b.toString();
	}

	@Override
	public UpKeep convertToEntityAttribute(String s) {
		UpKeep upKeep = new UpKeep();
		String[] split = s.split(";");
		for (String s1 : split) {
			String[] split1 = s1.split("/");
			Optional<ItemType> type = Sponge.getGame().getRegistry().getType(ItemType.class, split[0]);
			if (type.isPresent()) {
				ItemStack.Builder builder = ItemStack.builder();
				if (split1.length == 2) {
					builder.itemType(type.get()).quantity(Integer.parseInt(split[1]));
				} else {
					builder.keyValue(Keys.DISPLAY_NAME, Text.of(split1[2]));
				}
				upKeep.getUpkeep().add(builder.build());
			}
		}
		return upKeep;
	}
}

