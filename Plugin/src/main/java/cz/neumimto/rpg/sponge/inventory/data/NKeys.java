package cz.neumimto.rpg.sponge.inventory.data;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.api.effects.EffectParams;
import cz.neumimto.rpg.sponge.inventory.ItemDamage;
import cz.neumimto.rpg.sponge.inventory.LoreDurability;
import cz.neumimto.rpg.sponge.inventory.LoreSectionDelimiter;
import cz.neumimto.rpg.sponge.inventory.items.ItemMetaType;
import cz.neumimto.rpg.sponge.inventory.items.subtypes.ItemSubtype;
import cz.neumimto.rpg.sponge.inventory.sockets.SocketType;
import cz.neumimto.rpg.sponge.gui.SkillTreeControllsButton;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.text.Text;

/**
 * Created by ja on 26.12.2016.
 */
public class NKeys {

    public static Key<Value<String>> ATTRIBUTE_REF = null;
    public static Key<Value<String>> COMMAND = null;
    public static Key<Value<Boolean>> MENU_INVENTORY = null;
    public static Key<Value<SkillTreeControllsButton>> SKILLTREE_CONTROLLS = null;
    public static Key<Value<Integer>> ITEM_RARITY = null;
    public static Key<MapValue<String, Integer>> ITEM_ATTRIBUTE_REQUIREMENTS = null;
    public static Key<MapValue<String, Integer>> ITEM_ATTRIBUTE_BONUS = null;
    public static Key<MapValue<String, Float>> ITEM_PROPERTY_BONUS = null;
    public static Key<MapValue<String, EffectParams>> ITEM_EFFECTS = null;

    public static Key<ListValue<SocketType>> ITEM_SOCKET_CONTAINER = null;
    public static Key<ListValue<Text>> ITEM_SOCKET_CONTAINER_CONTENT = null;

    public static Key<MapValue<String, Integer>> ITEM_PLAYER_ALLOWED_GROUPS = null;
    public static Key<Value<Text>> ITEM_META_HEADER = null;
    public static Key<Value<Integer>> ITEM_LEVEL = null;
    public static Key<Value<LoreDurability>> ITEM_LORE_DURABILITY = null;
    public static Key<Value<LoreSectionDelimiter>> ITEM_SECTION_DELIMITER = null;
    public static Key<Value<ItemDamage>> ITEM_DAMAGE = null;
    public static Key<Value<SocketType>> ITEMSTACK_UPGRADE = null;
    public static Key<Value<String>> SKILLTREE_NODE = null;
    public static Key<Value<String>> SKILLBIND = null;
    public static Key<Value<ItemMetaType>> ITEM_META_TYPE = null;
    public static Key<Value<ItemSubtype>> ITEM_META_SUBTYPE = null;


    public NKeys() {

        ATTRIBUTE_REF = Key.builder()
                .type(new TypeToken<Value<String>>() {
                })
                .name("Attribute Ref")
                .query(DataQuery.of(".", "ntrpg.inventory.attrref"))
                .id("nt-rpg:item_attribute_ref")
                .build();
        COMMAND = Key.builder()
                .type(new TypeToken<Value<String>>() {
                })
                .name("Custom Inventory Command")
                .query(DataQuery.of(".", "ntrpg.inventory.command"))
                .id("nt-rpg:custom_inventory_command")
                .build();

        MENU_INVENTORY = Key.builder()
                .type(new TypeToken<Value<Boolean>>() {
                })
                .query(DataQuery.of(".", "ntrpg.inventory.menu"))
                .name("Inventory menu")
                .id("nt-rpg:menu_inventory")
                .build();

        SKILLTREE_CONTROLLS = Key.builder()
                .type(new TypeToken<Value<SkillTreeControllsButton>>() {
                })
                .id("nt-rpg:skilltree_controlls")
                .query(DataQuery.of(".", "ntrpg.skilltree.controlls"))
                .name("Skilltree controls")
                .build();

        SKILLTREE_NODE = Key.builder()
                .type(new TypeToken<Value<String>>() {
                })
                .id("nt-rpg:skilltree_node")
                .query(DataQuery.of(".", "ntrpg.skilltree.node"))
                .name("Skilltree node")
                .build();

        SKILLBIND = Key.builder()
                .type(new TypeToken<Value<String>>() {
                })
                .id("nt-rpg:skillbind")
                .query(DataQuery.of(".", "ntrpg.skillbind"))
                .name("Skillbind")
                .build();

        ITEM_RARITY = Key.builder()
                .type(new TypeToken<Value<Integer>>() {
                })
                .query(DataQuery.of("ntrpg:itemrarity"))
                .id("ntrpgitemrarity")
                .name("Item rarity")
                .build();

        ITEM_META_HEADER = Key.builder()
                .type(new TypeToken<Value<Text>>() {
                })
                .query(DataQuery.of(".", "ntrpg.item.meta.header"))
                .id("nt-rpg:item_meta_header")
                .name("Item meta header")
                .build();

        ITEM_META_TYPE = Key.builder()
                .type(new TypeToken<Value<ItemMetaType>>() {
                })
                .query(DataQuery.of(".", "ntrpg.item.type"))
                .id("nt-rpg:item_type")
                .name("Item type")
                .build();

        ITEM_META_SUBTYPE = Key.builder()
                .type(new TypeToken<Value<ItemSubtype>>() {
                })
                .query(DataQuery.of(".", "ntrpg.item.subtype"))
                .id("nt-rpg:item_subtype")
                .name("Item subtype")
                .build();


        ITEM_ATTRIBUTE_REQUIREMENTS = Key.builder()
                .type(new TypeToken<MapValue<String, Integer>>() {
                })
                .query(DataQuery.of("ntrpg:itemattributerequirements"))
                .name("Item attribute requirements")
                .id("ntrpgattrequ")
                .build();

        ITEM_ATTRIBUTE_BONUS = Key.builder()
                .type(new TypeToken<MapValue<String, Integer>>() {
                })
                .query(DataQuery.of("ntrpg:itemattributebonus"))
                .name("Item attribute bonus")
                .id("ntrpgattbonus")
                .build();

        ITEM_PROPERTY_BONUS = Key.builder()
                .type(new TypeToken<MapValue<String, Float>>() {
                })
                .query(DataQuery.of("ntrpg:itempropertybonus"))
                .name("Item property bonus")
                .id("ntrpgattpropbonus")
                .build();

        ITEM_EFFECTS = Key.builder()
                .type(new TypeToken<MapValue<String, EffectParams>>() {
                })
                .id("nt-rpg:item_effects")
                .name("Item Effects")
                .query(DataQuery.of('.', "ntrpg.itemeffects"))
                .build();

        ITEM_SOCKET_CONTAINER = Key.builder()
                .type(new TypeToken<ListValue<SocketType>>() {
                })
                .id("nt-rpg:item_socket_container")
                .name("Item Socket Container")
                .query(DataQuery.of(".", "ntrpg.item.sockets.container"))
                .build();

        ITEM_SOCKET_CONTAINER_CONTENT = Key.builder()
                .type(new TypeToken<ListValue<Text>>() {
                })
                .id("nt-rpg:item_sockets_content")
                .name("Item Socket Content")
                .query(DataQuery.of(".", "ntrpg.item.sockets.content"))
                .build();

        ITEM_PLAYER_ALLOWED_GROUPS = Key.builder()
                .type(new TypeToken<MapValue<String, Integer>>() {
                })
                .query(DataQuery.of("ntrpg:itemplayergroupsrestrictions"))
                .name("Item group restriction")
                .id("ntrpgitemplayerallowedgroups")
                .build();

        ITEM_META_HEADER = Key.builder()
                .type(new TypeToken<Value<Text>>() {
                })
                .query(DataQuery.of("ntrpg:itemtype"))
                .name("Item type")
                .id("ntrpgitemtype")
                .build();

        ITEM_LEVEL = Key.builder()
                .type(new TypeToken<Value<Integer>>() {
                })
                .query(DataQuery.of("ntrpg:itemlevel"))
                .name("Item level")
                .id("ntrpgitemlevel")
                .build();

        ITEM_LORE_DURABILITY = Key.builder()
                .type(new TypeToken<Value<LoreDurability>>() {
                })
                .query(DataQuery.of("ntrpg:itemloredurability"))
                .name("Item loredurability")
                .id("ntrpgdurability")
                .build();

        ITEM_SECTION_DELIMITER = Key.builder()
                .type(new TypeToken<Value<LoreSectionDelimiter>>() {
                })
                .query(DataQuery.of("ntrpg:itemsectiondelimiter"))
                .name("Item section delimiter")
                .id("ntrpgsectiondelimiter")
                .build();

        ITEM_DAMAGE = Key.builder()
                .type(new TypeToken<Value<ItemDamage>>() {
                })
                .query(DataQuery.of("ntrpg:itemdamage"))
                .name("Item damage")
                .id("ntrpgitemdamage")
                .build();

        ITEMSTACK_UPGRADE = Key.builder()
                .type(new TypeToken<Value<SocketType>>() {
                })
                .query(DataQuery.of(".", "ntrpg.item.upgrade"))
                .name("ItemStack Upgrade")
                .id("nt-rpg:itemstack_upgrade")
                .build();
    }
}
