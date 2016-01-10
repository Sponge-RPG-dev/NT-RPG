package cz.neumimto.listeners;

import cz.neumimto.IEntity;
import cz.neumimto.IEntityType;
import cz.neumimto.ResourceLoader;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.EffectService;
import cz.neumimto.effects.IEffect;
import cz.neumimto.entities.EntityService;
import cz.neumimto.entities.IMob;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.ExperienceSource;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.utils.Utils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by NeumimTo on 3.1.2016.
 */
@ResourceLoader.ListenerClass
public class EntityLifecycleListener {

    @Inject
    private CharacterService characterService;

    @Inject
    private EffectService effectService;

    @Inject
    private EntityService entityService;

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Auth event) {
        if (event.isCancelled())
            return;
        UUID id = event.getProfile().getUniqueId();
        characterService.loadPlayerData(id);
    }

    @Listener
    public void onPlayerRespawn(RespawnPlayerEvent event) {
        //todo
    }

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Join event) {
        IActiveCharacter character = characterService.getCharacter(event.getTargetEntity().getUniqueId());
        characterService.assignPlayerToCharacter(event.getTargetEntity());
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        IActiveCharacter character = characterService.removeCachedWrapper(player.getUniqueId());
        if (!character.isStub()) {
            Location loc = player.getLocation();
            World ex = (World) loc.getExtent();
            character.getCharacterBase().setLastKnownPlayerName(event.getTargetEntity().getName());
            character.updateLastKnownLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockY(), ex.getName());
            characterService.putInSaveQueue(character.getCharacterBase());
            effectService.removeAllEffects(character);
            /*Always reset the persistent properties back to vanilla values in a case
             some dummy decides to remove my awesome plugin :C */
            Utils.resetPlayerToDefault(player);
        }
    }

    @Listener
    public void onUserBan(BanUserEvent event) {
        if (PluginConfig.REMOVE_PLAYERDATA_AFTER_PERMABAN) {
            if (!event.getBan().getExpirationDate().isPresent()) {
                characterService.removePlayerData(event.getTargetUser().getUniqueId());
            }
        }
    }


    @Listener(order = Order.LAST)
    public void onEntityDespawn(DestructEntityEvent event) {
        Entity targetEntity = event.getTargetEntity();
        if (targetEntity.getType() == EntityTypes.PLAYER) {
            IActiveCharacter character = characterService.getCharacter(targetEntity.getUniqueId());
            if (character.isStub())
                return;

        } else {
            if (Utils.isLivingEntity(event.getTargetEntity())) {
                IMob mob = (IMob) entityService.get(event.getTargetEntity());
                Collection<IEffect> values = mob.getEffectMap().values();
                for (IEffect value : values) {
                    effectService.stopEffect(value);
                }
                mob.detach();
                entityService.remove(event.getTargetEntity().getUniqueId());
                System.out.println("Clearing references of" + mob);
            }
        }
    }

    @Listener(order = Order.BEFORE_POST)
    public void onEntityDestruct(DestructEntityEvent.Death event) {
        Entity targetEntity = event.getTargetEntity();
        if (targetEntity.getType() == EntityTypes.PLAYER) {
            IActiveCharacter character = characterService.getCharacter(targetEntity.getUniqueId());
            if (character.isStub())
                return;
            //todo pvp exp
            character.getEffects().stream().forEach(iEffect -> effectService.removeEffect(iEffect, character));

        } else {
            Optional<EntityDamageSource> first = event.getCause().first(EntityDamageSource.class);
            if (first.isPresent()) {

                EntityDamageSource entityDamageSource = first.get();
                if (!Utils.isLivingEntity(entityDamageSource.getSource())) {
                    return;
                }
                double exp = entityService.getExperiences(targetEntity.getType());
                //todo share in party
                IEntity source = entityService.get(entityDamageSource.getSource());
                if (source.getType() == IEntityType.CHARACTER) {
                    IActiveCharacter character = (IActiveCharacter) source;
                    characterService.addExperiences(character, exp, ExperienceSource.PVE);
                }
            }
        }
    }
}
