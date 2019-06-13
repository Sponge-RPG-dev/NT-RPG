package cz.neumimto.rpg.common.impl;

import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterServise;

import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Singleton
public class TestCharacterService extends SpongeCharacterServise {

    @Override
    protected void scheduleNextTick(Runnable r) {
        r.run();
    }

    @Override
    protected void addCharacterToGame(UUID id, ISpongeCharacter character, List<CharacterBase> playerChars) {
        completePlayerDataPreloading(id, character, playerChars);
    }
}
