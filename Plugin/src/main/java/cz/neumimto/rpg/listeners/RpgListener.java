/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.rpg.listeners;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.GroupService;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.events.CharacterChangeGroupEvent;
import cz.neumimto.rpg.events.CharacterGainedLevelEvent;
import cz.neumimto.rpg.events.PlayerGuiModInitEvent;
import cz.neumimto.rpg.events.character.PlayerDataPreloadComplete;
import cz.neumimto.rpg.events.party.PartyJoinEvent;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.HealEntityEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;
import java.util.UUID;


/**
 * Created by NeumimTo on 12.2.2015.
 */
@ResourceLoader.ListenerClass
public class RpgListener {

    @Inject
    private CharacterService characterService;

    @Inject
    private Game game;

    @Inject
    private GroupService groupService;

    @Inject
    private NtRpgPlugin plugin;

    @Listener
    public void onPlayerDataPreloadComplete(PlayerDataPreloadComplete event) {
        Optional<Player> retardedOptional = game.getServer().getPlayer(event.getPlayer());
        if (retardedOptional.isPresent()) {
            Player player = retardedOptional.get();
            if (!event.getCharacterBases().isEmpty()) {
                System.out.println(Thread.currentThread().getName());
                if (PluginConfig.PLAYER_AUTO_CHOOSE_LAST_PLAYED_CHAR || event.getCharacterBases().size() == 1) {
                    Sponge.getScheduler().createTaskBuilder().async().execute(() -> {
                        final IActiveCharacter character = characterService.buildActiveCharacterAsynchronously(player, event.getCharacterBases().get(0));
                        Sponge.getScheduler().createTaskBuilder().execute(() -> {
                            characterService.setActiveCharacter(event.getPlayer(), character);
                        }).submit(plugin);
                    }).submit(plugin);
                } else {
                    Gui.invokeCharacterMenu(player, event.getCharacterBases());
                }
            }
        }
    }

    @Listener
    public void onGuiInit(PlayerGuiModInitEvent event) {
        UUID uuid = event.getUuid();
        characterService.getCharacter(uuid).setUsingGuiMod(true);
    }


    @Listener(order = Order.EARLY)
    public void onPartyJoin(PartyJoinEvent event) {
        if (PluginConfig.MAX_PARTY_SIZE > -1) {
            if (event.getParty().getPlayers().size() > PluginConfig.MAX_PARTY_SIZE) {
                event.setCancelled(true);
            }
        }
    }

    @Listener
    public void onHealthRegen(HealEntityEvent event, @First(typeFilter = Player.class) Player player) {

    }

    @Listener
    @IsCancelled(Tristate.FALSE)
    public void onChangeGroup(CharacterChangeGroupEvent event, @First(typeFilter = IActiveCharacter.class) IActiveCharacter character) {
        groupService.removePermissions(character, groupService.getPermissionsToRemove(character, event.getOld()));
        groupService.addAllPermissions(character, event.getNew());
    }

}
