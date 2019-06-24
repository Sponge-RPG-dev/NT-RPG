package cz.neumimto.rpg.api.entity.players.classes;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ja on 22.7.2017.
 */
@ConfigSerializable
public class PlayerClassPermission implements Comparable<PlayerClassPermission> {

    @Setting("Level")
    private int level;

    @Setting("Permissions")
    private List<String> permissions;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<String> getPermissions() {
        if (permissions == null) {
            permissions = new ArrayList<>();
        }
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public int compareTo(PlayerClassPermission o) {
        return level - o.getLevel();
    }
}
