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
import cz.neumimto.rpg.utils.UUIDs;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.effect.potion.PotionEffect;

import java.util.*;

/**
 * Created by NeumimTo.
 */
public class EffectBase<Value> implements IEffect<Value> {
    protected Set<EffectType> effectTypes = new HashSet<>();
    private boolean stackable = false;
    private String name;
    private int level;
    private Set<PotionEffect> potions = new HashSet<>();
    private IEffectConsumer consumer;
    private long duration = -1;
    private long period = -1;
    private long lastTickTime;
    private long expireTime;
    private int tickCount;
    private long timeCreated;
    private String applyMessage;
    private String expireMessage;
    private UUID uuid;
    private IEffectSourceProvider effectSourceProvider;
    private Value value;
    private EffectStackingStrategy<Value> effectStackingStrategy;

    public EffectBase(String name, IEffectConsumer consumer) {
        this();
        this.name = name;
        this.consumer = consumer;
    }

    public EffectBase() {
        timeCreated = System.currentTimeMillis();
        uuid = UUIDs.random();
    }

    public static GlobalScope getGlobalScope() {
        return NtRpgPlugin.GlobalScope;
    }

    @Override
    public boolean requiresRegister() {
        return duration >= 0 || period >= 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void onApply() {
        for (PotionEffect e : getPotions()) {
            getConsumer().addPotionEffect(e);
        }
    }

    @Override
    public void onRemove() {
        for (PotionEffect e : getPotions()) {
            getConsumer().removePotionEffect(e.getType());
        }
    }

    @Override
    public boolean isStackable() {
        return stackable;
    }

    @Override
    public void setStackable(boolean b, EffectStackingStrategy<Value> stackingStrategy) {
        this.stackable = b;
	    setEffectStackingStrategy(stackingStrategy);
    }

    @Override
    public int getStacks() {
        return level;
    }


    @Override
    public void setStacks(int level) {
        this.level = level;
    }

    public IEffectConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(IEffectConsumer consumer) {
        if (consumer != null)
            this.consumer = consumer;
    }

    @Override
    public Set<PotionEffect> getPotions() {
        return potions;
    }

    @Override
    public long getExpireTime() {
        return timeCreated + duration;
    }

    protected void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public long getTimeLeft(long currenttime) {
        return timeCreated + duration - currenttime;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void setDuration(long l) {
        this.duration = l;
    }

    @Override
    public void tickCountIncrement() {
        tickCount++;
    }

    protected int getTickCount() {
        return tickCount;
    }

    @Override
    public long getPeriod() {
        return period;
    }

    @Override
    public void setPeriod(long period) {
        this.period = period;
    }

    @Override
    public long getLastTickTime() {
        return lastTickTime;
    }

    @Override
    public void setLastTickTime(long lastTickTime) {
        this.lastTickTime = lastTickTime;
    }

    @Override
    public void onTick() {

    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String getExpireMessage() {
        return expireMessage;
    }

    @Override
    public void setExpireMessage(String expireMessage) {
        this.expireMessage = expireMessage;
    }

    @Override
    public String getApplyMessage() {
        return applyMessage;
    }

    @Override
    public void setApplyMessage(String applyMessage) {
        this.applyMessage = applyMessage;
    }

    @Override
    public Set<EffectType> getEffectTypes() {
        return effectTypes;
    }

    @Override
    public IEffectSourceProvider getEffectSourceProvider() {
        return effectSourceProvider;
    }

    @Override
    public void setEffectSourceProvider(IEffectSourceProvider effectSourceProvider) {
        this.effectSourceProvider = effectSourceProvider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof EffectBase)) return false;

        EffectBase that = (EffectBase) o;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public void setEffectTypes(Set<EffectType> effectTypes) {
        this.effectTypes = effectTypes;
    }

    @Override
    public void setValue(Value o) {
        this.value = o;
    }

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public EffectStackingStrategy<Value> getEffectStackingStrategy() {
        return effectStackingStrategy;
    }

    @Override
    public void setEffectStackingStrategy(EffectStackingStrategy<Value> effectStackingStrategy) {
        this.effectStackingStrategy = effectStackingStrategy;
    }
}
