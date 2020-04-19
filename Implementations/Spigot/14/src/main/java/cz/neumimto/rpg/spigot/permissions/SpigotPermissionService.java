package cz.neumimto.rpg.spigot.permissions;

import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Collection;
import java.util.Set;

public class SpigotPermissionService implements PermissionService<ISpigotCharacter> {
    @Override
    public boolean hasPermission(ISpigotCharacter character, String value) {
        return character.getEntity().hasPermission(value);
    }

    @Override
    public void removePermissions(ISpigotCharacter character, Collection<String> perms) {
        Player player = character.getPlayer();
        for (String perm : perms) {
            player.addAttachment(SpigotRpgPlugin.getInstance(), perm, false);
        }
    }

    @Override
    public void addPermissions(ISpigotCharacter character, Collection<String> perms) {
        Player player = character.getPlayer();
        for (String perm : perms) {
            player.addAttachment(SpigotRpgPlugin.getInstance(), perm, true);
        }
    }

}
