package cz.neumimto.rpg.common.items;

import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.items.WeaponClass;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
public class ItemServiceTest {

    @Inject
    private static ItemService itemService;

    @Test
    public void getWeaponClassByNameUnknown() {
        Assertions.assertFalse(itemService.getWeaponClassByName("unknown").isPresent());;
    }

    @Test
    public void getItemTypesByWeaponClassUnknown() {
        new ItemServiceTest().registerWeaponClassSubClassesExists();

        RpgItemTypeImpl rpgItemType = new RpgItemTypeImpl("testItem", "testModel", null, 0D, 0D);
        itemService.getWeaponClassByName("test").get().getItems().add(rpgItemType);
    }

    @Test
    public void registerWeaponClass() {
        WeaponClass weaponClass = new WeaponClass("test");
        itemService.registerWeaponClass(weaponClass);
        Assertions.assertTrue(itemService.getWeaponClassByName("test").isPresent());
    }

    @Test
    public void registerWeaponClassSubClassesExists() {
        WeaponClass weaponClass = new WeaponClass("test");
        WeaponClass ts = new WeaponClass("testsub");
        weaponClass.getSubClass().add(ts);
        itemService.registerWeaponClass(weaponClass);

        Assertions.assertTrue(itemService.getWeaponClassByName("test").isPresent());
        Assertions.assertTrue(itemService.getWeaponClassByName("testsub").isPresent());
    }

    @Test
    public void loadItemGroups01() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("testconfig/ItemGroups.conf").getFile());
        itemService.loadItemGroups(Paths.get(file.getPath()));

        Optional<WeaponClass> blades = itemService.getWeaponClassByName("Blades");
        Assertions.assertTrue(blades.isPresent());
        WeaponClass weaponClass = blades.get();
        Assertions.assertEquals(2, weaponClass.getSubClass().size());

        boolean cFound = false;
        boolean bFound = false;
        for (WeaponClass subClass : weaponClass.getSubClass()) {

            if (subClass.getName().equalsIgnoreCase("Cleaving")) {
                Assertions.assertEquals(5, subClass.getItems().size());
                Assertions.assertEquals(1, subClass.getProperties().size());
                Assertions.assertEquals(1, subClass.getPropertiesMults().size());
                for (RpgItemType item : subClass.getItems()) {
                    Assertions.assertEquals(0, item.getDamage());
                    Assertions.assertNull(item.getModelId());
                    cFound = true;
                }
            }

            if (subClass.getName().equalsIgnoreCase("Swords")) {
                Assertions.assertEquals(5, subClass.getItems().size());
                Assertions.assertEquals(1, subClass.getProperties().size());
                Assertions.assertEquals(1, subClass.getPropertiesMults().size());
                for (RpgItemType item : subClass.getItems()) {
                    Assertions.assertEquals(10, item.getDamage());
                    Assertions.assertEquals("model", item.getModelId());
                    bFound = true;
                }
            }
        }
        Assertions.assertTrue(cFound);
        Assertions.assertTrue(bFound);
    }

}