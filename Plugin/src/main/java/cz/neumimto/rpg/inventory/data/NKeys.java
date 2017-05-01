package cz.neumimto.rpg.inventory.data;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.text.Text;
import org.yaml.snakeyaml.tokens.ValueToken;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ja on 26.12.2016.
 */
public class NKeys {

	public static final Key<Value<String>> ANY_STRING = KeyFactory.makeSingleKey(TypeToken.of(String.class),
			new TypeToken<Value<String>>(){},
			DataQuery.of('.', "ntrpg:anystring"),
			"ntrpg:any_string",
			"Any String"
	);


	public static final Key<Value<Boolean>> MENU_INVENTORY = KeyFactory.makeSingleKey(TypeToken.of(Boolean.class),
			new TypeToken<Value<Boolean>>(){},
			DataQuery.of('.', "ntrpg:menuinventory"),
			"ntrpg:menu_inventory",
			"Inventory menu"
	);

	public static final Key<Value<Text>> ITEM_RARITY = KeyFactory.makeSingleKey(TypeToken.of(Text.class),
			new TypeToken<Value<Text>>(){},
			DataQuery.of('.', "ntrpg:cusotmitemrarity"),
			"ntrpg:custom_item_rarity",
			"Custom Item Data rarity"
	);

	public static final Key<Value<Integer>> CUSTOM_ITEM_DATA_ITEM_LEVEL = KeyFactory
			.makeSingleKey(TypeToken.of(Integer.class),
			new TypeToken<Value<Integer>>(){},
			DataQuery.of('.', "ntrpg:customitemdatalevel"),
			"ntrpg:custom_idem_data_level",
			"ntrpg custom item level"
	);

	public static final Key<Value<Integer>> CUSTOM_ITEM_DATA_SOCKET_COUNT = KeyFactory
			.makeSingleKey(TypeToken.of(Integer.class),
					new TypeToken<Value<Integer>>(){},
					DataQuery.of('.', "ntrpg:customitemdatasocketcount"),
					"ntrpg:custom_idem_data_socket_count",
					"ntrpg custom item socket count"
			);

	public static final Key<MapValue<String, String>> CUSTOM_ITEM_DATA_ENCHANTEMENTS = KeyFactory.makeMapKey(
			new TypeToken<Map<String, String>>() { },
			new TypeToken<MapValue<String, String>>() { },
			DataQuery.of('.', "ntrpg:customitemdataenchantements"),
			"ntrpg:custom_idem_data_enchantements",
			"ntrpg custom item enchantements"
	);

	public static final Key<ListValue<String>> CUSTOM_ITEM_DATA_RESTRICTIONS = KeyFactory.makeListKey(
			new TypeToken<List<String>>() { },
			new TypeToken<ListValue<String>>() { },
			DataQuery.of('.', "ntrpg:customitemdatarestrictions"),
			"ntrpg:custom_idem_data_restrictions",
			"ntrpg custom item restrictions"
	);



	public static final Key<Value<String>> ITEM_LORE = KeyFactory.makeSingleKey(TypeToken.of(String.class),
			new TypeToken<Value<String>>(){},
			DataQuery.of('.', "n.customitemlore"),
			"ntrpg:custom_item_lore",
			"Custom Item Data Lore"
	);


}
