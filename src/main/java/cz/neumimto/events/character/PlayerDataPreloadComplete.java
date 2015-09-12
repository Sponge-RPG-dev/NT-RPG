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

package cz.neumimto.events.character;

import cz.neumimto.events.CancellableEvent;
import cz.neumimto.players.CharacterBase;

import java.util.List;
import java.util.UUID;

/**
 * Created by NeumimTo on 10.7.2015.
 */
public class PlayerDataPreloadComplete extends CancellableEvent {
    private UUID player;
    private List<CharacterBase> characterBases;

    public PlayerDataPreloadComplete(UUID player, List<CharacterBase> characterBases) {
        this.player = player;
        this.characterBases = characterBases;
    }

    public UUID getPlayer() {
        return player;
    }

    public List<CharacterBase> getCharacterBases() {
        return characterBases;
    }

    public void setCharacterBases(List<CharacterBase> characterBases) {
        this.characterBases = characterBases;
    }
}
