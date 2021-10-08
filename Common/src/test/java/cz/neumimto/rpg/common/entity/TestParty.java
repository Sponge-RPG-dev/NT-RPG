package cz.neumimto.rpg.common.entity;

import cz.neumimto.rpg.common.entity.players.party.IParty;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TestParty implements IParty<TestCharacter> {

    private Set<TestCharacter> testCharacters = new HashSet<>();
    private TestCharacter leader;
    private Set<UUID> uuid = new HashSet<>();

    @Override
    public void addPlayer(TestCharacter character) {
        testCharacters.add(character);
    }

    @Override
    public TestCharacter getLeader() {
        return leader;
    }

    @Override
    public void setLeader(TestCharacter leader) {
        this.leader = leader;
    }

    @Override
    public void removePlayer(TestCharacter character) {
        testCharacters.remove(character);
    }

    @Override
    public Set<TestCharacter> getPlayers() {
        return testCharacters;
    }

    @Override
    public Set<UUID> getInvites() {
        return uuid;
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
