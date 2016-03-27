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
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.utils.Utils;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;

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

    public Map<String, SkillTree> getAll() {
        Path dir = ResourceLoader.skilltreeDir.toPath();
        Map<String, SkillTree> map = new HashMap<>();
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(dir, "*.conf")) {
            paths.forEach(path -> {
                Config config = ConfigFactory.parseFile(path.toFile());
                SkillTree skillTree = new SkillTree();
                skillTree.setDescription(config.getString("Description"));
                skillTree.setId(config.getString("Name"));
                skillTree.getSkills().put(StartingPoint.name, StartingPoint.SKILL_DATA);
                Config sub = config.getObject("Skills").toConfig();
                for (Map.Entry<String, ConfigValue> entry : sub.root().entrySet()) {
                    SkillData info = getSkillInfo(entry.getKey(), skillTree);
                    ISkill skill = skillService.getSkill(info.getSkillName());

                    ConfigObject value = (ConfigObject) entry.getValue();
                    Config c = value.toConfig();
                    info.setMinPlayerLevel(c.getInt("MinPlayerLevel"));
                    info.setMaxSkillLevel(c.getInt("MaxSkillLevel"));
                    for (String conflicts : c.getStringList("Conflicts")) {
                        info.getConflicts().add(getSkillInfo(conflicts, skillTree));
                    }
                    for (String conflicts : c.getStringList("SoftDepends")) {
                        SkillData i = getSkillInfo(conflicts, skillTree);
                        info.getSoftDepends().add(i);
                        i.getDepending().add(info);
                    }
                    for (String conflicts : c.getStringList("HardDepends")) {
                        SkillData i = getSkillInfo(conflicts, skillTree);
                        info.getHardDepends().add(i);
                        i.getDepending().add(info);
                    }
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
                            Float aFloat = null;
                            skillSettings.addNode(name, norm);
                            name = name+SkillSettings.bonus;
                            //todo if not exists set to 0;
                            float bon = Float.parseFloat(settings.getString(name));
                            skillSettings.addNode(name, bon);
                        } else {
                            skillSettings.addObjectNode(e.getKey(), val);
                        }
                    }
                    addRequiredIfMissing(skillSettings);
                    info.setSkillSettings(skillSettings);
                    skillTree.getSkills().put(info.getSkillName(), info);
                    map.put(skillTree.getId(), skillTree);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private void addRequiredIfMissing(SkillSettings skillSettings) {
        Map.Entry<String, Float> q = skillSettings.getFloatNodeEntry(SkillNode.HPCOST.name());
        if (q == null) {
            skillSettings.addNode(SkillNode.HPCOST, 0, 0);
        }
        q = skillSettings.getFloatNodeEntry(SkillNode.MANACOST.name());
        if (q == null) {
            skillSettings.addNode(SkillNode.MANACOST, 0, 0);
        }
        q = skillSettings.getFloatNodeEntry(SkillNode.COOLDOWN.name());
        if (q == null) {
            skillSettings.addNode(SkillNode.COOLDOWN, 0, 0);
        }
    }

    private SkillData getSkillInfo(String name, SkillTree tree) {
        SkillData info = tree.getSkills().get(name);
        if (info == null) {
            info = new SkillData(name);
            tree.getSkills().put(name, info);
        }
        return info;
    }
}
