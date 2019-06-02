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

package cz.neumimto.rpg.api.events.skill;

import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.entity.IEntity;

/**
 * Created by NeumimTo on 1.8.2015.
 */
public interface SkillPreUsageEvent extends SkillEvent {
    SkillContext getSkillContext();

    void setSkillContext(SkillContext  context);

    void setCaster(IEntity iEntity);

    IEntity getCaster();
}
