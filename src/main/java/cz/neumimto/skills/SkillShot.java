/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.skills;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.core.ioc.Inject;
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
        cache(getProjectileProperties(character,info,e));

        return null;
    }

    protected abstract ProjectileProperties getProjectileProperties(IActiveCharacter character, ExtendedSkillInfo info, Entity e);

    protected abstract Entity getProjectile(IActiveCharacter character, ExtendedSkillInfo info, Vector3d direction);
}
