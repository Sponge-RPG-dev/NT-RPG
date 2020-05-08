package cz.neumimto.rpg.common.impl;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.entity.PropertyServiceImpl;
import cz.neumimto.rpg.common.entity.TestCharacter;
import cz.neumimto.rpg.common.entity.players.AbstractCharacterService;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class TestCharacterService extends AbstractCharacterService<TestCharacter> {

    private Map<UUID, TestCharacter> characterMap = new HashMap<>();


    @Override
    protected boolean hasCharacter(UUID uniqueId) {
        return characterMap.containsKey(uniqueId);
    }

    @Override
    protected TestCharacter removeCharacter(UUID uuid) {
        return characterMap.remove(uuid);
    }

    @Override
    protected TestCharacter createCharacter(UUID player, CharacterBase characterBase) {
        return new TestCharacter(player, characterBase, PropertyServiceImpl.LAST_ID);
    }

    @Override
    public TestCharacter buildDummyChar(UUID uuid) {
        return new TestCharacter(uuid, createCharacterBase(), PropertyServiceImpl.LAST_ID) {

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
    public void addDefaultEffects(TestCharacter character) {

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
    public void notifyCooldown(IActiveCharacter caster, PlayerSkillContext skillInfo, long cd) {

    }

    @Override
    protected void scheduleNextTick(Runnable r) {
        r.run();
    }


}
