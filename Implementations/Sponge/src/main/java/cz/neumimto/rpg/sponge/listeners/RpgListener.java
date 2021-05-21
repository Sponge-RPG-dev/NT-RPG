

package cz.neumimto.rpg.sponge.listeners;

import com.google.inject.Singleton;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.events.PlayerGuiModInitEvent;
import cz.neumimto.rpg.sponge.events.character.SpongeCharacterChangeGroupEvent;
import cz.neumimto.rpg.sponge.events.party.SpongePartyJoinEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;

import javax.inject.Inject;
import java.util.UUID;


/**
 * Created by NeumimTo on 12.2.2015.
 */
@Singleton
@ResourceLoader.ListenerClass
public class RpgListener {

    @Inject
    private SpongeCharacterService characterService;

    @Inject
    private ClassService classService;

    @Inject
    private PermissionService permissionService;

    @Listener
    public void onGuiInit(PlayerGuiModInitEvent event) {
        UUID uuid = event.getUuid();
        characterService.getCharacter(uuid).setUsingGuiMod(true);
    }


    @Listener(order = Order.EARLY)
    public void onPartyJoin(SpongePartyJoinEvent event) {
        PluginConfig pluginConfig = Rpg.get().getPluginConfig();
        if (pluginConfig.MAX_PARTY_SIZE > -1) {
            if (event.getParty().getPlayers().size() > pluginConfig.MAX_PARTY_SIZE) {
                event.setCancelled(true);
            }
        }
    }

    @Listener
    public void onChangeGroup(SpongeCharacterChangeGroupEvent event) {
        permissionService.removePermissions(event.getTarget(), classService.getPermissionsToRemove(event.getTarget(), event.getOldClass()));
        //	classService.addAllPermissions(character, event.getNew());
    }
}
