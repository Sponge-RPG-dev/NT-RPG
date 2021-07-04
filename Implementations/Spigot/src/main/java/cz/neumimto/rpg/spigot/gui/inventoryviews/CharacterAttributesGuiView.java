package cz.neumimto.rpg.spigot.gui.inventoryviews;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.google.auto.service.AutoService;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.gui.elements.GuiCommand;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.spigot.gui.SpigotGuiHelper.itemLoreFactory;

@Singleton
@AutoService(ConfigurableInventoryGui.class)
public class CharacterAttributesGuiView extends ConfigurableInventoryGui {

    private static final Map<UUID, ChestGui> cached = new HashMap<>();

    private static CharacterAttributesGuiView instance;

    @Inject
    private SpigotCharacterService characterService;

    @Inject
    private LocalizationService localizationService;

    public CharacterAttributesGuiView() {
        super("CharacterAttributes.conf");
        instance = this;
    }

    public static ChestGui get(Player player) {
        if (cached.containsKey(player.getUniqueId())) {
            return cached.get(player.getUniqueId());
        } else {
            ChestGui chestGui = instance.loadGui(player);
            cached.put(player.getUniqueId(), chestGui);
            return chestGui;
        }
    }

    public static void clearCache(Player executor) {
        instance.clearCache(executor.getUniqueId());
    }


    @Override
    public void clearCache() {
        super.clearCache();
        cached.clear();
    }

    @Override
    public void clearCache(UUID uuid) {
        cached.remove(uuid);
    }

    @Override
    protected String getTitle(CommandSender commandSender, GuiConfig guiConfig, String param) {
        return localizationService.translate(LocalizationKeys.ATTRIBUTES);
    }

    @Override
    public Map<String, List<GuiCommand>> getPaneData(CommandSender commandSender, String className, GuiConfig guiConfig) {
        Map<String, List<GuiCommand>> map = new HashMap<>();

        ISpigotCharacter character = characterService.getCharacter((Player) commandSender);

        List<AttributeConfig> collect = Rpg.get().getPropertyService().getAttributes()
                .values()
                .stream()
                .sorted(Comparator.comparing(AttributeConfig::getName))
                .collect(Collectors.toList());

        List<GuiCommand> attributes = new ArrayList<>();
        for (AttributeConfig attributeConfig : collect) {
            Integer attributeValue = character.getAttributeValue(attributeConfig);
            GuiCommand item = new GuiCommand(characterAttributeItemStack(character, attributeConfig, attributeValue),
                    CharacterAttributesGuiView::handleEvent);
            attributes.add(item);
        }
        map.put("Attributes", attributes);
        return map;
    }

    private static void handleEvent(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }

        NBTItem nbtItem = new NBTItem(event.getCurrentItem());
        String attrId = nbtItem.getString("ntrpg-attribute");
        if (attrId == null) {
            return;
        }

        ClickType click = event.getClick();
        HumanEntity whoClicked = event.getWhoClicked();
        int slot = event.getSlot();
        ISpigotCharacter character = (ISpigotCharacter) Rpg.get().getCharacterService().getCharacter(whoClicked.getUniqueId());
        int attributePoints = character.getAttributePoints();
        Map<String, Integer> attributesTransaction = character.getAttributesTransaction();
        AttributeConfig aConfig = Rpg.get().getPropertyService().getAttributeById(attrId).get();
        Integer attributeValue = character.getAttributeValue(aConfig);

        Map<String, Integer> transientAttributes = character.getTransientAttributes();
        int transientVal = transientAttributes.get(aConfig.getId());

        ItemStack item = event.getClickedInventory().getItem(slot);
        ItemMeta itemMeta = item.getItemMeta();
        Integer baseValue = character.getCharacterBase().getAttributes().get(aConfig.getId());
        if (click.isLeftClick() && attributePoints > 0 && (baseValue == null || baseValue < aConfig.getMaxValue())) {
            List<String> lore = itemLoreFactory.toLore(aConfig, attributeValue + 1, transientVal);
            itemMeta.setLore(lore);
            event.getClickedInventory().setItem(slot, item);
            attributesTransaction.compute(aConfig.getId(), (s, integer) -> integer == null ? 1 : integer + 1);
        } else if (click.isRightClick() && (baseValue == null || baseValue > 1)
                && (attributesTransaction.get(aConfig.getId()) == null || attributesTransaction.get(aConfig.getId()) > 0)) {
            List<String> lore = itemLoreFactory.toLore(aConfig, attributeValue - 1, transientVal);
            itemMeta.setLore(lore);
            attributesTransaction.compute(aConfig.getId(), (s, integer) -> integer == null ? 0 : integer - 1);
        }
        item.setItemMeta(itemMeta);
        event.getClickedInventory().setItem(slot, item);
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
    }

    public static ItemStack characterAttributeItemStack(ISpigotCharacter character,
                                                        AttributeConfig config,
                                                        Integer attributeValue) {
        Map<String, Integer> transientAttributes = character.getTransientAttributes();

        String attId = config.getId();
        int transientVal = transientAttributes.get(attId);

        ItemStack itemStack = i(Material.matchMaterial(config.getItemType()), config.getModel());

        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemLoreFactory.toLore(config, attributeValue, transientVal);

        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.values());
        itemMeta.setDisplayName(" ");
        itemStack.setItemMeta(itemMeta);

        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("ntrpg-attribute", attId);
        return nbtItem.getItem();
    }

    public void refreshClicked(Player player, Inventory inventory, ItemStack itemStack) {

    }

    @Override
    protected void handleTag(String tag, CommandSender commandSender, ItemStack item) {
        if (tag.equalsIgnoreCase("RemainingAP")) {
            ItemMeta itemMeta = item.getItemMeta();
            List<String> lore = itemMeta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            ISpigotCharacter character = (ISpigotCharacter) Rpg.get().getCharacterService().getCharacter(((Player) commandSender).getUniqueId());
            Map<String, Integer> attributesTransaction = character.getAttributesTransaction();
            int i = 0;
            for (Integer value : attributesTransaction.values()) {
                i += value;
            }
            int points = character.getAttributePoints() - i;
            String line = Rpg.get().getLocalizationService().translate(LocalizationKeys.ATTRIBUTE_POINTS, Arg.arg("points", points));

            lore.add(line);
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        }
    }
}

