package cz.neumimto.rpg.junit;

import cz.neumimto.rpg.common.entity.TestCharacter;
import cz.neumimto.rpg.common.entity.TestPropertyService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.persistance.model.CharacterClass;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.UUID;

import static cz.neumimto.rpg.junit.TestDictionary.*;

public class CharactersExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(ActiveCharacter.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Annotation[] annotations = parameterContext.getParameter().getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Stage) {
                switch (((Stage) annotation).value()) {
                    case READY:
                        return initializedCharacter();
                    default:
                        throw new IllegalStateException("Unknown Character Stage");
                }
            }
        }
        throw new IllegalStateException("Unknown Character Stage, use @Stage annotation");
    }


    private TestCharacter initializedCharacter() {
        CharacterBase characterBase = new CharacterBase();
        characterBase.setUuid(UUID.randomUUID());
        characterBase.getAttributes().put(STR.getId(), 0);
        characterBase.getAttributes().put(AGI.getId(), 0);
        TestCharacter activeCharacter = new TestCharacter(UUID.randomUUID(), characterBase, TestPropertyService.LAST_ID);


        CharacterClass primaryCC = new CharacterClass();
        primaryCC.setId(1L);
        primaryCC.setCharacterBase(characterBase);
        primaryCC.setName("Primary");
        PlayerClassData playerClassData = new PlayerClassData(CLASS_PRIMARY, primaryCC);
        playerClassData.setLevel(5);
        characterBase.getCharacterClasses().add(primaryCC);
        activeCharacter.getClasses().put(playerClassData.getClassDefinition().getName(), playerClassData);


        CharacterClass secondaryCC = new CharacterClass();
        secondaryCC.setId(2L);
        secondaryCC.setCharacterBase(characterBase);
        secondaryCC.setName("Secondary");

        PlayerClassData playerClassDataSecondary = new PlayerClassData(CLASS_SECONDARY, secondaryCC);
        playerClassDataSecondary.setLevel(10);
        characterBase.getCharacterClasses().add(secondaryCC);
        activeCharacter.getClasses().put(playerClassDataSecondary.getClassDefinition().getName(), playerClassDataSecondary);


        characterBase.getAttributes().put(TestDictionary.AGI.getId(), 5);
        characterBase.getAttributes().put(TestDictionary.STR.getId(), 5);
        activeCharacter.getTransientAttributes().put(TestDictionary.AGI.getId(), 94);
        activeCharacter.getTransientAttributes().put(TestDictionary.STR.getId(), 5);


        activeCharacter.setAttributesTransaction(new HashMap<>());

        return activeCharacter;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Stage {
        Stages value();

        enum Stages {
            READY
        }
    }

}
