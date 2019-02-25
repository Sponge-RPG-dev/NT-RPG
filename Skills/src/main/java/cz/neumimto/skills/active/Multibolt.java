package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.negative.MultiboltEffect;
import cz.neumimto.model.MultiboltModel;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.NDamageType;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.Targetted;
import cz.neumimto.rpg.skills.tree.SkillType;
import org.spongepowered.api.entity.living.Living;


/**
 * Created by NeumimTo on 6.7.2017.
 */
@ResourceLoader.Skill("ntrpg:multibolt")
public class Multibolt extends Targetted {

	@Inject
	private EntityService entityService;

	@Inject
	private EffectService effectService;

	public void init() {
		super.init();
		setDamageType(NDamageType.LIGHTNING);
		settings.addNode(SkillNodes.DAMAGE, 10, 20);
		settings.addNode("times-hit", 10, 20);
		addSkillType(SkillType.ELEMENTAL);
		addSkillType(SkillType.LIGHTNING);
	}

	@Override
	public void castOn(Living target, IActiveCharacter source, PlayerSkillContext info, SkillContext skillContext) {
		float damage = skillContext.getFloatNodeValue(SkillNodes.DAMAGE);
		int timesToHit = skillContext.getIntNodeValue("times-hit");
		MultiboltModel model = new MultiboltModel(timesToHit, damage);
		IEntity iEntity = entityService.get(target);
		IEffect effect = new MultiboltEffect(iEntity, source, model);
		effectService.addEffect(effect, iEntity, this);
		skillContext.next(source, info, SkillResult.OK);
	}
}
