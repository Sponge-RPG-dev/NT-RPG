package cz.neumimto.rpg.sponge.entities.players;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.PropertyServiceImpl;
import cz.neumimto.rpg.common.entity.players.CharacterMana;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.UserActionType;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.effects.common.def.ClickComboActionComponent;
import cz.neumimto.rpg.sponge.entities.SpongeEntityService;
import cz.neumimto.rpg.sponge.entities.players.party.SpongePartyService;
import cz.neumimto.rpg.sponge.events.PlayerDataPreloadComplete;
import cz.neumimto.rpg.sponge.utils.PermissionUtils;
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
public class SpongeCharacterService extends CharacterService<ISpongeCharacter> {

    @Inject
    private NtRpgPlugin plugin;

    @Inject
    private SpongeEntityService spongeEntityService;

    @Inject
    private SpongePartyService partyService;

    @Override
    public ISpongeCharacter createCharacter(UUID player, CharacterBase characterBase) {
        SpongeCharacter spongeCharacter = new SpongeCharacter(player, characterBase, PropertyService.LAST_ID);
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
    protected void addCharacterToGame(UUID id, ISpongeCharacter character, List<CharacterBase> playerChars) {
        Sponge.getScheduler().createTaskBuilder().name("Callback-PlayerDataLoad" + id).execute(() -> {
             completePlayerDataPreloading(id, character, playerChars);
        }).submit(plugin);
    }

    protected void completePlayerDataPreloading(UUID id, ISpongeCharacter character, List<CharacterBase> playerChars) {
        PlayerDataPreloadComplete event = new PlayerDataPreloadComplete(id, playerChars);
        Game game = Sponge.getGame();
        game.getEventManager().post(event);
        Optional<Player> popt = game.getServer().getPlayer(event.getPlayer());
        if (popt.isPresent()) {
            finalizePlayerDataPreloadStage(id, character, event);
            assignPlayerToCharacter(id);
        } else {
            playerDataPreloadStagePlayerNotReady(id, character);
        }
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
                .execute(r).submit(NtRpgPlugin.GlobalScope.plugin);
    }

    public boolean processUserAction(IActiveCharacter character, UserActionType userActionType) {
        IEffectContainer effect = character.getEffect(ClickComboActionComponent.name);
        if (effect == null) {
            return false;
        }
        ClickComboActionComponent e = (ClickComboActionComponent) effect;
        if (userActionType == UserActionType.L && e.hasStarted()) {
            e.processLMB();
            return false;
        }
        if (userActionType == UserActionType.R) {
            e.processRMB();
            return false;
        }
        PluginConfig pluginConfig = Rpg.get().getPluginConfig();
        if (userActionType == UserActionType.Q && pluginConfig.ENABLED_Q && e.hasStarted()) {
            e.processQ();
            return true;
        }
        if (userActionType == UserActionType.E && pluginConfig.ENABLED_E && e.hasStarted()) {
            e.processE();
            return true;
        }
        return false;
    }

    @Override
    public void removePersistantSkill(CharacterSkill characterSkill) {
        playerDao.removePeristantSkill(characterSkill);
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
