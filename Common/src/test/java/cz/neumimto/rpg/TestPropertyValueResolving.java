package cz.neumimto.rpg;

import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.items.ItemClass;
import cz.neumimto.rpg.common.entity.PropertyServiceImpl;

import javax.inject.Inject;


public class TestPropertyValueResolving {

    @Inject
    private PropertyService propertyService;

    //@Test
    public void test0() {


        String b1 = "test_bonus1";
        String b2 = "test_bonus2";

        String m1 = "test_mult2";
        String m2 = "test_mult2";

        propertyService.registerProperty(b1, PropertyServiceImpl.getAndIncrement.get());
        propertyService.registerProperty(b2, PropertyServiceImpl.getAndIncrement.get());

        propertyService.registerProperty(m1, PropertyServiceImpl.getAndIncrement.get());
        propertyService.registerProperty(m2, PropertyServiceImpl.getAndIncrement.get());

        propertyService.registerDefaultValue(propertyService.getIdByName(m1), 1);
        propertyService.registerDefaultValue(propertyService.getIdByName(m2), 1);

        ItemClass itemClass0 = new ItemClass("test");
        itemClass0.getProperties().add(propertyService.getIdByName(b2));
        itemClass0.getPropertiesMults().add(propertyService.getIdByName(m2));
/*
        i.registerItemType(ItemTypes.DIAMOND_AXE, null, itemClass0, 0);

        RPGItemTypeToRemove item = i.getByItemTypeAndName(ItemTypes.DIAMOND_AXE, (Text) null);

        UUID uuid = UUID.randomUUID();
        IActiveCharacter character = new ActiveCharacter(uuid, new CharacterBase());

        characterService.initActiveCharacter(character);
*/
    }

}
