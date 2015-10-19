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

package cz.neumimto.listeners;

import java.util.Optional;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.events.character.PlayerDataPreloadComplete;
import cz.neumimto.gui.Gui;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.ListenerClass;
import cz.neumimto.players.CharacterService;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;


/**
 * Created by NeumimTo on 12.2.2015.
 */
@ListenerClass
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
            if (event.getCharacterBases().isEmpty() && PluginConfig.CREATE_FIRST_CHAR_AFTER_LOGIN) {
                characterService.characterCreateState(player, true);
            }
            if (!event.getCharacterBases().isEmpty()) {
                if (PluginConfig.PLAYER_AUTO_CHOOSE_LAST_PLAYED_CHAR || event.getCharacterBases().size() == 1) {
                    characterService.setActiveCharacter(event.getPlayer(), characterService.buildActiveCharacter(player, event.getCharacterBases().get(0)));
                } else {
                    Gui.invokeCharacterMenu(player, event.getCharacterBases());
                }
            }
        }
    }
}
