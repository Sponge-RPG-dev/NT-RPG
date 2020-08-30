package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.spigot.Resourcepack;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.effects.SpigotEffectService;
import cz.neumimto.rpg.spigot.effects.positive.IceSpikeEffect;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.skills.particles.ResettingVortexEffect;
import de.slikey.effectlib.effect.VortexEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@ResourceLoader.Skill("ntrpg:icespike")
public class IceSpike extends TargetedBlockSkill {

    @Inject
    private SpigotDamageService damageService;

    @Inject
    private SpigotEffectService effectService;

    @Override
    public void init() {
        super.init();
        setDamageType(EntityDamageEvent.DamageCause.MAGIC.name());
        settings.addNode(SkillNodes.DAMAGE, 10, 10);
        settings.addNode(SkillNodes.RANGE, 10f, .5f);
        settings.addNode(SkillNodes.DURATION, 10000, 1000);
        settings.addNode("damage-initial", 30, 5);
        settings.addNode(SkillNodes.RADIUS, 5, 0);
        addSkillType(SkillType.SUMMON);
        addSkillType(SkillType.ICE);
    }

    @Override
    protected SkillResult castOn(Block block, ISpigotCharacter character, PlayerSkillContext skillContext) {

        double damage = skillContext.getDoubleNodeValue(SkillNodes.DAMAGE);
        double damageInitial = skillContext.getDoubleNodeValue("damage-initial");
        int radius = skillContext.getIntNodeValue(SkillNodes.RADIUS);

        Entity spike = Resourcepack.summonLargeIceSpike(block.getLocation());

        spike.setVelocity(new Vector(0, 0.5, 0));
        List<Entity> nearbyEntities = spike.getNearbyEntities(radius, radius, radius);
        Player player = character.getPlayer();
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                if (damageService.canDamage(character, livingEntity)) {
                    damageService.damage(player, livingEntity, EntityDamageEvent.DamageCause.MAGIC, damageInitial, false);
                }
            }
        }

        long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
        VortexEffect vortexEffect = new ResettingVortexEffect(SpigotRpgPlugin.getEffectManager(), 5);
        vortexEffect.helixes = 5;
        vortexEffect.circles = 2;
        vortexEffect.color = Color.WHITE;
        vortexEffect.radius = radius;

        vortexEffect.grow = 0.075f;
        vortexEffect.particleCount = 10;
        vortexEffect.particle = Particle.CLOUD;
        vortexEffect.duration = (int) duration;
        Location location = spike.getLocation();
        location.setPitch(-90);
        vortexEffect.setLocation(location);

        IceSpikeEffect iceSpikeAura = new IceSpikeEffect(spike, character, duration, radius, damage, damageService, vortexEffect);
        effectService.addEffect(iceSpikeAura);


        return SkillResult.OK;
    }
}
