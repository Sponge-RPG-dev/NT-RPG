package cz.neumimto;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.effects.EnderPearlEffect;
import cz.neumimto.effects.ManaDrainEffect;
import cz.neumimto.effects.ResoluteTechniqueEffect;
import cz.neumimto.effects.negative.StunEffect;
import cz.neumimto.effects.positive.*;
import cz.neumimto.events.CriticalStrikeEvent;
import cz.neumimto.events.DamageDodgedEvent;
import cz.neumimto.events.ManaDrainEvent;
import cz.neumimto.model.*;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.utils.rng.XORShiftRnd;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.entities.IEntityType;
import cz.neumimto.rpg.entities.IReservable;
import cz.neumimto.rpg.api.events.damage.DamageIEntityEarlyEvent;
import cz.neumimto.rpg.api.events.damage.DamageIEntityLateEvent;
import cz.neumimto.rpg.api.events.damage.IEntityWeaponDamageEarlyEvent;
import cz.neumimto.rpg.api.events.effect.EffectApplyEvent;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.sponge.properties.SpongeDefaultProperties;
import cz.neumimto.rpg.sponge.properties.SpongePropertyService;
import cz.neumimto.skills.active.GrapplingHook;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.EnderPearl;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.arrow.TippedArrow;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.CollideEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by ja on 21.5.2016.
 */
@Singleton
@SuppressWarnings("unchecked")
@ResourceLoader.ListenerClass
public class SkillListener {

	@Inject
	private NtRpgPlugin plugin;

	@Inject
	private CharacterService characterService;

	@Inject
	private EntityService entityService;

	@Inject
	private EffectService effectService;

	@Inject
	private SpongePropertyService spongePropertyService;

	@Inject
	private Game game;

	@Listener
	public void onKill(DestructEntityEvent.Death event) {
		Optional<Player> first = event.getCause().first(Player.class);
		if (first.isPresent()) {
			Player player = first.get();
			IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
			if (!character.isStub()) {
				IEffectContainer container = character.getEffect(LifeAfterKillEffect.name);
				if (container != null) {
					float l = (float) container.getStackedValue();
					entityService.healEntity(character, l, null);
				}
			}
		}
	}

	@Listener
	@SuppressWarnings("unchecked")
	public void onEntityDamage(DamageIEntityEarlyEvent event, @First EntityDamageSource damageSource) {
		IEntity source;
		if (damageSource.getSource() instanceof Projectile)
			source = entityService.get((Entity) ((Projectile) damageSource.getSource()).getShooter());
		else source = entityService.get(damageSource.getSource());

		//invis
		if (event.getTarget().hasEffect(Invisibility.name)) {
			effectService.removeEffectContainer(event.getTarget().getEffect(Invisibility.name), event.getTarget());
		}
		if (source.hasEffect(Invisibility.name)) {
			effectService.removeEffectContainer(event.getTarget().getEffect(Invisibility.name), source);
		}

		//shadowrun
		if (event.getTarget().hasEffect(ShadowRunEffect.name)) {
			effectService.removeEffectContainer(event.getTarget().getEffect(ShadowRunEffect.name), event.getTarget());
		}
		if (source.hasEffect(ShadowRunEffect.name) && damageSource.getType() != DamageTypes.FALL) {
			IEffectContainer<ShadowRunModel, ShadowRunEffect> container = source.getEffect(ShadowRunEffect.name);
			if (damageSource.getType() == DamageTypes.ATTACK) {
				ShadowRunModel stackedValue = container.getStackedValue();
				event.setDamage(stackedValue.damage + event.getDamage() * stackedValue.attackmult);
			}
			effectService.removeEffectContainer(container, source);
		}
	}

	@Listener(order = Order.LAST)
	public void onEntityDamageLast(DamageIEntityLateEvent event) {
		if (event.getTarget().hasEffect(ManaShieldEffect.name)) {
			IEffectContainer<ManaShieldEffectModel, ManaShieldEffect> effect = event.getTarget().getEffect(ManaShieldEffect.name);
			if (event.getTarget().getType() == IEntityType.CHARACTER) {
				IActiveCharacter character = (IActiveCharacter) event.getTarget();
				double value = character.getMana().getValue();
				double futuremana = value - effect.getStackedValue().reductionCost;
				if (futuremana <= 0) {
					character.getMana().setValue(0);
					effectService.removeEffectContainer(effect, event.getTarget());
				} else {
					character.getMana().setValue(futuremana);
				}
			}
			event.setDamage(event.getDamage() - effect.getStackedValue().reduction);
		}
	}


	@Listener
	public void onWeaponDamage(IEntityWeaponDamageEarlyEvent event, @First EntityDamageSource damageSource) {
		IEntity source = entityService.get(damageSource.getSource());
		IEntity target = event.getTarget();

		if (target.hasEffect(DampenEffect.name)) {
			if (source.getType() == IEntityType.CHARACTER) {
				IActiveCharacter character = (IActiveCharacter) event.getSource();
				IEffectContainer<Double, DampenEffect> effect = target.getEffect(DampenEffect.name);
				if (character.getMana().getValue() <= effect.getStackedValue()) {
					event.setCancelled(true);
					return;
				}
			}
		}
		XORShiftRnd random = new XORShiftRnd();
		//dodge
		if (target.hasEffect(DodgeEffect.name)) {
			IEffectContainer<Float, DodgeEffect> effect = source.getEffect(DodgeEffect.name);
			Float stackedValue = effect.getStackedValue();
			float next = random.nextFloat(100);
			if (stackedValue <= next) {
				DamageDodgedEvent event0 = new DamageDodgedEvent(source, target, effect);
				boolean t = game.getEventManager().post(event0);
				if (t) {
					event.setCancelled(t);
					event.setDamage(0);
					return;
				}
			}
		}
		//bash
		if (source.hasEffect(Bash.name)) {
			IEffectContainer<BashModel, Bash> effect = source.getEffect(Bash.name);
			BashModel stackedValue = effect.getStackedValue();
			long time = System.currentTimeMillis();
			float reduced = entityService.getEntityProperty(target, SpongeDefaultProperties.cooldown_reduce_mult);
			long cooldown = (long) (reduced * (float) stackedValue.cooldown);
			if (stackedValue.lasttime + cooldown <= time) {
				int rnd = random.nextInt(100);
				if (rnd <= stackedValue.chance) {
					StunEffect stunEffect = new StunEffect(target, stackedValue.stunDuration);
					if (stackedValue.damage != 0) {
						event.setDamage(event.getDamage() + stackedValue.damage);
					}
					if (effectService.addEffect(stunEffect, effect, source)) {
						stackedValue.lasttime = time;
					}
				}
			}
		}
		//critical
		if (source.hasEffect(CriticalEffect.name)) {
			IEffectContainer<CriticalEffectModel, CriticalEffect> effect = source.getEffect(CriticalEffect.name);
			CriticalEffectModel stackedValue = effect.getStackedValue();
			if (stackedValue.chance <= random.nextInt(100)) {
				CriticalStrikeEvent criticalStrikeEvent =
						new CriticalStrikeEvent(source, target, stackedValue.mult * event.getDamage());
				if (!game.getEventManager().post(criticalStrikeEvent)) {
					event.setDamage(criticalStrikeEvent.getDamage());
				}
			}
		}
		//manadrain
		if (target.getType() == IEntityType.CHARACTER) {
			IActiveCharacter character = (IActiveCharacter) target;
			if (character.hasEffect(ManaDrainEffect.name)) {
				IEffectContainer<Float, ManaDrainEffect> container = character.getEffect(ManaDrainEffect.name);
				IReservable mana = character.getMana();
				double k = character.getMana().getValue() - container.getStackedValue();
				ManaDrainEvent mde = new ManaDrainEvent(source, character, k);
			}
		}
	}

	@Listener
	public void onStunApply(EffectApplyEvent<StunEffect> event, @First IEntity source) {
		float f = entityService.getEntityProperty(source, AdditionalProperties.stun_duration_mult);
		event.getEffect().setDuration((long) (f * event.getEffect().getDuration()));
	}

	@Listener(order = Order.LATE)
	@IsCancelled(Tristate.UNDEFINED)
	public void onCriticalStrike(CriticalStrikeEvent event) {
		if (event.isCancelled()) {
			if (event.getSource().hasEffect(ResoluteTechniqueEffect.name)) {
				event.setCancelled(false);
			}
		}
	}

	@Listener(order = Order.LATE)
	@IsCancelled(Tristate.UNDEFINED)
	public void onDodge(DamageDodgedEvent event) {
		if (event.isCancelled()) {
			if (event.getSource().hasEffect(ResoluteTechniqueEffect.name)) {
				event.setCancelled(false);
			}
		}
	}

	@Listener(order = Order.LAST)
	@IsCancelled(Tristate.FALSE)
	public void onManaDrain(ManaDrainEvent mde) {
		IReservable mana = mde.getTarget().getMana();
		double k = mana.getValue() - mde.getAmountDrained() <= 0 ? 0 : mana.getValue() - mde.getAmountDrained();
		mana.setValue(k);
	}

	@Listener(order = Order.FIRST)
	public void onItemConsume(UseItemStackEvent.Start event, @Root(typeFilter = Player.class) Player player) {
		Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
		if (itemInHand.isPresent()) {
			ItemStack itemStack = itemInHand.get();
			if (itemStack.getType() == ItemTypes.POTION) {
				IActiveCharacter character = characterService.getCharacter(player);
				if (character.hasEffect(PotionEffect.name)) {
					Optional<PotionEffectData> potionEffectData = itemStack.get(PotionEffectData.class);
					if (potionEffectData.isPresent()) {
						PotionEffectData o = potionEffectData.get();
						ListValue<org.spongepowered.api.effect.potion.PotionEffect> effects = o.effects();
						if (effects.size() >= 1) {
							org.spongepowered.api.effect.potion.PotionEffect potionEffect = effects.get(0);
							IEffectContainer<PotionEffectModel, PotionEffect> effect = character.getEffect(PotionEffect.name);
							PotionEffectModel value = effect.getStackedValue();
							Long aLong = value.cooldowns.get(potionEffect.getType());
							if (aLong == null) {
							//	player.sendMessage(SkillLocalization.CANNOT_DRIK_POTION_TYPE.toText(Arg.arg("potion", potionEffect.getClassType().getName())));
								event.setCancelled(true);
								return;
							}
							long l = System.currentTimeMillis();
							Long next = value.nextUseTime.get(potionEffect.getType());
							if (next != null && next < l) {
						//		player.sendMessage(SkillLocalization.CANNOT_DRIK_POTION_TYPE_COOLDOWN.toText(Arg.arg("potion", potionEffect.getClassType().getName())));
								event.setCancelled(true);
								return;
							}
						}
					}
				} else {
					event.setCancelled(true);
				}
			}
		}
	}


	@Listener(order = Order.LATE)
	public void onItemConsumerFinish(UseItemStackEvent.Finish event,
									 @Root(typeFilter = Player.class) Player player) {
		ItemStackSnapshot itemStack = event.getItemStackInUse();
		if (itemStack.getType() == ItemTypes.POTION
				|| itemStack.getType() == ItemTypes.SPLASH_POTION
				|| itemStack.getType() == ItemTypes.LINGERING_POTION) {
			IActiveCharacter character = characterService.getCharacter(player);
			if (character.hasEffect(PotionEffect.name)) {
				Optional<PotionEffectData> potionEffectData = itemStack.createStack().get(PotionEffectData.class);
				if (potionEffectData.isPresent()) {
					PotionEffectData o = potionEffectData.get();
					ListValue<org.spongepowered.api.effect.potion.PotionEffect> effects = o.effects();
					if (effects.size() > 0) {
						IEffectContainer effect = character.getEffect(PotionEffect.name);
						PotionEffectModel value = (PotionEffectModel) effect.getStackedValue();
						value.nextUseTime.put(effects.get(0).getType(), value.cooldowns.get(effects.get(0).getType()) + System.currentTimeMillis());
					}
				}
			} else {
				event.setCancelled(true);
			}
		}
	}

	@Listener
	public void onBlockInteract(InteractBlockEvent event, @First(typeFilter = Player.class) Player player) {
		if (event.getTargetBlock().getState().getType() == BlockTypes.BREWING_STAND) {
			IActiveCharacter character = characterService.getCharacter(player);
			if (!character.hasEffect(AlchemyEffect.name)) {
				event.setCancelled(true);
			}
		}
	}

	@Listener
	public void event(CollideEvent.Impact event, @First(typeFilter = TippedArrow.class) TippedArrow arrow) {
		if (GrapplingHook.cache.containsKey(arrow.getUniqueId())) {
			IActiveCharacter character = characterService.getCharacter(((Player) arrow.getShooter()).getUniqueId());
			Player player = character.getPlayer();
			Vector3d velocity = player.getLocation().getPosition().sub(event.getImpactPoint().getPosition()).normalize().mul(-2);


			player.getLocation().getExtent().playSound(SoundTypes.BLOCK_ANVIL_LAND, event.getImpactPoint().getPosition(), 1);

			Location<World> startLoc = player.getLocation();
			double distToGrapple = startLoc.getPosition().distance(event.getImpactPoint().getPosition());

			Sponge.getScheduler().createTaskBuilder()
					.execute(new Consumer<Task>() {
						int ticks = 0;
						Location<World> prev;
						double distTraveled = 0.0D;

						@Override
						public void accept(Task task) {
							if (player.getLocation().getPosition().distance(event.getImpactPoint().getPosition()) <= 2.0D
									|| distTraveled > distToGrapple
									|| player.get(Keys.IS_SNEAKING).orElse(Boolean.FALSE)) {
								player.setVelocity(new Vector3d(0, 0, 0));
								GrapplingHook.cache.remove(arrow.getUniqueId());
								arrow.remove();
								task.cancel();
								return;
							}
							if (prev != null) {
								if (player.getLocation().getPosition().distance(prev.getPosition()) <= 0.25) {
									ticks++;
								}
							}
							distTraveled = player.getLocation().getPosition().distance(startLoc.getPosition());
							prev = player.getLocation();
							player.setVelocity(velocity);
							player.getLocation().getExtent().playSound(SoundTypes.BLOCK_TRIPWIRE_DETACH, event.getImpactPoint().getPosition(), 1);
						}
					}).interval(50, TimeUnit.MILLISECONDS).submit(plugin);
		}
	}


	@Listener
	public void onEntityTeleport(MoveEntityEvent.Teleport event, @First(typeFilter = EnderPearl.class) EnderPearl ep) {
		Entity targetEntity = event.getTargetEntity();
		IActiveCharacter iEntity = characterService.getCharacter(targetEntity.getUniqueId());
		if (iEntity.hasEffect(EnderPearlEffect.name)) {
			EnderPearlEffect.Container container = (EnderPearlEffect.Container) iEntity.getEffect(EnderPearlEffect.name);
			if (container.getLastTimeUsed() < System.currentTimeMillis() - container.getStackedValue()) {
				Gui.sendCooldownMessage(iEntity, "Ender Pearl", System.currentTimeMillis() - container.getStackedValue());
				event.setCancelled(true);
			} else {
				container.setLastTimeUsed(System.currentTimeMillis());
			}
		}
	}
}
