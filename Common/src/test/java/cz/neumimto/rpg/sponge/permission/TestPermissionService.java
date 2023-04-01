package cz.neumimto.rpg.sponge.permission;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.permissions.PermissionService;

import javax.inject.Singleton;
import java.util.Collection;

@Singleton
public class TestPermissionService implements PermissionService {

    public static final String MISSING_PERM = ".QWE789456189.";

    @Override
    public boolean hasPermission(ActiveCharacter character, String value) {
        return !value.toLowerCase().contains(MISSING_PERM.toLowerCase());
    }

    @Override
    public void refreshPermGroups(ActiveCharacter tpActiveCharacter) {

    }

    @Override
    public void addPermissions(ActiveCharacter character, Collection perms) {

    }

    @Override
    public void removePermissions(ActiveCharacter character, Collection perms) {

    }
}