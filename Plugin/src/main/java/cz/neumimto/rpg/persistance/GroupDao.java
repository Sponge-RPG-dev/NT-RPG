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

package cz.neumimto.rpg.persistance;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.players.ExperienceSource;
import cz.neumimto.rpg.players.groups.Guild;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.players.groups.PlayerGroup;
import cz.neumimto.rpg.players.groups.Race;
import cz.neumimto.rpg.players.properties.PlayerPropertyService;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.skills.SkillTree;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

    private Map<String, Race> races = new HashMap<>();
    private Map<String, ConfigClass> classes = new HashMap<>();
    private Map<String, Guild> guilds = new HashMap<>();

    public Map<String, Race> getRaces() {
        return races;
    }

    public Map<String, ConfigClass> getClasses() {
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
                //     Guild guild = new Guild(c.getString("Name"));
                //     loadPlayerGroup(c, guild);
                //     getGuilds().put(guild.getName().toLowerCase(), guild);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostProcess(priority = 399)
    public void loadNClasses() {
        Path path = ResourceLoader.classDir.toPath();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.conf")) {
            stream.forEach(p -> {
                Config c = ConfigFactory.parseFile(p.toFile());
                ConfigClass configClass = new ConfigClass(c.getString("Name"));
                loadPlayerGroup(c, configClass);
                SkillTree skillTree = skillService.getSkillTrees().get(c.getString("SkillTree"));
                if (skillTree == null) {
                    skillTree = SkillTree.Default;
                }
                configClass.setSkillTree(skillTree);
                List<String> experienceSources = c.getStringList("ExperienceSources");
                HashSet<ExperienceSource> objects = new HashSet<>();
                experienceSources.forEach(a -> objects.add(ExperienceSource.valueOf(a)));
                configClass.setExperienceSources(objects);
                configClass.setSkillpointsperlevel(c.getInt("SkillPointsPerLevel"));
                configClass.setAttributepointsperlevel(c.getInt("AttributePointsPerLevel"));
                int maxLevel = c.getInt("MaxLevel");
                double first = c.getDouble("ExpFirstLevel");
                double last = c.getDouble("ExpLastLevel");

                initLevelCurve(configClass, maxLevel, first, last);
                getClasses().put(configClass.getName().toLowerCase(), configClass);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostProcess(priority = 450)
    public void loadRaces() {
        Path path = ResourceLoader.raceDir.toPath();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.conf")) {
            stream.forEach(p -> {
                Config c = ConfigFactory.parseFile(p.toFile());
                Race race = new Race(c.getString("Name"));
                loadPlayerGroup(c, race);
                Set<ConfigClass> set = new HashSet<>();
                for (String a : c.getStringList("AllowedClasses")) {
                    ConfigClass configClass = getClasses().get(a.toLowerCase());
                    if (configClass == null) {
                        configClass = ConfigClass.Default;
                    }
                    set.add(configClass);
                }
                race.setAllowedClasses(set);
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
        prop = c.getConfig("Attributes");
        set = prop.entrySet();
        for (Map.Entry<String, ConfigValue> entry : set) {
            String attribute = entry.getKey();
            int i = Integer.parseInt(entry.getValue().render());
            ICharacterAttribute attribute1 = propertyService.getAttribute(attribute);
            group.getStartingAttributes().put(attribute1, i);
        }
        Optional<ItemType> menuIcon = game.getRegistry().getType(ItemType.class, c.getString("MenuIcon"));
        if (menuIcon.isPresent()) {
            group.setItemType(menuIcon.get());
        } else {
            group.setItemType(ItemTypes.STONE);
        }

        group.setDescription(c.getString("Description"));
        List<String> permissions = c.getStringList("Permissions");
        group.setPermissions(new HashSet<>(permissions));
    }


    public void initLevelCurve(ConfigClass configClass, int maxlevel, double expFirstLevel, double expForLastLevel) {
        double factora = Math.log(expForLastLevel / expFirstLevel) / (maxlevel - 1);
        double factorb = expFirstLevel / (Math.exp(factora) - 1.0);
        double[] levels = new double[maxlevel];
        double k = 0;
        for (int i = 1; i <= maxlevel; i++) {
            double oldxp = Math.round(factorb * Math.exp(factora * (i - 1)));
            double newxp = Math.round(factorb * Math.exp(factora * i));
            levels[i - 1] = newxp - oldxp;
            k += levels[i - 1];
        }
        configClass.setLevels(levels);
        configClass.setTotalExp(k);
    }
}