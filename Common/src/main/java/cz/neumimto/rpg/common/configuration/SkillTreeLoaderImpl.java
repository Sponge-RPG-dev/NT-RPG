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

package cz.neumimto.rpg.common.configuration;

import com.google.inject.Injector;
import com.typesafe.config.*;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.SkillTreeDao;
import cz.neumimto.rpg.api.effects.model.mappers.SingleValueModelMapper;
import cz.neumimto.rpg.api.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.*;
import cz.neumimto.rpg.api.skills.WrappedSkill.WrappedSkillData;
import cz.neumimto.rpg.api.skills.scripting.ScriptedSkillNodeDescription;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.skills.utils.SkillLoadingErrors;
import cz.neumimto.rpg.api.utils.FileUtils;
import cz.neumimto.rpg.api.utils.Pair;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.skills.SkillConfigLoader;
import cz.neumimto.rpg.common.skills.SkillConfigLoaders;
import cz.neumimto.rpg.common.skills.SkillExecutor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.*;

import static cz.neumimto.rpg.api.logging.Log.*;

/**
 * Created by NeumimTo on 24.7.2015.
 */
//todo use nightconfig
@Singleton
public class SkillTreeLoaderImpl implements SkillTreeDao {

    @Inject
    private SkillService skillService;

    @Inject
    private LocalizationService localizationService;

    @Inject
    private Injector injector;

    @Inject
    private ClassService classService;

    @Inject
    private AssetService assetService;

    @Override
    public Map<String, SkillTree> getAll() {
        Path dir = Paths.get(Rpg.get().getWorkingDirectory());
        FileUtils.createDirectoryIfNotExists(dir);
        Path skillTreeDir = dir.resolve("skilltrees/");
        Map<String, SkillTree> map = new HashMap<>();
        if (classService.isClassDirEmpty()) {
            Log.info("No classes found in classes folder, loading skilltrees from within ntrpg.jar");
            dir = assetService.getTempWorkingDir();
        }
        FileUtils.createDirectoryIfNotExists(skillTreeDir);
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(skillTreeDir, "*.conf")) {
            paths.forEach(path -> {
                try {
                    info("Loading skilltree from a file " + path.getFileName());
                    Config config = ConfigFactory.parseFile(path.toFile());
                    populateMap(map, config);
                } catch (ConfigException e) {
                    Log.error("Unable to load skilltree - Malformed file format" + path, e);
                } catch (Throwable e) {
                    Log.error("Unable to load skilltree " + path, e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }

    public void populateMap(Map<String, SkillTree> map, Config config) {
        SkillTree skillTree = new SkillTree();
        if (loadTree(config, skillTree)) {
            return;
        }
        loadAsciiMaps(config, skillTree);
        map.put(skillTree.getId(), skillTree);
    }

    public void loadAsciiMaps(Config config, SkillTree skillTree) {
        try {
            List<String> asciiMap = config.getStringList("AsciiMap");
            Optional<String> max = asciiMap.stream().max(Comparator.comparingInt(String::length));
            if (max.isPresent()) {
                int length = max.get().length();
                int rows = asciiMap.size();

                short[][] array = new short[rows][length];

                int i = 0;
                int j = 0;
                StringBuilder num = new StringBuilder();
                for (String s : asciiMap) {
                    for (char c1 : s.toCharArray()) {
                        if (Character.isDigit(c1)) {
                            num.append(c1);
                            continue;
                        } else if (c1 == 'X') {
                            skillTree.setCenter(new Pair<>(i, j));
                            j++;
                            continue;
                        }
                        if (!num.toString().equals("")) {
                            array[i][j] = Short.parseShort(num.toString());
                            j++;
                        }
                        ISkillTreeInterfaceModel guiModelByCharacter = skillService.getGuiModelByCharacter(c1);
                        if (guiModelByCharacter != null) {
                            array[i][j] = guiModelByCharacter.getId();
                        }
                        num = new StringBuilder();
                        j++;
                    }
                    j = 0;
                    i++;
                }
                skillTree.setSkillTreeMap(array);
            }
        } catch (ConfigException | ArrayIndexOutOfBoundsException ignored) {
            error("Could not read ascii map in the skilltree " + skillTree.getId(), ignored);
            skillTree.setSkillTreeMap(new short[][]{});
        }
    }


    protected boolean loadTree(Config config, SkillTree skillTree) {

        try {
            skillTree.setId(config.getString("Name"));
        } catch (ConfigException e) {
            warn("Missing \"Name\" skipping to another file");
            return true;
        }

        try {
            List<? extends ConfigObject> skills = config.getObjectList("Skills");
            createConfigSkills(skills, skillTree);
            loadSkills(skills, skillTree);
        } catch (ConfigException e) {
            error("Could not read \"Skills\" section. skipping", e);

        }
        return false;
    }

    private void createConfigSkills(List<? extends ConfigObject> sub, SkillTree skillTree) {
        for (ConfigObject co : sub) {
            Config c = co.toConfig();
            createConfigSkill(skillTree, c);
        }
    }

    private void createConfigSkill(SkillTree skillTree, Config c) {
        String type = null;
        try {
            type = c.getString("Type");
        } catch (ConfigException.Missing ignored) {
            return;
        }

        String id = c.getString("SkillId");

        ISkill skill = Rpg.get().getSkillService().getSkills().get(id.toLowerCase());
        if (skill == null) {
            try {
                final String cType = type;
                SkillConfigLoader loader = SkillConfigLoaders.getById(type)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Unknown skill type " + cType + " in a skiltree " + skillTree.getId())
                        );

                skill = loader.build(id.toLowerCase());

            } catch (ConfigException.Missing ignored) {
            }

        } else {
            Log.warn("Found duplicit Config skill " + id + ", will be skipped try to avoid such use cases.");
        }
    }

    private void loadSkills(List<? extends ConfigObject> sub, SkillTree skillTree) {
        for (ConfigObject co : sub) {
            loadSkill(skillTree, co.toConfig());
        }
    }

    protected void loadSkill(SkillTree skillTree, Config c) {
        String skillId = c.getString("SkillId");
        SkillData info = getSkillInfo(skillId, skillTree);

        try {
            Config parentConfig = c;
            SkillData childSkill = info;
            while (parentConfig.hasPath("Parent")) {
                parentConfig = c.getConfig("Parent");
                String wrappedSkillId = parentConfig.getString("SkillId");
                SkillData infoWrapped = createSkillInfo(skillTree, wrappedSkillId);
                loadSkill(skillTree, parentConfig, infoWrapped);
                if (infoWrapped.getSkill() instanceof ActiveSkill) {
                    infoWrapped.setSkillExecutor(injector.getInstance(SkillExecutor.class).init(info));
                }
                WrappedSkillData wrapper = (WrappedSkillData) childSkill;
                wrapper.setWrapped(infoWrapped);
                childSkill = infoWrapped;
            }

        } catch (ConfigException ignored) {
        }

        loadSkill(skillTree, c, info);
        if (info.getSkill() instanceof ActiveSkill) {
            info.setSkillExecutor(injector.getInstance(SkillExecutor.class).init(info));
        }
        skillTree.getSkills().put(info.getSkillId().toLowerCase(), info);
    }

    private void loadSkill(SkillTree skillTree, Config c, SkillData info) {
        try {
            info.setMaxSkillLevel(c.getInt("MaxSkillLevel"));
        } catch (ConfigException e) {
            info.setMaxSkillLevel(1);
            warn("Missing \"MaxSkillLevel\" node for a skill \"" + info.getSkillId() + "\", setting to 1");
        }
        try {
            String combination = c.getString("Combination");
            combination = combination.trim();
            if (!"".equals(combination)) {
                info.setCombination(combination);
            }
        } catch (ConfigException e) {
        }

        try {
            Integer modelId = c.getInt("ModelId");
            info.setModelId(modelId);
        } catch (ConfigException e) {
        }

        try {
            String icon = c.getString("Icon");
            info.setIcon(icon);
        } catch (ConfigException e) {

        }

        try {
            info.setMinPlayerLevel(c.getInt("MinPlayerLevel"));
        } catch (ConfigException e) {
            info.setMinPlayerLevel(1);
            warn("Missing \"MinPlayerLevel\" node for a skill \"" + info.getSkillId() + "\", setting to 1");
        }

        try {
            info.setLevelGap(c.getInt("LevelGap"));
        } catch (ConfigException e) {
            info.setLevelGap(0);
            warn("Missing \"LevelGap\" node for a skill \"" + info.getSkillId() + "\", setting to 1");
        }

        ISkillNodeDescription skillNodeDescription = null;
        try {
            List<String> description = c.getStringList("Description");
            skillNodeDescription = new SkillNodeDescription(description);
        } catch (ConfigException e) {
            try {
                Config description = c.getConfig("Description");

                List<String> template = description.getStringList("Template");
                ScriptedSkillNodeDescription scriptedSkillNodeDescription = new ScriptedSkillNodeDescription();
                scriptedSkillNodeDescription.setTemplate(template);

                scriptedSkillNodeDescription.setJSFunction(description.getString("Function"));

                skillNodeDescription = scriptedSkillNodeDescription;
            } catch (ConfigException ee) {
                skillNodeDescription = character -> Collections.emptyList();
            }
        }
        info.setDescription(skillNodeDescription);

        // try {
        //     Config reagent = c.getConfig("InvokeCost");
        //     SkillCost itemCost = new SkillCost();
        //     info.setInvokeCost(itemCost);
        //     List<? extends ConfigObject> list = reagent.getObjectList("Items");

        //     for (ConfigObject configObject : list) {
        //         try {
        //             SkillItemCost q = new SkillItemCost();
        //             q.setAmount(Integer.parseInt(configObject.get("Amount").unwrapped().toString()));
        //             String type = configObject.get("Item").unwrapped().toString();
        //             boolean consume = Boolean.valueOf(configObject.get("Consume").unwrapped().toString());
        //             q.setConsumeItems(consume);
        //             ItemString parse = ItemString.parse(type);
        //             q.setItemType(parse);
        //             itemCost.getItemCost().add(q);

        //         } catch (Exception e) {
        //             e.printStackTrace();
        //         }
        //      }

        //  } catch (Exception e) {

        //  }

        try {
            for (String conflicts : c.getStringList("Conflicts")) {
                info.getConflicts().add(getSkillInfo(conflicts, skillTree));
            }
        } catch (ConfigException ignored) {
        }

        try {
            Config softDepends = c.getConfig("SoftDepends");
            for (Map.Entry<String, ConfigValue> entry : softDepends.entrySet()) {
                String skillId = entry.getKey().replaceAll("\"", "");
                String render = entry.getValue().render();
                int i = Integer.parseInt(render);
                SkillData skill = getSkillInfo(skillId, skillTree);
                info.getSoftDepends().add(new SkillDependency(skill, i));
                skill.getDepending().add(info);
            }
        } catch (ConfigException ignored) {
        }
        try {
            Config softDepends = c.getConfig("HardDepends");
            for (Map.Entry<String, ConfigValue> entry : softDepends.entrySet()) {
                String skillId = entry.getKey();
                String render = entry.getValue().render();
                int i = Integer.parseInt(render);
                SkillData skill = getSkillInfo(skillId, skillTree);
                info.getHardDepends().add(new SkillDependency(skill, i));
                skill.getDepending().add(info);
            }
        } catch (ConfigException ignored) {
        } catch (NumberFormatException e) {
            Log.error("Failed to load Skill HardDependencies", e);
        }

        try {
            info.setSkillTreeId(c.getInt("SkillTreeId"));
            skillTree.addSkillTreeId(info);
        } catch (ConfigException ignored) {
            info(" - Skill " + info.getSkillId() + " missing SkillTreeId, it wont be possible to reference this skill in the ascii map");
        }

        String id = info.getSkill().getId();
        try {
            info.setSkillName(c.getString("Name"));
            info(" - Alternate name defined for skill " + id + " > " + info.getSkillName());
            Rpg.get().getSkillService().registerSkillAlternateName(info.getSkillName(), info.getSkill());
        } catch (ConfigException missing) {
            info.setSkillName(id);
            warn(" - No alternate name defined for skill " + id + " !!falling back to default!! > " + info.getSkillName());
        }


        try {
            List<? extends Config> upg = c.getConfigList("Upgrades");
            for (Config config : upg) {
                if (config.hasPath("SkillId")) {
                    if (config.hasPath("SkillSettings")) {
                        String skillId = config.getString("SkillId");
                        Config cs = config.getConfig("SkillSettings");

                        SkillData skillInfo = getSkillInfo(skillId, skillTree);

                        info.getUpgradedBy().add(skillInfo);

                        SkillSettings skillSettings = new SkillSettings();
                        loadSkillSettings(skillSettings, cs);
                        skillInfo.setUpgradeSkillSettings(skillSettings);
                        skillInfo.getUpgrades().put(info.getSkillId(), skillSettings);

                        continue;
                    }
                    Log.warn("Skill Upgrade for " + info.getSkillName() + " missing SkillSettings, skipping");
                    continue;
                }
                Log.warn("Skill Upgrade for " + info.getSkillName() + " missing SkillId, skipping");
            }
        } catch (ConfigException missing) {}

        try {
            List<? extends ConfigObject> preprocessors = c.getObjectList("CastConditions");

            Map<String, SkillCastCondition> map = new HashMap<>();
            info.setSkillCastConditions(map);
            for (ConfigObject preprocessor : preprocessors) {
                loadCastConditions(preprocessor, map);
            }
        } catch (ConfigException e) {

        }

        SkillSettings skillSettings = new SkillSettings();
        try {
            Config settings = c.getConfig("SkillSettings");
            loadSkillSettings(skillSettings, settings);
            addRequiredIfMissing(skillSettings);
        } catch (ConfigException ignored) {
            warn(" - missing SkillSettings section " + info.getSkillId());
        }
        info.setSkillSettings(skillSettings);

        SkillSettings defaultSkillSettings = info.getSkill().getDefaultSkillSettings();
        if (defaultSkillSettings != null && defaultSkillSettings.getNodes() != null) {
            Iterator<Map.Entry<String, String>> iterator = defaultSkillSettings.getNodes().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                String value = next.getValue();
                String key = next.getKey();
                if (!skillSettings.hasNode(key)) {
                    String val2 = defaultSkillSettings.getNodes().get(key);
                    skillSettings.addExpression(key, value);
                    warn(" - Missing settings node " + key + " for a skill " + info.getSkillId() + " - inherited from default: " + value + " / " + val2);
                }
            }
        }


        SkillLoadingErrors errors = new SkillLoadingErrors(skillTree.getId());
        try {
            info.getSkill().loadSkillData(info, skillTree, errors, c);
        } catch (ConfigException e) {

        }
        for (String s : errors.getErrors()) {
            info(s);
        }


    }

    private void loadSkillSettings(SkillSettings skillSettings, Config settings) {
        for (Map.Entry<String, ConfigValue> e : settings.entrySet()) {
            ConfigValue value = e.getValue();
            String val = value.unwrapped().toString();
            skillSettings.addExpression(e.getKey(), val);
        }
    }

    //todo
    private ConfigObject loadCastConditions(ConfigObject preprocessor, Map<String, SkillCastCondition> map) {
        return null;
    }


    private void addRequiredIfMissing(SkillSettings skillSettings) {

        if (!skillSettings.hasNode(SkillNodes.HPCOST)) {
            skillSettings.addNode(SkillNodes.HPCOST, 0);
        }
        if (!skillSettings.hasNode(SkillNodes.MANACOST)) {
            skillSettings.addNode(SkillNodes.MANACOST, 0);
        }
        if (!skillSettings.hasNode(SkillNodes.COOLDOWN)) {
            skillSettings.addNode(SkillNodes.COOLDOWN, 0);
        }
    }

    private SkillData getSkillInfo(String id, SkillTree tree) {
        final String lowercased = id.toLowerCase();
        SkillData info = tree.getSkills().get(lowercased.replaceAll("\"", ""));
        if (info == null) {
            info = createSkillInfo(tree, lowercased);
            tree.addSkill(info);
        }
        return info;
    }

    private SkillData createSkillInfo(SkillTree tree, String lowercased) {

        ISkill skill = Rpg.get().getSkillService().getSkills().get(lowercased.replaceAll("\"",""));
        if (skill == null) {
            throw new InvalidSkillTreeException("Could not find a skill " + lowercased + " referenced in the skilltree " + tree.getId());
        }
        SkillData info = constructSkillData(skill);
        info.setSkill(skill);
        return info;
    }

    private SkillData constructSkillData(ISkill skill) {
        SkillData info = skill.constructSkillData();
        return info;
    }

    private class InvalidSkillTreeException extends RuntimeException {
        public InvalidSkillTreeException(String s) {
            super(s);
        }
    }
}
