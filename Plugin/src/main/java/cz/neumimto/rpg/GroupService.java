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

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.persistance.GroupDao;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.players.groups.PlayerGroupPermission;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.util.Tristate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by NeumimTo on 28.12.2014.
 */
@Singleton
public class GroupService {

	private static final String CLASS_ACCESS_PERM = "ntrpg.class.";

	@Inject
	private DamageService damageService;

	@Inject
	private GroupDao groupDao;



	public ClassDefinition getClassDefinitionByName(String name) {
		return groupDao.getClasses().get(name.toLowerCase());
	}

	public void registerPlaceholders() {
		damageService.createDamageToColorMapping();
	}

	public boolean existsClass(String s) {
		return groupDao.getClasses().containsKey(s.toLowerCase());
	}

	public Collection<ClassDefinition> getClassDefinitions() {
		return groupDao.getClasses().values();
	}

	public Set<ClassDefinition> filterByPlayerAndType(Player player, String type) {
		Set<ClassDefinition> defs = new HashSet<>();
		for (Map.Entry<String, ClassDefinition> entry : groupDao.getClasses().entrySet()) {
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

	public void addAllPermissions(IActiveCharacter character, ClassDefinition classDefinition) {
		for (PlayerGroupPermission playerGroupPermission : classDefinition.getPermissions()) {
			if (playerGroupPermission.getLevel() <= character.getLevel()) {
				addPermissions(character, playerGroupPermission.getPermissions());
			}
		}
	}

	public void addPermissions(IActiveCharacter character, ClassDefinition classDefinition) {
		for (PlayerGroupPermission playerGroupPermission : classDefinition.getPermissions()) {
			if (playerGroupPermission.getLevel() == character.getLevel()) {
				addPermissions(character, playerGroupPermission.getPermissions());
			}
		}
	}
}
