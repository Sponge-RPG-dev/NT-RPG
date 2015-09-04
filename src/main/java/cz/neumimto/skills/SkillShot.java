package cz.neumimto.skills;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.ioc.Inject;
import cz.neumimto.players.IActiveCharacter;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.Entity;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by NeumimTo on 12.3.2015.
 */
public abstract class SkillShot extends ActiveSkill {

    @Inject
    private Game game;

    @Override
    public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info) {
        Vector3d eyeloc = character.getPlayer().getRotation();
        double pitch = eyeloc.getX();
        double yaw = eyeloc.getY();

        double x = sin(pitch) * cos(yaw);
        double y = sin(pitch) * sin(yaw);
        double z = cos(pitch);
        Vector3d direction = new Vector3d(x, y, z);

        Entity e = getProjectile(character, info, direction);

        return null;
    }

    protected abstract Entity getProjectile(IActiveCharacter character, ExtendedSkillInfo info, Vector3d direction);
}
