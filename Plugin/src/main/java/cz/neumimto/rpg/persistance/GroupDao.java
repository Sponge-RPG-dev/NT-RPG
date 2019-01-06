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

import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static cz.neumimto.rpg.Log.info;

/**
 * Created by NeumimTo on 10.7.2015.
 */
@Singleton
public class GroupDao {

    private Map<String, ClassDefinition> classes = new HashMap<>();

    public void loadClassDefinitions() {
        Path path = ResourceLoader.raceDir.toPath();
        try (Stream<Path> s = Files.walk(path, 50, FileVisitOption.FOLLOW_LINKS)) {
            final ObjectMapper<ClassDefinition> mapper = ObjectMapper.forClass(ClassDefinition.class);
            s.peek(a-> info("Loading class file file: " + a.getFileName().toString()))
                .forEach(a -> {
                        HoconConfigurationLoader hcl = HoconConfigurationLoader.builder()
                                .setPath(a).build();
                        try {
                            ClassDefinition classDefinition = mapper.bind(new ClassDefinition()).populate(hcl.load());
                            classes.put(classDefinition.getName(), classDefinition);
                        } catch (ObjectMappingException | IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
        } catch (IOException | ObjectMappingException e) {
            e.printStackTrace();
        }
        info("Loaded " + classes.size() + " Classes");
        //todo initialize class dependencies

    }

    public Map<String, ClassDefinition> getClasses() {
        return classes;
    }
}