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

package cz.neumimto.persistance;

import com.google.common.base.Optional;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import cz.neumimto.ResourceLoader;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.PostProcess;
import cz.neumimto.ioc.Singleton;
import cz.neumimto.players.groups.Guild;
import cz.neumimto.players.groups.NClass;
import cz.neumimto.players.groups.PlayerGroup;
import cz.neumimto.players.groups.Race;
import cz.neumimto.players.properties.PlayerPropertyService;
import cz.neumimto.skills.SkillService;
import cz.neumimto.skills.SkillTree;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.item.ItemType;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by NeumimTo on 10.7.2015.
 */
@Singleton
public class GroupDao {

    @Inject
    PlayerPropertyService propertyService;

    @Inject
    Game game;

    @Inject
    Logger logger;

    @Inject
    SkillService skillService;

    private Map<String, Race> races = new ConcurrentHashMap<>();
    private Map<String, NClass> classes = new ConcurrentHashMap<>();
    private Map<String, Guild> guilds = new ConcurrentHashMap<>();

    public Map<String, Race> getRaces() {
        return races;
    }

    public Map<String, NClass> getClasses() {
        return classes;
    }

    public Map<String, Guild> getGuilds() {
        return guilds;
    }


    @PostProcess(priority = 400)
    public void loadGuilds() {
        Path path = ResourceLoader.guildsDir.toPath();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.conf")) {
            stream.forEach(p -> {
                Config c = ConfigFactory.parseFile(p.toFile());
                Guild guild = new Guild(c.getString("Name"));
                loadPlayerGroup(c, guild);
                getGuilds().put(guild.getName().toLowerCase(), guild);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostProcess(priority = 400)
    public void loadNClasses() {
        Path path = ResourceLoader.classDir.toPath();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.conf")) {
            stream.forEach(p -> {
                Config c = ConfigFactory.parseFile(p.toFile());
                NClass nClass = new NClass(c.getString("Name"));
                loadPlayerGroup(c, nClass);
                SkillTree skillTree = skillService.getSkillTrees().get(c.getString("SkillTree"));
                if (skillTree == null) {
                    skillTree = SkillTree.Default;
                }
                nClass.setSkillTree(skillTree);
                getClasses().put(nClass.getName().toLowerCase(), nClass);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostProcess(priority = 400)
    public void loadRaces() {
        Path path = ResourceLoader.raceDir.toPath();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.conf")) {
            stream.forEach(p -> {
                Config c = ConfigFactory.parseFile(p.toFile());
                Race race = new Race(c.getString("Name"));
                loadPlayerGroup(c, race);
                getRaces().put(race.getName().toLowerCase(), race);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadPlayerGroup(Config c, PlayerGroup group) {
        group.setShowsInMenu(c.getBoolean("Wildcart"));
        group.setChatPrefix(c.getString("Chat.prefix"));
        group.setChatSufix(c.getString("Chat.suffix"));
        Config prop = c.getConfig("BonusProperties");
        Set<Map.Entry<String, ConfigValue>> set = prop.entrySet();
        int id = 0;
        float bonus;
        for (Map.Entry<String, ConfigValue> m : set) {
            id = propertyService.getIdByName(m.getKey());
            bonus = Float.parseFloat(m.getValue().render());
            group.getPropBonus().put(id, bonus);
        }
        Config propl = c.getConfig("BonusPropertiesPerLevel");
        Set<Map.Entry<String, ConfigValue>> setl = propl.entrySet();
        for (Map.Entry<String, ConfigValue> m : setl) {
            id = propertyService.getIdByName(m.getKey());
            bonus = Float.parseFloat(m.getValue().render());
            group.getPropLevelBonus().put(id, bonus);
        }
        List<String> list = c.getStringList("AllowedArmor");
        list.stream().forEach(a -> {
            Optional<ItemType> type = game.getRegistry().getType(ItemType.class, a);
            if (type.isPresent()) {
                group.getAllowedArmor().add(type.get());
            } else logger.warn("Defined invalid itemtype  " + a + " in " + group.getName());
        });
        prop = c.getConfig("AllowedWeapons");
        set = prop.entrySet();
        for (Map.Entry<String, ConfigValue> m : set) {
            Optional<ItemType> type = game.getRegistry().getType(ItemType.class, m.getKey());
            if (type.isPresent()) {
                group.getWeapons().put(type.get(), Double.parseDouble(m.getValue().render()));
            } else logger.warn("Defined invalid itemtype  " + m.getKey() + " in " + group.getName());

        }
        List<String> permissions = c.getStringList("Permissions");
        group.setPermissions(new HashSet<>(permissions));
    }

}
