package cz.neumimto.rpg.players.properties.attributes;

import cz.neumimto.rpg.players.attributes.Attributes;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.util.annotation.CatalogedBy;

import java.util.Map;

/**
 * Created by NeumimTo on 14.1.16.
 */
@CatalogedBy(Attributes.class)
public interface ICharacterAttribute extends CatalogType {

	String getName();

	void setName(String name);

	String getId();

	void setId(String id);

	Map<Integer, Float> affectsProperties();

	String getDescription();

	void setDescription(String desc);

	ItemType getItemRepresentation();

	void setItemRepresentation(ItemType itemType);

	int getMaxValue();

	void setMaxValue(int value);

}
