package cz.neumimto.players.parties;

import cz.neumimto.NtRpgPlugin;
import cz.neumimto.players.ActiveCharacter;
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
    private Set<ActiveCharacter> players = new HashSet<>();
    private ActiveCharacter leader;
    private Set<UUID> invites = new HashSet<>();
    private Team team;


    public Party(ActiveCharacter leader) {
        this.leader = leader;
        TeamBuilder tb = NtRpgPlugin.GlobalScope.game.getRegistry().createTeamBuilder();
        team = tb.allowFriendlyFire(false).canSeeFriendlyInvisibles(true).color(TextColors.GREEN).build();

    }

    public void addPlayer(ActiveCharacter character) {
        players.add(character);

    }

    public ActiveCharacter getLeader() {
        return leader;
    }

    public void removePlayer(ActiveCharacter character) {
        players.remove(character);

    }

    public Set<ActiveCharacter> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public void setPlayers(Set<ActiveCharacter> players) {
        this.players = players;
    }

    public void setLeader(ActiveCharacter leader) {
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
}
