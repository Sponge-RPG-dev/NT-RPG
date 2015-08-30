package cz.neumimto.listeners;


import cz.neumimto.LoggingService;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.effects.EffectService;
import cz.neumimto.effects.IEffect;
import cz.neumimto.inventory.InventoryService;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.Listener;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.ban.PlayerBanEvent;
import org.spongepowered.api.event.entity.living.LivingChangeHealthEvent;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.entity.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * Created by NeumimTo on 12.2.2015.
 */

@Listener
public class BasicListener {

    @Inject
    private CharacterService characterService;

    @Inject
    private Game game;

    @Inject
    private LoggingService logger;

    @Inject
    private InventoryService inventoryService;

    @Inject
    private EffectService effectService;

    @Subscribe
    public void onPlayerLogin(PlayerJoinEvent event) {
        UUID id = event.getEntity().getUniqueId();
        characterService.putInLoadQueue(id);
        HealthData healthData = event.getEntity().getHealthData();
        healthData.set(Keys.MAX_HEALTH,20d);
        healthData.set(Keys.HEALTH,20d);
        event.getEntity().offer(healthData);
    }

    @Subscribe
    public void onPlayerQuit(PlayerQuitEvent event) {
        IActiveCharacter character = characterService.getCharacter(event.getEntity().getUniqueId());
        if (character.isStub())
            return;

        characterService.putInSaveQueue(character.getCharacterBase());
        for (IEffect effect : character.getEffects()) {
            effectService.purgeEffect(effect);
        }
        characterService.removeCachedWrapper(event.getEntity().getUniqueId());
    }

    @Subscribe
    public void onInventoryOpen(PlayerBanEvent event) {
        if (PluginConfig.REMOVE_PLAYERDATA_AFTER_PERMABAN) {
            if (!event.getBan().getExpirationDate().isPresent()) {
                characterService.removePlayerData(event.getEntity().getUniqueId());
            }
        }
    }

    @Subscribe
    public void onPreDamage(LivingChangeHealthEvent event) {

    }
}
