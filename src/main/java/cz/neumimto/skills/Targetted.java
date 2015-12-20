/*  Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
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
 */

package cz.neumimto.skills;

import cz.neumimto.events.skills.SkillFindTargetEvent;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.utils.Utils;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.Living;

public abstract class Targetted extends ActiveSkill implements ITargetted {

    @Inject
    private Game game;

    @Override
    public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info) {
        int range = (int)info.getSkillInfo().getSkillSettings().getLevelNodeValue(SkillNode.RANGE,info.getLevel());
        Living l = Utils.getTargettedEntity(character,range);
        if (l != null) {
            SkillFindTargetEvent event = new SkillFindTargetEvent(character,l,this);
            game.getEventManager().post(event);
            if (event.isCancelled()) {
                return SkillResult.CANCELLED;
            }
            castOn(event.getTarget(),event.getCharacter(),info);
        }
        return SkillResult.NO_TARGET;
    }

}
