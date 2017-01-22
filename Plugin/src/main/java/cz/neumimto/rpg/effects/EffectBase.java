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
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by NeumimTo.
 */
public class EffectBase implements IEffect {
    protected Set<EffectType> effectTypes = new HashSet<>();
    private boolean stackable = false;
    private String name;
    private int level;
    private Set<PotionEffect> potions = new HashSet<>();
    private IEffectConsumer consumer;
    private IEffectSource effectSource = EffectSource.TEMP;
    private long duration = -1;
    private long period = -1;
    private long lastTickTime;
    private long expireTime;
    private int tickCount;
    private long timeCreated;
    private String applyMessage;
    private String expireMessage;
    private UUID uuid;

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
    public boolean setStackable(boolean b) {
        return stackable;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
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
    public IEffectSource getEffectSource() {
        return effectSource;
    }

    @Override
    public void setEffectSource(IEffectSource effectSource) {
        this.effectSource = effectSource;
    }

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
    public void onStack(int level) {
        for (PotionEffect e : getPotions()) {
            getConsumer().addPotionEffect(e.getType(), e.getAmplifier(), e.getDuration());
        }
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
}
