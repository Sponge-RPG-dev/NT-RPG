package cz.neumimto.rpg.sponge.entities.players.party;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.party.IParty;
import cz.neumimto.rpg.common.entity.parties.PartyServiceImpl;

import javax.inject.Singleton;

@Singleton
public class SpongePartyService extends PartyServiceImpl {
    @Override
    protected IParty createParty(IActiveCharacter leader) {
        return new SpongeParty(leader);
    }
}
