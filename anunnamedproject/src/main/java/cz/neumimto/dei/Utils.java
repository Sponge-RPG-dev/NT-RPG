package cz.neumimto.dei;

import cz.neumimto.dei.entity.database.structure.ItemStackResource;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by NeumimTo on 24.7.2016.
 */
public class Utils {

	public static ItemStack toItemStack(ItemStackResource itemStackResource) {
		try {
			return deSerializeJson(itemStackResource.getData(), ItemStack.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	/*
	https://forums.spongepowered.org/t/itemstack-to-json/13529
	 */
	public static String toJson(ConfigurationNode node) throws IOException {
		StringWriter writer = new StringWriter();
		GsonConfigurationLoader.builder().build().saveInternal(node, writer); //I'm not totally positive on this line, I think this is right....
		return writer.toString();
	}

	public static String serializeToJson(DataContainer container) {
		try {
			return toJson(Sponge.getDataManager().getTranslator(ConfigurationNode.class).get().translate(container));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends DataSerializable> T deSerializeJson(String json, Class<T> type) throws IOException {
		DataContainer translate = Sponge.getDataManager().getTranslator(String.class).get().translate(json);

		//DataView target = ConfigurateTranslator.instance().translateFrom(GsonConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(json))).build().load());
		DataManager manager = Sponge.getGame().getDataManager();
		return manager.deserialize(type, translate).orElse(null);
	}
}
