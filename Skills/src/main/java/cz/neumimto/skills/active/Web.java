package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.negative.WebEffect;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.Targetted;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.item.ItemTypes;

/**
 * Created by NeumimTo on 20.8.2017.
 */
@ResourceLoader.Skill("ntrpg:web")
public class Web extends Targetted {

	@Inject
	EntityService entityService;

	@Inject
	EffectService effectService;

	public void init() {
		super.init();
		settings.addNode(SkillNodes.DURATION, 5000, 100);
		setIcon(ItemTypes.WEB);
	}

	@Override
	public void castOn(Living target, IActiveCharacter source, PlayerSkillContext info, SkillContext skillContext) {
		long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
		IEntity iEntity = entityService.get(target);
		WebEffect eff = new WebEffect(iEntity, duration);
		effectService.addEffect(eff, iEntity, this);
		skillContext.next(source, info, skillContext.result(SkillResult.OK));
	}


}
