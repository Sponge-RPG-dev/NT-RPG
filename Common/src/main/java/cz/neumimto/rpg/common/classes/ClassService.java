

package cz.neumimto.rpg.common.classes;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by NeumimTo on 28.12.2014.
 */
public interface ClassService {

    void load();

    Map<String, ClassDefinition> getClasses();

    ClassDefinition getClassDefinitionByName(String name);

    void registerClassDefinition(ClassDefinition classDefinition);

    boolean existsClass(String s);

    Collection<ClassDefinition> getClassDefinitions();

    Set<ClassDefinition> filterByPlayerAndType(IActiveCharacter player, String type);

    Set<String> getPermissionsToRemove(IActiveCharacter character, ClassDefinition toBeReplaced);

    boolean isClassDirEmpty();
}
