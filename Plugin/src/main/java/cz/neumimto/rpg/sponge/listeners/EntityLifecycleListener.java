package cz.neumimto.rpg.sponge.listeners;

import com.google.inject.Singleton;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.IEntityType;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.exp.ExperienceSources;
import cz.neumimto.rpg.sponge.damage.SkillDamageSource;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterServise;
import cz.neumimto.rpg.sponge.inventory.SpongeInventoryService;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.event.world.chunk.UnloadChunkEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static cz.neumimto.rpg.sponge.NtRpgPlugin.pluginConfig;

/**
 * Created by NeumimTo on 3.1.2016.
 */
@Singleton
@ResourceLoader.ListenerClass
public class EntityLifecycleListener {

    @Inject
    private SpongeCharacterServise characterService;

    @Inject
    private EffectService effectService;

    @Inject
    private EntityService entityService;

    @Inject
    private SpongeInventoryService spongeInventoryService;

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Auth event) {
        if (event.isCancelled()) {
            return;
        }
        UUID id = event.getProfile().getUniqueId();
        characterService.loadPlayerData(id, event.getProfile().getName().get());
    }

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Join event) {
        //  IActiveCharacter character = characterService.getTarget(event.getTarget().getUniqueId());
        characterService.checkPlayerDataStatus(event.getTargetEntity().getUniqueId());
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
        if (pluginConfig.REMOVE_PLAYERDATA_AFTER_PERMABAN) {
            if (!event.getBan().getExpirationDate().isPresent()) {
                characterService.removePlayerData(event.getTargetUser().getUniqueId());
            }
        }
    }


    @Listener
    public void onEntityDespawn(DestructEntityEvent.Death event) {
        Entity targetEntity = event.getTargetEntity();
        if (targetEntity.getType() == EntityTypes.PLAYER) {
            IActiveCharacter character = characterService.getCharacter(targetEntity.getUniqueId());
            if (character.isStub()) {
                return;
            }
            effectService.removeAllEffects(character);
        } else {
            if (!event.getTargetEntity().get(Keys.HEALTH).isPresent()) {
                return;
            }

            Entity source = null;
            Optional<IndirectEntityDamageSource> ieds = event.getCause().first(IndirectEntityDamageSource.class);
            if (ieds.isPresent()) {
                source = ieds.get().getIndirectSource();
            } else {
                Optional<EntityDamageSource> first = event.getCause().first(EntityDamageSource.class);
                if (first.isPresent()) {
                    EntityDamageSource eds = first.get();
                    source = eds.getSource();
                }
            }


            if (source != null) {
                ISpongeCharacter character = characterService.getCharacter(source.getUniqueId());
                if (character != null) {
                    if (!Utils.isLivingEntity(source)) {
                        return;
                    }
                    double exp = entityService.getExperiences(targetEntity.getWorld().getName(), targetEntity.getType().getId());

                    exp += character.getExperienceBonusFor(targetEntity.getLocation().getExtent().getName(), targetEntity.getType());
                    String experienceSource = targetEntity.getType() == EntityTypes.PLAYER ? ExperienceSources.PVP : ExperienceSources.PVE;

                    if (character.hasParty()) {
                        exp *= pluginConfig.PARTY_EXPERIENCE_MULTIPLIER;
                        double dist = Math.pow(pluginConfig.PARTY_EXPERIENCE_SHARE_DISTANCE, 2);
                        Set<ISpongeCharacter> set = new HashSet<>();
                        for (ISpongeCharacter member : character.getParty().getPlayers()) {
                            Player player = member.getPlayer();
                            if (player.getLocation().getPosition()
                                    .distanceSquared(character.getPlayer().getLocation().getPosition()) <= dist) {
                                set.add(member);
                            }
                        }
                        exp /= set.size();
                        for (ISpongeCharacter character1 : set) {
                            characterService.addExperiences(character1, exp, experienceSource);
                        }
                    } else {
                        characterService.addExperiences(character, exp, experienceSource);
                    }

                }
            }

            Optional<SkillDamageSource> sds = event.getCause().first(SkillDamageSource.class);
            if (sds.isPresent()) {
                SkillDamageSource skillDamageSource = sds.get();
                IEntity caster = skillDamageSource.getSourceIEntity();
                if (caster.getType() == IEntityType.CHARACTER) {
                    double exp = entityService.getExperiences(event.getTargetEntity().getWorld().getName(), event.getTargetEntity().getType().getId());
                    characterService.addExperiences((ISpongeCharacter) caster, exp, ExperienceSources.PVE);
                }
            }
            entityService.remove(event.getTargetEntity().getUniqueId());
        }
    }

    @Listener
    public void onChunkDespawn(UnloadChunkEvent event) {
        entityService.remove(event.getTargetChunk().getEntities(Utils::isLivingEntity));
    }
}

