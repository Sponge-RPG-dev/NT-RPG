package cz.neumimto.rpg.api.permissions;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;

import java.util.Collection;

public interface PermissionService<T extends IActiveCharacter> {

    boolean hasPermission(T character, String value);

    void removePermissions(T character, Collection<String> perms);

    void addPermissions(T character, Collection<String> perms);

    void addAllPermissions(T character, PlayerClassData classDefinition);

    void addPermissions(T character, PlayerClassData classDefinition);
}
