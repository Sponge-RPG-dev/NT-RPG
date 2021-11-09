package cz.neumimto.rpg.sponge.permission;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.permissions.PermissionService;

import javax.inject.Singleton;
import java.util.Collection;

@Singleton
public class TestPermissionService implements PermissionService {

    public static final String MISSING_PERM = ".QWE789456189.";

    @Override
    public boolean hasPermission(IActiveCharacter character, String value) {
        return !value.toLowerCase().contains(MISSING_PERM.toLowerCase());
    }

    @Override
    public void addAllPermissions(IActiveCharacter character, PlayerClassData classDefinition) {

    }

    @Override
    public void addPermissions(IActiveCharacter character, PlayerClassData classDefinition) {

    }

    @Override
    public void addPermissions(IActiveCharacter character, Collection perms) {

    }

    @Override
    public void removePermissions(IActiveCharacter character, Collection perms) {

    }
}