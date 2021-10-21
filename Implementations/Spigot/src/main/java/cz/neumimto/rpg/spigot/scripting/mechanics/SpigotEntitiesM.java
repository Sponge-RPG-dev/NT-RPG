package cz.neumimto.rpg.spigot.scripting.mechanics;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.scripting.mechanics.NTScriptProxy;
import cz.neumimto.rpg.common.skills.ISkill;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import javax.inject.Singleton;

import static cz.neumimto.nts.annotations.ScriptMeta.Handler;
import static cz.neumimto.nts.annotations.ScriptMeta.NamedParam;

@Singleton
@AutoService(NTScriptProxy.class)
public class SpigotEntitiesM implements NTScriptProxy {

    @Handler
    @ScriptMeta.Function("get_location")
    public Location getLocation(
            @NamedParam("e|entity") IEntity e
    ) {
        return ((LivingEntity)e.getEntity()).getLocation();
    }

    @Handler
    @ScriptMeta.Function("teleport")
    public void teleport(
            @NamedParam("e|entity") IEntity e,
            @NamedParam("l|location") Location l
    ) {
        ((LivingEntity)e.getEntity()).teleport(l, PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    @Handler
    @ScriptMeta.Function("set_velocity")
    public void setVelocity(
            @NamedParam("t|target") IEntity target,
            @NamedParam("v|vector") Vector v
    ) {
        LivingEntity livingEntity = (LivingEntity) target.getEntity();
        livingEntity.setVelocity(v);
    }

    @Handler
    @ScriptMeta.Function("damage")
    public boolean damage(
            @NamedParam("t|target") IEntity target,
            @NamedParam("e|damager") IEntity damager,
            @NamedParam("d|damage") double damage,
            @NamedParam("k|knockback") double knockback,
            @NamedParam("c|cause") EntityDamageEvent.DamageCause cause,
            @NamedParam("s|skill") ISkill e) {

        LivingEntity tEntity = (LivingEntity) target.getEntity();

        if (tEntity.getHealth() <= 0) {
            return false;
        }

        LivingEntity dEntity = (LivingEntity) damager.getEntity();

        if (cause == null && e.getDamageType() != null) {
            cause = EntityDamageEvent.DamageCause.valueOf(e.getDamageType());
        } else {
            cause = EntityDamageEvent.DamageCause.ENTITY_ATTACK;
        }

        EntityDamageEvent event = handleEntityDamageEvent(tEntity, dEntity, cause, damage, knockback);
        return !event.isCancelled() && event.getFinalDamage() > 0;
    }

    public static EntityDamageEvent handleEntityDamageEvent(LivingEntity target,
                                                            LivingEntity damager,
                                                            EntityDamageEvent.DamageCause source,

                                                            double damage,
                                                            double knockbackPower) {

        EntityDamageEvent event = new EntityDamageByEntityEvent(damager, target, source, damage);

        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            event.getEntity().setLastDamageCause(event);
        } else {
            return event;
        }

        double oldHealth = target.getHealth();
        double newHealth = oldHealth - event.getDamage();
        if (newHealth < 0.0D) {
            newHealth = 0.0D;
        }

        net.minecraft.world.entity.LivingEntity ed = ((CraftLivingEntity) damager).getHandle();
        net.minecraft.world.entity.LivingEntity el = ((CraftLivingEntity) target).getHandle();
        if (el.isDeadOrDying() || el.isRemoved()) {
            return event;
        }
        el.setNoActionTime(0);
        el.invulnerableTime = 0;
        /**
         * @see net.minecraft.world.entity.LivingEntity#hurt(DamageSource, float)
         */

        el.lastHurt = (float) event.getDamage();
//        el.hurtTicks = el.ax = 10;
        el.invulnerableTime = el.invulnerableDuration;
        el.hurtDuration = 10;
        el.hurtTime = el.hurtDuration;
        if (knockbackPower != 0) {
            el.knockback(knockbackPower, el.getX() - ed.getX(), el.getZ() - ed.getZ(), ed);
        }

        if (el instanceof Animal a) {
            a.resetLove();
            if (a instanceof TamableAnimal t) {
                t.setOrderedToSit(false);
            }
        }

        byte b0;

        if (source == EntityDamageEvent.DamageCause.DROWNING) {
            b0 = 36;
        } else if (source == EntityDamageEvent.DamageCause.FIRE || source == EntityDamageEvent.DamageCause.FIRE_TICK || source == EntityDamageEvent.DamageCause.LAVA) {
            b0 = 37;
        } else if (source == EntityDamageEvent.DamageCause.FREEZE) {
            b0 = 57;
        } else {
            b0 = 2;
        }

        el.level.broadcastEntityEvent(el, b0);
        el.lastHurtByMob = ed;
        if (ed instanceof Player p) {
            el.lastHurtByPlayer = p;
        }

        el.setHealth((float) newHealth);
        el.hurtMarked = el.getRandom().nextDouble() >= el.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);

        DamageSource dmgSource = dmgSourceFromCause(source, ed);
        if (el.isDeadOrDying()) {
            el.die(dmgSource);
        } else {

            SoundEvent soundeffect = getHurtSound(el);
            el.playSound(soundeffect, el.getSoundVolume(), el.getVoicePitch());

            el.lastHurtByMobTimestamp = (int) el.level.getGameTime();

            if (el instanceof net.minecraft.world.entity.monster.Monster em) {
                em.setGoalTarget(ed, EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY, true);
            } else if (target instanceof NeutralMob w) {

                if (w.getTarget() == null) {
                    w.setPersistentAngerTarget(ed.getUUID());
                    w.startPersistentAngerTimer();
                    w.setGoalTarget(ed, EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY, true);
                }
            }

        }
        return event;
    }

    private static DamageSource dmgSourceFromCause(EntityDamageEvent.DamageCause source, net.minecraft.world.entity.LivingEntity attacker) {

        if (attacker instanceof Player p) {
            return DamageSource.playerAttack(p);
        } else {
            return DamageSource.mobAttack(attacker);
        }
    }

    public static SoundEvent getHurtSound(Entity entity) {
        EntityType<?> type = entity.getType();
        if (type == EntityType.PLAYER) {
            return SoundEvents.PLAYER_HURT;
        }
        if (type == EntityType.AXOLOTL) {
            return SoundEvents.AXOLOTL_HURT;
        }
        if (type == EntityType.BAT) {
            return SoundEvents.BAT_HURT;
        }
        if (type == EntityType.BEE) {
            return SoundEvents.BEE_HURT;
        }
        if (type == EntityType.BLAZE) {
            return SoundEvents.BLAZE_HURT;
        }
        if (type == EntityType.CAT) {
            return SoundEvents.CAT_HURT;
        }
        if (type == EntityType.CAVE_SPIDER) {
            return SoundEvents.SPIDER_HURT;
        }
        if (type == EntityType.CHICKEN) {
            return SoundEvents.CHICKEN_HURT;
        }
        if (type == EntityType.COD) {
            return SoundEvents.COD_HURT;
        }
        if (type == EntityType.COW) {
            return SoundEvents.COW_HURT;
        }
        if (type == EntityType.CREEPER) {
            return SoundEvents.CREEPER_HURT;
        }
        if (type == EntityType.DOLPHIN) {
            return SoundEvents.DOLPHIN_HURT;
        }
        if (type == EntityType.DONKEY) {
            return SoundEvents.DONKEY_HURT;
        }
        if (type == EntityType.DROWNED) {
            return SoundEvents.DROWNED_HURT;
        }
        if (type == EntityType.ELDER_GUARDIAN) {
            return SoundEvents.ELDER_GUARDIAN_HURT;
        }
        if (type == EntityType.ENDER_DRAGON) {
            return SoundEvents.ENDER_DRAGON_HURT;
        }
        if (type == EntityType.ENDERMAN) {
            return SoundEvents.ENDERMAN_HURT;
        }
        if (type == EntityType.ENDERMITE) {
            return SoundEvents.ENDERMITE_HURT;
        }
        if (type == EntityType.EVOKER) {
            return SoundEvents.EVOKER_HURT;
        }
        if (type == EntityType.FOX) {
            return SoundEvents.FOX_HURT;
        }
        if (type == EntityType.GHAST) {
            return SoundEvents.GHAST_HURT;
        }
        if (type == EntityType.GIANT) {
            return SoundEvents.ZOMBIE_HURT;
        }
        if (type == EntityType.GLOW_SQUID) {
            return SoundEvents.GLOW_SQUID_HURT;
        }
        if (type == EntityType.GOAT) {
            return SoundEvents.GOAT_HURT;
        }
        if (type == EntityType.GUARDIAN) {
            return SoundEvents.GUARDIAN_HURT;
        }
        if (type == EntityType.HOGLIN) {
            return SoundEvents.HOGLIN_HURT;
        }
        if (type == EntityType.HORSE) {
            return SoundEvents.HORSE_HURT;
        }
        if (type == EntityType.HUSK) {
            return SoundEvents.HUSK_HURT;
        }
        if (type == EntityType.ILLUSIONER) {
            return SoundEvents.ILLUSIONER_HURT;
        }
        if (type == EntityType.IRON_GOLEM) {
            return SoundEvents.IRON_GOLEM_HURT;
        }
        if (type == EntityType.LLAMA) {
            return SoundEvents.LLAMA_HURT;
        }
        if (type == EntityType.MAGMA_CUBE) {
            return SoundEvents.MAGMA_CUBE_HURT;
        }
        if (type == EntityType.MULE) {
            return SoundEvents.MULE_HURT;
        }
        if (type == EntityType.MOOSHROOM) {
            return SoundEvents.COW_HURT;
        }
        if (type == EntityType.OCELOT) {
            return SoundEvents.OCELOT_HURT;
        }
        if (type == EntityType.PANDA) {
            return SoundEvents.PANDA_HURT;
        }
        if (type == EntityType.PARROT) {
            return SoundEvents.PARROT_HURT;
        }
        if (type == EntityType.PHANTOM) {
            return SoundEvents.PHANTOM_HURT;
        }
        if (type == EntityType.PIG) {
            return SoundEvents.PIG_HURT;
        }
        if (type == EntityType.PIGLIN) {
            return SoundEvents.PIGLIN_HURT;
        }
        if (type == EntityType.PIGLIN_BRUTE) {
            return SoundEvents.PIGLIN_BRUTE_HURT;
        }
        if (type == EntityType.PILLAGER) {
            return SoundEvents.PILLAGER_HURT;
        }
        if (type == EntityType.POLAR_BEAR) {
            return SoundEvents.POLAR_BEAR_HURT;
        }
        if (type == EntityType.PUFFERFISH) {
            return SoundEvents.PUFFER_FISH_HURT;
        }
        if (type == EntityType.RABBIT) {
            return SoundEvents.RABBIT_HURT;
        }
        if (type == EntityType.RAVAGER) {
            return SoundEvents.RAVAGER_HURT;
        }
        if (type == EntityType.SALMON) {
            return SoundEvents.SALMON_HURT;
        }
        if (type == EntityType.SHEEP) {
            return SoundEvents.SHEEP_HURT;
        }
        if (type == EntityType.SHULKER) {
            return SoundEvents.SHULKER_HURT;
        }
        if (type == EntityType.SHULKER_BULLET) {
            return SoundEvents.SHULKER_BULLET_HURT;
        }
        if (type == EntityType.SILVERFISH) {
            return SoundEvents.SILVERFISH_HURT;
        }
        if (type == EntityType.SKELETON) {
            return SoundEvents.SKELETON_HURT;
        }
        if (type == EntityType.SKELETON_HORSE) {
            return SoundEvents.SKELETON_HORSE_HURT;
        }
        if (type == EntityType.SLIME) {
            return SoundEvents.SLIME_HURT;
        }
        if (type == EntityType.SNOW_GOLEM) {
            return SoundEvents.SNOW_GOLEM_HURT;
        }
        if (type == EntityType.SPIDER) {
            return SoundEvents.SPIDER_HURT;
        }
        if (type == EntityType.SQUID) {
            return SoundEvents.SQUID_HURT;
        }
        if (type == EntityType.STRAY) {
            return SoundEvents.STRAY_HURT;
        }
        if (type == EntityType.STRIDER) {
            return SoundEvents.STRIDER_HURT;
        }
        if (type == EntityType.TRADER_LLAMA) {
            return SoundEvents.LLAMA_HURT;
        }
        if (type == EntityType.TROPICAL_FISH) {
            return SoundEvents.TROPICAL_FISH_HURT;
        }
        if (type == EntityType.TURTLE) {
            return SoundEvents.TURTLE_HURT;
        }
        if (type == EntityType.VEX) {
            return SoundEvents.VEX_HURT;
        }
        if (type == EntityType.VILLAGER) {
            return SoundEvents.VILLAGER_HURT;
        }
        if (type == EntityType.VINDICATOR) {
            return SoundEvents.VINDICATOR_HURT;
        }
        if (type == EntityType.WANDERING_TRADER) {
            return SoundEvents.WANDERING_TRADER_HURT;
        }
        if (type == EntityType.WITCH) {
            return SoundEvents.WITCH_HURT;
        }
        if (type == EntityType.WITHER) {
            return SoundEvents.WITHER_HURT;
        }
        if (type == EntityType.WITHER_SKELETON) {
            return SoundEvents.WITHER_SKELETON_HURT;
        }
        if (type == EntityType.WOLF) {
            return SoundEvents.WOLF_HURT;
        }
        if (type == EntityType.ZOGLIN) {
            return SoundEvents.ZOGLIN_HURT;
        }
        if (type == EntityType.ZOMBIE) {
            return SoundEvents.ZOMBIE_HURT;
        }
        if (type == EntityType.ZOMBIE_HORSE) {
            return SoundEvents.ZOMBIE_HORSE_HURT;
        }
        if (type == EntityType.ZOMBIE_VILLAGER) {
            return SoundEvents.ZOMBIE_VILLAGER_HURT;
        }
        if (type == EntityType.ZOMBIFIED_PIGLIN) {
            return SoundEvents.ZOMBIFIED_PIGLIN_HURT;
        }
        return SoundEvents.GENERIC_HURT;

    }

}
