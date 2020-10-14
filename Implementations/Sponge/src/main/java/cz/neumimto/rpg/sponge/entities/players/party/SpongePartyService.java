package cz.neumimto.rpg.sponge.entities.players.party;

import cz.neumimto.rpg.api.entity.players.party.IParty;
import cz.neumimto.rpg.common.entity.parties.PartyServiceImpl;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;

import javax.inject.Singleton;

@Singleton
public class SpongePartyService extends PartyServiceImpl<ISpongeCharacter> {
    @Override
    protected IParty createParty(ISpongeCharacter leader) {
        return new SpongeParty(leader);
    }
}
