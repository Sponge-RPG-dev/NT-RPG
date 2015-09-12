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

package cz.neumimto.effects;

import org.spongepowered.api.potion.PotionEffect;
import org.spongepowered.api.potion.PotionEffectType;

import java.util.Collection;

/**
 * Created by NeumimTo on 17.1.2015.
 */
public interface IEffectConsumer {
    Collection<IEffect> getEffects();

    IEffect getEffect(Class<? extends IEffect> cl);

    boolean hasEffect(Class<? extends IEffect> cl);

    void addEffect(IEffect effect);

    void removeEffect(Class<? extends IEffect> cl);

    void addPotionEffect(PotionEffectType p, int amplifier, long duration);

    void addPotionEffect(PotionEffectType p, int amplifier, long duration, boolean partciles);

    void removePotionEffect(PotionEffectType type);

    boolean hasPotionEffect(PotionEffectType type);

    void removeAllTempEffects();

    void addPotionEffect(PotionEffect e);

    void sendMessage(String message);
}
