package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.common.skills.mech.DamageMechanic;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.LineEffect;
import de.slikey.effectlib.util.DynamicLocation;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
@ResourceLoader.Skill("ntrpg:firebeam")
public class FireBeam extends ActiveSkill<ISpigotCharacter> {

    @Inject
    private SpigotDamageService damageService;

    @Override
    public void init() {
        setDamageType("FIRE");
        addSkillType(SkillType.ELEMENTAL);
        addSkillType(SkillType.FIRE);
        settings.addNode("max-length", 50, 5);
        settings.addNode("damage", 10, 5);
        settings.addNode("fire-ticks", 40, 5);
    }

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext info) {
        Player player = character.getPlayer();
        int maxLength = info.getIntNodeValue("max-length");
        RayTraceResult rayTraceResult = player.getWorld().rayTrace(player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                maxLength,
                FluidCollisionMode.ALWAYS,
                true,
                2,
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

        LineEffect lineEffect = new LineEffect(SpigotRpgPlugin.getEffectManager());
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

        SpigotRpgPlugin.getEffectManager().start(lineEffect);
        return SkillResult.OK;
    }


}