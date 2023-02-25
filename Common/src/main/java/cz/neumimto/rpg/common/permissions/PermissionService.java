package cz.neumimto.rpg.common.permissions;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;

import java.util.Collection;

public interface PermissionService<T extends IActiveCharacter> {

    boolean hasPermission(T character, String value);

    void removePermissions(T character, Collection<String> perms);

    void addPermissions(T character, Collection<String> perms);

}
