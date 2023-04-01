package cz.neumimto.rpg.spigot.entities.players.party;


import cz.neumimto.rpg.common.entity.parties.PartyServiceImpl;
import cz.neumimto.rpg.common.entity.players.party.IParty;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;

import javax.inject.Singleton;

@Singleton
public class SpigotPartyService extends PartyServiceImpl<SpigotCharacter> {
    @Override
    protected IParty createParty(SpigotCharacter leader) {
        return new SpigotParty(leader);
    }
}
