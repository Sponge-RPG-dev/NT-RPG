package cz.neumimto.rpg;

import cz.neumimto.core.ioc.IoC;
import cz.neumimto.rpg.configuration.DebugLevel;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.inventory.ItemService;
import cz.neumimto.rpg.inventory.RPGItemType;
import cz.neumimto.rpg.inventory.WeaponClass;
import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.PropertyService;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;

import java.util.UUID;

public class TestPropertyValueResolving {

    @BeforeClass
    public static void initLogger() {
        Log.logger = LoggerFactory.getLogger(NtRpgPlugin.class);
        NtRpgPlugin.pluginConfig = Mockito.mock(PluginConfig.class);
        NtRpgPlugin.pluginConfig.DEBUG = DebugLevel.NONE;
        Game mock = Mockito.mock(Game.class);
        IoC.get().registerInterfaceImplementation(Game.class, mock);
        IoC.get().registerInterfaceImplementation(ResourceLoader.class, Mockito.mock(ResourceLoader.class));
    }

    //@Test
    public void test0() {
        PropertyService propertyService = IoC.get().build(PropertyService.class);
        DamageService ds = IoC.get().build(DamageService.class);
        ItemService i = IoC.get().build(ItemService.class);

        CharacterService characterService = IoC.get().build(CharacterService.class);

        String b1 = "test_bonus1";
        String b2 = "test_bonus2";

        String m1 = "test_mult2";
        String m2 = "test_mult2";

        propertyService.registerProperty(b1, PropertyService.getAndIncrement.get());
        propertyService.registerProperty(b2, PropertyService.getAndIncrement.get());

        propertyService.registerProperty(m1, PropertyService.getAndIncrement.get());
        propertyService.registerProperty(m2, PropertyService.getAndIncrement.get());

        propertyService.registerDefaultValue(propertyService.getIdByName(m1), 1);
        propertyService.registerDefaultValue(propertyService.getIdByName(m2), 1);

        WeaponClass weaponClass0 = new WeaponClass("test");
        weaponClass0.getProperties().add(propertyService.getIdByName(b2));
        weaponClass0.getPropertiesMults().add(propertyService.getIdByName(m2));

        i.registerItemType(ItemTypes.DIAMOND_AXE, null, weaponClass0);

        RPGItemType item = i.getByItemTypeAndName(ItemTypes.DIAMOND_AXE, (Text) null);

        Player player = Mockito.mock(Player.class);
        UUID uuid = UUID.randomUUID();
        Mockito.when(player.getUniqueId()).thenReturn(uuid);
        IActiveCharacter character = new ActiveCharacter(player, new CharacterBase());

        characterService.initActiveCharacter(character);

    }
}
