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

package cz.neumimto.rpg.sponge.entities.players.party;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.party.IParty;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by NeumimTo on 10.8.2015.
 */
public class SpongeParty implements IParty<ISpongeCharacter> {

    private Set<ISpongeCharacter> players = new HashSet<>();
    private ISpongeCharacter leader;
    private Set<UUID> invites = new HashSet<>();
    private Team team;

    public SpongeParty(ISpongeCharacter leader) {
        this.leader = leader;
        addPlayer(leader);
    }

    @Override
    public void addPlayer(ISpongeCharacter c) {
        ISpongeCharacter character = (ISpongeCharacter) c;
        players.add(character);
        if (team == null) {
            team = Team.builder()
                    .name(character.getName())
                    .prefix(Text.of(TextColors.GREEN))
                    .allowFriendlyFire(false)
                    .canSeeFriendlyInvisibles(true)
                    .color(TextColors.GREEN)
                    .build();
        }
        team.addMember(character.getPlayer().getTeamRepresentation());
    }

    @Override
    public ISpongeCharacter getLeader() {
        return leader;
    }

    @Override
    public void setLeader(ISpongeCharacter leader) {
        this.leader = leader;
    }

    @Override
    public void removePlayer(ISpongeCharacter character) {
        players.remove(character);
        team.removeMember(((ISpongeCharacter)character).getPlayer().getTeamRepresentation());
    }

    @Override
    public Set<ISpongeCharacter> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    @Override
    public Set<UUID> getInvites() {
        return invites;
    }

    @Override
    public boolean isFriendlyfire() {
        return team.allowFriendlyFire();
    }

    @Override
    public void setFriendlyfire(boolean friendlyfire) {
        team.setAllowFriendlyFire(friendlyfire);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SpongeParty party = (SpongeParty) o;

        return leader.equals(party.leader);

    }

    @Override
    public void sendPartyMessage(String t) {
        String translate = Rpg.get().getLocalizationService().translate(LocalizationKeys.PARTY_CHAT_PREFIX);
        String text = translate + t;
        for (IActiveCharacter player : players) {
            player.sendMessage(text);
        }
    }

    @Override
    public int hashCode() {
        return invites.size() * 77 * players.size();
    }
}
