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

package cz.neumimto.events;

import org.spongepowered.api.event.Order;
import org.spongepowered.api.util.event.callback.EventCallback;

/**
 * Created by NeumimTo on 7.8.2015.
 */
public abstract class AbstractEventCallback implements EventCallback {

    @Override
    public boolean isBaseGame() {
        return false;
    }

    @Override
    public Order getOrder() {
        return Order.DEFAULT;
    }

}
