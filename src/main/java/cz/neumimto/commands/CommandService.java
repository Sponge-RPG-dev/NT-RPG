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

package cz.neumimto.commands;

import cz.neumimto.NtRpgPlugin;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.Optional;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@Singleton
public class CommandService {

    @Inject
    private NtRpgPlugin plugin;

    public void registerCommand(CommandBase commandCallable) {
        Sponge.getCommandDispatcher().register(plugin, commandCallable, commandCallable.getAliases());
    }


}
