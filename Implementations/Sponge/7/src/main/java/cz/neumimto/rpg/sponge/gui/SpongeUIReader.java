package cz.neumimto.rpg.sponge.gui;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.common.gui.GuiParser;
import cz.neumimto.rpg.common.gui.InventorySlotProcessor;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.items.SpongeRpgItemType;
import cz.neumimto.rpg.sponge.utils.TextHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.AcceptsItems;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;
import java.util.function.Supplier;

public class SpongeUIReader extends GuiParser<ItemStack, Inventory> {

    public Map<String, Object> initInventories() {
        return super.initInventories(SpongeUIReader.class.getClassLoader(), "guis.conf");
    }

    @Override
    protected InventorySlotProcessor<ItemStack, Inventory> getInventorySlotProcessor() {

        return (item, inventory, slotId) -> {
            inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(slotId)))
                    .offer(item);
        };
    }

    @Override
    protected ItemStack classTypeButton(Map.Entry<String, ClassTypeDefinition> entry) {
        TextColor type = Sponge.getRegistry().getType(TextColor.class, entry.getValue().getPrimaryColor()).get();

        return GuiHelper.command("ninfo classes " + entry.getKey(),
                Text.of(type, entry.getKey()),
                ItemTypes.CRAFTING_TABLE);
    }

    @Override
    protected ItemStack toItemStack(ClassDefinition a) {
        return GuiHelper.toItemStack(a);
    }

    @Override
    protected ItemStack itemStringToItemStack(String[] split, Supplier<String> command) {
        String s = split[2];
        if (s.equals("minecraft:oak_sapling")) {
            s = "minecraft:sapling";
        }
        if (s.equals("minecraft:gray_stained_glass_pane")) {
            s = "minecraft:stained_glass_pane";
        }
        if (s.equals("minecraft:red_stained_glass_pane")) {
            s = "minecraft:stained_glass_pane";
        }
        if (s.equals("minecraft:white_stained_glass_pane")) {
            s = "minecraft:stained_glass_pane";
        }
        Optional<ItemType> type = Sponge.getRegistry().getType(ItemType.class, s);
        ItemType itemType = type.get();
        ItemStack itemStack = ItemStack.of(itemType);
        itemStack.offer(Keys.HIDE_ATTRIBUTES, true);
        itemStack.offer(Keys.HIDE_ENCHANTMENTS, true);

        int i1 = Integer.parseInt(split[3]);
        if (i1 > 0) {
            //todo 1.15
        }
        itemStack.offer(Keys.DISPLAY_NAME, TextHelper.parse(Rpg.get().getLocalizationService().translate(split[1])));

        String cmd = command.get();
        if (cmd == null || "".equals(cmd) || "---".equalsIgnoreCase(cmd)) {
            return GuiHelper.unclickableInterface(itemStack);
        } else {
            return GuiHelper.command(cmd, itemStack);
        }
    }

    @Override
    protected ItemStack toItemStack(ClassItem weapon) {
        SpongeRpgItemType type = (SpongeRpgItemType) weapon.getType();
        Optional<ItemType> itemType = Sponge.getRegistry().getType(ItemType.class, type.getId());

        ItemStack itemStack = ItemStack.of(itemType.get());
        double damage = weapon.getDamage();

        if (damage <= 0) {
            damage = type.getDamage();
        }

        TextColor colorByDamage = TextColors.RED;

        if (type.getModelId() != null) {
            //todo 1.15
        }
        LocalizationService localizationService = Rpg.get().getLocalizationService();
        String damageStr = localizationService.translate(LocalizationKeys.ITEM_DAMAGE);
        List<Text> list = new ArrayList<>();

        list.add(Text.of(TextColors.GRAY, damageStr + ": ", colorByDamage, damage));
        itemStack.offer(Keys.ITEM_LORE, list);
        return itemStack;
    }

    @Override
    protected Inventory createInventory(String preferedColor, String header) {
        TextColor c = TextColors.WHITE;
        if (preferedColor != null) {
            c = Sponge.getRegistry().getType(TextColor.class, preferedColor.toLowerCase()).get();
        }
        String translate = Rpg.get().getLocalizationService().translate(header);


        Inventory i = Inventory.builder()
                .of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(Text.of(c, translate)))
                .property(AcceptsItems.of(Collections.EMPTY_LIST))
                .build(SpongeRpgPlugin.getInstance());
        return i;
    }
}
