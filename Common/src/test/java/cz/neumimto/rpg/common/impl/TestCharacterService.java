package cz.neumimto.rpg.common.impl;

import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.TestCharacter;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.persistance.model.CharacterSkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class TestCharacterService extends CharacterService<TestCharacter> {

    private Map<UUID, TestCharacter> characterMap = new HashMap<>();

    @Override
    protected boolean hasCharacter(UUID uniqueId) {
        return characterMap.containsKey(uniqueId);
    }

    @Override
    protected void initSpellbook(TestCharacter activeCharacter, String[][] spellbookPages) {

    }

    @Override
    protected void initSpellbook(TestCharacter activeCharacter, int i, int j, PlayerSkillContext skill) {

    }

    @Override
    protected TestCharacter removeCharacter(UUID uuid) {
        return characterMap.remove(uuid);
    }

    @Override
    protected TestCharacter createCharacter(UUID player, CharacterBase characterBase) {
        return new TestCharacter(player, characterBase, PropertyService.LAST_ID);
    }

    @Override
    public TestCharacter buildDummyChar(UUID uuid) {
        return new TestCharacter(uuid, new CharacterBase(), PropertyService.LAST_ID) {

            @Override
            public boolean isStub() {
                return true;
            }
        };
    }

    @Override
    public void registerDummyChar(TestCharacter dummy) {
        characterMap.put(dummy.getUUID(), dummy);
    }

    @Override
    public TestCharacter getCharacter(UUID uuid) {
        return characterMap.get(uuid);
    }

    @Override
    public void addCharacter(UUID uuid, TestCharacter character) {
        characterMap.put(uuid, character);
    }

    @Override
    public Collection<TestCharacter> getCharacters() {
        return characterMap.values();
    }

    @Override
    public boolean assignPlayerToCharacter(UUID uniqueId) {
        throw new RuntimeException("assignPlayerToCharacter Not implemeneted");
    }


    @Override
    public int canCreateNewCharacter(UUID uniqueId, String name) {
        return 0;
    }

    @Override
    public void removePersistantSkill(CharacterSkill characterSkill) {

    }

    @Override
    public void setHeathscale(TestCharacter character, double scale) {

    }

    @Override
    public void notifyCooldown(ActiveCharacter caster, PlayerSkillContext skillInfo, long cd) {

    }

    @Override
    public void updateSpellbook(TestCharacter character) {

    }


    @Override
    protected void scheduleNextTick(Runnable r) {
        r.run();
    }


}
