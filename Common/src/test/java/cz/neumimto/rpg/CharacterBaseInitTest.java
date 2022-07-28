package cz.neumimto.rpg;

import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.model.CharacterBase;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
public class CharacterBaseInitTest {

    @Inject
    CharacterService characterService;

    @Test
    public void testInitialization() {
        CharacterBase test = characterService.createCharacterBase("test", UUID.randomUUID(), "plName");
        Method[] declaredMethods = CharacterBase.class.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getName().equals("getVersion"))
                continue;
            if (declaredMethod.getReturnType().isPrimitive() && declaredMethod.getParameterCount() == 0) {
                Log.info("Executing CharacterBase#" + declaredMethod.getName());
                try {
                    declaredMethod.invoke(test);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}
