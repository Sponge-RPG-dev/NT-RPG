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

package cz.neumimto.rpg.api.effects;

import cz.neumimto.rpg.api.entity.IEffectConsumer;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo.
 */
public abstract class EffectBase<VALUE> implements IEffect<VALUE> {

    protected Set<EffectType> effectTypes = new HashSet<>();
    private boolean stackable = false;

    private String name;

    private IEffectConsumer consumer;

    private long duration = -1;
    private long period = -1;
    private long lastTickTime;

    private long timeCreated;

    private IEffectSourceProvider effectSourceProvider;

    private VALUE value;

    private EffectStackingStrategy<VALUE> effectStackingStrategy;

    private IEffectContainer<VALUE, IEffect<VALUE>> container;

    private boolean tickingDisabled = false;

    public EffectBase() {
        timeCreated = System.currentTimeMillis();
    }

    public EffectBase(String name, IEffectConsumer consumer) {
        this();
        this.name = name;
        this.consumer = consumer;
    }

    public void init(IEffectConsumer consumer, String name, long duration, long period) {
        this.name = name;
        this.consumer = consumer;
        this.setPeriod(period);
        this.setDuration(duration);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IEffectConsumer getConsumer() {
        return consumer;
    }

    @Override
    public boolean requiresRegister() {
        return getDuration() >= 0 || getPeriod() >= 0;
    }

    @Override
    public boolean isStackable() {
        return stackable;
    }

    @Override
    public void setStackable(boolean b, EffectStackingStrategy<VALUE> stackingStrategy) {
        this.stackable = b;
        setEffectStackingStrategy(stackingStrategy);
    }

    @Override
    public long getExpireTime() {
        return timeCreated + duration;
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
    public Set<EffectType> getEffectTypes() {
        return effectTypes;
    }

    protected void addEffectType(EffectType e) {
        effectTypes.add(e);
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
    public VALUE getValue() {
        return value;
    }

    @Override
    public void setValue(VALUE o) {
        this.value = o;
    }

    @Override
    public EffectStackingStrategy<VALUE> getEffectStackingStrategy() {
        return effectStackingStrategy;
    }

    @Override
    public void setEffectStackingStrategy(EffectStackingStrategy<VALUE> effectStackingStrategy) {
        this.effectStackingStrategy = effectStackingStrategy;
    }

    @Override
    public IEffectContainer<VALUE, IEffect<VALUE>> getEffectContainer() {
        return container;
    }

    @Override
    public void setEffectContainer(IEffectContainer<VALUE, IEffect<VALUE>> iEffectContainer) {
        this.container = iEffectContainer;
    }

    @Override
    public boolean isTickingDisabled() {
        return tickingDisabled;
    }

    @Override
    public void setTickingDisabled(boolean tickingDisabled) {
        this.tickingDisabled = tickingDisabled;
    }

}
