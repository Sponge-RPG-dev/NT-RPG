package cz.neumimto.rpg.junit;

import cz.neumimto.rpg.common.entity.IReservable;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.inventory.ManagedSlot;
import cz.neumimto.rpg.common.items.ItemClass;
import cz.neumimto.rpg.common.model.CharacterBase;
import cz.neumimto.rpg.common.model.CharacterClass;
import cz.neumimto.rpg.common.entity.TestCharacter;
import cz.neumimto.rpg.common.entity.TestPropertyService;
import cz.neumimto.rpg.common.inventory.FilteredManagedSlotImpl;
import cz.neumimto.rpg.common.inventory.RpgInventoryImpl;
import cz.neumimto.rpg.model.CharacterBaseTest;
import cz.neumimto.rpg.model.CharacterClassTest;
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
        return parameterContext.getParameter().getType().isAssignableFrom(IActiveCharacter.class);
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
        CharacterBase characterBase = new CharacterBaseTest();
        characterBase.setUuid(UUID.randomUUID());
        characterBase.getAttributes().put(STR.getId(), 0);
        characterBase.getAttributes().put(AGI.getId(), 0);
        TestCharacter activeCharacter = new TestCharacter(UUID.randomUUID(), characterBase, TestPropertyService.LAST_ID);


        CharacterClass primaryCC = new CharacterClassTest();
        primaryCC.setId(1L);
        primaryCC.setCharacterBase(characterBase);
        primaryCC.setName("Primary");
        PlayerClassData playerClassData = new PlayerClassData(CLASS_PRIMARY, primaryCC);
        playerClassData.setLevel(5);
        characterBase.getCharacterClasses().add(primaryCC);
        activeCharacter.getClasses().put(playerClassData.getClassDefinition().getName(), playerClassData);


        CharacterClass secondaryCC = new CharacterClassTest();
        secondaryCC.setId(2L);
        secondaryCC.setCharacterBase(characterBase);
        secondaryCC.setName("Secondary");

        PlayerClassData playerClassDataSecondary = new PlayerClassData(CLASS_SECONDARY, secondaryCC);
        playerClassDataSecondary.setLevel(10);
        characterBase.getCharacterClasses().add(secondaryCC);
        activeCharacter.getClasses().put(playerClassDataSecondary.getClassDefinition().getName(), playerClassDataSecondary);


        ManagedSlot managedSlot = new FilteredManagedSlotImpl(0,
                weaponClass -> weaponClass.getItemType().getItemClass() == ItemClass.ARMOR);
        ManagedSlot filteredSlot = new FilteredManagedSlotImpl(1,
                weaponClass -> weaponClass.getItemType().getItemClass() == TestDictionary.WEAPON_CLASS_1);


        activeCharacter.getAllowedWeapons().put(TestDictionary.ITEM_TYPE_WEAPON_1, 20D);
        activeCharacter.getAllowedArmor().add(TestDictionary.ARMOR_TYPE_1);
        activeCharacter.getManagedInventory().put(Object.class, new RpgInventoryImpl());
        activeCharacter.getManagedInventory().get(Object.class).getManagedSlots().put(managedSlot.getId(), managedSlot);
        activeCharacter.getManagedInventory().get(Object.class).getManagedSlots().put(filteredSlot.getId(), filteredSlot);


        characterBase.getAttributes().put(TestDictionary.AGI.getId(), 5);
        characterBase.getAttributes().put(TestDictionary.STR.getId(), 5);
        activeCharacter.getTransientAttributes().put(TestDictionary.AGI.getId(), 94);
        activeCharacter.getTransientAttributes().put(TestDictionary.STR.getId(), 5);

        activeCharacter.setHealth(new TestPool());
        activeCharacter.setMana(new TestPool());

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

    private static class TestPool implements IReservable {
        double max = 100;
        private double value = 50;
        private float regen = 1;

        @Override
        public void setReservedAmnout(float f) {

        }

        @Override
        public double getReservedAmount() {
            return 0;
        }

        @Override
        public double getMaxValue() {
            return max;
        }

        @Override
        public void setMaxValue(double f) {
            this.max = f;
        }

        @Override
        public double getValue() {
            return value;
        }

        @Override
        public void setValue(double f) {
            this.value = f;
        }

        @Override
        public double getRegen() {
            return this.regen;
        }

        @Override
        public void setRegen(float f) {
            this.regen = f;
        }
    }


}
