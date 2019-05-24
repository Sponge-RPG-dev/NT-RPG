package cz.neumimto.rpg.players.groups;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ja on 22.7.2017.
 */
@ConfigSerializable
public class PlayerGroupPermission implements Comparable<PlayerGroupPermission> {

    @Setting("Level")
    private int level;

    @Setting("Permissions")
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
