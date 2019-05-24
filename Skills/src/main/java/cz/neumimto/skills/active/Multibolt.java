package cz.neumimto.skills.active;

import cz.neumimto.effects.negative.MultiboltEffect;
import cz.neumimto.model.MultiboltModel;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.NDamageType;
import cz.neumimto.rpg.sponge.skills.types.Targeted;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * Created by NeumimTo on 6.7.2017.
 */
@Singleton
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
