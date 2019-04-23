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
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static cz.neumimto.rpg.api.logging.Log.error;
import static cz.neumimto.rpg.api.logging.Log.info;

/**
 * Created by NeumimTo on 10.7.2015.
 */
@Singleton
public class ClassDefinitionDao {

    public Set<ClassDefinition> parseClassFiles() throws ObjectMappingException {
        Path path = ResourceLoader.classDir.toPath();
        Set<ClassDefinition> set = new HashSet<>();
        try {
            Map<String, Path> stringPathMap = preloadClassDefs(path, set);
            final ObjectMapper<ClassDefinition> mapper = NotSoStupidObjectMapper.forClass(ClassDefinition.class);
            for (Map.Entry<String, Path> stringPathEntry : stringPathMap.entrySet()) {
                String key = stringPathEntry.getKey();
                Path p = stringPathEntry.getValue();

                try {
                    info("Loading class definition file " + p.getFileName());
                    HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(p).build();
                    ClassDefinition definition = null;
                    for (ClassDefinition classDefinition : set) {
                        if (classDefinition.getName().equalsIgnoreCase(key)) {
                            definition = classDefinition;
                            break;
                        }
                    }

                    ClassDefinition result = mapper.bind(definition).populate(hcl.load());

                    if (result.getLevelProgression() != null) {
                        result.getLevelProgression().setLevelMargins(result.getLevelProgression().initCurve());
                    }
                    set.add(result);
                } catch (Exception e) {
                    error("Could not read class file: ", e);
                    throw new ObjectMappingException(e);
                }

            }
        } catch (IOException e) {
            error("Could not read class file: ", e);
            throw new ObjectMappingException(e);
        }
        return set;
    }

    //because of dependency graph
    private Map<String, Path> preloadClassDefs(Path path, Set<ClassDefinition> set) throws IOException {
        Map<String, Path> map = new HashMap<>();
        Files.walk(path)
                .filter(Files::isRegularFile)
                .forEach(p -> {
                    info("Preloading class definition file " + p.getFileName());
                    HoconConfigurationLoader build = HoconConfigurationLoader.builder().setPath(p).build();
                    ClassDefinition classDefinition = null;
                    try {
                        CommentedConfigurationNode load = build.load();
                        if (!load.getChildrenMap().containsKey("Name") && !load.getChildrenMap().containsKey("ClassType")) {
                            error(" - Nodes Name and ClassType are mandatory");
                        } else {
                            String name = (String) load.getNode("Name").getValue();
                            String classType = (String) load.getNode("ClassType").getValue();
                            Map<String, ClassTypeDefinition> types = NtRpgPlugin.pluginConfig.CLASS_TYPES;
                            ClassTypeDefinition classTypeDefinition = null;

                            for (Map.Entry<String, ClassTypeDefinition> e : types.entrySet()) {
                                if (e.getKey().equalsIgnoreCase(classType)) {
                                    classType = e.getKey();
                                    classTypeDefinition = e.getValue();
                                    break;
                                }
                            }

                            if (classTypeDefinition == null) {
                                error(" - Unknown ClassType; Allowed Class Types: " + String.join(", ", types.keySet()));
                            } else {
                                classDefinition = new ClassDefinition(name, classType);
                                map.put(name, p);
                                set.add(classDefinition);
                            }
                        }
                    } catch (IOException e) {
                        error(" - File malformed", e);
                    }
                });
        return map;
    }
}