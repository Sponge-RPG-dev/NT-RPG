package cz.neumimto.skills.active;

import cz.neumimto.effects.positive.ShadowRunEffect;
import cz.neumimto.model.ShadowRunModel;
import cz.neumimto.rpg.SpongeResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import org.spongepowered.api.data.property.block.GroundLuminanceProperty;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
@SpongeResourceLoader.Skill("ntrpg:shadowrun")
public class ShadowRun extends ActiveSkill<ISpongeCharacter> {

	@Inject
	private EffectService effectService;

	@Override
	public void init() {
		super.init();
		addSkillType(SkillType.STEALTH);
		addSkillType(SkillType.MOVEMENT);
		addSkillType(SkillType.ESCAPE);
		settings.addNode(SkillNodes.DURATION, 20000, 1750);
		settings.addNode(SkillNodes.DAMAGE, 10, 5);
		settings.addNode(SkillNodes.MULTIPLIER, 15, 8);
		settings.addNode("max-light-level", 12, -2);
		settings.addNode("walk-speed", 0.07f, 0.007f);
	}

	@Override
	public void cast(ISpongeCharacter character, PlayerSkillContext info, SkillContext skillContext) {
		Location<World> location = character.getPlayer().getLocation();
		Optional<GroundLuminanceProperty> property = location.add(0, -1, 0).getBlock().getProperty(GroundLuminanceProperty.class);
		GroundLuminanceProperty groundLuminanceProperty = property.get();
		double llevel = skillContext.getDoubleNodeValue("max-light-level");
		if (groundLuminanceProperty.getValue() <= llevel) {
			long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
			double damage = skillContext.getDoubleNodeValue(SkillNodes.DAMAGE);
			double attackmult = skillContext.getDoubleNodeValue(SkillNodes.MULTIPLIER);
			float walkspeed = skillContext.getFloatNodeValue("walk-speed");
			ShadowRunModel model = new ShadowRunModel();
			model.damage = damage;
			model.attackmult = attackmult;
			model.walkspeed = walkspeed;
			IEffect effect = new ShadowRunEffect(character, 0, model);
			effect.setDuration(duration);
			effectService.addEffect(effect, this);
		}
		skillContext.next(character, info, skillContext.result(SkillResult.OK));
	}
}
