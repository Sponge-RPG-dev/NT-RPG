package cz.neumimto.rpg.sponge.entities.players;

import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.common.entity.PropertyServiceImpl;
import cz.neumimto.rpg.common.entity.players.AbstractCharacterService;
import cz.neumimto.rpg.common.entity.players.CharacterMana;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.entities.SpongeEntityService;
import cz.neumimto.rpg.sponge.entities.players.party.SpongePartyService;
import cz.neumimto.rpg.sponge.utils.PermissionUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static cz.neumimto.rpg.api.logging.Log.error;
import static cz.neumimto.rpg.api.logging.Log.info;


@Singleton
public class SpongeCharacterService extends AbstractCharacterService<ISpongeCharacter> {

    @Inject
    private SpongeRpgPlugin plugin;

    @Inject
    private SpongeEntityService spongeEntityService;

    @Inject
    private SpongePartyService partyService;

    @Override
    public ISpongeCharacter createCharacter(UUID player, CharacterBase characterBase) {
        SpongeCharacter spongeCharacter = new SpongeCharacter(player, characterBase, PropertyServiceImpl.LAST_ID);
        spongeCharacter.setMana(new CharacterMana(spongeCharacter));
        spongeCharacter.setHealth(new SpongeCharacterHealth(spongeCharacter));
        return spongeCharacter;
    }

    @Override
    public void registerDummyChar(ISpongeCharacter dummy) {
        characters.put(dummy.getUUID(), dummy);
    }

    @Override
    public ISpongeCharacter buildDummyChar(UUID uuid) {
        info("Creating a dummy character for " + uuid);
        return new SpongePreloadCharacter(uuid);
    }

    public ISpongeCharacter getCharacter(Player player) {
        return getCharacter(player.getUniqueId());
    }


    @Override
    public boolean assignPlayerToCharacter(UUID uniqueId) {
        info("Assigning player to character " + uniqueId);
        if (!hasCharacter(uniqueId)) {
            error("Could not find any character for player " + uniqueId + " Auth event not fired?");
            return false;
        }

        ISpongeCharacter character = getCharacter(uniqueId);
        if (character.isStub()) {
            return false;
        }

        Player pl = character.getPlayer();
        if (character.getCharacterBase().getHealthScale() != null) {
            pl.offer(Keys.HEALTH_SCALE, character.getCharacterBase().getHealthScale());
        }
        return true;
    }

    public void setHeathscale(ISpongeCharacter character, double i) {
        character.getCharacterBase().setHealthScale(i);
        character.getPlayer().offer(Keys.HEALTH_SCALE, i);
        putInSaveQueue(character.getCharacterBase());
    }

    @Override
    public void respawnCharacter(ISpongeCharacter character) {
        super.respawnCharacter(character);
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            invalidateCaches(character);
            Double d = character.getHealth().getMaxValue();
            character.getPlayer().offer(Keys.HEALTH, d);
        }).delay(1, TimeUnit.MILLISECONDS).submit(plugin);
    }

    @Override
    protected void scheduleNextTick(Runnable r) {
        Sponge.getScheduler().createTaskBuilder().delay(1, TimeUnit.MILLISECONDS)
                .execute(r).submit(plugin);
    }

    @Override
    public void removePersistantSkill(CharacterSkill characterSkill) {
        playerDao.removePersitantSkill(characterSkill);
    }

    @Override
    public int canCreateNewCharacter(UUID uniqueId, String name) {
        List<CharacterBase> list = getPlayersCharacters(uniqueId);
        if (list.size() >= PermissionUtils.getMaximalCharacterLimit(uniqueId)) {
            return 1;
        }
        if (list.stream().anyMatch(c -> c.getName().equalsIgnoreCase(name))) {
            return 2;
        }
        return 0;
    }
}
