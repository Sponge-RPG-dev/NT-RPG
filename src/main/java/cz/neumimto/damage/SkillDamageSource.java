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

package cz.neumimto.damage;

import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.ISkill;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.cause.entity.damage.source.common.AbstractDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.common.AbstractDamageSourceBuilder;

public class SkillDamageSource extends AbstractDamageSource {
    private ISkill skill;
    private IActiveCharacter caster;
    private DamageType damageType = DamageTypes.ATTACK;

    public SkillDamageSource(AbstractDamageSourceBuilder builder) {
        super(builder);
    }

    public SkillDamageSource() {
        this(new SkillDamageSourceBuilder());
    }

    public void setSkill(ISkill skill) {
        this.skill = skill;
    }

    public void setCaster(IActiveCharacter caster) {
        this.caster = caster;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    public ISkill getSkill() {
        return skill;
    }

    public IActiveCharacter getCaster() {
        return caster;
    }

    @Override
    public DamageType getType() {
        return damageType;
    }


    @Override
    public boolean isScaledByDifficulty() {
        return false;
    }

    @Override
    public boolean doesAffectCreative() {
        return false;
    }
}
