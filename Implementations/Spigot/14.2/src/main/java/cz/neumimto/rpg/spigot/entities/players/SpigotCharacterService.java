package cz.neumimto.rpg.spigot.entities.players;

import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.common.entity.players.CharacterService;

import javax.inject.Singleton;
import java.util.*;

@Singleton
public class SpigotCharacterService extends CharacterService<ISpigotCharacter> {




    @Override
    protected void addCharacterToGame(UUID id, ISpigotCharacter character, List<CharacterBase> playerChars) {

    }

    @Override
    protected boolean hasCharacter(UUID uniqueId) {
        return false;
    }

    @Override
    protected ISpigotCharacter removeCharacter(UUID uuid) {
        return null;
    }

    @Override
    protected ISpigotCharacter createCharacter(UUID player, CharacterBase characterBase) {
        return null;
    }

    @Override
    public ISpigotCharacter buildDummyChar(UUID uuid) {
        return null;
    }

    @Override
    public void registerDummyChar(ISpigotCharacter dummy) {

    }

    @Override
    public ISpigotCharacter getCharacter(UUID uuid) {
        return null;
    }

    @Override
    public void addCharacter(UUID uuid, ISpigotCharacter character) {

    }

    @Override
    public Collection<ISpigotCharacter> getCharacters() {
        return null;
    }

    @Override
    public boolean assignPlayerToCharacter(UUID uniqueId) {
        return false;
    }

    @Override
    public void addDefaultEffects(ISpigotCharacter character) {

    }

    @Override
    public int canCreateNewCharacter(UUID uniqueId, String name) {
        return 0;
    }

    @Override
    public void removePersistantSkill(CharacterSkill characterSkill) {

    }

    @Override
    protected void scheduleNextTick(Runnable r) {

    }
}
