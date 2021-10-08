package cz.neumimto.rpg;

import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.items.RpgItemStack;
import cz.neumimto.rpg.common.items.RpgItemType;
import cz.neumimto.rpg.common.impl.TestItemService;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.CharactersExtension.Stage;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestDictionary;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

import static cz.neumimto.rpg.junit.CharactersExtension.Stage.Stages.READY;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class, CharactersExtension.class})
@IncludeModule(TestGuiceModule.class)
public class DamageServiceTest {

    @Inject
    private TestItemService testItemService;

    @Inject
    private DamageService damageService;

    @Inject
    private PropertyService propertyService;

    @Test
    void recalculateCharacterWeaponDamageTest01(@Stage(READY) IActiveCharacter character) {
        RpgItemType itemType = TestDictionary.ITEM_TYPE_WEAPON_1;
        RpgItemStack rpgItemStack = testItemService.getRpgItemStack(itemType);
        character.getAllowedWeapons().put(itemType, 10D);
        character.setMainHand(rpgItemStack, 1);
        damageService.recalculateCharacterWeaponDamage(character);
        Assertions.assertEquals(10D, character.getWeaponDamage());
    }

    @Test
    void recalculateCharacterWeaponDamage02(@Stage(READY) IActiveCharacter character) {

        RpgItemType itemType = TestDictionary.ITEM_TYPE_WEAPON_1;
        itemType.getItemClass().getPropertiesMults().clear();
        itemType.getItemClass().getProperties().add(1);

        RpgItemStack rpgItemStack = testItemService.getRpgItemStack(itemType);
        character.setMainHand(rpgItemStack, 1);
        character.setProperty(1, 10);
        character.getAllowedWeapons().put(itemType, 10D);

        damageService.recalculateCharacterWeaponDamage(character);

        Assertions.assertEquals(20D, character.getWeaponDamage());
    }

    @Test
    void recalculateCharacterWeaponDamage03(@Stage(READY) IActiveCharacter character) {
        RpgItemType itemType = TestDictionary.ITEM_TYPE_WEAPON_2;
        itemType.getItemClass().getPropertiesMults().clear();
        itemType.getItemClass().getPropertiesMults().add(1);

        RpgItemStack rpgItemStack = testItemService.getRpgItemStack(itemType);
        character.setMainHand(rpgItemStack, 1);
        character.setProperty(1, 1.1f);
        character.getAllowedWeapons().put(itemType, 100D);

        damageService.recalculateCharacterWeaponDamage(character);

        Assertions.assertEquals((int) (100 + 100 * 0.1), (int) character.getWeaponDamage());
    }


}