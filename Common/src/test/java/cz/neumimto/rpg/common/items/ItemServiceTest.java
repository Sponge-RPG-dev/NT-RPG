package cz.neumimto.rpg.common.items;

import com.typesafe.config.ConfigFactory;
import cz.neumimto.rpg.RpgTest;
import cz.neumimto.rpg.common.RpgApi;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.io.File;
import java.util.Optional;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
public class ItemServiceTest {

    @Inject
    private ItemService itemService;

    @Inject
    private RpgApi api;

    @BeforeEach
    public void before() {
        new RpgTest(api);
    }

    @Test
    public void getWeaponClassByNameUnknown() {
        Assertions.assertFalse(itemService.getWeaponClassByName("unknown").isPresent());
    }

    @Test
    public void getItemTypesByWeaponClassUnknown() {
        registerWeaponClassSubClassesExists();

        RpgItemTypeImpl rpgItemType = new RpgItemTypeImpl("testItem", "testModel", null,  null);
        itemService.getWeaponClassByName("test").get().getItems().add(rpgItemType);
    }

    @Test
    public void registerWeaponClass() {
        ItemClass itemClass = new ItemClass("test");
        itemService.registerWeaponClass(itemClass);
        Assertions.assertTrue(itemService.getWeaponClassByName("test").isPresent());
    }

    @Test
    public void registerWeaponClassSubClassesExists() {
        ItemClass itemClass = new ItemClass("test");
        ItemClass ts = new ItemClass("testsub");
        itemClass.getSubClass().add(ts);
        itemService.registerWeaponClass(itemClass);

        Assertions.assertTrue(itemService.getWeaponClassByName("test").isPresent());
        Assertions.assertTrue(itemService.getWeaponClassByName("testsub").isPresent());
    }

    @Test
    public void loadItemGroups01() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("testconfig/ItemGroups.conf").getFile());
        itemService.loadItemGroups(ConfigFactory.parseFile(file));

        Optional<ItemClass> blades = itemService.getWeaponClassByName("Blades");
        Assertions.assertTrue(blades.isPresent());
        ItemClass itemClass = blades.get();
        Assertions.assertEquals(2, itemClass.getSubClass().size());

        boolean cFound = false;
        boolean bFound = false;
        for (ItemClass subClass : itemClass.getSubClass()) {

            if (subClass.getName().equalsIgnoreCase("Cleaving")) {
                Assertions.assertEquals(6, subClass.getItems().size());
                Assertions.assertEquals(1, subClass.getProperties().size());
                Assertions.assertEquals(1, subClass.getPropertiesMults().size());
                for (RpgItemType item : subClass.getItems()) {
                    cFound = true;
                }
            }

            if (subClass.getName().equalsIgnoreCase("Swords")) {
                Assertions.assertEquals(11, subClass.getItems().size());
                Assertions.assertEquals(1, subClass.getProperties().size());
                Assertions.assertEquals(1, subClass.getPropertiesMults().size());
                for (RpgItemType item : subClass.getItems()) {
                    bFound = true;
                }
            }
        }
        Assertions.assertTrue(cFound);
        Assertions.assertTrue(bFound);
    }

}