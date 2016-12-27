package cz.neumimto.rpg.inventory.data;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;

/**
 * Created by ja on 26.12.2016.
 */
public class NKeys {

	public static Key<Value<String>> ANY_STRING = KeyFactory.makeSingleKey(TypeToken.of(String.class),
			new TypeToken<Value<String>>(){},
			DataQuery.of('.', "n.anystring"),
			"n:any_string",
			"Any String"
	);
}
