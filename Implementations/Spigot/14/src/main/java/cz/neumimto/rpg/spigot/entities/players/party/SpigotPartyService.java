package cz.neumimto.rpg.spigot.entities.players.party;


import cz.neumimto.rpg.api.entity.players.party.IParty;
import cz.neumimto.rpg.common.entity.parties.PartyServiceImpl;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;

import javax.inject.Singleton;

@Singleton
public class SpigotPartyService extends PartyServiceImpl<ISpigotCharacter> {
    @Override
    protected IParty createParty(ISpigotCharacter leader) {
        return new SpigotParty(leader);
    }
}
