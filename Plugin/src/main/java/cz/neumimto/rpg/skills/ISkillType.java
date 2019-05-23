package cz.neumimto.rpg.skills;

import cz.neumimto.rpg.api.utils.TriState;
import org.spongepowered.api.CatalogType;

/**
 * Created by NeumimTo on 23.12.2015.
 */
public interface ISkillType extends CatalogType {

	TriState isNegative();
}
