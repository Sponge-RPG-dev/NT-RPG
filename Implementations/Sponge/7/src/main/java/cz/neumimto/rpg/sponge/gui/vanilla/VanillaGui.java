package cz.neumimto.rpg.sponge.gui.vanilla;

import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.inventory.data.InventoryActionItemMenuData;
import cz.neumimto.rpg.sponge.inventory.data.MenuInventoryData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class VanillaGui {

    protected static SpongeRpgPlugin plugin = SpongeRpgPlugin.getInstance();
    protected UUID owner;
    protected Inventory inventory;

    protected VanillaGui(UUID owner) {
        VanillaGuiListener.clearActions(owner);
        this.inventory = emptyInventory().build(plugin);
        this.owner = owner;
    }

    public Inventory getInventory() {
        return inventory;
    }

    protected Inventory.Builder emptyInventory() {
        return Inventory.builder()
                .of(InventoryArchetypes.DOUBLE_CHEST);
    }

    protected Inventory.Builder characterEmptyInventory(ISpongeCharacter character) {
        return Inventory.builder()
                .of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(Text.of(character.getCharacterBase().getName(), TextStyles.BOLD)));
    }

    protected void setItem(SlotPos pos, ItemStack item) {
        inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(pos)).set(item.copy());
    }

    protected ItemStack getItem(SlotPos pos) {
        Optional<ItemStack> stack = inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(pos)).peek();
        return stack.orElseGet(ItemStack::empty);
    }

    protected void makeBorder(DyeColor dyeColor) {
        if (inventory.getArchetype() == InventoryArchetypes.DOUBLE_CHEST) {
            ItemStack item = itemPanel(dyeColor);
            for (int j = 0; j < 9; j++) {
                setItem(SlotPos.of(j, 0), item);
                setItem(SlotPos.of(j, 5), item);
            }

            for (int j = 1; j < 5; j++) {
                setItem(SlotPos.of(0, j), item);
                setItem(SlotPos.of(8, j), item);
            }
        }
    }

    protected ItemType itemType(String itemType) {
        if (itemType == null) {
            return ItemTypes.STONE;
        }
        return Sponge.getRegistry().getType(ItemType.class, itemType).orElse(ItemTypes.STONE);
    }

    protected ItemStack itemStack(ItemType type) {
        ItemStack is = ItemStack.of(type, 1);
        is.offer(new MenuInventoryData(true));
        is.offer(Keys.HIDE_ATTRIBUTES, true);
        is.offer(Keys.HIDE_MISCELLANEOUS, true);
        return is;
    }

    protected ItemStack itemStack(ItemType type, Text name) {
        ItemStack is = itemStack(type);
        is.offer(Keys.DISPLAY_NAME, name);
        return is;
    }

    protected ItemStack itemStack(ItemType type, Text name, List<Text> lore) {
        ItemStack is = itemStack(type, name);
        if (lore.size() > 0) is.offer(Keys.ITEM_LORE, lore);
        return is;
    }

    protected ItemStack itemPanel(DyeColor dyeColor) {
        ItemStack of = itemStack(ItemTypes.STAINED_GLASS_PANE, Text.EMPTY);
        of.offer(Keys.DYE_COLOR, dyeColor);
        return of;
    }

    protected ItemStack itemAction(ItemType type, Text displayName, Consumer<ISpongeCharacter> action) {
        ItemStack of = itemStack(type, displayName);
        int actionId = VanillaGuiListener.putAction(owner, action);
        of.offer(new InventoryActionItemMenuData(actionId));
        return of;
    }

    protected ItemStack itemCommand(ItemType type, Text displayName, String command) {
        return itemAction(type, displayName, (c) -> Sponge.getCommandManager().process(c.getPlayer(), command));
    }
}
