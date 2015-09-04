package cz.neumimto.listeners;


import com.google.common.base.Optional;
import cz.neumimto.LoggingService;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.damage.DamageService;
import cz.neumimto.effects.EffectService;
import cz.neumimto.effects.IEffect;
import cz.neumimto.inventory.InventoryService;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.ListenerClass;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by NeumimTo on 12.2.2015.
 */

@ListenerClass
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

    @Inject
    private DamageService damageService;

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Login event) {
        UUID id = event.getProfile().getUniqueId();
        characterService.putInLoadQueue(id);
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();

    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        IActiveCharacter character = characterService.removeCachedWrapper(player.getUniqueId());
        if (!character.isStub()) {
            characterService.putInSaveQueue(character.getCharacterBase());
            for (IEffect effect : character.getEffects()) {
                effectService.purgeEffect(effect);
            }
        }
        /*Always reset the persistent properties back to vanilla values in a case
         some dummy decides to remove my awesome plugin :C */
        //HP
        HealthData healthData = player.getHealthData();
        healthData.set(Keys.MAX_HEALTH, 20d);
        healthData.set(Keys.HEALTH, 10d);
        player.offer(healthData);
        //player walkspeed
        //player.offer(Keys.WALK_SPEED,0.2d);

    }

    @Listener
    public void onUseBan(BanUserEvent event) {
        if (event.isCancelled())
            return;
        if (PluginConfig.REMOVE_PLAYERDATA_AFTER_PERMABAN) {
            if (!event.getBan().getExpirationDate().isPresent()) {
                characterService.removePlayerData(event.getTargetUser().getUniqueId());
            }
        }
    }

    @Listener
    public void onItemChange() {

    }

    @Listener(order = Order.BEFORE_POST)
    public void onPreDamage(DamageEntityEvent event) {
        if (event.isCancelled())
            return;
        final Cause cause = event.getCause();
        Optional<EntityDamageSource> first = cause.getFirst(EntityDamageSource.class);
        if (event.getBaseDamage() <= 0) {
            return;
        }
        if (first.isPresent()) {
            Entity targetEntity = event.getTargetEntity();
            EntityDamageSource entityDamageSource = first.get();
            Entity source = entityDamageSource.getSource();
            if (source.getType() == EntityTypes.PLAYER) {
                Player player = (Player) source;
                IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
                if (character.isStub() && !PluginConfig.ALLOW_COMBAT_FOR_CHARACTERLESS_PLAYERS) {
                    event.setCancelled(true);
                    return;
                }
                Optional<ItemStack> itemInHand = player.getItemInHand();
                if (itemInHand.isPresent()) {
                    character.getMainHand().getDamage();
                }
            }
        }
    }
}
