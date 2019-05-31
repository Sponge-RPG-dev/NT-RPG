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

package cz.neumimto.rpg.sponge.events.skill;

import cz.neumimto.rpg.api.IRpgElement;
import cz.neumimto.rpg.api.events.skill.SkillHealEvent;
import cz.neumimto.rpg.entities.IEntity;

/**
 * Created by NeumimTo on 7.8.2015.
 */
public class SpongeHealEvent extends AbstractSkillEvent implements SkillHealEvent {

    float amount;
    IRpgElement source;
    private IEntity entity;

    @Override
    public float getAmount() {
        return amount;
    }

    @Override
    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public IRpgElement getSource() {
        return source;
    }

    @Override
    public void setSource(IRpgElement source) {
        this.source = source;
    }

    @Override
    public IEntity getEntity() {
        return entity;
    }

    @Override
    public void setEntity(IEntity entity) {
        this.entity = entity;
    }
}
