package cz.neumimto.rpg.spigot.scripting;

import cz.neumimto.rpg.common.skills.scripting.CustomSkillGenerator;
import cz.neumimto.rpg.spigot.effects.common.StunEffect;

import javax.inject.Singleton;

@Singleton
public class SpigotCustomSkillGenerator extends CustomSkillGenerator {

    @Override
    protected String getDefaultEffectPackage() {
        return StunEffect.class.getPackage().getName();
    }
}
