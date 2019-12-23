package cz.neumimto.rpg.spigot.listeners;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.exp.ExperienceService;
import cz.neumimto.rpg.common.exp.ExperienceSources;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.inventory.SpigotInventoryService;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.projectiles.ProjectileSource;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

@Singleton
@ResourceLoader.ListenerClass
public class SpigotEntityLifecycleListener implements Listener {

    @Inject
    private SpigotCharacterService characterService;

    @Inject
    private EffectService effectService;

    @Inject
    private SpigotEntityService entityService;

    @Inject
    private SpigotInventoryService spigotInventoryService;

    @Inject
    private ExperienceService experienceService;

    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        //  IActiveCharacter character = characterService.getTarget(event.getTarget().getUniqueId());
        characterService.loadPlayerData(event.getUniqueId(), event.getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        IActiveCharacter character = characterService.removeCachedWrapper(player.getUniqueId());
        if (!character.isStub()) {
            Location loc = player.getLocation();
            World ex = loc.getWorld();
            character.getCharacterBase().setLastKnownPlayerName(event.getPlayer().getName());
            character.updateLastKnownLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), ex.getName());
            characterService.putInSaveQueue(character.getCharacterBase());
            effectService.removeAllEffects(character);
			/*Always reset the persistent properties back to vanilla values in a case
             some dummy decides to remove my awesome plugin :C */
            //Utils.resetPlayerToDefault(player);
        }
    }

    @EventHandler
    public void onUserBan(PlayerQuitEvent event) {
        PluginConfig pluginConfig = Rpg.get().getPluginConfig();
        if (pluginConfig.REMOVE_PLAYERDATA_AFTER_PERMABAN) {
            if (!event.getPlayer().isBanned()) {
                characterService.removePlayerData(event.getPlayer().getUniqueId());
            }
        }
    }


    @EventHandler
    public void onEntityDespawn(EntityDeathEvent event) {
        Entity targetEntity = event.getEntity();
        if (targetEntity.getType() == EntityType.PLAYER) {
            IActiveCharacter character = characterService.getCharacter(targetEntity.getUniqueId());
            if (character.isStub()) {
                return;
            }
            effectService.removeAllEffects(character);
        } else {

            EntityDamageEvent lastDamageCause = targetEntity.getLastDamageCause();
            Entity entity = lastDamageCause.getEntity();
            Entity source = null;
            if (entity instanceof LivingEntity) {
                source = entity;
            } else if (entity instanceof Projectile) {
                Projectile projectile = (Projectile) entity;
                ProjectileSource shooter = projectile.getShooter();
                if (shooter instanceof LivingEntity) {
                    source = (Entity) shooter;
                }
            }

            if (source != null) {
                ISpigotCharacter character = characterService.getCharacter(source.getUniqueId());
                if (character != null && source instanceof LivingEntity) {

                    double exp = entityService.getExperiences(targetEntity.getWorld().getName(), targetEntity.getType().getKey().getKey());

                    exp += character.getExperienceBonusFor(targetEntity.getLocation().getWorld().getName(), targetEntity.getType().getKey().getKey());
                    String experienceSource = targetEntity.getType() == EntityType.PLAYER ? ExperienceSources.PVP : ExperienceSources.PVE;

                    if (exp != 0) {
                        if (character.hasParty()) {
                            PluginConfig pluginConfig = Rpg.get().getPluginConfig();
                            exp *= pluginConfig.PARTY_EXPERIENCE_MULTIPLIER;
                            double dist = Math.pow(pluginConfig.PARTY_EXPERIENCE_SHARE_DISTANCE, 2);
                            Set<ISpigotCharacter> set = new HashSet<>();
                            for (ISpigotCharacter member : character.getParty().getPlayers()) {
                                Player player = member.getPlayer();
                                if (player.getLocation().distanceSquared(character.getPlayer().getLocation()) <= dist) {
                                    set.add(member);
                                }
                            }
                            exp /= set.size();
                            for (ISpigotCharacter character1 : set) {
                                characterService.addExperiences(character1, exp, experienceSource);
                            }
                        } else {
                            characterService.addExperiences(character, exp, experienceSource);
                        }
                    }
                }
            }

            entityService.remove(event.getEntity());
        }
    }

    @EventHandler
    public void onChunkDespawn(ChunkUnloadEvent event) {
        Entity[] entities = event.getChunk().getEntities();
        for (Entity entity : entities) {
            entityService.remove(entity.getUniqueId());
        }
    }

    @EventHandler
    public void onCatchFish(PlayerFishEvent event) {
        Entity caught = event.getCaught();
        if (caught != null) {
            Double fishingExperience = experienceService.getFishingExperience(caught.getType().name());
            if (fishingExperience != null) {
                ISpigotCharacter character = characterService.getCharacter(event.getPlayer());
                characterService.addExperiences(character, fishingExperience, ExperienceSources.FISHING);
            }
        }
    }

}