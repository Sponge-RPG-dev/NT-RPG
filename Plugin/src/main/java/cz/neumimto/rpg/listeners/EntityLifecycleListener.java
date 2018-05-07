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
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
	public void onEntityDespawn(DestructEntityEvent.Death event) {
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

				if (!Utils.isLivingEntity(source)) {
					return;
				}
				double exp = entityService.getExperiences(targetEntity);

				IActiveCharacter character = characterService.getCharacter(source.getUniqueId());
				ExperienceSource experienceSource = targetEntity.getType() == EntityTypes.PLAYER ? ExperienceSource.PVP : ExperienceSource.PVE;
				if (character != null) {
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
				IEntity caster = skillDamageSource.getCaster();
				if (caster.getType() == IEntityType.CHARACTER) {
					double exp = entityService.getExperiences(event.getTargetEntity());
					characterService.addExperiences((IActiveCharacter) caster, exp, ExperienceSource.PVE);
				}
			}
			entityService.remove(event.getTargetEntity().getUniqueId());
		}
	}
}

