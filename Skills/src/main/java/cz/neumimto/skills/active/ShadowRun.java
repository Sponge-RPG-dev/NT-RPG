package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.ShadowRunEffect;
import cz.neumimto.model.ShadowRunModel;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import cz.neumimto.rpg.skills.mods.SkillContext;
import org.spongepowered.api.data.property.block.GroundLuminanceProperty;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

@ResourceLoader.Skill("ntrpg:shadowrun")
public class ShadowRun extends ActiveSkill {

	@Inject
	private EffectService effectService;

	public void init() {
		super.init();
		addSkillType(SkillType.STEALTH);
		addSkillType(SkillType.MOVEMENT);
		addSkillType(SkillType.ESCAPE);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DURATION, 20000, 1750);
		settings.addNode(SkillNodes.DAMAGE, 10, 5);
		settings.addNode(SkillNodes.MULTIPLIER, 15, 8);
		settings.addNode("max-light-level", 12, -2);
		settings.addNode("walk-speed", 0.07f, 0.007f);
		setSettings(settings);
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillContext modifier) {
		Location<World> location = character.getPlayer().getLocation();
		Optional<GroundLuminanceProperty> property = location.add(0, -1, 0).getBlock().getProperty(GroundLuminanceProperty.class);
		GroundLuminanceProperty groundLuminanceProperty = property.get();
		double llevel = getDoubleNodeValue(info, "max-light-level");
		if (groundLuminanceProperty.getValue() <= llevel) {
			long duration = getLongNodeValue(info, SkillNodes.DURATION);
			double damage = getDoubleNodeValue(info, SkillNodes.DAMAGE);
			double attackmult = getDoubleNodeValue(info, SkillNodes.MULTIPLIER);
			float walkspeed = getFloatNodeValue(info, "walk-speed");
			ShadowRunModel model = new ShadowRunModel();
			model.duration = duration;
			model.damage = damage;
			model.attackmult = attackmult;
			model.walkspeed = walkspeed;
			IEffect effect = new ShadowRunEffect(character, 0, model);
			effectService.addEffect(effect, character, this);
		}
		return modifier.next(character, info, SkillResult.OK);
	}
}
