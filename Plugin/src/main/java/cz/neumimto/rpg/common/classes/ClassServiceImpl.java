package cz.neumimto.rpg.common.classes;

import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassPermission;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.common.persistance.dao.ClassDefinitionDao;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class ClassServiceImpl implements ClassService {

    private static final String CLASS_ACCESS_PERM = "ntrpg.class.";

    @Inject
    private DamageService damageService;

    @Inject
    private ClassDefinitionDao classDefinitionDao;

    @Inject
    private PermissionService permissionService;

    private Map<String, ClassDefinition> classes = new HashMap<>();

    @Override
    public Map<String, ClassDefinition> getClasses() {
        return classes;
    }

    @Override
    public ClassDefinition getClassDefinitionByName(String name) {
        if (name == null) {
            return null;
        }
        return getClasses().get(name.toLowerCase());
    }

    @Override
    public void registerClassDefinition(ClassDefinition classDefinition) {
        classes.put(classDefinition.getName().toLowerCase(), classDefinition);
    }

    @Override
    public boolean existsClass(String s) {
        return getClasses().containsKey(s.toLowerCase());
    }

    @Override
    public Collection<ClassDefinition> getClassDefinitions() {
        return getClasses().values();
    }

    @Override
    public Set<ClassDefinition> filterByPlayerAndType(IActiveCharacter player, String type) {
        Set<ClassDefinition> defs = new HashSet<>();
        for (Map.Entry<String, ClassDefinition> entry : getClasses().entrySet()) {
            ClassDefinition value = entry.getValue();
            if (value.getClassType().equalsIgnoreCase(type)) {
                if (permissionService.hasPermission(player, CLASS_ACCESS_PERM + value.getName().toLowerCase())) {
                    defs.add(value);
                }
            }
        }
        return defs;
    }

    @Override
    public Set<String> getPermissionsToRemove(IActiveCharacter character, ClassDefinition toBeReplaced) {
        Set<String> intersection = new HashSet<>();

        Set<String> toBeRemoved = new HashSet<>();
        Map<String, PlayerClassData> map = character.getClasses();
        for (PlayerClassData nClass : map.values()) {
            ClassDefinition configClass = nClass.getClassDefinition();
            if (configClass == toBeReplaced) {
                for (PlayerClassPermission pgp : configClass.getPermissions()) {
                    if (pgp.getLevel() <= character.getLevel()) {
                        toBeRemoved.addAll(pgp.getPermissions());
                    }
                }
            } else {
                for (PlayerClassPermission playerClassPermission : configClass.getPermissions()) {
                    if (playerClassPermission.getLevel() <= character.getLevel()) {
                        intersection.addAll(playerClassPermission.getPermissions());
                    }
                }
            }
        }


        intersection.removeIf(next -> !toBeRemoved.contains(next));

        toBeRemoved.removeAll(intersection);
        return toBeRemoved;
    }

    @Override
    public void loadClasses() {
        try {
            Set<ClassDefinition> classDefinitions = classDefinitionDao.parseClassFiles();
            classes.clear();

            classDefinitions.forEach(this::registerClassDefinition);

            for (ClassDefinition result : classDefinitions) {
                Map<String, ClassDefinition> classes = getClasses();
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
