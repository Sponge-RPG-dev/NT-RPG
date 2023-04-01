package cz.neumimto.rpg.common.entity.players.party;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;

import java.util.Set;
import java.util.UUID;

public interface IParty<T extends ActiveCharacter> {
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
