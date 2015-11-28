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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import cz.neumimto.ResourceLoader;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.skills.*;
import cz.neumimto.utils.Utils;

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
                Config sub = config.getObject("Skills").toConfig();
                for (Map.Entry<String, ConfigValue> entry : sub.root().entrySet()) {
                    SkillInfo info = getSkillInfo(entry.getKey(), skillTree);
                    ISkill skill = skillService.getSkill(info.getSkillName());

                    ConfigObject value = (ConfigObject) entry.getValue();
                    Config c = value.toConfig();
                    info.setMinPlayerLevel(c.getInt("MinPlayerLevel"));
                    info.setMinPlayerLevel(c.getInt("MaxSkillLevel"));
                    for (String conflicts : c.getStringList("Conflicts")) {
                        info.getConflicts().add(getSkillInfo(conflicts, skillTree));
                    }
                    for (String conflicts : c.getStringList("SoftDepends")) {
                        SkillInfo i = getSkillInfo(conflicts, skillTree);
                        info.getSoftDepends().add(i);
                        i.getDepending().add(info);
                    }
                    for (String conflicts : c.getStringList("HardDepends")) {
                        SkillInfo i = getSkillInfo(conflicts, skillTree);
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
                            if (skill.getDefaultSkillSettings().hasNode(e.getKey())) {
                                float norm = Float.parseFloat(val);
                                Map.Entry<String, Float> q = skill.getDefaultSkillSettings().getFloatNodeEntry(e.getKey());
                                Map.Entry<String, Float> w = skill.getDefaultSkillSettings().getFloatNodeEntry(e.getKey() + SkillSettings.bonus);
                                skillSettings.addNode(q.getKey(), norm);
                                float bon = Float.parseFloat(settings.getString(w.getKey()));
                                skillSettings.addNode(w.getKey(), bon);
                            }
                        } else {
                            skillSettings.addObjectNode(e.getKey(), val);
                        }
                    }
                    info.setSkillSettings(skillSettings);
                    skillTree.getSkills().put(info.getSkillName(), info);
                    map.put(info.getSkillName().toLowerCase(), skillTree);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private SkillInfo getSkillInfo(String name, SkillTree tree) {
        SkillInfo info = tree.getSkills().get(name);
        if (info == null) {
            info = new SkillInfo(name);
            tree.getSkills().put(name, info);
        }
        return info;
    }
}
