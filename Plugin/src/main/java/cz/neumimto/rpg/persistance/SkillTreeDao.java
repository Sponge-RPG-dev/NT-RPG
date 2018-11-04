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

import static cz.neumimto.rpg.Log.error;
import static cz.neumimto.rpg.Log.info;
import static cz.neumimto.rpg.Log.warn;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.Pair;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.gui.SkillTreeInterfaceModel;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.SkillCost;
import cz.neumimto.rpg.skills.SkillData;
import cz.neumimto.rpg.skills.SkillItemCost;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.configs.SkillConfigLoader;
import cz.neumimto.rpg.skills.mods.ActiveSkillPreProcessorWrapper;
import cz.neumimto.rpg.skills.mods.SkillPreProcessorFactory;
import cz.neumimto.rpg.skills.parents.StartingPoint;
import cz.neumimto.rpg.skills.tree.SkillTree;
import cz.neumimto.rpg.skills.utils.SkillLoadingErrors;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
				info("Loading skilltree from a file " + path.getFileName());
				Config config = ConfigFactory.parseFile(path.toFile());
				SkillTree skillTree = new SkillTree();
				try {
					skillTree.setDescription(config.getString("Description"));
				} catch (ConfigException e) {
					skillTree.setDescription("");
					warn("Missing \"Description\" node");
				}
				try {
					skillTree.setId(config.getString("Name"));
				} catch (ConfigException e) {
					warn("Missing \"Name\" skipping to another file");
					return;
				}
				skillTree.getSkills().put(StartingPoint.name.toPlain(), StartingPoint.SKILL_DATA);
				try {
					List<? extends ConfigObject> skills = config.getObjectList("Skills");
					createConfigSkills(skills, skillTree);
					loadSkills(skills, skillTree);
				} catch (ConfigException e) {
					warn("Missing \"Skills\" section. No skills defined");

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
								SkillTreeInterfaceModel guiModelByCharacter = skillService.getGuiModelByCharacter(c1);
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
				map.put(skillTree.getId(), skillTree);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private void createConfigSkills(List<? extends ConfigObject> sub, SkillTree skillTree) {
		for (ConfigObject co : sub) {
			Config c = co.toConfig();
			String id = c.getString("SkillId");
			Optional<ISkill> byId = skillService.getById(id);
			if (!byId.isPresent()) {


				try {
					String type = c.getString("Type");
					SkillConfigLoader type1 = Sponge.getRegistry().getType(SkillConfigLoader.class, type)
							.orElseThrow(() -> new IllegalArgumentException("Unknown skill type " + type + " in a skiltree " + skillTree.getId()));

					type1.build(id);

				} catch (ConfigException.Missing ignored) {
				}
			} else {
			}
		}
	}

	private void loadSkills(List<? extends ConfigObject> sub, SkillTree skillTree) {
		for (ConfigObject co : sub) {

			Config c = co.toConfig();
			SkillData info = getSkillInfo(c.getString("SkillId"), skillTree);

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



			try {
				Config reagent = c.getConfig("Invoke-Cost");
				SkillCost itemCost = new SkillCost();
				info.setInvokeCost(itemCost);
				List<? extends ConfigObject> list = reagent.getObjectList("Items");

				for (ConfigObject configObject : list) {
					try {
						SkillItemCost q = new SkillItemCost();
						q.setAmount(Integer.parseInt(configObject.get("Amount").unwrapped().toString()));
						String type = configObject.get("Item-Type").unwrapped().toString();
						boolean consume = Boolean.valueOf(configObject.get("Consume").unwrapped().toString());
						q.setConsumeItems(consume);
						Optional<ItemType> type1 = Sponge.getRegistry().getType(ItemType.class, type);
						if (type1.isPresent()) {
							q.setItemType(type1.get());
							itemCost.getItemCost().add(q);
						} else {
							warn(" - Unknown ItemType " + type + " Defined in Invoke-Cost section for a skill " + info.getSkillId());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
                list = reagent.getObjectList("Insufficient");
                for (ConfigObject configObject : list) {
					Optional<SkillPreProcessorFactory> id = Sponge.getRegistry().getType(SkillPreProcessorFactory.class, configObject.get("Id").unwrapped().toString());
					if (id.isPresent()) {
						SkillPreProcessorFactory skillPreProcessorFactory = id.get();
						ActiveSkillPreProcessorWrapper parse = skillPreProcessorFactory.parse(configObject);
						itemCost.getInsufficientProcessors().add(parse);
					} else {
						warn("- Unknown processor type " + configObject.get("Id").render() + ", use one of: " + Sponge.getRegistry().getAllOf(SkillPreProcessorFactory.class)
								.stream().map(SkillPreProcessorFactory::getId).collect(Collectors.joining(", ")));
					}
                }
            } catch (Exception e) {

            }

			try {
				for (String conflicts : c.getStringList("Conflicts")) {
					info.getConflicts().add(getSkillInfo(conflicts, skillTree));
				}
			} catch (ConfigException ignored) {
			}

			try {
				for (String conflicts : c.getStringList("SoftDepends")) {
					SkillData i = getSkillInfo(conflicts, skillTree);
					info.getSoftDepends().add(i);
					i.getDepending().add(info);
				}
			} catch (ConfigException ignored) {
			}


			try {
				for (String conflicts : c.getStringList("HardDepends")) {
					SkillData i = getSkillInfo(conflicts, skillTree);
					info.getHardDepends().add(i);
					i.getDepending().add(info);
				}
			} catch (ConfigException ignored) {
			}

			try {
				info.setSkillTreeId(c.getInt("SkillTreeId"));
			} catch (ConfigException ignored) {
				info(" - Skill " + info.getSkillId() + " missing SkillTreeId, it wont be possible to reference this skill in the ascii map");
			}

			try {
				info.setSkillName(TextHelper.parse(c.getString("Name")));
				info(" - Alternate name defined for skill " + info.getSkill().getId() + " > " + info.getSkillName().toPlain());
				skillService.registerSkillAlternateName(info.getSkillName().toPlain(), info.getSkill());
			} catch (ConfigException missing) {
				info.setSkillName(info.getSkill().getLocalizableName());
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
						} catch (ConfigException ignored) {
						}
						skillSettings.addNode(name, bonus);
					} else {
						skillSettings.addObjectNode(e.getKey(), val);
					}
				}
				addRequiredIfMissing(skillSettings);
				info.setSkillSettings(skillSettings);
			} catch (ConfigException ignored) {
			}


			SkillLoadingErrors errors = new SkillLoadingErrors(skillTree.getId());
			try {
				info.getSkill().loadSkillData(info, skillTree, errors, c);
			} catch (ConfigException e) {

			}
			for (String s : errors.getErrors()) {
				info(s);
			}


			skillTree.getSkills().put(info.getSkillId().toLowerCase(), info);


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

	private SkillData getSkillInfo(String id, SkillTree tree) {
		final String lowercased = id.toLowerCase();
		SkillData info = tree.getSkills().get(lowercased);
		if (info == null) {
			ISkill skill = skillService.getById(lowercased)
					.orElseThrow(() -> new IllegalStateException("Could not find a skill " + lowercased + " referenced in the skilltree " + tree.getId()));

			info = skill.constructSkillData();
			info.setSkill(skill);
			tree.getSkills().put(lowercased, info);
		}
		return info;
	}
}
