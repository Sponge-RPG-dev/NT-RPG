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

import com.typesafe.config.*;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.utils.Utils;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

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
                } catch (ConfigException e) {
                    skillTree.setDescription("");
                    logger.warn("Missing \"Description\" node");
                }
                try {
                    skillTree.setId(config.getString("Name"));
                } catch (ConfigException e) {
                    logger.warn("Missing \"Name\" skipping to another file");
                    return;
                }
                skillTree.getSkills().put(StartingPoint.name, StartingPoint.SKILL_DATA);
                try {
                    Config sub = config.getObject("Skills").toConfig();
                    createConfigSkills(sub, skillTree);
                    loadSkills(sub, skillTree);
                } catch (ConfigException e) {
                    logger.warn("Missing \"Skills\" section. No skills defined");

                }

                try {
                    List<String> asciiMap = config.getStringList("AsciiMap");
                    java.util.Optional<String> max = asciiMap.stream().max(Comparator.comparingInt(String::length));
                    if (max.isPresent()) {
                        int length = max.get().length();
                        int rows = asciiMap.size();

                        short[][] array = new short[rows][length];

                        int i = 0;
                        int j = 0;
                        String num = "";
                        for (String s : asciiMap) {
                            for (char c1 : s.toCharArray()) {
                                if (Character.isDigit(c1)){
                                    num += c1;
                                    continue;
                                }
                                if (!num.equals("")) {
                                    array[i][j] = Short.parseShort(num);
                                } else if (SkillService.SKILL_CONNECTION_TYPES.keySet().contains(c1)){
                                    array[i][j] = SkillService.SKILL_CONNECTION_TYPES.get(c1).value;
                                }
                                num = "";
                                j++;
                            }
                            j=0;
                            i++;
                        }
                        skillTree.setSkillTreeMap(array);
                    }
                } catch (ConfigException ignored) {}


                map.put(skillTree.getId(), skillTree);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private void createConfigSkills(Config sub, SkillTree skillTree) {
        for (Map.Entry<String, ConfigValue> entry : sub.root().entrySet()) {
            String name = entry.getKey();
            ISkill skill = skillService.getSkill(name);
            if (skill == null) {
                ConfigObject value = (ConfigObject) entry.getValue();
                Config c = value.toConfig();

                try {
                    boolean bool = c.getBoolean("path");
                    if (!bool)
                        continue;
                    SkillTreePath path = new SkillTreePath(name);
                    skillService.addSkill(path);
                } catch (ConfigException.Missing ignored) {}
            }
        }
    }

    private void loadSkills(Config sub, SkillTree skillTree) {
        for (Map.Entry<String, ConfigValue> entry : sub.root().entrySet()) {

            ConfigObject value = (ConfigObject) entry.getValue();
            Config c = value.toConfig();


            SkillData info = getSkillInfo(entry.getKey(), skillTree);
            if (info instanceof SkillPathData) {
                SkillPathData pdata = (SkillPathData) info;
                try {
                    List<String> permissions = c.getStringList("permissions");
                    pdata.getPermissions().addAll(permissions);
                } catch (ConfigException e) {
                    logger.info("Found SkillPath in the tree \"" + skillTree.getId() + "\" but no permissions defined");
                }
                try {
                    int tier = c.getInt("tier");
                    pdata.setTier(tier);
                } catch (ConfigException e) {
                    logger.info("Found SkillPath in the tree \"" + skillTree.getId() + "\" but no tier defined, setting to 0");
                }

                try {
                    pdata.setSkillPointsRequired(c.getInt("SkillPointsRequired"));
                } catch (ConfigException e) {
                    logger.info("Found SkillPath in the tree \"" + skillTree.getId() + "\" but no permissions defined, setting to 1");
                    pdata.setSkillPointsRequired(1);
                }
                info.setMaxSkillLevel(1);
                try {
                    List<? extends Config> skillBonus = c.getConfigList("SkillBonus");
                    for (Config s : skillBonus) {
                        try {
                            String skill = s.getString("skill");
                            int levels = s.getInt("levels");
                            pdata.addSkillBonus(skill, levels);
                        } catch (ConfigException e) {
                            logger.info("Found SkillPath.SkillBonus in the tree \"" + skillTree.getId() + "\" missing \"skill\" or \"level\" configuration node");
                        }

                    }
                } catch (ConfigException e) {
                    //logger.info("Found SkillPath in the tree \"" + skillTree.getId() + "\" but no permissions defined, setting to 1");
                }
            } else {
                try {
                    info.setMaxSkillLevel(c.getInt("MaxSkillLevel"));
                } catch (ConfigException e) {
                    info.setMaxSkillLevel(1);
                    logger.warn("Missing \"MaxSkillLevel\" node for a skill \""+info.getSkillName()+"\", setting to 1");
                }
                try {
                    String combination = c.getString("Combination");
                    combination = combination.trim();
                    if (!"".equals(combination)) {
                        info.setCombination(combination);
                    }
                } catch (ConfigException e) {
                }
            }
            try {
                info.setMinPlayerLevel(c.getInt("MinPlayerLevel"));
            } catch (ConfigException e) {
                info.setMinPlayerLevel(1);
                logger.warn("Missing \"MinPlayerLevel\" node for a skill \""+info.getSkillName()+"\", setting to 1");
            }

            try {
                for (String conflicts : c.getStringList("Conflicts")) {
                    info.getConflicts().add(getSkillInfo(conflicts, skillTree));
                }
            } catch (ConfigException ignored) {}

            try {
                for (String conflicts : c.getStringList("SoftDepends")) {
                    SkillData i = getSkillInfo(conflicts, skillTree);
                    info.getSoftDepends().add(i);
                    i.getDepending().add(info);
                }
            } catch (ConfigException ignored) {}


            try {
                for (String conflicts : c.getStringList("HardDepends")) {
                    SkillData i = getSkillInfo(conflicts, skillTree);
                    info.getHardDepends().add(i);
                    i.getDepending().add(info);
                }
            } catch (ConfigException ignored) {}

            try {
                info.setSkillTreeId(c.getInt("SkillTreeId"));
            } catch (ConfigException ignored) {
                logger.info(" - Skill " + info.getSkillName() + " missing SkillTreeId, it wont be possible to reference this skill in the ascii map");
            }

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
                        float bonus = 0f;
                        try {
                            bonus = Float.parseFloat(settings.getString(name));
                        } catch (ConfigException ignored) {}
                        skillSettings.addNode(name, bonus);
                    } else {
                        skillSettings.addObjectNode(e.getKey(), val);
                    }
                }
                addRequiredIfMissing(skillSettings);
                info.setSkillSettings(skillSettings);
            } catch (ConfigException ignored) {}

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
            ISkill skill = skillService.getSkill(name);
            if (skill instanceof SkillTreePath) {
                info = new SkillPathData(name);
            } else {
                info = new SkillData(name);
            }
            info.setSkill(skill);
            tree.getSkills().put(name, info);
        }
        return info;
    }
}
