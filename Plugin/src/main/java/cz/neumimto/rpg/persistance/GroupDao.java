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
import cz.neumimto.config.blackjack.and.hookers.NotSoStupidObjectMapper;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.inventory.ItemService;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.skills.SkillService;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Game;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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
	SkillService skillService;

	@Inject
	ItemService itemService;

	private Map<String, ClassDefinition> classes = new HashMap<>();


	public Map<String, ClassDefinition> getClasses() {
		return classes;
	}


	public void loadClassDefs() {
		Path path = ResourceLoader.classDir.toPath();

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.conf")) {
			ObjectMapper<ClassDefinition> mapper = NotSoStupidObjectMapper.forClass(ClassDefinition.class);
			stream.forEach(p -> {
				Config c = ConfigFactory.parseFile(p.toFile());
				try {
					HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(p).build();
					ClassDefinition result = mapper.bind(new ClassDefinition()).populate(hcl.load());
					classes.put(result.getName(), result);
				} catch (ObjectMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
	}

}