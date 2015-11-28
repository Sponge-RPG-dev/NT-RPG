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
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

public class SkillDamageSource implements ISkillDamageSource {
    private ISkill skill;
    private IActiveCharacter caster;
    private DamageType damageType = DamageTypes.ATTACK;
    private boolean isBypassingArmor;
    private boolean isExplosion;
    private boolean isStarvationBased;
    private boolean isMagic;

    public void setSkill(ISkill skill) {
        this.skill = skill;
    }

    public void setCaster(IActiveCharacter caster) {
        this.caster = caster;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    public void setBypassingArmor(boolean bypassingArmor) {
        isBypassingArmor = bypassingArmor;
    }

    public void setExplosion(boolean explosion) {
        isExplosion = explosion;
    }

    public void setStarvationBased(boolean starvationBased) {
        isStarvationBased = starvationBased;
    }

    public void setMagic(boolean magic) {
        isMagic = magic;
    }

    @Override
    public ISkill getSkill() {
        return skill;
    }

    @Override
    public IActiveCharacter getCaster() {
        return caster;
    }

    @Override
    public DamageType getType() {
        return damageType;
    }

    @Override
    public boolean isAbsolute() {
        return false;
    }

    @Override
    public boolean isBypassingArmor() {
        return isBypassingArmor;
    }

    @Override
    public boolean isScaledByDifficulty() {
        return false;
    }

    @Override
    public boolean isExplosive() {
        return isExplosion;
    }


    @Override
    public boolean isMagic() {
        return isMagic;
    }

    @Override
    public boolean doesAffectCreative() {
        return false;
    }
}
