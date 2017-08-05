package cz.neumimto;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.RepresentedItemData;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by NeumimTo on 1.8.2017.
 */
public class Utils {

    public static TextColor teleportationScrollColor = TextColors.GREEN;

    public static ItemStack createTeleportationScroll(Location<World> location) {
        ItemStack of = ItemStack.of(ItemTypes.PAPER, 1);
        of.offer(Keys.DISPLAY_NAME, Text.builder(SkillLocalization.TELEPORTATION_SCROLL)
                .color(teleportationScrollColor).build());
        List<Text> lore = new ArrayList<>();
        String name = location.getExtent().getName();
        lore.add(Text.builder(name).color(TextColors.DARK_GRAY).style(TextStyles.BOLD).build());
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        lore.add(Text.builder(x + ", " + y + ", " + z).color(TextColors.GRAY).build());
        of.offer(Keys.ITEM_LORE, lore);
        return of;
    }

    public static Location extractLocationFromItem(Item i) {
        if (i.getItemType() == ItemTypes.PAPER) {

            ItemStackSnapshot itemData = i.getItemData().item().get();
            Optional<Text> text = itemData.get(Keys.DISPLAY_NAME);
            if (!text.isPresent()) {
                return null;
            }
            Text text1 = text.get();
            if (text1.getChildren().get(0).getColor() != teleportationScrollColor) {
                return null;
            }

            if (!text1.toPlain().equalsIgnoreCase(SkillLocalization.TELEPORTATION_SCROLL)) {
                return null;
            }


            Optional<List<Text>> texts = itemData.get(Keys.ITEM_LORE);
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
                    Integer.parseInt(xyz[0].trim()),
                    Integer.parseInt(xyz[1].trim()),
                    Integer.parseInt(xyz[2].trim())
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
