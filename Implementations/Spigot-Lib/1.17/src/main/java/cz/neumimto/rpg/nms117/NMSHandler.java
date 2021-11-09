package cz.neumimto.rpg.nms117;

import com.google.auto.service.AutoService;
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
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.WitherSkull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_17_R1.event.CraftEventFactory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Trident;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.*;

@AutoService(cz.neumimto.rpg.nms.NMSHandler.class)
public class NMSHandler extends cz.neumimto.rpg.nms.NMSHandler {

    @Override
    public String getVersion() {
        return "1.17";
    }

    @Override
    public EntityDamageEvent handleEntityDamageEvent(LivingEntity target,
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

        // ServerLevel level = MinecraftServer.getServer().getLevel(ResourceKey.create());
        // Entity entity = level.getEntity(damager.getUniqueId());

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

    private static DamageSource dmgSourceFromCause(EntityDamageEvent.DamageCause damageCause,
                                                   net.minecraft.world.entity.Entity attackingEntity) {
        switch (damageCause) {
            case CONTACT:
                return DamageSource.CACTUS;
            case ENTITY_ATTACK:
                if (attackingEntity instanceof Player p) {
                    return DamageSource.playerAttack(p);
                }
                return DamageSource.mobAttack((net.minecraft.world.entity.LivingEntity) attackingEntity);
            case ENTITY_SWEEP_ATTACK:
                if (attackingEntity instanceof Player p) {
                    return DamageSource.playerAttack(p).sweep();
                }
                return DamageSource.mobAttack((net.minecraft.world.entity.LivingEntity) attackingEntity).sweep();

            case PROJECTILE:
                if (attackingEntity instanceof Projectile projectile) {
                    EntityType entityType = projectile.getType();
                    if (entityType == EntityType.TRIDENT) {
                        return DamageSource.trident(attackingEntity, projectile);
                    } else if (entityType == EntityType.ARROW || entityType == EntityType.SPECTRAL_ARROW) {
                        return DamageSource.arrow((Arrow) attackingEntity, projectile.getOwner());
                    } else if (entityType == EntityType.SNOWBALL || entityType == EntityType.EGG ||
                            entityType == EntityType.ENDER_PEARL || entityType == EntityType.POTION) {
                        return DamageSource.indirectMobAttack(attackingEntity, (net.minecraft.world.entity.LivingEntity) projectile.getOwner());
                    } else if (entityType == EntityType.FIREWORK_ROCKET) {
                        return DamageSource.fireworks((FireworkRocketEntity) attackingEntity, projectile);
                    } else if (entityType == EntityType.WITHER_SKULL) {
                        return DamageSource.witherSkull((WitherSkull) attackingEntity, projectile);
                    }
                    return DamageSource.indirectMobAttack(projectile, (net.minecraft.world.entity.LivingEntity) projectile.getOwner());
                }
                return DamageSource.GENERIC;
            case SUFFOCATION:
                return DamageSource.IN_WALL;
            case FALL:
                return DamageSource.FALL;
            case FIRE:
                return DamageSource.IN_FIRE;
            case FIRE_TICK:
                return DamageSource.ON_FIRE;
            case MELTING:
                return CraftEventFactory.MELTING;
            case LAVA:
                return DamageSource.LAVA;
            case DROWNING:
                return DamageSource.DROWN;
            case VOID:
                return DamageSource.OUT_OF_WORLD;
            case LIGHTNING:
                return DamageSource.LIGHTNING_BOLT;
            case STARVATION:
                return DamageSource.STARVE;
            case POISON:
                return CraftEventFactory.POISON;
            case MAGIC:
                return DamageSource.MAGIC;
            case WITHER:
                return DamageSource.WITHER;
            case FALLING_BLOCK:
                return DamageSource.FALLING_BLOCK;
            case THORNS:
                if (attackingEntity == null) {
                    return DamageSource.GENERIC;
                }
                return DamageSource.thorns(attackingEntity);
            case DRAGON_BREATH:
                return DamageSource.DRAGON_BREATH;
            case CUSTOM:
                return DamageSource.GENERIC;
            case FLY_INTO_WALL:
                return DamageSource.FLY_INTO_WALL;
            case HOT_FLOOR:
                return DamageSource.HOT_FLOOR;
            case CRAMMING:
                return DamageSource.CRAMMING;
            case DRYOUT:
                return DamageSource.DRY_OUT;
        }
        return DamageSource.GENERIC;
    }


    public static SoundEvent getHurtSound(Entity entity) {
        net.minecraft.world.entity.EntityType<?> type = entity.getType();
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
