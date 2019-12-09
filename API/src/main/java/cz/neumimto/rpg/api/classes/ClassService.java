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

package cz.neumimto.rpg.api.classes;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;

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

}
