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

package cz.neumimto.rpg;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.common.AbstractResourceManager;
import cz.neumimto.rpg.sponge.commands.CommandBase;
import cz.neumimto.rpg.sponge.commands.CommandService;

import javax.inject.Inject;
import javax.inject.Singleton;

import static cz.neumimto.rpg.api.logging.Log.info;

/**
 * Created by NeumimTo on 27.12.2014.
 */
@SuppressWarnings("unchecked")
@Singleton
public class ResourceLoader extends AbstractResourceManager {



    @Inject
    private CommandService commandService;

    @Override
    public Object loadClass(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        Object o = super.loadClass(clazz);

        if (clazz.isAnnotationPresent(Command.class)) {
            o = injector.getInstance(clazz);
            info("registering command class" + clazz.getName(), Rpg.get().getPluginConfig().DEBUG);
            commandService.registerCommand((CommandBase) o);
        }
        return o;
    }

}
