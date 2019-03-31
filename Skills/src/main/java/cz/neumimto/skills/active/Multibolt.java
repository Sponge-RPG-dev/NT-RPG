package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.negative.MultiboltEffect;
import cz.neumimto.model.MultiboltModel;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.Targeted;
import cz.neumimto.rpg.skills.tree.SkillType;


/**
 * Created by NeumimTo on 6.7.2017.
 */
@ResourceLoader.Skill("ntrpg:multibolt")
public class Multibolt extends Targeted {

	@Inject
	private EffectService effectService;

	@Override
	public void init() {
		super.init();
		setDamageType(NDamageType.LIGHTNING);
		settings.addNode(SkillNodes.DAMAGE, 10, 20);
		settings.addNode("times-hit", 10, 20);
		addSkillType(SkillType.ELEMENTAL);
		addSkillType(SkillType.LIGHTNING);
	}

	@Override
	public void castOn(IEntity target, IActiveCharacter source, PlayerSkillContext info, SkillContext skillContext) {
		float damage = skillContext.getFloatNodeValue(SkillNodes.DAMAGE);
		int timesToHit = skillContext.getIntNodeValue("times-hit");
		MultiboltModel model = new MultiboltModel(timesToHit, damage);
		IEffect effect = new MultiboltEffect(target, source, model);
		effectService.addEffect(effect, this);
		skillContext.next(source, info, SkillResult.OK);
	}
}
