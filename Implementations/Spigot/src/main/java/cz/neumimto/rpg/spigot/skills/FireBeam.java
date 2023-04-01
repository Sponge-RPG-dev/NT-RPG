package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.tree.SkillType;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.LineEffect;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.RayTraceResult;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:firebeam")
public class FireBeam extends ActiveSkill<SpigotCharacter> {

    @Inject
    private SpigotDamageService damageService;

    @Override
    public void init() {
        setDamageType("FIRE");
        addSkillType(SkillType.ELEMENTAL);
        addSkillType(SkillType.FIRE);
        settings.addNode("max-length", 50);
        settings.addNode("damage", 10);
        settings.addNode("fire-ticks", 40);
    }

    @Override
    public SkillResult cast(SpigotCharacter character, PlayerSkillContext info) {
        Player player = character.getPlayer();
        int maxLength = info.getIntNodeValue("max-length");

        RayTraceResult rayTraceResult = player.getWorld().rayTrace(player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                maxLength,
                FluidCollisionMode.ALWAYS,
                true,
                1,
                entity -> entity != player && entity instanceof LivingEntity && !entity.isDead()
        );

        if (rayTraceResult != null) {
            Entity hitEntity = rayTraceResult.getHitEntity();
            if (hitEntity != null) {
                LivingEntity l = (LivingEntity) hitEntity;
                if (damageService.canDamage(character, l)) {
                    double damage = info.getDoubleNodeValue("damage");
                    int ft = info.getIntNodeValue("fire-ticks");
                    damageService.damage(player, l, EntityDamageEvent.DamageCause.FIRE, damage, false);
                    l.setFireTicks(ft);
                }
            }
        }

        LineEffect lineEffect = new ParticleEffect(SpigotRpgPlugin.getEffectManager());
        lineEffect.length = maxLength;
        lineEffect.isZigZag = false;
        lineEffect.type = EffectType.INSTANT;
        lineEffect.particles = 3 * maxLength;
        lineEffect.particle = Particle.FLAME;
        lineEffect.setLocation(player.getEyeLocation().clone().add(
                ThreadLocalRandom.current().nextDouble(-0.5, 0.5),
                ThreadLocalRandom.current().nextDouble(-0.2, 0.2),
                ThreadLocalRandom.current().nextDouble(-0.5, 0.5)
        ));

        player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.PLAYERS, 1, 1);
        SpigotRpgPlugin.getEffectManager().start(lineEffect);
        return SkillResult.OK;
    }

    public static class ParticleEffect extends LineEffect {

        public ParticleEffect(EffectManager effectManager) {
            super(effectManager);
        }

        @Override
        protected void display(Particle particle, Location location, Color color, float speed, int amount) {
            effectManager.display(particle, location, particleOffsetX, particleOffsetY, particleOffsetZ, speed, amount,
                    particleSize, color, material, materialData, visibleRange, targetPlayers);
            effectManager.display(Particle.LAVA, location, particleOffsetX, particleOffsetY, particleOffsetZ, speed, amount,
                    particleSize, color, material, materialData, visibleRange, targetPlayers);
        }
    }
}