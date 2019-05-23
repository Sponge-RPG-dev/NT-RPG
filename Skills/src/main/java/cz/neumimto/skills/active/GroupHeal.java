package cz.neumimto.skills.active;

import cz.neumimto.Decorator;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import org.spongepowered.api.item.ItemTypes;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 6.8.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:groupheal")
public class GroupHeal extends ActiveSkill {

	@Inject
	private EntityService entityService;

	public void init() {
		super.init();
		settings.addNode(SkillNodes.RADIUS, 10, 10);
		settings.addNode(SkillNodes.HEALED_AMOUNT, 10, 10);
		addSkillType(SkillType.HEALING);
		addSkillType(SkillType.AOE);
		setIcon(ItemTypes.PAPER);
	}

	@Override
	public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
		float amnt = skillContext.getFloatNodeValue(SkillNodes.HEALED_AMOUNT);
		if (character.hasParty()) {
			double rad = Math.pow(skillContext.getDoubleNodeValue(SkillNodes.RADIUS), 2);
			for (IActiveCharacter a : character.getParty().getPlayers()) {
				if (a.getLocation().getPosition().distanceSquared(character.getLocation().getPosition()) <= rad) {
					entityService.healEntity(a, amnt, this);
					Decorator.healEffect(a.getLocation());
				}
			}
		} else {
			entityService.healEntity(character, amnt, this);
			Decorator.healEffect(character.getEntity().getLocation().add(0, 1, 0));
		}

		skillContext.next(character, info, SkillResult.OK);
	}
}
