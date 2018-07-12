package cz.neumimto.attributes;

import cz.neumimto.rpg.players.properties.attributes.CharacterAttribute;
import org.spongepowered.api.item.ItemTypes;

/**
 * Created by ja on 10.6.2017.
 */
public class Agility extends CharacterAttribute {
	public Agility() {
		//setName(SkillLocalization.AGI);
		//setDescription(SkillLocalization.AGI_DESC);
		setItemRepresentation(ItemTypes.FEATHER);
	}
}
