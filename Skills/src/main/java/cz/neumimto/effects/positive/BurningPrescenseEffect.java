package cz.neumimto.effects.positive;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.model.BPModel;
import cz.neumimto.rpg.api.effects.*;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.sponge.damage.SkillDamageSource;
import cz.neumimto.rpg.sponge.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.skills.NDamageType;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;


/**
 * Created by ja on 5.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "An effect which periodically damages all enemies around the target")
public class BurningPrescenseEffect extends EffectBase<BPModel> {

    public static final String name = "Burning Prescense";
    public static ParticleEffect CASTER_EFFECT = ParticleEffect.builder()
            .quantity(5)
            .type(ParticleTypes.SMOKE)
            .offset(new Vector3d(1, 0, 1))
            .velocity(new Vector3d(0, 1, 0).normalize())
            .build();
    public static ParticleEffect TARGET_EFFECT = ParticleEffect.builder()
            .quantity(8)
            .type(ParticleTypes.FLAME)
            .offset(new Vector3d(1, 0, 1))
            .velocity(new Vector3d(0, 1, 0).normalize())
            .build();

    public BurningPrescenseEffect(IEffectConsumer consumer, long duration, BPModel model) {
        super(name, consumer);
        setDuration(model.duration);
        setPeriod(model.period);
        setValue(model);
    }

    @Override
    public void onTick(IEffect self) {
        Living entity = ((ISpongeEntity) getConsumer()).getEntity();
        entity.getLocation().getExtent().spawnParticles(CASTER_EFFECT, entity.getLocation().getPosition());
        float radius = getValue().radius;
        if (radius > 0) {
            if (entity.getType() == EntityTypes.PLAYER) {
                ISpongeCharacter character = (ISpongeCharacter) getConsumer();
                SkillDamageSourceBuilder builder = new SkillDamageSourceBuilder();
                builder.setEffect(this);
                builder.type(NDamageType.FIRE);
                builder.setSource(character);
                SkillDamageSource sds = builder.build();
                for (Entity target : entity.getNearbyEntities(radius)) {
                    if (!Utils.isLivingEntity(target)) {
                        continue;
                    }
                    Living livingEntity = (Living) target;
                    if (!Utils.canDamage(character, livingEntity)) {
                        continue;
                    }
                    boolean success = livingEntity.damage(getValue().damage, sds);
                    if (success) {
                        livingEntity.getLocation().getExtent().spawnParticles(TARGET_EFFECT, livingEntity.getLocation().getPosition());
                    }
                }
            } else {
                SkillDamageSourceBuilder builder = new SkillDamageSourceBuilder();
                builder.setEffect(this);
                builder.setSource((IEntity) getConsumer());
                builder.type(NDamageType.FIRE);
                SkillDamageSource sds = builder.build();
                for (Entity target : entity.getNearbyEntities(radius)) {
                    if (!Utils.isLivingEntity(target)) {
                        continue;
                    }
                    Living livingEntity = (Living) target;
                    boolean success = livingEntity.damage(getValue().damage, sds);
                    if (success) {
                        livingEntity.getLocation().getExtent().spawnParticles(TARGET_EFFECT, livingEntity.getLocation().getPosition());
                    }
                }
            }
        }
    }

    @Override
    public IEffectContainer constructEffectContainer() {
        return new EffectContainer.UnstackableSingleInstance(this);
    }
}
