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

package cz.neumimto.effects.common.global;

import cz.neumimto.effects.IEffectConsumer;
import cz.neumimto.effects.IGlobalEffect;
import cz.neumimto.effects.common.positive.SpeedBoost;
import cz.neumimto.players.IActiveCharacter;

/**
 * Created by NeumimTo on 6.8.2015.
 */
public class SpeedBoostGlobal implements IGlobalEffect<SpeedBoost> {
    @Override
    public SpeedBoost construct(IEffectConsumer consumer, long duration, int level) {
        return new SpeedBoost((IActiveCharacter) consumer, duration, level / 10);
    }

    @Override
    public String getName() {
        return SpeedBoost.name;
    }

    @Override
    public Class<SpeedBoost> asEffectClass() {
        return SpeedBoost.class;
    }
}
