package cz.neumimto;

import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.PostProcess;
import cz.neumimto.ioc.Singleton;
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
