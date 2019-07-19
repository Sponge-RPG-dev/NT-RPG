package cz.neumimto.rpg.sponge.gui;

import com.google.common.collect.Lists;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.common.items.RpgItemTypeImpl;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.items.SpongeRpgItemType;
import cz.neumimto.rpg.sponge.utils.TextHelper;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;
import java.util.stream.Collectors;

public class ArmorAndWeaponMenuHelper {

    private static Map<UUID, List<Inventory>> armor = new HashMap<>();
    private static Map<UUID, List<Inventory>> weapon = new HashMap<>();


    public static Inventory listArmor(IActiveCharacter character, int idx) {
        List<Inventory> inventories = armor.get(character.getUUID());
        if (inventories == null) {
            inventories = buildArmorViewFor(character);
            armor.put(character.getUUID(), inventories);
        }
        return inventories.get(idx);
    }

    public static Inventory listWeapons(IActiveCharacter character, int idx) {
        List<Inventory> inventories = weapon.get(character.getUUID());
        if (inventories == null) {
            inventories = buildWeaponViewFor(character);
            weapon.put(character.getUUID(), inventories);
        }
        return inventories.get(idx);
    }

    private static List<Inventory> buildArmorViewFor(IActiveCharacter character) {
        List<Inventory> i = new ArrayList<>();

        Set<SpongeRpgItemType> allowedArmor = character.getAllowedArmor();
        List<SpongeRpgItemType> list = allowedArmor.stream().sorted(
                Comparator.comparingDouble(RpgItemTypeImpl::getArmor)
                        .thenComparing(RpgItemTypeImpl::getId))
                .collect(Collectors.toList());
        final int max = list.size();
        int q = 0;
        Inventory inventory;
        List<List<SpongeRpgItemType>> partition = Lists.partition(list, 21);

        for (List<SpongeRpgItemType> inner : partition) {
            inventory = prepareArmorInventory(q, max, "Armor", "armor");
            fillInventoryWithItems(inventory, inner);
            i.add(inventory);
            q++;
        }

        return i;
    }


    private static List<Inventory> buildWeaponViewFor(IActiveCharacter character) {
        List<Inventory> i = new ArrayList<>();

        Map<SpongeRpgItemType, Double> allowedWeapons = character.getAllowedWeapons();
        List<Map.Entry<SpongeRpgItemType, Double>> list = new ArrayList<>(allowedWeapons.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new)).entrySet());
        final int max = list.size();
        int q = 0;
        Inventory inventory;
        List<List<Map.Entry<SpongeRpgItemType, Double>>> partition = Lists.partition(list, 21);

        for (List<Map.Entry<SpongeRpgItemType, Double>> inner : partition) {
            inventory = prepareArmorInventory(q, max, "Weapons", "weapon");
            fillInventoryWithWeaponItems(inventory, inner);
            i.add(inventory);
            q++;
        }

        return i;
    }

    private static void fillInventoryWithWeaponItems(Inventory inventory, List<Map.Entry<SpongeRpgItemType, Double>> inner) {
        int row = 2;
        int column = 1;
        String iDmgLabel = Rpg.get().getLocalizationService().translate(LocalizationKeys.ITEM_DAMAGE);
        for (Map.Entry<SpongeRpgItemType, Double> entry : inner) {
            SpongeRpgItemType spongeRpgItemType = entry.getKey();
            ItemStack itemStack = GuiHelper.itemStack(spongeRpgItemType.getItemType());
            TextColor colorByDamage = NtRpgPlugin.GlobalScope.damageService.getColorByDamage(entry.getValue());
            Text t = Text.builder(iDmgLabel + ": " + entry.getValue()).color(colorByDamage).style(TextStyles.BOLD).build();
            itemStack.offer(Keys.ITEM_LORE, Collections.singletonList(t));
            inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(column,row))).offer(itemStack);
            if ((column) % 7 == 0) {
                column = 1;
                row++;
            } else {
                column++;
            }
        }
    }

    private static void fillInventoryWithItems(Inventory inventory, List<SpongeRpgItemType> inner) {
        int row = 2;
        int column = 1;
        for (SpongeRpgItemType spongeRpgItemType : inner) {
            ItemStack itemStack = GuiHelper.itemStack(spongeRpgItemType.getItemType());
            inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(column,row))).offer(itemStack);
            if ((column) % 7 == 0) {
                column = 1;
                row++;
            } else {
                column++;
            }
        }
    }

    private static Inventory prepareArmorInventory(int idx, int max, String title, String command) {
        Inventory i = Inventory.builder()
                .of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(Text.of(title)))
                .build(NtRpgPlugin.GlobalScope.plugin);
        GuiHelper.makeBorder(i, DyeColors.ORANGE);
        String translate = NtRpgPlugin.GlobalScope.localizationService.translate(LocalizationKeys.BACK);

        ItemStack back = GuiHelper.back("char", TextHelper.parse(translate));
        i.offer(back);

        if (idx * 21 < max) {
            translate = NtRpgPlugin.GlobalScope.localizationService.translate(LocalizationKeys.NEXT);
            ItemStack c = GuiHelper.command("char " + command + " " + (idx + 1), TextHelper.parse(translate), ItemTypes.GLOWSTONE_DUST);
            i.offer(c);
        }
        if (idx > 0) {
            translate = NtRpgPlugin.GlobalScope.localizationService.translate(LocalizationKeys.BACK);
            ItemStack c = GuiHelper.command("char " + command + " " + (idx - 1), TextHelper.parse(translate), ItemTypes.REDSTONE);
            i.offer(c);
        }
        return i;
    }

    public static void reset(IActiveCharacter character) {
        armor.remove(character.getUUID());
        weapon.remove(character.getUUID());
    }

    public static void resetAll() {
        armor.clear();
        weapon.clear();
    }
}
