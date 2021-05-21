
package cz.neumimto.rpg.sponge.damage;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.ISkill;
import org.spongepowered.api.event.cause.entity.damage.source.common.AbstractEntityDamageSource;

/**
 * Created by NeumimTo on 29.12.2015.
 */
public class SkillDamageSource extends AbstractEntityDamageSource implements ISkillDamageSource {

    private final ISkill skill;
    private final IEntity nSource;
    private final IEffect effect;

    public SkillDamageSource(SkillDamageSourceBuilder builder) {
        super(builder);
        this.skill = builder.getSkill();
        this.nSource = builder.getSource();
        this.effect = builder.getEffect();
    }

    @Override
    public final ISkill getSkill() {
        return this.skill;
    }

    @Override
    public IEntity getSourceIEntity() {
        return nSource;
    }

    @Override
    public IEffect getEffect() {
        return effect;
    }
}