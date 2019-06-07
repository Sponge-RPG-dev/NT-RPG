package cz.neumimto.rpg.sponge.configuration;

import cz.neumimto.config.blackjack.and.hookers.annotations.CustomAdapter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.text.format.TextColors;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigSerializable
public class SpongePluginConfig {

    @Setting(comment = "Class types")
    @CustomAdapter(ClassTypesDeserializer.class)
    public Map<String, ClassTypeDefinition> CLASS_TYPES = new LinkedHashMap<String, ClassTypeDefinition>() {{
        put("Race", new ClassTypeDefinition(TextColors.GREEN, TextColors.DARK_GREEN, DyeColors.GREEN, false, 1));
        put("Primary", new ClassTypeDefinition(TextColors.YELLOW, TextColors.GOLD, DyeColors.YELLOW, true, 2));
        put("Profession", new ClassTypeDefinition(TextColors.GRAY, TextColors.BLACK, DyeColors.GRAY, true, 3));
    }};
}
