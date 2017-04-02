package cz.neumimto.rpg.listeners;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.IEntityType;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.damage.SkillDamageSource;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.entities.IMob;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.ExperienceSource;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

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

    @Inject
    private InventoryService inventoryService;

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Auth event) {
        if (event.isCancelled())
            return;
        UUID id = event.getProfile().getUniqueId();
        characterService.loadPlayerData(id);
    }
    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Join event) {
      //  IActiveCharacter character = characterService.getCharacter(event.getTargetEntity().getUniqueId());
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


    @Listener
    public void onEntityDespawn(DestructEntityEvent event) {
        Entity targetEntity = event.getTargetEntity();
        if (targetEntity.getType() == EntityTypes.PLAYER) {
            IActiveCharacter character = characterService.getCharacter(targetEntity.getUniqueId());
            if (character.isStub())
                return;
            for (IEffectContainer<Object, IEffect<Object>> iEffectIEffectContainer : character.getEffects()) {
                iEffectIEffectContainer.forEach(a -> effectService.stopEffect(a));
            }
        } else {
            if (!event.getTargetEntity().get(Keys.HEALTH).isPresent()) {
                return;
            }

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
                    if (character.hasParty()) {
                        exp *= PluginConfig.PARTY_EXPERIENCE_MULTIPLIER;
                        double dist = Math.pow(PluginConfig.PARTY_EXPERIENCE_SHARE_DISTANCE, 2);
                        Set<IActiveCharacter> set = new HashSet<>();
                        for (IActiveCharacter member : character.getParty().getPlayers()) {
                            if (member.getPlayer().getLocation().getPosition()
                                    .distanceSquared(character.getPlayer().getLocation().getPosition()) <= dist) {
                                set.add(member);
                            }
                        }
                        exp /= set.size();
                        for (IActiveCharacter character1 : set) {
                            characterService.addExperiences(character1, exp, ExperienceSource.PVE);
                        }
                    } else {
                        characterService.addExperiences(character, exp, ExperienceSource.PVE);
                    }
                }
            }
            Optional<SkillDamageSource> sds = event.getCause().first(SkillDamageSource.class);
            if (sds.isPresent()) {

                SkillDamageSource source = sds.get();
                IActiveCharacter caster = source.getCaster();
                double exp = entityService.getExperiences(event.getTargetEntity().getType());
                characterService.addExperiences(caster, exp, ExperienceSource.PVE);
            }
            IMob mob = (IMob) entityService.get(event.getTargetEntity());
            Collection<IEffect> values = mob.getEffectMap().values();
            for (IEffect value : values) {
                effectService.stopEffect(value);
            }
            mob.detach();
            entityService.remove(event.getTargetEntity().getUniqueId());
        }
    }
}

