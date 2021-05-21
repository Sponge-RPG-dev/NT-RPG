
package cz.neumimto.rpg.sponge.damage;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.ISkill;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.common.AbstractEntityDamageSource;

/**
 * Created by NeumimTo on 29.12.2015.
 */
public class SkillDamageSourceBuilder extends AbstractEntityDamageSource.AbstractEntityDamageSourceBuilder<SkillDamageSource, SkillDamageSourceBuilder> {

    protected IEntity nSource;
    protected ISkill skill;
    protected IEffect effect;

    public SkillDamageSourceBuilder fromSkill(ISkill skill) {
        this.skill = skill;
        DamageType damageType = Sponge.getRegistry().getType(DamageType.class, skill.getDamageType()).get();
        type(damageType);
        return this;
    }

    public ISkill getSkill() {
        return skill;
    }

    public SkillDamageSourceBuilder setSkill(ISkill skill) {
        this.skill = skill;
        return this;
    }

    public IEntity getSource() {
        return nSource;
    }

    public SkillDamageSourceBuilder setSource(IEntity<? extends Living> source) {
        this.nSource = source;
        this.source = source.getEntity();
        return this;
    }

    public IEffect getEffect() {
        return effect;
    }

    public SkillDamageSourceBuilder setEffect(IEffect effect) {
        this.effect = effect;
        return this;
    }

    @Override
    public SkillDamageSource build() throws IllegalStateException {
        return new SkillDamageSource(this);
    }
}
