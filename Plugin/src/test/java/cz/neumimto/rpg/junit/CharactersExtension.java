package cz.neumimto.rpg.junit;

import cz.neumimto.rpg.api.inventory.ManagedSlot;
import cz.neumimto.rpg.api.items.WeaponClass;
import cz.neumimto.rpg.common.inventory.FilteredManagedSlotImpl;
import cz.neumimto.rpg.common.inventory.RpgInventoryImpl;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.UUID;

import static cz.neumimto.rpg.junit.TestDictionary.CLASS_PRIMARY;
import static cz.neumimto.rpg.junit.TestDictionary.CLASS_SECONDARY;

public class CharactersExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == IActiveCharacter.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Annotation[] annotations = parameterContext.getParameter().getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Stage) {
                switch (((Stage)annotation).value()) {
                    case READY:
                        return initializedCharacter();
                    default:
                        throw new IllegalStateException("Unknown Character Stage");
                }
            }
        }
        throw new IllegalStateException("Unknown Character Stage, use @Stage annotation");
    }


    private IActiveCharacter initializedCharacter() {
        CharacterBase characterBase = new CharacterBase();
        ActiveCharacter activeCharacter = new ActiveCharacter(UUID.randomUUID(), characterBase);


        CharacterClass primaryCC = new CharacterClass();
        primaryCC.setId(1L);
        primaryCC.setCharacterBase(characterBase);

        PlayerClassData playerClassData = new PlayerClassData(CLASS_PRIMARY, primaryCC);
        playerClassData.setLevel(5);
        characterBase.getCharacterClasses().add(primaryCC);
        activeCharacter.getClasses().put(playerClassData.getClassDefinition().getName(), playerClassData);


        CharacterClass secondaryCC = new CharacterClass();
        secondaryCC.setId(2L);
        secondaryCC.setCharacterBase(characterBase);

        PlayerClassData playerClassDataSecondary = new PlayerClassData(CLASS_SECONDARY, secondaryCC);
        playerClassDataSecondary.setLevel(10);
        characterBase.getCharacterClasses().add(secondaryCC);
        activeCharacter.getClasses().put(playerClassDataSecondary.getClassDefinition().getName(), playerClassDataSecondary);


        ManagedSlot managedSlot = new FilteredManagedSlotImpl(0,
                weaponClass -> weaponClass == WeaponClass.ARMOR);
        ManagedSlot filteredSlot = new FilteredManagedSlotImpl(1,
                weaponClass -> weaponClass == TestDictionary.WEAPON_CLASS_1);


        activeCharacter.getAllowedWeapons().put(TestDictionary.ITEM_TYPE_WEAPON_1, 20D);
        activeCharacter.getAllowedArmor().add(TestDictionary.ARMOR_TYPE_1);
        activeCharacter.getManagedInventory().put(Object.class, new RpgInventoryImpl());
        activeCharacter.getManagedInventory().get(Object.class).getManagedSlots().put(managedSlot.getId(), managedSlot);
        activeCharacter.getManagedInventory().get(Object.class).getManagedSlots().put(filteredSlot.getId(), filteredSlot);


        characterBase.getAttributes().put(TestDictionary.AGI.getId(), 5);
        characterBase.getAttributes().put(TestDictionary.STR.getId(), 5);
        activeCharacter.getTransientAttributes().put(TestDictionary.AGI.getId(), 94);
        activeCharacter.getTransientAttributes().put(TestDictionary.STR.getId(), 5);

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
