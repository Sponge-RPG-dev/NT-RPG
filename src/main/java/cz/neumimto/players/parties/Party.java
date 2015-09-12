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

package cz.neumimto.players.parties;

import cz.neumimto.NtRpgPlugin;
import cz.neumimto.players.IActiveCharacter;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.TeamBuilder;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by NeumimTo on 10.8.2015.
 */
public class Party {
    private Set<IActiveCharacter> players = new HashSet<>();
    private IActiveCharacter leader;
    private Set<UUID> invites = new HashSet<>();
    private Team team;

    public Party(IActiveCharacter leader) {
        this.leader = leader;
        addPlayer(leader);
        TeamBuilder tb = NtRpgPlugin.GlobalScope.game.getRegistry().createTeamBuilder();
        team = tb.allowFriendlyFire(false).canSeeFriendlyInvisibles(true).color(TextColors.GREEN).build();
    }

    public void addPlayer(IActiveCharacter character) {
        players.add(character);
        team.addMember(character.getPlayer().getTeamRepresentation());
    }

    public IActiveCharacter getLeader() {
        return leader;
    }

    public void removePlayer(IActiveCharacter character) {
        players.remove(character);
        team.removeMember(character.getPlayer().getTeamRepresentation());
    }

    public Set<IActiveCharacter> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public void setPlayers(Set<IActiveCharacter> players) {
        this.players = players;
    }

    public void setLeader(IActiveCharacter leader) {
        this.leader = leader;
    }

    public Set<UUID> getInvites() {
        return invites;
    }

    public void setInvites(Set<UUID> invites) {
        this.invites = invites;
    }

    public boolean isFriendlyfire() {
        return team.allowFriendlyFire();
    }

    public void setFriendlyfire(boolean friendlyfire) {
        team.setAllowFriendlyFire(friendlyfire);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Party party = (Party) o;

        return leader.equals(party.leader);

    }

    @Override
    public int hashCode() {
        return invites.size() * 77 * players.size();
    }
}
