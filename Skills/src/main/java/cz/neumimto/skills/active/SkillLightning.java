package cz.neumimto.skills.active;

import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;

/**
 * Created by NeumimTo on 29.12.2015.
 */
@ResourceLoader.Skill
public class SkillLightning extends Targetted {

	@Inject
	EntityService entityService;

	public SkillLightning() {
		setName("Lightning");
		setLore(SkillLocalization.SKILL_LIGHTNING_LORE);
		setDescription(SkillLocalization.SKILL_LIGHTNING_DESC);
		setDamageType(NDamageType.LIGHTNING);
		SkillSettings skillSettings = new SkillSettings();
		skillSettings.addNode(SkillNodes.DAMAGE, 10, 20);
		skillSettings.addNode(SkillNodes.RANGE, 10, 10);
		super.settings = skillSettings;
		setDamageType(NDamageType.LIGHTNING);
	}

	@Override
	public SkillResult castOn(Living target, IActiveCharacter source, ExtendedSkillInfo info) {
		float damage = settings.getLevelNodeValue(SkillNodes.DAMAGE, info.getTotalLevel());
		SkillDamageSourceBuilder build = new SkillDamageSourceBuilder();
		build.fromSkill(this);
		build.setCaster(source);
		target.damage(damage, build.build());
		Entity q = target.getLocation().getExtent().createEntity(EntityTypes.LIGHTNING, target.getLocation().getPosition());
		target.getLocation().getExtent().spawnEntity(q, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());
		return SkillResult.OK;
	}
}
