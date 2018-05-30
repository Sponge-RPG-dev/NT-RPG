package cz.neumimto.skills.active;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.SkillLocalization;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import org.spongepowered.api.data.key.Keys;

/**
 * Created by NeumimTo on 23.12.2015.
 */
@ResourceLoader.Skill
public class SkillJump extends ActiveSkill {

	public SkillJump() {
		setName(SkillLocalization.SKILL_JUMP_NAME);
		setDamageType(null);
		setDescription(SkillLocalization.SKILL_JUMP_DESC);
		SkillSettings skillSettings = new SkillSettings();
		skillSettings.addNode(SkillNodes.VELOCITY, 2, 2);
		settings = skillSettings;
		addSkillType(SkillType.STEALTH);
		addSkillType(SkillType.MOVEMENT);
		addSkillType(SkillType.PHYSICAL);
	}


	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier skillModifier) {
		Vector3d rotation = character.getEntity().getRotation();
		Vector3d direction = Quaterniond.fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ()).getDirection();
		Vector3d mul = new Vector3d(0, 1, 0).mul(info.getSkillData().getSkillSettings().getLevelNodeValue(SkillNodes.VELOCITY, info.getTotalLevel()));
		direction = mul.add(direction.getX(), 0, direction.getZ());
		character.getEntity().offer(Keys.VELOCITY, direction);
		return SkillResult.OK;
	}
}