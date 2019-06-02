package cz.neumimto.rpg.common.impl;

import cz.neumimto.rpg.common.entity.players.CharacterBase;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Singleton
public class TestCharacterService extends CharacterService {
    @Override
    protected void addCharacterToGame(UUID id, IActiveCharacter character, List<CharacterBase> playerChars) {

    }
}
