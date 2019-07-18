package cz.neumimto.rpg.spigot.permissions;

import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;

import java.util.Collection;

public class SpigotPermissionService implements PermissionService<ISpigotCharacter> {
    @Override
    public boolean hasPermission(ISpigotCharacter character, String value) {
        return character.getEntity().hasPermission(value);
    }

    @Override
    public void removePermissions(ISpigotCharacter character, Collection<String> perms) {

    }

    @Override
    public void addPermissions(ISpigotCharacter character, Collection<String> perms) {

    }

    @Override
    public void addAllPermissions(ISpigotCharacter character, PlayerClassData classDefinition) {

    }

    @Override
    public void addPermissions(ISpigotCharacter character, PlayerClassData classDefinition) {

    }
}
