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

package cz.neumimto.rpg.effects.common.positive;

import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.DefaultProperties;

/**
 * Created by NeumimTo on 6.8.2015.
 */
public class DamageBonus extends EffectBase {

    public static final String name = "Bonus Damage";
    float bonusDamage;

    public DamageBonus(IEffectConsumer consumer, long duration, float bonusDamage) {
        super(name, consumer);
        setDuration(duration);
        setBonusDamage(bonusDamage);
    }

    public float getBonusDamage() {
        return bonusDamage;
    }

    public void setBonusDamage(float bonusDamage) {
        this.bonusDamage = bonusDamage;
    }

    @Override
    public void onApply() {
        super.onApply();
        IActiveCharacter character = (IActiveCharacter) getConsumer();
        character.setCharacterProperty(DefaultProperties.weapon_damage_bonus, getGlobalScope().characterService.getCharacterProperty(character, DefaultProperties.weapon_damage_bonus) + getBonusDamage());
        getGlobalScope().damageService.recalculateCharacterWeaponDamage(character);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        IActiveCharacter character = (IActiveCharacter) getConsumer();
        character.setCharacterProperty(DefaultProperties.weapon_damage_bonus, getGlobalScope().characterService.getCharacterProperty(character, DefaultProperties.weapon_damage_bonus) - getBonusDamage());
        getGlobalScope().damageService.recalculateCharacterWeaponDamage(character);
    }
}
