package cz.neumimto.rpg.skills;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.Tristate;

/**
 * Created by NeumimTo on 23.12.2015.
 */
public interface ISkillType extends CatalogType {
	Tristate isNegative();
}
