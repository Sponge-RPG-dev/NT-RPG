package cz.neumimto.rpg.sponge.entities.players;

import cz.neumimto.rpg.common.entity.PropertyServiceImpl;
import cz.neumimto.rpg.players.*;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.events.PlayerDataPreloadComplete;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static cz.neumimto.rpg.api.logging.Log.error;
import static cz.neumimto.rpg.api.logging.Log.info;

@Singleton
public class SpongeCharacterServise extends CharacterService {

    @Inject
    private Game game;

    @Inject
    private NtRpgPlugin plugin;

    protected Map<UUID, ISpongeCharacter> characters = new HashMap<>();

    @Override
    public Collection<ISpongeCharacter> getCharacters() {
        return characters.values();
    }

    @Override
    protected void addCharacter(UUID uuid, IActiveCharacter character) {
        characters.put(uuid, (ISpongeCharacter) character);
    }

    @Override
    protected IActiveCharacter removeCharacter(UUID uuid) {
        return characters.remove(uuid);
    }

    @Override
    public ISpongeCharacter getCharacter(UUID uuid) {
        return characters.get(uuid);
    }

    @Override
    public ActiveCharacter createCharacter(UUID player, CharacterBase characterBase) {
        return new SpongeCharacter(player, characterBase, PropertyServiceImpl.LAST_ID);
    }

    @Override
    public void registerDummyChar(IActiveCharacter dummy) {
        characters.put(dummy.getUUID(), (ISpongeCharacter) dummy);
    }

    @Override
    public PreloadCharacter buildDummyChar(UUID uuid) {
        info("Creating a dummy character for " + uuid);
        return new SpongePreloadCharacter(uuid);
    }

    @Override
    protected boolean hasCharacter(UUID uniqueId) {
        return characters.containsKey(uniqueId);
    }

    public ISpongeCharacter getCharacter(Player player) {
        return characters.get(player.getUniqueId());
    }

    @Override
    protected void addCharacterToGame(UUID id, IActiveCharacter character, List<CharacterBase> playerChars) {
        game.getScheduler().createTaskBuilder().name("Callback-PlayerDataLoad" + id).execute(() -> {
            PlayerDataPreloadComplete event = new PlayerDataPreloadComplete(id, playerChars);
            game.getEventManager().post(event);

            Optional<Player> popt = game.getServer().getPlayer(event.getPlayer());
            if (popt.isPresent()) {
                finalizePlayerDataPreloadStage(id, character, event);
                assignPlayerToCharacter(id);
                initActiveCharacter(character);
            } else {
                playerDataPreloadStagePlayerNotReady(id, character);
            }
        }).submit(plugin);
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
        inventoryService.initializeCharacterInventory(character);
        return true;
    }

    public void setHeathscale(ISpongeCharacter character, double i) {
        character.getCharacterBase().setHealthScale(i);
        character.getPlayer().offer(Keys.HEALTH_SCALE, i);
        putInSaveQueue(character.getCharacterBase());
    }

    @Override
    public void respawnCharacter(IActiveCharacter character) {
        super.respawnCharacter(character);
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            invalidateCaches(character);
            Double d = character.getHealth().getMaxValue();
            ((ISpongeCharacter)character).getPlayer().offer(Keys.HEALTH, d);
        }).delay(1, TimeUnit.MILLISECONDS).submit(plugin);
    }

    @Override
    protected void scheduleNextTick(Runnable r) {
        Sponge.getScheduler().createTaskBuilder().delay(1, TimeUnit.MILLISECONDS)
                .execute(r).submit(NtRpgPlugin.GlobalScope.plugin);
    }
}
