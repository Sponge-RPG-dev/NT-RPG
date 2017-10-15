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

package cz.neumimto.rpg.commands;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.NtRpgPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@Singleton
public class CommandService {

	@Inject
	private NtRpgPlugin plugin;

	public void registerCommand(CommandBase commandCallable) {
		try {
			Sponge.getCommandManager().register(plugin, commandCallable, commandCallable.getAliases());
		} catch (NoSuchMethodError e) {
			try {
				Object o = Sponge.class.getDeclaredMethod("getCommandDispatcher").invoke(null);
				o.getClass().getDeclaredMethod("register", Object.class, CommandCallable.class, List.class).invoke(o, plugin, commandCallable, commandCallable.getAliases());
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
				e1.printStackTrace();
			}
		}
	}


}
