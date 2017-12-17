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
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.persistance.GroupDao;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.*;
import org.slf4j.Logger;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.util.Tristate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 28.12.2014.
 */
@Singleton
public class GroupService {

	@Inject
	private GroupDao groupDao;

	@Inject
	Logger logger;

	@Inject
	DamageService damageService;

	public GroupService() {

	}

	public Guild getGuild(String name) {
		name = name.toLowerCase();
		if (!groupDao.getGuilds().containsKey(name)) {
			return Guild.Default;
		}
		return groupDao.getGuilds().get(name.toLowerCase());
	}

	public void registerGuild(Guild g) {
		groupDao.getGuilds().put(g.getName().toLowerCase(), g);
	}

	public Race getRace(String name) {
		name = name.toLowerCase();
		if (!groupDao.getRaces().containsKey(name)) {
			return Race.Default;
		}
		return groupDao.getRaces().get(name.toLowerCase());
	}

	public void registerRace(Race g) {
		groupDao.getRaces().put(g.getName().toLowerCase(), g);
	}

	public ConfigClass getNClass(String name) {
		name = name.toLowerCase();
		if (!groupDao.getClasses().containsKey(name)) {
			return ConfigClass.Default;
		}
		return groupDao.getClasses().get(name.toLowerCase());
	}

	public void registerClass(ConfigClass g) {
		groupDao.getClasses().put(g.getName().toLowerCase(), g);
	}

	public Collection<Race> getRaces() {
		return groupDao.getRaces().values();
	}

	public Collection<Guild> getGuilds() {
		return groupDao.getGuilds().values();
	}

	@PostProcess(priority = 401)
	public void registerPlaceholders() {

		registerClass(ConfigClass.Default);
		registerRace(Race.Default);

		for (ConfigClass configClass : getClasses()) {
			if (configClass.isDefaultClass()) {
				setDefaultClass(configClass);
				break;
			}
		}

		damageService.createDamageToColorMapping();
	}

	public boolean existsGuild(String s) {
		return groupDao.getGuilds().containsKey(s.toLowerCase());
	}

	public boolean existsClass(String s) {
		return groupDao.getClasses().containsKey(s.toLowerCase());
	}

	public boolean existsRace(String s) {
		return groupDao.getRaces().containsKey(s.toLowerCase());
	}

	public Set<PlayerGroup> getAll() {
		Set<PlayerGroup> set = new HashSet<>();
		set.addAll(getRaces());
		set.addAll(getClasses());
		return set;

	}

	public Collection<ConfigClass> getClasses() {
		return groupDao.getClasses().values();
	}

	public int getLevel(ConfigClass configClass, double experiendec) {
		double l = 0;
		int k = 1;
		for (double v : configClass.getLevels()) {
			if (l < experiendec) {
				return k;
			}
			k += v;
		}
		return k;
	}

	public PlayerGroup getByName(String arg) {
		if (existsClass(arg))
			return getNClass(arg);
		if (existsRace(arg))
			return getRace(arg);
		return null;
	}

	public void setDefaultClass(ConfigClass configClass) {
		ConfigClass.Default = configClass;
		ExtendedNClass.Default.setConfigClass(configClass);
		logger.info("Default class set to \"" + configClass.getName() + "\"");
	}

	public Set<String> getPermissionsToRemove(IActiveCharacter character, PlayerGroup toBeReplaced) {
		Set<String> intersection = new HashSet<>();

		Set<String> toBeRemoved = new HashSet<>();

		if (character.getRace() != toBeReplaced) {
			for (PlayerGroupPermission playerGroupPermission : character.getRace().getPermissions()) {
				if (playerGroupPermission.getLevel() <= character.getLevel()) {
					intersection.addAll(playerGroupPermission.getPermissions());
				} else {

					break;
				}
			}
		} else {
			for (PlayerGroupPermission pgp : character.getRace().getPermissions()) {
				if (pgp.getLevel() <= character.getLevel()) {
					toBeRemoved.addAll(pgp.getPermissions());
				}
			}
		}


		for (ExtendedNClass nClass : character.getClasses()) {
			ConfigClass configClass = nClass.getConfigClass();
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

	public void addAllPermissions(IActiveCharacter character, PlayerGroup playerGroup) {
		for (PlayerGroupPermission playerGroupPermission : playerGroup.getPermissions()) {
			if (playerGroupPermission.getLevel() <= character.getLevel()) {
				addPermissions(character, playerGroupPermission.getPermissions());
			}
		}
	}

	public void addPermissions(IActiveCharacter character, PlayerGroup playerGroup) {
		for (PlayerGroupPermission playerGroupPermission : playerGroup.getPermissions()) {
			if (playerGroupPermission.getLevel() == character.getLevel()) {
				addPermissions(character, playerGroupPermission.getPermissions());
			}
		}
	}
}
