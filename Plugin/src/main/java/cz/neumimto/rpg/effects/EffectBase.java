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
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.utils.UUIDs;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by NeumimTo.
 */
@JsBinding(JsBinding.Type.CLASS)
public class EffectBase<Value> implements IEffect<Value> {

	protected Set<EffectType> effectTypes = new HashSet<>();
	private boolean stackable = false;

	private String name;

	private IEffectConsumer consumer;
	private long duration = -1;
	private long period = -1;
	private long lastTickTime;

	private long timeCreated;
	private String applyMessage;
	private String expireMessage;
	private UUID uuid;

	private IEffectSourceProvider effectSourceProvider;

	private Value value;

	private EffectStackingStrategy<Value> effectStackingStrategy;

	private IEffectContainer<Value, IEffect<Value>> container;

	private boolean tickingDisabled = false;

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
		return getDuration() >= 0 || getPeriod() >= 0;
	}

	@Override
	public String getName() {
		return name;
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

	public IEffectConsumer getConsumer() {
		return consumer;
	}

	public void setConsumer(IEffectConsumer consumer) {
		if (consumer != null) {
			this.consumer = consumer;
		}
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

	public void setEffectTypes(Set<EffectType> effectTypes) {
		this.effectTypes = effectTypes;
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
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (!(o instanceof EffectBase)) {
			return false;
		}

		EffectBase that = (EffectBase) o;
		return uuid != null ? uuid.equals(that.uuid) : that.uuid == null;
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

	@Override
	public Value getValue() {
		return value;
	}

	@Override
	public void setValue(Value o) {
		this.value = o;
	}

	@Override
	public EffectStackingStrategy<Value> getEffectStackingStrategy() {
		return effectStackingStrategy;
	}

	@Override
	public void setEffectStackingStrategy(EffectStackingStrategy<Value> effectStackingStrategy) {
		this.effectStackingStrategy = effectStackingStrategy;
	}

	@Override
	public IEffectContainer<Value, IEffect<Value>> getEffectContainer() {
		return container;
	}

	@Override
	public void setEffectContainer(IEffectContainer<Value, IEffect<Value>> iEffectContainer) {
		this.container = iEffectContainer;
	}

	protected void addEffectType(EffectType e) {
		if (effectTypes == null) {
			effectTypes = new HashSet<>();
		}
		effectTypes.add(e);
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
