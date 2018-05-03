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
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.inventory.ConfigRPGItemType;
import cz.neumimto.rpg.inventory.ItemService;
import cz.neumimto.rpg.inventory.RPGItemType;
import cz.neumimto.rpg.players.ExperienceSource;
import cz.neumimto.rpg.players.groups.*;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.skills.SkillTree;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Optional;

/**
 * Created by NeumimTo on 10.7.2015.
 */
@Singleton
public class GroupDao {

	@Inject
	PropertyService propertyService;

	@Inject
	EffectService effectService;

	@Inject
	Game game;

	@Inject
	Logger logger;

	@Inject
	SkillService skillService;

	@Inject
	ItemService itemService;

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
				logger.info("Loading file: " + p.getFileName().toString());
				Config c = ConfigFactory.parseFile(p.toFile());
				ConfigClass configClass = new ConfigClass(c.getString("Name"));
				loadPlayerGroup(c, configClass);
				long k = classes.values().stream().filter(ConfigClass::isDefaultClass).count();
				try {
					boolean aDefault = c.getBoolean("default");
					if (aDefault) {
						if (k == 0) {
							configClass.setDefaultClass(aDefault);
						} else {
							logger.warn("One default class already loaded, class \"" + configClass.getName() + "\" will be ignored");
						}
					}
				} catch (ConfigException e) {

				}

				try {
					SkillTree skillTree = skillService.getSkillTrees().get(c.getString("SkillTree"));
					if (skillTree == null) {
						logger.warn(" - Unknown \"SkillTree\", setting to default value");
						skillTree = SkillTree.Default;
					}
					configClass.setSkillTree(skillTree);
				} catch (ConfigException e) {
					configClass.setSkillTree(SkillTree.Default);
					logger.warn(" - Missing configuration \"SkillTree\", setting to default value");
				}

				try {
					List<String> experienceSources = c.getStringList("ExperienceSources");
					HashSet<ExperienceSource> objects = new HashSet<>();
					experienceSources.forEach(a -> objects.add(ExperienceSource.valueOf(a.toUpperCase())));
					configClass.setExperienceSources(objects);
				} catch (ConfigException e) {
					logger.warn(" - Missing configuration \"ExperienceSources\", skipping");
				}

				try {
					configClass.setSkillpointsperlevel(c.getInt("SkillPointsPerLevel"));
				} catch (ConfigException e) {
					logger.warn(" - Missing configuration \"SkillPointsPerLevel\", skipping");
				}

				try {
					configClass.setAttributepointsperlevel(c.getInt("AttributePointsPerLevel"));
				} catch (ConfigException e) {
					logger.warn(" - Missing configuration \"AttributePointsPerLevel\", skipping");
				}

				try {
					int maxLevel = c.getInt("MaxLevel");
					double first = c.getDouble("ExpFirstLevel");
					double last = c.getDouble("ExpLastLevel");
					initLevelCurve(configClass, maxLevel, first, last);
				} catch (ConfigException e) {
					logger.error(" - Missing one of configuration nodes \"MaxLevel\", \"ExpFirstLevel\", \"ExpLastLevel\"");
					initLevelCurve(configClass, 2, 1, 2); //just some not null values which might cause npes later
				}

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
				logger.info("Loading file: " + p.getFileName().toString());
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
		try {
			group.setShowsInMenu(c.getBoolean("Wildcard"));
		} catch (ConfigException e) {
			group.setShowsInMenu(true);
			logger.warn(" - Missing configuration \"Wildcard\", setting to true");
		}

		int id = 0;
		float bonus;
		Config prop;
		Set<Map.Entry<String, ConfigValue>> set;
		try {
			prop = c.getConfig("BonusProperties");
			set = prop.entrySet();

			for (Map.Entry<String, ConfigValue> m : set) {
				try {
					id = propertyService.getIdByName(m.getKey());
					bonus = Float.parseFloat(m.getValue().render());
					group.getPropBonus().put(id, bonus);
				} catch (NullPointerException e) {
					logger.error("Unknown property name \"" + m.getKey() + "\", check out your file properties_dump.info");
				}
			}
		} catch (ConfigException e) {
			logger.warn(" - Missing configuration \"BonusProperties\", skipping");
		}

		try {
			Config propl = c.getConfig("BonusPropertiesPerLevel");
			Set<Map.Entry<String, ConfigValue>> setl = propl.entrySet();
			for (Map.Entry<String, ConfigValue> m : setl) {
				try {
					id = propertyService.getIdByName(m.getKey());
					bonus = Float.parseFloat(m.getValue().render());
					group.getPropLevelBonus().put(id, bonus);
				} catch (NullPointerException e) {
					logger.error("Unknown property name \"" + m.getKey() + "\", check out your file properties_dump.info");
				}
			}
		} catch (ConfigException e) {
			logger.warn(" - Missing configuration \"BonusPropertiesPerLevel\", skipping");

		}

		try {
			List<String> list = c.getStringList("AllowedArmor");
			list.stream().forEach(a -> {
				String[] k = a.split(";");
				Optional<ItemType> type = game.getRegistry().getType(ItemType.class, k[0]);
				if (type.isPresent()) {
					String w = k.length == 1 ? null : k[0];
					RPGItemType rpgitemType = itemService.getByItemTypeAndName(type.get(), w);
					group.getAllowedArmor().add(rpgitemType);
				} else logger.warn("Defined invalid itemtype  " + a + " in " + group.getName());
			});
		} catch (ConfigException e) {
			logger.warn(" - Missing configuration \"AllowedArmor\", skipping");
		}

		try {
			List<String> allowedWeapons = c.getStringList("AllowedWeapons");
			for (String allowedWeapon : allowedWeapons) {
				String[] split = allowedWeapon.split(";");
				String s = split[0];
				double damage = 0;
				String itemName = null;

				ItemType type = game.getRegistry().getType(ItemType.class, s).orElse(null);
				if (type == null) {
					logger.error(" - Unknown item type " + s);
				} else {
					String s1 = split[1];
					damage = Double.parseDouble(s1);
					if (split.length == 3) {
						itemName = split[2];
					}
				}
				RPGItemType rpgitemType = itemService.getByItemTypeAndName(type, itemName);
				ConfigRPGItemType t = new ConfigRPGItemType(rpgitemType, group, damage);
				group.addWeapon(t);
			}

		} catch (ConfigException e) {
			logger.warn(" - Missing configuration \"AllowedWeapons\", skipping");
		}

		try {
			prop = c.getConfig("ProjectileDamage");
			set = prop.entrySet();
			for (Map.Entry<String, ConfigValue> m : set) {
				if (m.getKey().equalsIgnoreCase("arrow") || m.getKey().equalsIgnoreCase("minecraft:arrow")) {
					group.getProjectileDamage().put(EntityTypes.SPECTRAL_ARROW, Double.parseDouble(m.getValue().render()));
					group.getProjectileDamage().put(EntityTypes.TIPPED_ARROW, Double.parseDouble(m.getValue().render()));
				} else {
					Optional<EntityType> type = game.getRegistry().getType(EntityType.class, m.getKey());
					if (type.isPresent()) {
						group.getProjectileDamage().put(type.get(), Double.parseDouble(m.getValue().render()));
					} else logger.warn("Defined invalid projectile type  " + m.getKey() + " in " + group.getName());
				}
			}
		} catch (ConfigException e) {

		}

		try {
			prop = c.getConfig("Attributes");
			set = prop.entrySet();
			for (Map.Entry<String, ConfigValue> entry : set) {
				String attribute = entry.getKey();
				int i = Integer.parseInt(entry.getValue().render());
				ICharacterAttribute attribute1 = propertyService.getAttribute(attribute);
				group.getStartingAttributes().put(attribute1, i);
			}
		} catch (ConfigException e) {
			logger.warn(" - Missing configuration \"Attributes\", skipping");
		}

		try {
			Optional<ItemType> menuIcon = game.getRegistry().getType(ItemType.class, c.getString("MenuIcon"));
			if (menuIcon.isPresent()) {
				group.setItemType(menuIcon.get());
			} else {
				logger.warn(" - Unknown item type in \"MenuIcon\", setting STONE as default");
				group.setItemType(ItemTypes.STONE);
			}
		} catch (ConfigException e) {
			logger.warn(" - Missing configuration \"MenuIcon\", setting STONE as default");
			group.setItemType(ItemTypes.STONE);
		}

		try {
			group.setDescription(c.getString("Description"));
		} catch (ConfigException e) {
			group.setDescription("");
			logger.warn(" - Missing configuration \"Description\", setting an empty string as default");

		}

		try {
			String color = c.getString("color");
			Optional<TextColor> type = Sponge.getRegistry().getType(TextColor.class, color);
			if (type.isPresent()) {
				group.setPreferedColor(type.get());
			} else {
				group.setPreferedColor(TextColors.WHITE);
			}
		} catch (ConfigException e) {
			group.setPreferedColor(TextColors.WHITE);
		}

		try {
			Config commands = c.getConfig("Commands");
			try {
				List<String> enter = commands.getStringList("enter");
				group.setEnterCommands(enter);
			} catch (ConfigException e) {
				group.setEnterCommands(new ArrayList<>());
				logger.warn(" - Missing configuration \"Commands.enter\", skipping");
			}

			try {
				List<String> exit = commands.getStringList("exit");
				group.setExitCommands(exit);
			} catch (ConfigException e) {
				group.setExitCommands(new ArrayList<>());
				logger.warn(" - Missing configuration \"Commands.exit\", skipping");
			}
		} catch (ConfigException e) {
			logger.warn(" - Missing configuration \"Commands\", skipping");
		}

		try {
			List<? extends Config> permissions = c.getConfigList("Permissions");
			for (Config permission : permissions) {
				group.getPermissions().add(ConfigBeanFactory.create(permission, PlayerGroupPermission.class));
			}
		} catch (ConfigException e) {
			group.setPermissions(new HashSet<>());
			logger.warn(" - Missing configuration \"Permissions\", skipping");
		}

		try {


			Config effects = c.getConfig("Effects");
			for (Map.Entry<String, ConfigValue> entry : effects.root().entrySet()) {
					String effectName = entry.getKey();
					ConfigValueType type = entry.getValue().valueType();
					EffectParams value = new EffectParams();
					IGlobalEffect globalEffect = effectService.getGlobalEffect(effectName);
					switch (type) {
						case NULL:
							break;
						case STRING:
							value.put(effectName, entry.getValue().render());
							break;
						case OBJECT:
							ConfigObject object = (ConfigObject) entry.getValue();
							Map<String, Object> unwrapped1 = object.unwrapped();
							for (Map.Entry<String, Object> stringObjectEntry : unwrapped1.entrySet()) {
								value.put(stringObjectEntry.getKey(), stringObjectEntry.getValue() != null ? stringObjectEntry.getValue().toString() : null);
							}

					}
					group.getEffects().put(globalEffect, value);
			}
		} catch (ConfigException e) {
			logger.warn(" - Missing configuration \"Effects\", skipping");

		}

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