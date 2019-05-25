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

package cz.neumimto.effects.global;

import cz.neumimto.effects.positive.DamageBonus;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.api.effects.model.EffectModelFactory;

import java.util.Map;

/**
 * Created by NeumimTo on 6.8.2015.
 */

/**
 * An example class how to manually create global effect
 */
public class DamageBonusGlobal implements IGlobalEffect<DamageBonus> {

	public DamageBonusGlobal() {
	}

	@Override
	public DamageBonus construct(IEffectConsumer consumer, long duration, Map<String, String> value) {
		return new DamageBonus(consumer, duration, EffectModelFactory.create(DamageBonus.class, value, Float.class));
	}

	@Override
	public String getName() {
		return DamageBonus.name;
	}

	@Override
	public Class<DamageBonus> asEffectClass() {
		return DamageBonus.class;
	}
}
