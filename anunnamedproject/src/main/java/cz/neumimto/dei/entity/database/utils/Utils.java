package cz.neumimto.dei.entity.database.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

/**
 * Created by ja on 9.7.16.
 */
public class Utils {

    public static Optional<String> serializeItemStack(ItemStack item) {
        try {
            StringWriter sink = new StringWriter();
            GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
            ConfigurationNode node = loader.createEmptyNode();
            node.setValue(TypeToken.of(ItemStack.class), item);
            loader.save(node);
            return Optional.of(sink.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<ItemStack> deserializeItemStack(String json) {
        try {
            StringReader source = new StringReader(json);
            GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();
            ConfigurationNode node = loader.load();
            return Optional.of(node.getValue(TypeToken.of(ItemStack.class)));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
