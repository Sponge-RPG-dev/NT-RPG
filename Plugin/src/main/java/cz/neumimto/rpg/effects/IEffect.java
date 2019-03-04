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
import cz.neumimto.rpg.IRpgElement;
import cz.neumimto.rpg.NtRpgPlugin;

import java.util.Set;
import java.util.UUID;

/**
 * Created by NeumimTo on 17.1.2015.
 */
public interface IEffect<K> extends IRpgElement {

	static GlobalScope getGlobalScope() {
		return NtRpgPlugin.GlobalScope;
	}

	/**
	 * @param self The reference to the effect currently being processed. IE: self == this is always true. Useful mainly for JavaScript scripts
	 */
	default void onTick(IEffect self) {

	}

	default void onApply(IEffect self) {

	}

	default void onRemove(IEffect self) {

	}

	boolean isStackable();

	void setStackable(boolean b, EffectStackingStrategy<K> stackingStrategy);

	boolean requiresRegister();

	long getPeriod();

	void setPeriod(long period);

	long getLastTickTime();

	void setLastTickTime(long currTime);

	long getExpireTime();

	long getDuration();

	void setDuration(long l);

	UUID getUUID();

	String getExpireMessage();

	void setExpireMessage(String expireMessage);

	String getApplyMessage();

	void setApplyMessage(String applyMessage);

	IEffectConsumer getConsumer();

	void setConsumer(IEffectConsumer consumer);

	Set<EffectType> getEffectTypes();

	IEffectSourceProvider getEffectSourceProvider();

	void setEffectSourceProvider(IEffectSourceProvider effectSourceProvider);

	K getValue();

	void setValue(K k);

	default <T extends IEffect<K>> IEffectContainer<K, T> constructEffectContainer() {
		return new EffectContainer(this);
	}

	EffectStackingStrategy<K> getEffectStackingStrategy();

	void setEffectStackingStrategy(EffectStackingStrategy<K> effectStackingStrategy);

	IEffectContainer<K, IEffect<K>> getEffectContainer();

	void setEffectContainer(IEffectContainer<K, IEffect<K>> iEffectContainer);

	boolean isTickingDisabled();

	void setTickingDisabled(boolean tickingDisabled);
}
