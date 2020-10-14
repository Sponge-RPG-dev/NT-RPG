package cz.neumimto.rpg.sponge.scripting;

import cz.neumimto.rpg.common.skills.scripting.CustomSkillGenerator;
import cz.neumimto.rpg.sponge.effects.negative.StunEffect;

import javax.inject.Singleton;

@Singleton
public class SpongeCustomSkillGenerator extends CustomSkillGenerator {

    @Override
    protected String getDefaultEffectPackage() {
        return StunEffect.class.getPackage().getName();
    }
}
