package cz.neumimto.rpg.spigot.entities.players;

import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.players.CharacterMana;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import org.bukkit.Bukkit;

import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Singleton
public class SpigotCharacterService extends CharacterService<ISpigotCharacter> {

    @Override
    protected ISpigotCharacter createCharacter(UUID player, CharacterBase characterBase) {
        SpigotCharacter iActiveCharacter = new SpigotCharacter(player, characterBase, PropertyService.LAST_ID);
        iActiveCharacter.setMana(new CharacterMana(iActiveCharacter));
        iActiveCharacter.setHealth(new SpigotCharacterHealth(iActiveCharacter));
        return iActiveCharacter;
    }

    @Override
    protected void addCharacterToGame(UUID id, ISpigotCharacter character, List<CharacterBase> playerChars) {

    }

    @Override
    public ISpigotCharacter buildDummyChar(UUID uuid) {
        return null;
    }

    @Override
    public void registerDummyChar(ISpigotCharacter dummy) {

    }

    @Override
    public boolean assignPlayerToCharacter(UUID uniqueId) {
        return false;
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
        Bukkit.getScheduler().runTaskLater(SpigotRpgPlugin.getInstance(),r,1L);
    }
}
