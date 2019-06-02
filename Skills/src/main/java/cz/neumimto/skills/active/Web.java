package cz.neumimto.skills.active;

import cz.neumimto.effects.negative.WebEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.sponge.skills.types.Targeted;
import org.spongepowered.api.item.ItemTypes;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 20.8.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:web")
public class Web extends Targeted {

	@Inject
	private EffectService effectService;

	@Override
	public void init() {
		super.init();
		settings.addNode(SkillNodes.DURATION, 5000, 100);
		setIcon(ItemTypes.WEB);
	}

	@Override
	public void castOn(IEntity target, IActiveCharacter source, PlayerSkillContext info, SkillContext skillContext) {
		long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
		WebEffect eff = new WebEffect(target, duration);
		effectService.addEffect(eff, this);
		skillContext.next(source, info, skillContext.result(SkillResult.OK));
	}


}
