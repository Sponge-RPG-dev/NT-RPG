package cz.neumimto.rpg.skills.utils;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.SkillData;
import org.spongepowered.api.CatalogType;

/**
 * Created by ja on 22.10.2016.
 */
public interface SkillModifierProcessor extends CatalogType  {

	void process(IActiveCharacter iActiveCharacter, SkillData skillData);
}
