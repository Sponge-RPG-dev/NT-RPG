package cz.neumimto;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.EnderPearlEffect;
import cz.neumimto.effects.ManaDrainEffect;
import cz.neumimto.effects.ResoluteTechniqueEffect;
import cz.neumimto.effects.negative.StunEffect;
import cz.neumimto.effects.positive.AlchemyEffect;
import cz.neumimto.effects.positive.Bash;
import cz.neumimto.effects.positive.CriticalEffect;
import cz.neumimto.effects.positive.DamageToMana;
import cz.neumimto.effects.positive.DampenEffect;
import cz.neumimto.effects.positive.DodgeEffect;
import cz.neumimto.effects.positive.LifeAfterKillEffect;
import cz.neumimto.effects.positive.PotionEffect;
import cz.neumimto.effects.positive.ShadowRunEffect;
import cz.neumimto.events.CriticalStrikeEvent;
import cz.neumimto.events.DamageDodgedEvent;
import cz.neumimto.events.ManaDrainEvent;
import cz.neumimto.events.StunApplyEvent;
import cz.neumimto.model.BashModel;
import cz.neumimto.model.CriticalEffectModel;
import cz.neumimto.model.PotionEffectModel;
import cz.neumimto.model.ShadowRunModel;
import cz.neumimto.rpg.IEntityType;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.effects.common.positive.Invisibility;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.events.INEntityDamageEvent;
import cz.neumimto.rpg.events.INEntityWeaponDamageEvent;
import cz.neumimto.rpg.events.character.CharacterDamageEntityEvent;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.IReservable;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.utils.XORShiftRnd;
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
import org.spongepowered.api.entity.projectile.arrow.TippedArrow;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.CollideEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by ja on 21.5.2016.
 */
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
	private PropertyService propertyService;

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
	public void onDamage(CharacterDamageEntityEvent event) {
		IActiveCharacter character = characterService.getCharacter(event.getDamaged().getUniqueId());
		if (!character.isStub()) {
			IEffectContainer container = character.getEffect(DamageToMana.name);
			if (container != null) {
				double percentage = (double) container.getStackedValue();

			}
		}
	}

	@Listener
	@SuppressWarnings("unchecked")
	public void onEntityDamage(INEntityDamageEvent event) {
		//invis
		if (event.getTarget().hasEffect(Invisibility.name)) {
			effectService.removeEffectContainer(event.getTarget().getEffect(Invisibility.name), event.getTarget());
		}
		if (event.getSource().hasEffect(Invisibility.name)) {
			effectService.removeEffectContainer(event.getTarget().getEffect(Invisibility.name), event.getSource());
		}


		//shadowrun
		if (event.getTarget().hasEffect(ShadowRunEffect.name)) {
			effectService.removeEffectContainer(event.getTarget().getEffect(ShadowRunEffect.name), event.getTarget());
		}
		if (event.getSource().hasEffect(ShadowRunEffect.name) && event.getType() != DamageTypes.FALL) {
			IEffectContainer<ShadowRunModel, ShadowRunEffect> container = event.getSource().getEffect(ShadowRunEffect.name);
			if (event.getType() == DamageTypes.ATTACK) {
				ShadowRunModel stackedValue = container.getStackedValue();
				event.setDamage(stackedValue.damage + event.getDamage() * stackedValue.attackmult);
			}
			effectService.removeEffectContainer(container, event.getSource());
		}

	}

	@Listener
	@SuppressWarnings("unchecked")
	public void onWeaponDamage(INEntityWeaponDamageEvent event) {
		if (event.getTarget().hasEffect(DampenEffect.name)) {
			if (event.getSource().getType() == IEntityType.CHARACTER) {
				IActiveCharacter character = (IActiveCharacter) event.getSource();
				IEffectContainer<Double, DampenEffect> effect = event.getTarget().getEffect(DampenEffect.name);
				if (character.getMana().getValue() <= effect.getStackedValue()) {
					event.setCancelled(true);
					return;
				}
			}
		}
		XORShiftRnd random = new XORShiftRnd();
		//dodge
		if (event.getTarget().hasEffect(DodgeEffect.name)) {
			IEffectContainer<Float, DodgeEffect> effect = event.getSource().getEffect(DodgeEffect.name);
			Float stackedValue = effect.getStackedValue();
			float next = random.nextFloat(100);
			if (stackedValue <= next) {
				DamageDodgedEvent event0 = new DamageDodgedEvent(event.getSource(), event.getTarget(), effect);
				boolean t = game.getEventManager().post(event0);
				if (t) {
					event.setCancelled(t);
					event.setDamage(0);
					return;
				}
			}
		}
		//bash
		if (event.getSource().hasEffect(Bash.name)) {
			IEffectContainer<BashModel, Bash> effect = event.getSource().getEffect(Bash.name);
			BashModel stackedValue = effect.getStackedValue();
			long time = System.currentTimeMillis();
			float reduced = entityService.getEntityProperty(event.getTarget(), DefaultProperties.cooldown_reduce);
			long cooldown = (long) (reduced * (float) stackedValue.cooldown);
			if (stackedValue.lasttime + cooldown <= time) {
				int rnd = random.nextInt(100);
				if (rnd <= stackedValue.chance) {
					StunEffect stunEffect = new StunEffect(event.getTarget(), stackedValue.stunDuration);
					if (stackedValue.damage != 0) {
						event.setDamage(event.getDamage() + stackedValue.damage);
					}
					if (!game.getEventManager().post(new StunApplyEvent(event.getSource(), event.getTarget(), stunEffect))) {
						effectService.addEffect(stunEffect, event.getTarget(), effect);
						stackedValue.lasttime = time;
					}
				}
			}
		}
		//critical
		if (event.getSource().hasEffect(CriticalEffect.name)) {
			IEffectContainer<CriticalEffectModel, CriticalEffect> effect = event.getSource().getEffect(CriticalEffect.name);
			CriticalEffectModel stackedValue = effect.getStackedValue();
			if (stackedValue.chance <= random.nextInt(100)) {
				CriticalStrikeEvent criticalStrikeEvent = new CriticalStrikeEvent(event.getSource(), event.getTarget(), stackedValue.mult * event.getDamage());
				if (!game.getEventManager().post(criticalStrikeEvent)) {
					event.setDamage(event.getDamage() + criticalStrikeEvent.getDamage());
				}
			}
		}
		//manadrain
		if (event.getTarget().getType() == IEntityType.CHARACTER) {
			IActiveCharacter character = (IActiveCharacter) event.getTarget();
			if (character.hasEffect(ManaDrainEffect.name)) {
				IEffectContainer<Float, ManaDrainEffect> container = character.getEffect(ManaDrainEffect.name);
				IReservable mana = character.getMana();
				double k = character.getMana().getValue() - container.getStackedValue();
				ManaDrainEvent mde = new ManaDrainEvent(event.getSource(), character, k);
			}
		}
	}

	@Listener
	public void onStunApply(StunApplyEvent event) {
		StunEffect effect = event.getEffect();
		float f = entityService.getEntityProperty(event.getSource(), AdditionalProperties.stun_duration_mult);
		effect.setDuration((long) (f * event.getEffect().getDuration()));
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
							PotionEffect effect = (PotionEffect) character.getEffect(PotionEffect.name);
							PotionEffectModel value = effect.getValue();
							if (value.cooldowns.get(potionEffect.getType()) == null) {
								event.setCancelled(true);
								return;
							}
							Long next = value.nextUseTime.get(potionEffect.getType());
							if (next > System.currentTimeMillis()) {
								event.setCancelled(true);
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
		Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
		ItemStack itemStack = itemInHand.get();
		if (itemStack.getType() == ItemTypes.POTION
				|| itemStack.getType() == ItemTypes.SPLASH_POTION
				|| itemStack.getType() == ItemTypes.LINGERING_POTION) {
			IActiveCharacter character = characterService.getCharacter(player);
			if (character.hasEffect(PotionEffect.name)) {
				PotionEffect effect = (PotionEffect) character.getEffect(PotionEffect.name);
				long k = System.currentTimeMillis();
				PotionEffectModel value = effect.getValue();
				Optional<PotionEffectData> potionEffectData = itemStack.get(PotionEffectData.class);
				if (potionEffectData.isPresent()) {
					PotionEffectData o = potionEffectData.get();
					ListValue<org.spongepowered.api.effect.potion.PotionEffect> effects = o.effects();
					if (effects.size() > 1) {
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
			Vector3d velocity = character.getPlayer().getLocation().getPosition().sub(event.getImpactPoint().getPosition()).normalize().mul(-2);
			Player player = character.getPlayer();

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
