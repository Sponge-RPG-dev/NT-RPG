package cz.neumimto.rpg.spigot.permissions;

import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.permissions.PermissionService;
import cz.neumimto.rpg.spigot.SpigotRpg;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.util.Tristate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpigotPermissionService implements PermissionService<ISpigotCharacter> {

    private static final String CLASS_PERM_PREFIX = "ntrpg_internal_class_";
    @Inject
    private ClassService classService;

    private String getGroupForClass(ClassDefinition c) {
        return CLASS_PERM_PREFIX + c.getName();
    }

    @Override
    public void init() {
        GroupManager groupManager = SpigotRpgPlugin.getLuckPerms().getGroupManager();
        groupManager.getLoadedGroups()
                .forEach(group -> {
                    if (group.getName().startsWith(CLASS_PERM_PREFIX)) {
                        groupManager.deleteGroup(group).join();
                    }
                });

        classService.getClasses().values().forEach(classDefinition -> {
            String gName = getGroupForClass(classDefinition);

            groupManager.modifyGroup(gName, group -> {
                        classDefinition.getAllowedArmor().forEach(item -> group.data().add(PermissionNode.builder(item.getPermission()).build()));
                        classDefinition.getWeapons().forEach(item -> group.data().add(PermissionNode.builder(item.getPermission()).build()));
                        classDefinition.getOffHandWeapons().forEach(item -> group.data().add(PermissionNode.builder(item.getPermission()+"_1").build()));
                    })
                    .join();

        });

    }

    @Override
    public boolean hasPermission(ISpigotCharacter character, String value) {
        UUID uuid = character.getUUID();
        return SpigotRpgPlugin.getLuckPerms().getUserManager()
                .getUser(uuid)
                .getCachedData()
                .getPermissionData()
                .queryPermission(value)
                .result() == Tristate.TRUE;
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

    @Override
    public void refreshPermGroups(ISpigotCharacter tpActiveCharacter) {
        User user = SpigotRpgPlugin.getLuckPerms().getUserManager().getUser(tpActiveCharacter.getUUID());
        user.transientData().clear(node -> node instanceof InheritanceNode i && i.getGroupName().startsWith(CLASS_PERM_PREFIX));
        Map<String, PlayerClassData> classes = tpActiveCharacter.getClasses();
        for (Map.Entry<String, PlayerClassData> e : classes.entrySet()) {
            String group = getGroupForClass(e.getValue().getClassDefinition());
            Group group1 = SpigotRpgPlugin.getLuckPerms().getGroupManager().getGroup(group);
            user.transientData().add(InheritanceNode.builder(group1).build());
        }
    }
}
