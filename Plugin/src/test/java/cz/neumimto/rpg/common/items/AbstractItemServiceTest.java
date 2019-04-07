package cz.neumimto.rpg.common.items;

import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.items.WeaponClass;
import cz.neumimto.rpg.common.impl.TestItemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;


public class AbstractItemServiceTest {

    private static ItemService itemService;

    @BeforeEach
    public void setUp() throws Exception {
        itemService = new TestItemService();
    }

    @Test
    public void getWeaponClassByNameUnknown() {
        Assertions.assertFalse(itemService.getWeaponClassByName("unknown").isPresent());;
    }

    @Test
    public void getItemTypesByWeaponClassUnknown() {
        new AbstractItemServiceTest().registerWeaponClassSubClassesExists();

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
    public void loadItemGroups() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("testconfig/ItemGroups.conf").getFile());
        itemService.loadItemGroups(Paths.get(file.getPath()));
    }

}