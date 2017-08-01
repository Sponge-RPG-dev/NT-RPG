package cz.neumimto;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.RepresentedItemData;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

/**
 * Created by NeumimTo on 1.8.2017.
 */
public class Utils {

    public static TextColor teleportationScrollColor = TextColors.GREEN;

    public static Location extractLocationFromItem(Item i) {
        if (i.getType() == ItemTypes.PAPER) {

            RepresentedItemData itemData = i.getItemData();
            Optional<Text> text = itemData.get(Keys.DISPLAY_NAME);
            if (!text.isPresent()) {
                return null;
            }
            Text text1 = text.get();
            if (text1.getColor() != teleportationScrollColor) {
                return null;
            }

            if (!text1.toPlain().equalsIgnoreCase(SkillLocalization.TELEPORTATION_SCROLL)) {
                return null;
            }


            Optional<List<Text>> texts = i.getItemData().get(Keys.ITEM_LORE);
            if (!texts.isPresent()) {
                return null;
            }
            List<Text> texts1 = texts.get();
            String worldName = texts1.get(0).toPlain();
            Optional<World> world = Sponge.getServer().getWorld(worldName);
            if (!world.isPresent()) {
                return null;
            }

            String[] xyz = texts1.get(1).toPlain().split(",");
            return new Location<>(
                    world.get(),
                    Integer.parseInt(xyz[0]),
                    Integer.parseInt(xyz[1]),
                    Integer.parseInt(xyz[2])
            );
        }
        return null;
    }

    public static Location<World> locationFromString(String location) {
        String[] split = location.split(",");
        return new Location<>(
            Sponge.getServer().getWorld(split[0].trim()).get(),
            Integer.parseInt(split[1]),
            Integer.parseInt(split[2]),
            Integer.parseInt(split[3])
        );
    }
}
