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

import com.google.inject.ConfigurationException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.utils.Utils;
import org.slf4j.Logger;


import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 24.7.2015.
 */
@Singleton
public class SkillTreeDao {

    @Inject
    SkillService skillService;

    @Inject
    Logger logger;

    public Map<String, SkillTree> getAll() {
        Path dir = ResourceLoader.skilltreeDir.toPath();
        Map<String, SkillTree> map = new HashMap<>();
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(dir, "*.conf")) {
            paths.forEach(path -> {
                logger.info("Loading skilltree from a file " + path.getFileName());
                Config config = ConfigFactory.parseFile(path.toFile());
                SkillTree skillTree = new SkillTree();
                try {
                    skillTree.setDescription(config.getString("Description"));
                } catch (ConfigurationException e) {
                    skillTree.setDescription("");
                    logger.warn("Missing \"Description\" node");
                }
                try {
                    skillTree.setId(config.getString("Name"));
                } catch (ConfigurationException e) {
                    logger.warn("Missing \"Name\" skipping to another file");
                    return;
                }
                skillTree.getSkills().put(StartingPoint.name, StartingPoint.SKILL_DATA);
                try {
                    Config sub = config.getObject("Skills").toConfig();
                    loadSkills(sub, skillTree);
                } catch (ConfigurationException e) {
                    logger.warn("Missing \"Skills\" section. No skills defined");

                }
                map.put(skillTree.getId(), skillTree);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private void loadSkills(Config sub, SkillTree skillTree) {
        for (Map.Entry<String, ConfigValue> entry : sub.root().entrySet()) {
            SkillData info = getSkillInfo(entry.getKey(), skillTree);
            ISkill skill = skillService.getSkill(info.getSkillName());

            ConfigObject value = (ConfigObject) entry.getValue();
            Config c = value.toConfig();
            try {
                info.setMinPlayerLevel(c.getInt("MinPlayerLevel"));
            } catch (ConfigurationException e) {
                info.setMinPlayerLevel(1);
                logger.warn("Missing \"MinPlayerLevel\" node for a skill \""+info.getSkillName()+"\", setting to 1");
            }
            try {
                info.setMaxSkillLevel(c.getInt("MaxSkillLevel"));
            } catch (ConfigurationException e) {
                info.setMaxSkillLevel(1);
                logger.warn("Missing \"MaxSkillLevel\" node for a skill \""+info.getSkillName()+"\", setting to 1");
            }
            try {
                for (String conflicts : c.getStringList("Conflicts")) {
                    info.getConflicts().add(getSkillInfo(conflicts, skillTree));
                }
            } catch (ConfigurationException ignored) {}

            try {
                for (String conflicts : c.getStringList("SoftDepends")) {
                    SkillData i = getSkillInfo(conflicts, skillTree);
                    info.getSoftDepends().add(i);
                    i.getDepending().add(info);
                }
            } catch (ConfigurationException ignored) {}


            try {
                for (String conflicts : c.getStringList("HardDepends")) {
                    SkillData i = getSkillInfo(conflicts, skillTree);
                    info.getHardDepends().add(i);
                    i.getDepending().add(info);
                }
            } catch (ConfigurationException ignored) {}

            try {
                Config settings = c.getConfig("SkillSettings");
                SkillSettings skillSettings = new SkillSettings();
                for (Map.Entry<String, ConfigValue> e : settings.entrySet()) {
                    if (e.getKey().endsWith(SkillSettings.bonus)) {
                        continue;
                    }
                    String val = e.getValue().render();
                    if (Utils.isNumeric(val)) {
                        float norm = Float.parseFloat(val);
                        String name = e.getKey();
                        skillSettings.addNode(name, norm);
                        name = name + SkillSettings.bonus;
                        //todo if not exists set to 0;
                        float bon = Float.parseFloat(settings.getString(name));
                        skillSettings.addNode(name, bon);
                    } else {
                        skillSettings.addObjectNode(e.getKey(), val);
                    }
                }
                addRequiredIfMissing(skillSettings);
                info.setSkillSettings(skillSettings);
            } catch (ConfigurationException ignored) {}

            skillTree.getSkills().put(info.getSkillName(), info);

        }
    }

    private void addRequiredIfMissing(SkillSettings skillSettings) {
        Map.Entry<String, Float> q = skillSettings.getFloatNodeEntry(SkillNodes.HPCOST.name());
        if (q == null) {
            skillSettings.addNode(SkillNodes.HPCOST, 0, 0);
        }
        q = skillSettings.getFloatNodeEntry(SkillNodes.MANACOST.name());
        if (q == null) {
            skillSettings.addNode(SkillNodes.MANACOST, 0, 0);
        }
        q = skillSettings.getFloatNodeEntry(SkillNodes.COOLDOWN.name());
        if (q == null) {
            skillSettings.addNode(SkillNodes.COOLDOWN, 0, 0);
        }
    }

    private SkillData getSkillInfo(String name, SkillTree tree) {
        SkillData info = tree.getSkills().get(name);
        if (info == null) {
            info = new SkillData(name);
            info.setSkill(skillService.getSkill(name));
            tree.getSkills().put(name, info);
        }
        return info;
    }
}
