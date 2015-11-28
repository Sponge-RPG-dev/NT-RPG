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

package cz.neumimto;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.persistance.GroupDao;
import cz.neumimto.players.groups.Guild;
import cz.neumimto.players.groups.NClass;
import cz.neumimto.players.groups.Race;

import java.util.Collection;

/**
 * Created by NeumimTo on 28.12.2014.
 */
@Singleton
public class GroupService {

    @Inject
    private GroupDao groupDao;

    public GroupService() {

    }

    public Guild getGuild(String name) {
        name = name.toLowerCase();
        if (!groupDao.getGuilds().containsKey(name)) {
            return Guild.Default;
        }
        return groupDao.getGuilds().get(name.toLowerCase());
    }

    public void registerGuild(Guild g) {
        groupDao.getGuilds().put(g.getName().toLowerCase(), g);
    }

    public Race getRace(String name) {
        name = name.toLowerCase();
        if (!groupDao.getRaces().containsKey(name)) {
            return Race.Default;
        }
        return groupDao.getRaces().get(name.toLowerCase());
    }

    public void registerRace(Race g) {
        groupDao.getRaces().put(g.getName().toLowerCase(), g);
    }

    public NClass getNClass(String name) {
        name = name.toLowerCase();
        if (!groupDao.getClasses().containsKey(name)) {
            return NClass.Default;
        }
        return groupDao.getClasses().get(name.toLowerCase());
    }

    public void registerNClass(NClass g) {
        groupDao.getClasses().put(g.getName().toLowerCase(), g);
    }

    public Collection<Race> getRaces() {
        return groupDao.getRaces().values();
    }

    public Collection<Guild> getGuilds() {
        return groupDao.getGuilds().values();
    }

    @PostProcess
    public void registerPlaceholders() {
        registerGuild(Guild.Default);
        registerNClass(NClass.Default);
        registerRace(Race.Default);
    }
}
