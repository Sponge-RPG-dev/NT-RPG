package cz.neumimto.rpg.common.impl;

import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;

import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Singleton
public class TestCharacterService extends CharacterService {

    @Override
    protected void addCharacterToGame(UUID id, IActiveCharacter character, List<CharacterBase> playerChars) {

    }

    @Override
    public void updateWeaponRestrictions(IActiveCharacter character) {

    }

    @Override
    public void updateArmorRestrictions(IActiveCharacter character) {

    }
}
