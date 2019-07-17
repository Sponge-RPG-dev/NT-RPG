package cz.neumimto.rpg.common;

import cz.neumimto.rpg.api.entity.players.party.IParty;
import cz.neumimto.rpg.common.entity.TestCharacter;
import cz.neumimto.rpg.common.entity.TestParty;
import cz.neumimto.rpg.common.entity.parties.PartyServiceImpl;

public class TestPartyService extends PartyServiceImpl<TestCharacter> {

    @Override
    protected IParty createParty(TestCharacter leader) {
        TestParty testParty = new TestParty();
        testParty.setLeader(leader);
        return testParty;
    }
}
