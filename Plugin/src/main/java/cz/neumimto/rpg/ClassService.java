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

package cz.neumimto.rpg;

import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.damage.SpongeDamageService;
import cz.neumimto.rpg.common.persistance.dao.ClassDefinitionDao;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.players.groups.PlayerGroupPermission;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.util.Tristate;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

/**
 * Created by NeumimTo on 28.12.2014.
 */
@Singleton
public class ClassService {

    private static final String CLASS_ACCESS_PERM = "ntrpg.class.";

    @Inject
    private DamageService spongeDamageService;

    @Inject
    private ClassDefinitionDao classDefinitionDao;

    private Map<String, ClassDefinition> classes = new HashMap<>();

    public Map<String, ClassDefinition> getClasses() {
        return classes;
    }

    public ClassDefinition getClassDefinitionByName(String name) {
        if (name == null) {
            return null;
        }
        return getClasses().get(name.toLowerCase());
    }

    public void registerPlaceholders() {
        spongeDamageService.createDamageToColorMapping();
    }

    public boolean existsClass(String s) {
        return getClasses().containsKey(s.toLowerCase());
    }

    public Collection<ClassDefinition> getClassDefinitions() {
        return getClasses().values();
    }

    public Set<ClassDefinition> filterByPlayerAndType(Player player, String type) {
        Set<ClassDefinition> defs = new HashSet<>();
        for (Map.Entry<String, ClassDefinition> entry : getClasses().entrySet()) {
            ClassDefinition value = entry.getValue();
            if (value.getClassType().equalsIgnoreCase(type)) {
                if (player.hasPermission(CLASS_ACCESS_PERM + value.getName().toLowerCase())) {
                    defs.add(value);
                }
            }
        }
        return defs;
    }

    public Set<String> getPermissionsToRemove(IActiveCharacter character, ClassDefinition toBeReplaced) {
        Set<String> intersection = new HashSet<>();

        Set<String> toBeRemoved = new HashSet<>();

        for (PlayerClassData nClass : character.getClasses().values()) {
            ClassDefinition configClass = nClass.getClassDefinition();
            if (configClass == toBeReplaced) {
                for (PlayerGroupPermission pgp : configClass.getPermissions()) {
                    if (pgp.getLevel() <= character.getLevel()) {
                        toBeRemoved.addAll(pgp.getPermissions());
                    }
                }
            } else {
                for (PlayerGroupPermission playerGroupPermission : configClass.getPermissions()) {
                    if (playerGroupPermission.getLevel() <= character.getLevel()) {
                        intersection.addAll(playerGroupPermission.getPermissions());
                    }
                }
            }
        }


        intersection.removeIf(next -> !toBeRemoved.contains(next));

        toBeRemoved.removeAll(intersection);
        return toBeRemoved;
    }

    public void removePermissions(IActiveCharacter character, Set<String> perms) {
        SubjectData transientSubjectData = character.getPlayer().getTransientSubjectData();
        for (String perm : perms) {
            transientSubjectData.setPermission(SubjectData.GLOBAL_CONTEXT, perm, Tristate.UNDEFINED);
        }
    }


    public void addPermissions(IActiveCharacter character, Set<String> perms) {
        SubjectData transientSubjectData = character.getPlayer().getTransientSubjectData();
        for (String perm : perms) {
            transientSubjectData.setPermission(SubjectData.GLOBAL_CONTEXT, perm, Tristate.TRUE);
        }
    }

    public void addAllPermissions(IActiveCharacter character, PlayerClassData classDefinition) {
        for (PlayerGroupPermission playerGroupPermission : classDefinition.getClassDefinition().getPermissions()) {
            if (playerGroupPermission.getLevel() <= classDefinition.getLevel()) {
                addPermissions(character, playerGroupPermission.getPermissions());
            }
        }
    }

    public void addPermissions(IActiveCharacter character, PlayerClassData classDefinition) {
        for (PlayerGroupPermission playerGroupPermission : classDefinition.getClassDefinition().getPermissions()) {
            if (playerGroupPermission.getLevel() == classDefinition.getLevel()) {
                addPermissions(character, playerGroupPermission.getPermissions());
            }
        }
    }

    public void loadClasses() {
        try {
            Set<ClassDefinition> classDefinitions = classDefinitionDao.parseClassFiles();
            classes.clear();

            classDefinitions.forEach(a -> classes.put(a.getName().toLowerCase(), a));

            for (ClassDefinition result : classDefinitions) {
                Map<String, ClassDefinition> classes = NtRpgPlugin.GlobalScope.classService.getClasses();
                for (ClassDefinition classDefinition : classes.values()) {
                    if (classDefinition.getName().equalsIgnoreCase(result.getName())) {
                        continue;
                    }
                    if (classDefinition.getClassType().equalsIgnoreCase(result.getClassType())) {
                        result.getClassDependencyGraph().getConflicts().add(classDefinition);
                    }
                }

            }

            Log.info("Successfully loaded " + classes.size() + " classes");

        } catch (ObjectMappingException e) {
            Log.error("Could not load classes, ", e);
        }
    }
}
