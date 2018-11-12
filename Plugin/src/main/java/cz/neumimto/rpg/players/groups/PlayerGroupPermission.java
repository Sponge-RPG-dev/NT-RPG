package cz.neumimto.rpg.players.groups;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ja on 22.7.2017.
 */
public class PlayerGroupPermission implements Comparable<PlayerGroupPermission> {

	private int level;
	private Set<String> permissions;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Set<String> getPermissions() {
		if (permissions == null) {
			permissions = new HashSet<>();
		}
		return permissions;
	}

	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}

	@Override
	public int compareTo(PlayerGroupPermission o) {
		return level - o.getLevel();
	}
}
