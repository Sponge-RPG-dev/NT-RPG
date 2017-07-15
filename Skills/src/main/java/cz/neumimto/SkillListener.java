package cz.neumimto;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.ManaDrainEffect;
import cz.neumimto.effects.ResoluteTechniqueEffect;
import cz.neumimto.effects.decoration.ParticleDecorator;
import cz.neumimto.effects.negative.StunEffect;
import cz.neumimto.effects.positive.*;
import cz.neumimto.events.CriticalStrikeEvent;
import cz.neumimto.events.DamageDodgedEvent;
import cz.neumimto.events.ManaDrainEvent;
import cz.neumimto.events.StunApplyEvent;
import cz.neumimto.model.BashModel;
import cz.neumimto.model.CriticalEffectModel;
import cz.neumimto.model.PotionEffectModel;
import cz.neumimto.rpg.IEntityType;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.events.INEntityDamageEvent;
import cz.neumimto.rpg.events.INEntityWeaponDamageEvent;
import cz.neumimto.rpg.events.character.CharacterDamageEntityEvent;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.IReservable;
import cz.neumimto.rpg.players.Mana;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.utils.XORShiftRnd;
import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.projectile.LaunchProjectileEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.type.Include;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by ja on 21.5.2016.
 */
@ResourceLoader.ListenerClass
public class SkillListener {

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
                    entityService.healEntity(character,l);
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
    public void onEntityDamage(INEntityDamageEvent event) {

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
                    StunEffect stunEffect = new StunEffect(event.getTarget(),stackedValue.stunDuration);
                    if (stackedValue.damage > 0) {
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
        processConsumption(player, itemInHand, (e, l) -> {
            if (l.cooldowns.containsKey(e)) {
                if (l.cooldowns.get(e) <= System.currentTimeMillis())
                    event.setCancelled(true);
            }
        });
    }

    private void processConsumption(Player player, Optional<ItemStack> itemInHand, BiConsumer<PotionEffectType, PotionEffectModel> l) {
        if (itemInHand.isPresent()) {
            ItemStack itemStack = itemInHand.get();
            if (itemStack.getItem() == ItemTypes.POTION
                    || itemStack.getItem() == ItemTypes.SPLASH_POTION
                    || itemStack.getItem() == ItemTypes.LINGERING_POTION) {
                IActiveCharacter character = characterService.getCharacter(player);
                if (character.hasEffect(PotionEffect.name)) {
                    PotionEffect effect = (PotionEffect) character.getEffect(PotionEffect.name);
                    l.accept(null, effect.getValue());
                }
            }
        }
    }

    @Listener(order = Order.LATE)
    public void onItemConsumerFinish(UseItemStackEvent.Finish event, @Root(typeFilter = Player.class) Player player) {
        Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
        processConsumption(player, itemInHand, (e, l) -> {
            long k = System.currentTimeMillis();
            l.cooldowns.put(e, l.potions.get(e) + k);
        });
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

}
