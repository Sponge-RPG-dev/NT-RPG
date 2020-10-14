package cz.neumimto.rpg.common.scripting;

import cz.neumimto.rpg.common.skills.scripting.CustomSkillGenerator;
import cz.neumimto.rpg.effects.TestEffectFloat;

import javax.inject.Singleton;

@Singleton
public class TestCustomSkillGenerator extends CustomSkillGenerator {
    @Override
    protected String getDefaultEffectPackage() {
        return TestEffectFloat.class.getPackage().getName();
    }
}
