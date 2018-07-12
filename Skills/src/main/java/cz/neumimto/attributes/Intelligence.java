package cz.neumimto.attributes;

import cz.neumimto.rpg.players.properties.attributes.CharacterAttribute;
import org.spongepowered.api.item.ItemTypes;

/**
 * Created by NeumimTo on 10.6.2017.
 */
public class Intelligence extends CharacterAttribute {
	public Intelligence() {
		//setName(SkillLocalization.INT);
		//setDescription(SkillLocalization.INT_DESC);
		setItemRepresentation(ItemTypes.BOOK);
	}
}
