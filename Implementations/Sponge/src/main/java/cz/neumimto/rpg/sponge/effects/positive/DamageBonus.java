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

package cz.neumimto.rpg.sponge.effects.positive;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.IEntityType;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;

import java.util.Map;

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

    /**
     * @see IGlobalEffect#construct(IEffectConsumer, long, Map)
     */

    public DamageBonus(IEffectConsumer consumer, long duration, String bonusDamage) {
        this(consumer, duration, Float.parseFloat(bonusDamage));
    }


    public float getBonusDamage() {
        return bonusDamage;
    }

    public void setBonusDamage(float bonusDamage) {
        this.bonusDamage = bonusDamage;
    }

    @Override
    public void onApply(IEffect self) {
        getConsumer().setProperty(CommonProperties.weapon_damage_bonus,
                getConsumer().getProperty(CommonProperties.weapon_damage_bonus) + bonusDamage);
        ISpongeEntity iSpongeEntity = (ISpongeEntity) getConsumer();
        if (iSpongeEntity.getType() == IEntityType.CHARACTER) {
            Rpg.get().getDamageService().recalculateCharacterWeaponDamage((IActiveCharacter) getConsumer());
        }
    }

    @Override
    public void onRemove(IEffect self) {
        getConsumer().setProperty(CommonProperties.weapon_damage_bonus,
                getConsumer().getProperty(CommonProperties.weapon_damage_bonus) - bonusDamage);
        ISpongeEntity iSpongeEntity = (ISpongeEntity) getConsumer();
        if (iSpongeEntity.getType() == IEntityType.CHARACTER) {
            Rpg.get().getDamageService().recalculateCharacterWeaponDamage((IActiveCharacter) getConsumer());
        }
    }
}
