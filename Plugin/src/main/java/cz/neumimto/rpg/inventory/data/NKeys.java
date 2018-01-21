package cz.neumimto.rpg.inventory.data;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.inventory.ItemDamage;
import cz.neumimto.rpg.inventory.LoreDurability;
import cz.neumimto.rpg.inventory.LoreSectionDelimiter;
import cz.neumimto.rpg.inventory.runewords.ItemUpgrade;
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

	public static Key<Value<String>> COMMAND = null;
	public static Key<Value<Boolean>> MENU_INVENTORY = null;
	public static Key<Value<String>> SKILLTREE_CONTROLLS = null;
	public static Key<Value<Text>> ITEM_RARITY = null;
	public static Key<MapValue<String, Integer>> ITEM_ATTRIBUTE_REQUIREMENTS = null;
	public static Key<MapValue<String, Integer>> ITEM_ATTRIBUTE_BONUS = null;
	public static Key<MapValue<String, Float>> ITEM_PROPERTY_BONUS = null;
	public static Key<MapValue<String, EffectParams>> ITEM_EFFECTS = null;
	public static Key<ListValue<ItemSocket>> ITEM_STACK_UPGRADE_CONTAINER = null;
	public static Key<MapValue<String, Integer>> ITEM_PLAYER_ALLOWED_GROUPS = null;
	public static Key<Value<Text>> ITEM_TYPE = null;
	public static Key<Value<Integer>> ITEM_LEVEL = null;
	public static Key<Value<LoreDurability>> ITEM_LORE_DURABILITY = null;
	public static Key<Value<LoreSectionDelimiter>> ITEM_SECTION_DELIMITER = null;
	public static Key<Value<ItemDamage>> ITEM_DAMAGE = null;
	public static Key<Value<ItemUpgrade>> ITEMSTACK_UPGRADE = null;

	public NKeys() {
		COMMAND = Key.builder()
				.type(new TypeToken<Value<String>>() {})
				.name("Command")
				.query(DataQuery.of("ntrpg:command"))
				.id("ntrpgcommand")
				.build();
		MENU_INVENTORY = Key.builder()
				.type(new TypeToken<Value<Boolean>>() {	})
				.query(DataQuery.of("ntrpg:menuinventory"))
				.name("Inventory menu")
				.id("ntrpginventory")
				.build();
		 SKILLTREE_CONTROLLS = Key.builder()
				.type(new TypeToken<Value<String>>() {})
				.query(DataQuery.of( "ntrpg:skilltreecontrolls"))
			    .id("ntrpgskilltreecontrolls")
				.name("Skilltree controls")
			 .build();
		ITEM_RARITY = Key.builder()
				.type(new TypeToken<Value<Text>>() {})
				.query(DataQuery.of( "ntrpg:itemrarity"))
				.id("ntrpgitemrarity")
				.name("Item rarity")
				.build();
		ITEM_ATTRIBUTE_REQUIREMENTS = Key.builder()
				.type(new TypeToken<MapValue<String, Integer>>() {})
				.query(DataQuery.of("ntrpg:itemattributerequirements"))
				.name("Item attribute requirements")
				.id("ntrpgattrequ")
				.build();
		ITEM_ATTRIBUTE_BONUS = Key.builder()
				.type(new TypeToken<MapValue<String, Integer>>() {})
				.query(DataQuery.of( "ntrpg:itemattributebonus"))
				.name("Item attribute bonus")
				.id("ntrpgattbonus")
				.build();
		ITEM_PROPERTY_BONUS = Key.builder()
				.type(new TypeToken<MapValue<String, Float>>() {})
				.query(DataQuery.of( "ntrpg:itempropertybonus"))
				.name("Item property bonus")
				.id("ntrpgattpropbonus")
				.build();
		ITEM_EFFECTS = Key.builder()
				.type(new TypeToken<MapValue<String, EffectParams>>() {})
				.name("Item Effects")
				.query(DataQuery.of(".","item_effects"))
				.id("nt-rpg:item_effects")
				.build();
		ITEM_STACK_UPGRADE_CONTAINER = Key.builder()
				.type(new TypeToken<ListValue<ItemSocket>>() {})
				.query(DataQuery.of("ntrpg:itemsockets"))
				.name("Item sockets")
				.id("ntrpgitemstackupgradecontainer")
				.build();
		ITEM_PLAYER_ALLOWED_GROUPS = Key.builder()
				.type(new TypeToken<MapValue<String, Integer>>() {})
				.query(DataQuery.of("ntrpg:itemplayergroupsrestrictions"))
				.name("Item group restriction")
				.id("ntrpgitemplayerallowedgroups")
				.build();
		ITEM_TYPE = Key.builder()
				.type(new TypeToken<Value<Text>>() {})
				.query(DataQuery.of( "ntrpg:itemtype"))
				.name("Item type")
				.id("ntrpgitemtype")
				.build();
		ITEM_LEVEL = Key.builder()
				.type(new TypeToken<Value<Integer>>() {})
				.query(DataQuery.of("ntrpg:itemlevel"))
				.name("Item level")
				.id("ntrpgitemlevel")
				.build();
		ITEM_LORE_DURABILITY = Key.builder()
				.type(new TypeToken<Value<LoreDurability>>() {})
				.query(DataQuery.of("ntrpg:itemloredurability"))
				.name("Item loredurability")
				.id("ntrpgdurability")
				.build();
		ITEM_SECTION_DELIMITER  = Key.builder()
				.type(new TypeToken<Value<LoreSectionDelimiter>>() {})
				.query(DataQuery.of("ntrpg:itemsectiondelimiter"))
				.name("Item section delimiter")
				.id("ntrpgsectiondelimiter")
				.build();
		ITEM_DAMAGE = Key.builder()
				.type(new TypeToken<Value<ItemDamage>>() {})
				.query(DataQuery.of("ntrpg:itemdamage"))
				.name("Item damage")
				.id("ntrpgitemdamage")
				.build();
		ITEMSTACK_UPGRADE = Key.builder()
				.type(new TypeToken<Value<ItemUpgrade>>() {})
				.query(DataQuery.of("ntrpg:itemstackupgrade"))
				.name("itemstack upgrade")
				.id("ntrpgitemupgrade")
				.build();
	}
}
