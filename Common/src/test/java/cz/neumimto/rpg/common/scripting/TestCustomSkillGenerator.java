package cz.neumimto.rpg.common.scripting;

import cz.neumimto.rpg.common.entity.TestCharacter;
import cz.neumimto.rpg.common.skills.scripting.CustomSkillGenerator;
import cz.neumimto.rpg.effects.TestEffectFloat;

import javax.inject.Singleton;
import java.lang.reflect.Type;

@Singleton
public class TestCustomSkillGenerator extends CustomSkillGenerator {
    @Override
    protected String getDefaultEffectPackage() {
        return TestEffectFloat.class.getPackage().getName();
    }

    @Override
    protected Type characterClassImpl() {
        return TestCharacter.class;
    }

    @Override
    protected Class<?> targeted() {
        return Void.class;
    }
}
