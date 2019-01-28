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

import cz.neumimto.config.blackjack.and.hookers.NotSoStupidObjectMapper;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.inventory.ItemService;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.skills.SkillService;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static cz.neumimto.rpg.Log.info;

/**
 * Created by NeumimTo on 10.7.2015.
 */
@Singleton
public class ClassDefinitionDao {

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

        try {
            Map<String, Path> stringPathMap = preloadClassDefs(path);
            final ObjectMapper<ClassDefinition> mapper = NotSoStupidObjectMapper.forClass(ClassDefinition.class);
            for (Map.Entry<String, Path> stringPathEntry : stringPathMap.entrySet()) {
                String key = stringPathEntry.getKey();
                Path p = stringPathEntry.getValue();

                try {
                    info("Loading class definition file " + p.getFileName());
                    HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(p).build();
                    ClassDefinition result = mapper.bind(classes.get(key)).populate(hcl.load());
                    classes.put(key, result);
                    if (result.getLevelProgression() != null) {
                        result.getLevelProgression().setLevelMargins(result.getLevelProgression().initCurve());
                    }
                } catch (ObjectMappingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    //because of dependency graph
    public Map<String, Path> preloadClassDefs(Path path) throws IOException {
        Map<String, Path> map = new HashMap<>();
        Files.walk(path)
                .filter(Files::isRegularFile)
                .forEach(p -> {
                    info("Preloading class definition file " + p.getFileName());
                    HoconConfigurationLoader build = HoconConfigurationLoader.builder().setPath(p).build();
                    try {
                        CommentedConfigurationNode load = build.load();
                        String name = (String) load.getNode("Name").getValue();
                        ClassDefinition classDefinition = new ClassDefinition();
                        map.put(name, p);
                        classes.put(name, classDefinition);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        return map;
    }
}