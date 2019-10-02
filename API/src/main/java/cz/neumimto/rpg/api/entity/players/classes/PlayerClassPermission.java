package cz.neumimto.rpg.api.entity.players.classes;

import com.electronwill.nightconfig.core.conversion.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ja on 22.7.2017.
 */
public class PlayerClassPermission implements Comparable<PlayerClassPermission> {

    @Path("Level")
    private int level;

    @Path("Permissions")
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
