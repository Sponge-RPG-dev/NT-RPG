package cz.neumimto.rpg.sponge.permission;

import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;

import javax.inject.Singleton;
import java.util.Set;

@Singleton
public class TestPermissionService implements PermissionService {

    @Override
    public boolean hasPermission(IActiveCharacter character, String value) {
        return true;
    }

    @Override
    public void removePermissions(IActiveCharacter character, Set<String> perms) {

    }

    @Override
    public void addPermissions(IActiveCharacter character, Set<String> perms) {

    }

    @Override
    public void addAllPermissions(IActiveCharacter character, PlayerClassData classDefinition) {

    }

    @Override
    public void addPermissions(IActiveCharacter character, PlayerClassData classDefinition) {

    }
}