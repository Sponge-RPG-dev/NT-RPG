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

import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.events.PlayerGuiModInitEvent;
import cz.neumimto.rpg.events.character.PlayerDataPreloadComplete;
import cz.neumimto.rpg.players.CharacterService;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;

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

    @Listener
    public void onPlayerDataPreloadComplete(PlayerDataPreloadComplete event) {
        Optional<Player> retardedOptional = game.getServer().getPlayer(event.getPlayer());
        if (retardedOptional.isPresent()) {
            Player player = retardedOptional.get();
            if (!event.getCharacterBases().isEmpty()) {
                if (PluginConfig.PLAYER_AUTO_CHOOSE_LAST_PLAYED_CHAR || event.getCharacterBases().size() == 1) {
                    characterService.setActiveCharacter(event.getPlayer(), characterService.buildActiveCharacter(player, event.getCharacterBases().get(0)));
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
}
