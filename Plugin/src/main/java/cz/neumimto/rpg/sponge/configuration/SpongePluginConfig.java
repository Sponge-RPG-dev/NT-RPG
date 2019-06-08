package cz.neumimto.rpg.sponge.configuration;

import cz.neumimto.config.blackjack.and.hookers.annotations.CustomAdapter;
import cz.neumimto.rpg.common.configuration.ClassTypeDefinition;
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
        put("Race", new ClassTypeDefinition(TextColors.GREEN.getId(), TextColors.DARK_GREEN.getId(), DyeColors.GREEN.getId(), false, 1));
        put("Primary", new ClassTypeDefinition(TextColors.YELLOW.getId(), TextColors.GOLD.getId(), DyeColors.YELLOW.getId(), true, 2));
        put("Profession", new ClassTypeDefinition(TextColors.GRAY.getId(), TextColors.BLACK.getId(), DyeColors.GRAY.getId(), true, 3));
    }};
}
