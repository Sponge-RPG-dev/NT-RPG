package cz.neumimto.damage;

import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.common.AbstractDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.common.AbstractDamageSourceBuilder;
import org.spongepowered.api.util.ResettableBuilder;

/**
 * Created by NeumimTo on 28.12.2015.
 */
public class SkillDamageSourceBuilder extends AbstractDamageSourceBuilder {


    @Override
    public DamageSource build() throws IllegalStateException {
        return new SkillDamageSource(this);
    }

    @Override
    public ResettableBuilder from(Object o) {
        return new SkillDamageSourceBuilder();
    }
}
