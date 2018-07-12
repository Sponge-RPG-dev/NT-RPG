package cz.neumimto.attributes;

import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.players.properties.attributes.CharacterAttribute;
import org.spongepowered.api.item.ItemTypes;


/**
 * Created by ja on 10.6.2017.
 */
@ResourceLoader.Attribute
public class Strength extends CharacterAttribute {
	public Strength() {
		//setName(SkillLocalization.STR);
		//setDescription(SkillLocalization.STR_DESC);
		setItemRepresentation(ItemTypes.BLAZE_POWDER);
	}
}
