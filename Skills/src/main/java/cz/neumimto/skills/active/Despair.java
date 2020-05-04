package cz.neumimto.skills.active;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.Decorator;
import cz.neumimto.effects.negative.Blindness;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;

import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.sponge.damage.SkillDamageSource;
import cz.neumimto.rpg.sponge.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.sponge.damage.SpongeDamageService;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.util.Color;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

/**
 * Created by NeumimTo on 15.7.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:despair")
public class Despair extends ActiveSkill<ISpongeCharacter> {

    @Inject
    private EntityService entityService;

    @Inject
    private EffectService effectService;

    @Inject
    private SpongeDamageService spongeDamageService;

    @Override
    public void init() {
        super.init();
        setDamageType(DamageTypes.MAGIC.getId());
        settings.addNode(SkillNodes.DURATION, 1000L, 500);
        settings.addNode(SkillNodes.DAMAGE, 10L, 1.5f);
        settings.addNode(SkillNodes.RADIUS, 7L, 2);
        addSkillType(SkillType.AOE);
        addSkillType(SkillType.ESCAPE);
        addSkillType(SkillType.STEALTH);
    }

    @Override
    public SkillResult cast(ISpongeCharacter character, PlayerSkillContext skillContext) {
        int k = skillContext.getIntNodeValue(SkillNodes.RADIUS);
        Set<Entity> nearbyEntities = Utils.getNearbyEntities(character.getEntity().getLocation(), k);
        double damage = skillContext.getDoubleNodeValue(SkillNodes.DAMAGE);
        long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);

        for (Entity nearbyEntity : nearbyEntities) {
            if (Utils.isLivingEntity(nearbyEntity)) {
                Living l = (Living) nearbyEntity;
                if (spongeDamageService.canDamage(character, l)) {
                    IEntity iEntity = entityService.get(l);
                    SkillDamageSource build = new SkillDamageSourceBuilder()
                            .fromSkill(this)
                            .setSource(iEntity)
                            .build();
                    l.damage(damage, build);
                    Blindness blindness = new Blindness(iEntity, duration);
                    effectService.addEffect(blindness, this);
                }
            }
        }

        Vector3d vec = new Vector3d(0, 1, 0);
        Decorator.circle(character.getEntity().getLocation(), 36, k, location -> {
            ParticleEffect build = ParticleEffect.builder()
                    .type(ParticleTypes.SPELL)
                    .option(ParticleOptions.COLOR, Color.GRAY)
                    .build();
            character.getEntity().getLocation().getExtent().spawnParticles(build, location.getPosition().add(vec));
            build = ParticleEffect.builder()
                    .type(ParticleTypes.MOB_SPELL)
                    .option(ParticleOptions.COLOR, Color.GRAY)
                    .build();
            character.getEntity().getLocation().getExtent().spawnParticles(build, location.getPosition().add(vec));
        });

        return SkillResult.OK;
    }
}
