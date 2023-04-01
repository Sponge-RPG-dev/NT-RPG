package cz.neumimto.rpg.spigot.entities.players.party;

import cz.neumimto.rpg.common.entity.players.party.IParty;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class SpigotParty implements IParty<SpigotCharacter> {
    private Team team;

    public SpigotParty(SpigotCharacter leader) {

    }

    @Override
    public void addPlayer(SpigotCharacter character) {

    }

    @Override
    public SpigotCharacter getLeader() {
        return null;
    }

    @Override
    public void setLeader(SpigotCharacter leader) {

    }

    @Override
    public void removePlayer(SpigotCharacter character) {

    }

    @Override
    public Set<SpigotCharacter> getPlayers() {
        return Collections.emptySet();
    }

    @Override
    public Set<UUID> getInvites() {
        return Collections.emptySet();
    }

    @Override
    public boolean isFriendlyfire() {
        return false;
    }

    @Override
    public void setFriendlyfire(boolean friendlyfire) {

    }

    @Override
    public void sendPartyMessage(String t) {

    }
}
