package cz.neumimto.rpg.spigot.entities.players.party;

import cz.neumimto.rpg.api.entity.players.party.IParty;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class SpigotParty implements IParty<ISpigotCharacter> {
    private Team team;

    public SpigotParty(ISpigotCharacter leader) {

    }

    @Override
    public void addPlayer(ISpigotCharacter character) {

    }

    @Override
    public ISpigotCharacter getLeader() {
        return null;
    }

    @Override
    public void setLeader(ISpigotCharacter leader) {

    }

    @Override
    public void removePlayer(ISpigotCharacter character) {

    }

    @Override
    public Set<ISpigotCharacter> getPlayers() {
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
