package cz.neumimto.rpg;

import cz.neumimto.rpg.api.items.ItemClass;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.entity.PropertyServiceImpl;
import cz.neumimto.rpg.configuration.DebugLevel;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.inventory.SpongeItemService;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.properties.SpongePropertyService;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;

public class TestPropertyValueResolving {

    @BeforeAll
    public static void initLogger() {
        Log.setLogger(LoggerFactory.getLogger(NtRpgPlugin.class));
        NtRpgPlugin.pluginConfig = Mockito.mock(PluginConfig.class);
        NtRpgPlugin.pluginConfig.DEBUG = DebugLevel.NONE;
        Game mock = Mockito.mock(Game.class);
    }

    //@Test
    public void test0() {
        SpongePropertyService spongePropertyService = NtRpgPlugin.GlobalScope.spongePropertyService;
        SpongeItemService i = NtRpgPlugin.GlobalScope.itemService;

        CharacterService characterService = NtRpgPlugin.GlobalScope.characterService;

        String b1 = "test_bonus1";
        String b2 = "test_bonus2";

        String m1 = "test_mult2";
        String m2 = "test_mult2";

        spongePropertyService.registerProperty(b1, PropertyServiceImpl.getAndIncrement.get());
        spongePropertyService.registerProperty(b2, PropertyServiceImpl.getAndIncrement.get());

        spongePropertyService.registerProperty(m1, PropertyServiceImpl.getAndIncrement.get());
        spongePropertyService.registerProperty(m2, PropertyServiceImpl.getAndIncrement.get());

        spongePropertyService.registerDefaultValue(spongePropertyService.getIdByName(m1), 1);
        spongePropertyService.registerDefaultValue(spongePropertyService.getIdByName(m2), 1);

        ItemClass itemClass0 = new ItemClass("test");
        itemClass0.getProperties().add(spongePropertyService.getIdByName(b2));
        itemClass0.getPropertiesMults().add(spongePropertyService.getIdByName(m2));
/*
        i.registerItemType(ItemTypes.DIAMOND_AXE, null, itemClass0, 0);

        RPGItemTypeToRemove item = i.getByItemTypeAndName(ItemTypes.DIAMOND_AXE, (Text) null);

        UUID uuid = UUID.randomUUID();
        IActiveCharacter character = new ActiveCharacter(uuid, new CharacterBase());

        characterService.initActiveCharacter(character);
*/
    }
}
