package cz.neumimto.rpg.common.classes;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.permissions.PermissionService;
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
public class ClassService {

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

    public Map<String, ClassDefinition> getClasses() {
        return classes;
    }

    public ClassDefinition getClassDefinitionByName(String name) {
        if (name == null) {
            return null;
        }
        return getClasses().get(name.toLowerCase());
    }

    public void registerClassDefinition(ClassDefinition classDefinition) {
        classes.put(classDefinition.getName().toLowerCase(), classDefinition);
    }

    public boolean existsClass(String s) {
        return getClasses().containsKey(s.toLowerCase());
    }

    public Collection<ClassDefinition> getClassDefinitions() {
        return getClasses().values();
    }

    public Set<ClassDefinition> filterByPlayerAndType(ActiveCharacter player, String type) {
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

    public void load() {
        Path loadFrom = null;
        if (isClassDirEmpty()) {
            Log.info("No classes found in classes folder, loading classes from within ntrpg.jar");
            loadFrom = assetService.getTempWorkingDir();
            assetService.copyDefaultClasses(loadFrom);
            loadFrom = loadFrom.resolve("classes/");
        } else {
            loadFrom = classDefinitionDao.getClassDirectory();
        }
        Set<ClassDefinition> classDefinitions = classDefinitionDao.parseClassFiles(loadFrom);
        classes.clear();
        classDefinitions.forEach(this::registerClassDefinition);

        Log.info("Successfully loaded " + classes.size() + " classes");
    }

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
