package cz.neumimto.rpg;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by NeumimTo on 16.11.2017.
 */
public class TextHelper {

    public static Text parse(String text) {
        return TextSerializers.FORMATTING_CODE.deserialize(text);
    }

    public static Text makeText(String nameById, TextColor c) {
        return Text.builder(nameById).color(c).build();
    }

    public static Text parse(String text, Arg params) {
        for (Map.Entry<String, Object> par : params.getParams().entrySet()) {
            text = text.replace(par.getKey(),par.getValue().toString());
        }
        return TextSerializers.FORMATTING_CODE.deserialize(text);
    }

    public static List<Text> splitStringByDelimiter(String text) {
        List<Text> lore = new ArrayList<>();
        for (String s : text.split(":n")) {
            lore.add(parse(s));
        }
        return lore;
    }


}
