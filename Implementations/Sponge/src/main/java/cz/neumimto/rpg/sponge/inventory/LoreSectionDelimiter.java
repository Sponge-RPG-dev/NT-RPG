package cz.neumimto.rpg.sponge.inventory;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by NeumimTo on 14.1.2018.
 */
public class LoreSectionDelimiter {

    public static Text defaultFirstPart;
    public static Text defaultSecondPart;

    static {
        defaultFirstPart = Text.builder("=======[ ").color(TextColors.WHITE).build();
        defaultSecondPart = Text.builder(" ]=======").color(TextColors.WHITE).build();
    }

    public Text firstPart;
    public Text secondPart;

    public LoreSectionDelimiter(Text firstPart, Text secondPart) {
        this.firstPart = firstPart;
        this.secondPart = secondPart;
    }

    public LoreSectionDelimiter() {
    }
}
