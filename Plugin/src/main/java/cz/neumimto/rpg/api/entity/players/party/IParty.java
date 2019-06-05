package cz.neumimto.rpg.api.entity.players.party;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

import java.util.Set;
import java.util.UUID;

public interface IParty {
    void addPlayer(IActiveCharacter character);

    IActiveCharacter getLeader();

    void setLeader(IActiveCharacter leader);

    void removePlayer(IActiveCharacter character);

    Set<IActiveCharacter> getPlayers();

    Set<UUID> getInvites();

    boolean isFriendlyfire();

    void setFriendlyfire(boolean friendlyfire);

    void sendPartyMessage(String t);
}
