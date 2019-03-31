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

package cz.neumimto.effects.positive;

import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.properties.DefaultProperties;
import org.spongepowered.api.entity.EntityTypes;

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
	 * @see cz.neumimto.rpg.effects.IGlobalEffect#construct(IEffectConsumer, long, Map)
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
		getConsumer().setProperty(DefaultProperties.weapon_damage_bonus,
				getConsumer().getProperty(DefaultProperties.weapon_damage_bonus) + bonusDamage);
		if (getConsumer().getEntity().getType() == EntityTypes.PLAYER) {
			getGlobalScope().damageService.recalculateCharacterWeaponDamage((IActiveCharacter) getConsumer());
		}
	}

	@Override
	public void onRemove(IEffect self) {
		getConsumer().setProperty(DefaultProperties.weapon_damage_bonus,
				getConsumer().getProperty(DefaultProperties.weapon_damage_bonus) - bonusDamage);
		if (getConsumer().getEntity().getType() == EntityTypes.PLAYER) {
			getGlobalScope().damageService.recalculateCharacterWeaponDamage((IActiveCharacter) getConsumer());
		}
	}
}
