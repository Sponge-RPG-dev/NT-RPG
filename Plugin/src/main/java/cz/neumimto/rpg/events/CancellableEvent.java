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

package cz.neumimto.rpg.events;

import cz.neumimto.rpg.NtRpgPlugin;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.impl.AbstractEvent;


/**
 * Created by NeumimTo on 12.2.2015.
 */
public class CancellableEvent extends AbstractEvent implements Cancellable {

	public boolean cancelled;
	private Cause cause;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}

    @Override
    public Cause getCause() {
        return cause == null ? Cause.of(EventContext.empty(), NtRpgPlugin.GlobalScope.plugin) : cause;
    }

	public void setCause(Cause cause) {
		this.cause = cause;
	}
}
