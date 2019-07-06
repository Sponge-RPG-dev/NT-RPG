package cz.neumimto.rpg.sponge.gui;

import com.google.common.collect.Lists;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.common.items.RpgItemTypeImpl;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.items.SpongeRpgItemType;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;

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

    private static List<Inventory> buildArmorViewFor(IActiveCharacter character) {
        List<Inventory> i = new ArrayList<>();

        Set<SpongeRpgItemType> allowedArmor = character.getAllowedArmor();
        List<SpongeRpgItemType> list = allowedArmor.stream().sorted(Comparator.comparingDouble(RpgItemTypeImpl::getArmor)).collect(Collectors.toList());
        final int max = list.size();
        int q = 0;
        Inventory inventory;
        List<List<SpongeRpgItemType>> partition = Lists.partition(list, 21);

        for (List<SpongeRpgItemType> inner : partition) {
            inventory = prepareArmorInventory(q, max);
            fillInventoryWithItems(inventory, inner);
        }

        return i;
    }

    private static void fillInventoryWithItems(Inventory inventory, List<SpongeRpgItemType> inner) {
        int row = 3;
        int column = 1;
        for (SpongeRpgItemType spongeRpgItemType : inner) {
            if (column % 7 == 0) {
                column = 1;
                row++;
            }
            ItemStack itemStack = GuiHelper.itemStack(spongeRpgItemType.getItemType());

            inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(column,row))).offer(itemStack);
            column++;
        }
    }

    private static Inventory prepareArmorInventory(int idx, int max) {
        Inventory i = Inventory.builder()
                .of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(Text.of("Armor")))
                .build(NtRpgPlugin.GlobalScope.plugin);
        GuiHelper.makeBorder(i, DyeColors.ORANGE);
        String translate = NtRpgPlugin.GlobalScope.localizationService.translate(LocalizationKeys.BACK);

        ItemStack back = GuiHelper.back("char", TextHelper.parse(translate));
        i.offer(back);

        if (idx + 1 < max) {
            translate = NtRpgPlugin.GlobalScope.localizationService.translate(LocalizationKeys.NEXT);
            GuiHelper.command("char armor " + idx + 1, TextHelper.parse(translate), ItemTypes.GLOWSTONE_DUST);
            i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(1,7)));
        }
        if (idx - 1 > 0) {
            translate = NtRpgPlugin.GlobalScope.localizationService.translate(LocalizationKeys.BACK);
            GuiHelper.command("char armor " + (idx - 1), TextHelper.parse(translate), ItemTypes.REDSTONE);
            i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(1,6)));
        }
        return i;
    }

    public static void reset(IActiveCharacter character) {
        armor.remove(character.getUUID());
        weapon.remove(character.getUUID());
    }
}
