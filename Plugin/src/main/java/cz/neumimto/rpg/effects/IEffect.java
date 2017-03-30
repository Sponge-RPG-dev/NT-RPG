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

package cz.neumimto.rpg.effects;

import cz.neumimto.rpg.GlobalScope;
import cz.neumimto.rpg.NtRpgPlugin;
import org.spongepowered.api.effect.potion.PotionEffect;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by NeumimTo on 17.1.2015.
 */
public interface IEffect<T extends IEffect, K> {
    static GlobalScope getGlobalScope() {
        return NtRpgPlugin.GlobalScope;
    }

    String getName();

    void onApply();

    default void onStack(T effect, IEffectSource effectSource) {
        for (PotionEffect e : getPotions()) {
            getConsumer().addPotionEffect(e.getType(), e.getAmplifier(), e.getDuration());
        }
    }

    Map<IEffectSource, EffectValue<K>> getEffectSources();



    void onRemove();

    int getStacks();

    void setStacks(int level);

    boolean isStackable();

    void setStackable(boolean b);

    boolean requiresRegister();

    IEffectSource getEffectSource();

    void setEffectSource(IEffectSource effectSource);

    long getPeriod();

    void setPeriod(long period);

    long getLastTickTime();

    void setLastTickTime(long currTime);

    void onTick();

    Set<PotionEffect> getPotions();

    long getExpireTime();

    long getTimeLeft(long currenttime);

    long getDuration();

    void setDuration(long l);

    void tickCountIncrement();

    UUID getUUID();

    String getExpireMessage();

    void setExpireMessage(String expireMessage);

    String getApplyMessage();

    void setApplyMessage(String applyMessage);

    IEffectConsumer getConsumer();

    void setConsumer(IEffectConsumer consumer);

    Set<EffectType> getEffectTypes();
}
