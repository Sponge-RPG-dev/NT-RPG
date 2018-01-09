package cz.neumimto.rpg.inventory.data;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.Pair;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.model.mappers.SingleValueModelMapper;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Map;

/**
 * Created by ja on 26.12.2016.
 */
public class NKeys {

	public static final Key<Value<String>> COMMAND = KeyFactory.makeSingleKey(TypeToken.of(String.class),
			new TypeToken<Value<String>>() {
			},
			DataQuery.of('.', "ntrpg:command"),
			"ntrpg:command",
			"Command"
	);


	public static final Key<Value<Boolean>> MENU_INVENTORY = KeyFactory.makeSingleKey(TypeToken.of(Boolean.class),
			new TypeToken<Value<Boolean>>() {
			},
			DataQuery.of('.', "ntrpg:menuinventory"),
			"ntrpg:menu_inventory",
			"Inventory menu"
	);

	public static final Key<Value<String>> SKILLTREE_CONTROLLS = KeyFactory.makeSingleKey(TypeToken.of(String.class),
			new TypeToken<Value<String>>() {
			},
			DataQuery.of('.', "ntrpg:skilltreecontrolls"),
			"ntrpg:skilltreecontrolls",
			"Skilltree controls"
	);

	public static final Key<Value<Text>> ITEM_RARITY = KeyFactory.makeSingleKey(TypeToken.of(Text.class),
			new TypeToken<Value<Text>>() {
			},
			DataQuery.of('.', "ntrpg:itemrarity"),
			"ntrpg:item_rarity",
			"Item rarity"
	);

	public static final Key<Value<Integer>> CUSTOM_ITEM_DATA_ITEM_LEVEL = KeyFactory
			.makeSingleKey(TypeToken.of(Integer.class),
					new TypeToken<Value<Integer>>() {
					},
					DataQuery.of('.', "ntrpg:customitemdatalevel"),
					"ntrpg:custom_idem_data_level",
					"ntrpg custom item level"
			);

	public static final Key<Value<Integer>> CUSTOM_ITEM_DATA_SOCKET_COUNT = KeyFactory
			.makeSingleKey(TypeToken.of(Integer.class),
					new TypeToken<Value<Integer>>() {
					},
					DataQuery.of('.', "ntrpg:customitemdatasocketcount"),
					"ntrpg:custom_idem_data_socket_count",
					"ntrpg custom item socket count"
			);

	public static final Key<MapValue<String, EffectParams>> CUSTOM_ITEM_DATA_ENCHANTEMENTS = KeyFactory.makeMapKey(
			new TypeToken<Map<String, EffectParams>>() {
			},
			new TypeToken<MapValue<String, EffectParams>>() {
			},
			DataQuery.of('.', "ntrpg:customitemdataenchantements"),
			"ntrpg:custom_idem_data_enchantements",
			"ntrpg custom item enchantements"
	);

	public static final Key<ListValue<String>> CUSTOM_ITEM_DATA_RESTRICTIONS = KeyFactory.makeListKey(
			new TypeToken<List<String>>() {
			},
			new TypeToken<ListValue<String>>() {
			},
			DataQuery.of('.', "ntrpg:customitemdatarestrictions"),
			"ntrpg:custom_idem_data_restrictions",
			"ntrpg custom item restrictions"
	);


	public static final Key<Value<String>> ITEM_LORE = KeyFactory.makeSingleKey(TypeToken.of(String.class),
			new TypeToken<Value<String>>() {
			},
			DataQuery.of('.', "n.customitemlore"),
			"ntrpg:custom_item_lore",
			"Custom Item Data Lore"
	);


	public static Key<MapValue<String, Integer>> ITEM_ATTRIBUTE_REQUIREMENTS = KeyFactory.makeMapKey(
			new TypeToken<Map<String, Integer>>() {
			},
			new TypeToken<MapValue<String, Integer>>() {
			},
			DataQuery.of(".", "ntrpg:itemattributerequirements"),
			"ntrpg:item_attribute_requirements",
			"Item attribute requirements");
	
	public static Key<MapValue<String, Integer>> ITEM_ATTRIBUTE_BONUS = KeyFactory.makeMapKey(
			new TypeToken<Map<String, Integer>>() {
			},
			new TypeToken<MapValue<String, Integer>>() {
			},
			DataQuery.of(".", "ntrpg:itemattributebonus"),
			"ntrpg:item_attribute_bonus",
			"Item attribute bonus");
	
	public static Key<MapValue<String, Float>> ITEM_PROPERTY_BONUS = KeyFactory.makeMapKey(
			new TypeToken<Map<String, Float>>() {
			},
			new TypeToken<MapValue<String, Float>>() {
			},
			DataQuery.of(".", "ntrpg:itempropertybonus"),
			"ntrpg:item_property_bonus",
			"Item property bonus");
	
	public static Key<MapValue<String, EffectParams>> ITEM_EFFECTS = KeyFactory.makeMapKey(
			new TypeToken<Map<String, EffectParams>>() {
			},
			new TypeToken<MapValue<String, EffectParams>>() {
			},
			DataQuery.of(".", "ntrpg:itemeffects"),
			"ntrpg:item_effects",
			"Item effects");

	public static Key<MapValue<String, String>> ITEM_SOCKETS = KeyFactory.makeMapKey(
			new TypeToken<Map<String, String>>() {
			},
			new TypeToken<MapValue<String, String>>() {
			},
			DataQuery.of(".", "ntrpg:itemsockets"),
			"ntrpg:item_sockets",
			"Item sockets");
	public static Key<MapValue<String, Integer>> ITEM_PLAYER_ALLOWED_GROUPS = KeyFactory.makeMapKey(
			new TypeToken<Map<String, Integer>>() {
			},
			new TypeToken<MapValue<String, Integer>>() {
			},
			DataQuery.of(".", "ntrpg:itemplayergroupsrestrictions"),
			"ntrpg:item_group_restriction",
			"Item group restriction");

	public static Key<Value<Text>> ITEM_TYPE = KeyFactory.makeSingleKey(TypeToken.of(Text.class),
			new TypeToken<Value<Text>>() {
			},
			DataQuery.of('.', "ntrpg:itemtype"),
			"ntrpg:item_type",
			"Item type"
	);

	public static Key<Value<Integer>> ITEM_LEVEL = KeyFactory.makeSingleKey(TypeToken.of(Integer.class),
			new TypeToken<Value<Integer>>() {
			},
			DataQuery.of('.', "ntrpg:itemlevel"),
			"ntrpg:item_level",
			"Item level"
			);

	public static Key<Value<Pair<Integer, Integer>>> ITEM_LORE_DURABILITY = KeyFactory.makeSingleKey(
			new TypeToken<Pair<Integer, Integer>>() {
			},
			new TypeToken<Value<Pair<Integer, Integer>>>() {
			},
			DataQuery.of('.', "ntrpg:itemloredurability"),
			"ntrpg:item_loredurability",
			"Item loredurability"
	);


	public static Key<Value<Pair<Text, Text>>> ITEM_SECTION_DELIMITER  = KeyFactory.makeSingleKey(
			new TypeToken<Pair<Text, Text>>() {
			},
			new TypeToken<Value<Pair<Text, Text>>>() {
			},
			DataQuery.of('.', "ntrpg:itemsectiondelimiter"),
			"ntrpg:item_section_delimiter",
			"Item section delimiter"
	);
	public static Key<Value<Pair<Double, Double>>> ITEM_DAMAGE = KeyFactory.makeSingleKey(
			new TypeToken<Pair<Double, Double>>() {
			},
			new TypeToken<Value<Pair<Double, Double>>>() {
			},
			DataQuery.of('.', "ntrpg:itemdamage"),
			"ntrpg:item_damage",
			"Item damage"
	);
}
