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

package cz.neumimto.listeners;

import cz.neumimto.inventory.InventoryService;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.ListenerClass;
import cz.neumimto.players.CharacterService;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@ListenerClass
public class InventoryListener {

    @Inject
    private InventoryService inventoryService;

    @Inject
    private CharacterService characterService;

    @Inject
    private Game game;

}
