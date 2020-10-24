package cz.neumimto.rpg.sponge.scripting;

import cz.neumimto.rpg.common.skills.scripting.CustomSkillGenerator;
import cz.neumimto.rpg.sponge.effects.negative.StunEffect;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.skills.types.Targeted;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

import javax.inject.Singleton;
import java.lang.reflect.Type;
import java.util.Collection;

@Singleton
public class SpongeCustomSkillGenerator extends CustomSkillGenerator {

    @Override
    protected Object translateDamageType(String damageType) {
        Collection<DamageType> allOf = Sponge.getRegistry().getAllOf(DamageType.class);
        for (DamageType type : allOf) {
            if (type.getName().equalsIgnoreCase(damageType)) {
                return type;
            }
        }

        if ("PHYSICAL".equalsIgnoreCase(damageType)) {
            return DamageTypes.ATTACK;
        }

        return null;
    }

    @Override
    protected String getDefaultEffectPackage() {
        return StunEffect.class.getPackage().getName();
    }

    @Override
    protected Type characterClassImpl() {
        return ISpongeCharacter.class;
    }

    @Override
    protected Class<?> targeted() {
        return Targeted.class;
    }
}
