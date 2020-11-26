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

package cz.neumimto.rpg.common.persistance.dao;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.NoFormatFoundException;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.api.configuration.adapters.ClassDependencyGraphAdapter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.leveling.EmptyLevelProgression;
import cz.neumimto.rpg.api.logging.Log;

import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.api.logging.Log.error;
import static cz.neumimto.rpg.api.logging.Log.info;

/**
 * Created by NeumimTo on 10.7.2015.
 */
@Singleton
public class ClassDefinitionDao {

    public Path getClassDirectory() {
        Path path = Paths.get(Rpg.get().getWorkingDirectory(), "classes");
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            Log.error("Could not create classes directory " + e.getLocalizedMessage());
        }
        return path;
    }



    public Set<ClassDefinition> parseClassFiles(Path path) {
        Set<ClassDefinition> set = new HashSet<>();
        try {
            Map<String, Path> stringPathMap = preloadClassDefs(path, set);
            for (Map.Entry<String, Path> stringPathEntry : stringPathMap.entrySet()) {
                String key = stringPathEntry.getKey();
                Path p = stringPathEntry.getValue();
                try (FileConfig of = FileConfig.of(p)) {
                    of.load();
                    info("Loading class definition file " + p.getFileName());

                    ClassDefinition result = null;
                    for (ClassDefinition classDefinition : set) {
                        if (classDefinition.getName().equalsIgnoreCase(key)) {
                            result = new ObjectConverter().toObject(of, () -> classDefinition);
                            break;
                        }
                    }

                    if (result.getLevelProgression() != null && !(result.getLevelProgression() instanceof EmptyLevelProgression)) {
                        result.getLevelProgression().setLevelMargins(result.getLevelProgression().initCurve());
                    }
                    Set<String> expU = result.getExperienceSource().stream().map(String::toUpperCase).collect(Collectors.toSet());

                    result.setExperienceSources(expU);
                    set.add(result);


                }
            }
        } catch (IOException e) {
            error("Could not read class file: ", e);
        }
        return set;
    }

    //because of dependency graph
    private Map<String, Path> preloadClassDefs(Path path, Set<ClassDefinition> set) throws IOException {
        Map<String, Path> map = new HashMap<>();

        Files.walk(path)
                .filter(f -> Files.isRegularFile(f) || (Files.isSymbolicLink(f) && ! Files.isDirectory(f)))
                .forEach(p -> {
                    info("Preloading class definition file " + p.getFileName());

                    ClassDefinition classDefinition = null;
                    try (FileConfig fileConfig = FileConfig.of(p)) {
                        fileConfig.load();
                        preloadClassConfig(set, map, p, fileConfig);
                    } catch (NoFormatFoundException e) {
                        error(" - File malformed", e);
                    }
                });

        Files.walk(path)
                .filter(Files::isRegularFile)
                .forEach(p -> {
                    try (FileConfig fileConfig = FileConfig.of(p)) {
                        fileConfig.load();
                        preloadClassDependencyGraphConfig(set, p, fileConfig);
                    }
                });


        return map;
    }

    private void preloadClassDependencyGraphConfig(Set<ClassDefinition> set, Path p, FileConfig fileConfig) {
        if (fileConfig.contains("Name") && fileConfig.contains("ClassType") && fileConfig.contains("Dependencies")) {
            info("Preloading class dependency graph file " + p.getFileName());
            for (ClassDefinition classDefinition : set) {
                if (classDefinition.getName().equals(fileConfig.get("Name"))) {
                    ClassDependencyGraphAdapter.load(fileConfig.get("Dependencies"), classDefinition, set);
                }
            }
        }
    }

    public void preloadClassConfig(Set<ClassDefinition> set, Map<String, Path> map, Path p, Config fileConfig) {
        ClassDefinition classDefinition;
        if (!fileConfig.contains("Name") && !fileConfig.contains("ClassType")) {
            error(" - Nodes Name and ClassType are mandatory");
        } else {
            String name = fileConfig.get("Name");
            String classType = fileConfig.get("ClassType");
            Map<String, ClassTypeDefinition> types = Rpg.get().getPluginConfig().CLASS_TYPES;
            ClassTypeDefinition classTypeDefinition = null;

            for (Map.Entry<String, ClassTypeDefinition> e : types.entrySet()) {
                if (e.getKey().equalsIgnoreCase(classType)) {
                    classType = e.getKey();
                    classTypeDefinition = e.getValue();
                    break;
                }
            }

            if (classTypeDefinition == null) {
                error(" - Unknown ClassType " + classType + "; Allowed Class Types: " + String.join(", ", types.keySet()));
            } else {
                classDefinition = new ClassDefinition(name, classType);
                map.put(name, p);
                set.add(classDefinition);
            }
        }
    }
}