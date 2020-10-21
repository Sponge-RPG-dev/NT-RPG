package cz.neumimto.rpg.spigot.scripting;

import cz.neumimto.rpg.common.skills.scripting.CustomSkillGenerator;
import cz.neumimto.rpg.spigot.effects.common.StunEffect;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.skills.TargetedEntitySkill;

import javax.inject.Singleton;
import java.lang.reflect.Type;

@Singleton
public class SpigotCustomSkillGenerator extends CustomSkillGenerator {

    @Override
    protected String getDefaultEffectPackage() {
        return StunEffect.class.getPackage().getName();
    }

    @Override
    protected Type characterClassImpl() {
        return ISpigotCharacter.class;
    }

    @Override
    protected Class<?> targeted() {
        return TargetedEntitySkill.class;
    }
}
