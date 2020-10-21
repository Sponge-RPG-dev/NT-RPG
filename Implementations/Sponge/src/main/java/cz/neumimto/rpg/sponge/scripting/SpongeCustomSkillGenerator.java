package cz.neumimto.rpg.sponge.scripting;

import cz.neumimto.rpg.common.skills.scripting.CustomSkillGenerator;
import cz.neumimto.rpg.sponge.effects.negative.StunEffect;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.skills.types.Targeted;

import javax.inject.Singleton;
import java.lang.reflect.Type;

@Singleton
public class SpongeCustomSkillGenerator extends CustomSkillGenerator {

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
