package cz.neumimto.skills.active;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import org.spongepowered.api.data.key.Keys;

import javax.inject.Singleton;

/**
 * Created by NeumimTo on 23.12.2015.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:jump")
public class SkillJump extends ActiveSkill<ISpongeCharacter> {

    @Override
    public void init() {
        super.init();
        setDamageType(null);
        settings.addNode(SkillNodes.VELOCITY, 2, 2);
        addSkillType(SkillType.STEALTH);
        addSkillType(SkillType.MOVEMENT);
        addSkillType(SkillType.PHYSICAL);
    }


    @Override
    public SkillResult cast(ISpongeCharacter character, PlayerSkillContext skillContext) {
        Vector3d rotation = character.getEntity().getRotation();
        Vector3d direction = Quaterniond.fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ()).getDirection();
        Vector3d mul = new Vector3d(0, 1, 0).mul(skillContext.getFloatNodeValue(SkillNodes.VELOCITY));
        direction = mul.add(direction.getX(), 0, direction.getZ());
        character.getEntity().offer(Keys.VELOCITY, direction);
        return SkillResult.OK;
    }
}