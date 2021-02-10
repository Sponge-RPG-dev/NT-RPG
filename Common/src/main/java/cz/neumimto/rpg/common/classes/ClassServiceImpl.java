package cz.neumimto.rpg.common.classes;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassPermission;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.persistance.dao.ClassDefinitionDao;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Singleton
public class ClassServiceImpl implements ClassService {

    private static final String CLASS_ACCESS_PERM = "ntrpg.class.";

    @Inject
    private DamageService damageService;

    @Inject
    private ClassDefinitionDao classDefinitionDao;

    @Inject
    private PermissionService permissionService;

    @Inject
    private AssetService assetService;

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
    public void load() {
        Path loadFrom = null;
        if (isClassDirEmpty()) {
            Log.info("No classes found in classes folder, loading classes from within ntrpg.jar");
            loadFrom = assetService.getTempWorkingDir();
            assetService.copyDefaults(loadFrom);
        } else {
            loadFrom = classDefinitionDao.getClassDirectory();
        }
        Set<ClassDefinition> classDefinitions = classDefinitionDao.parseClassFiles(loadFrom);
        classes.clear();
        classDefinitions.forEach(this::registerClassDefinition);

        Log.info("Successfully loaded " + classes.size() + " classes");
    }

    @Override
    public boolean isClassDirEmpty() {
        Path path = Paths.get(Rpg.get().getWorkingDirectory(), "classes");
        if (!Files.exists(path)) {
            return true;
        }
        try {
            Stream<Path> pathStream = Files.find(path, Integer.MAX_VALUE, (p, bfa) -> bfa.isRegularFile() || bfa.isSymbolicLink());
            if (pathStream.count() == 0) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
