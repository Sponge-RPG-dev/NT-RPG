package cz.neumimto.rpg.common.permissions;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.party.IParty;

import java.util.Collection;

public interface PermissionService<T extends ActiveCharacter> {

    void init();

    boolean hasPermission(T character, String value);

    void removePermissions(T character, Collection<String> perms);

    void addPermissions(T character, Collection<String> perms);

    void refreshPermGroups(T tpActiveCharacter);
}
