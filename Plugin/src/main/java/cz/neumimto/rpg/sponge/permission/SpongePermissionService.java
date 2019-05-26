package cz.neumimto.rpg.sponge.permission;

import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import cz.neumimto.rpg.players.groups.PlayerGroupPermission;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.util.Tristate;

import java.util.Set;

public class SpongePermissionService implements PermissionService {

    @Override
    public boolean hasPermission(IActiveCharacter character, String value) {
        return character.getPlayer().hasPermission(value);
    }

    @Override
    public void removePermissions(IActiveCharacter character, Set<String> perms) {
        SubjectData transientSubjectData = character.getPlayer().getTransientSubjectData();
        for (String perm : perms) {
            transientSubjectData.setPermission(SubjectData.GLOBAL_CONTEXT, perm, Tristate.UNDEFINED);
        }
    }

    @Override
    public void addPermissions(IActiveCharacter character, Set<String> perms) {
        SubjectData transientSubjectData = character.getPlayer().getTransientSubjectData();
        for (String perm : perms) {
            transientSubjectData.setPermission(SubjectData.GLOBAL_CONTEXT, perm, Tristate.TRUE);
        }
    }

    @Override
    public void addAllPermissions(IActiveCharacter character, PlayerClassData classDefinition) {
        for (PlayerGroupPermission playerGroupPermission : classDefinition.getClassDefinition().getPermissions()) {
            if (playerGroupPermission.getLevel() <= classDefinition.getLevel()) {
                addPermissions(character, playerGroupPermission.getPermissions());
            }
        }
    }

    @Override
    public void addPermissions(IActiveCharacter character, PlayerClassData classDefinition) {
        for (PlayerGroupPermission playerGroupPermission : classDefinition.getClassDefinition().getPermissions()) {
            if (playerGroupPermission.getLevel() == classDefinition.getLevel()) {
                addPermissions(character, playerGroupPermission.getPermissions());
            }
        }
    }
}
