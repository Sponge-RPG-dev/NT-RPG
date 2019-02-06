package cz.neumimto.skills.active;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import org.spongepowered.api.data.key.Keys;

/**
 * Created by NeumimTo on 23.12.2015.
 */
@ResourceLoader.Skill("ntrpg:jump")
public class SkillJump extends ActiveSkill {

	public void init() {
		super.init();
		setDamageType(null);
		SkillSettings skillSettings = new SkillSettings();
		skillSettings.addNode(SkillNodes.VELOCITY, 2, 2);
		settings = skillSettings;
		addSkillType(SkillType.STEALTH);
		addSkillType(SkillType.MOVEMENT);
		addSkillType(SkillType.PHYSICAL);
	}


	@Override
	public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
		Vector3d rotation = character.getEntity().getRotation();
		Vector3d direction = Quaterniond.fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ()).getDirection();
		Vector3d mul = new Vector3d(0, 1, 0).mul(skillContext.getFloatNodeValue(SkillNodes.VELOCITY));
		direction = mul.add(direction.getX(), 0, direction.getZ());
		character.getEntity().offer(Keys.VELOCITY, direction);
		skillContext.next(character, info, skillContext.result(SkillResult.OK));
	}
}