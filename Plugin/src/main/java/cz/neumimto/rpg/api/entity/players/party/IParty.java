package cz.neumimto.rpg.api.entity.players.party;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

import java.util.Set;
import java.util.UUID;

public interface IParty<T extends IActiveCharacter> {
    void addPlayer(T character);

    T getLeader();

    void setLeader(T leader);

    void removePlayer(T character);

    Set<T> getPlayers();

    Set<UUID> getInvites();

    boolean isFriendlyfire();

    void setFriendlyfire(boolean friendlyfire);

    void sendPartyMessage(String t);
}
