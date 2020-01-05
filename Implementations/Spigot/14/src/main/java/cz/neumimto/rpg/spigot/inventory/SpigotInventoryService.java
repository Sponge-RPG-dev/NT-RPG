package cz.neumimto.rpg.spigot.inventory;

import cz.neumimto.rpg.api.configuration.ItemString;
import cz.neumimto.rpg.api.configuration.SkillItemCost;
import cz.neumimto.rpg.api.inventory.ManagedSlot;
import cz.neumimto.rpg.api.inventory.RpgInventory;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.persistance.model.EquipedSlot;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillCost;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;
import cz.neumimto.rpg.common.inventory.AbstractInventoryService;
import cz.neumimto.rpg.common.inventory.InventoryHandler;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.persistance.SpigotEquipedSlot;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class SpigotInventoryService extends AbstractInventoryService<ISpigotCharacter> {

    public static final String SKILLBIND = "ntrpg.skillbind";

    @Inject
    private InventoryHandler inventoryHandler;

    @Inject
    private SpigotItemService itemService;

    @Override
    public Set<ActiveSkillPreProcessorWrapper> processItemCost(ISpigotCharacter character, PlayerSkillContext skillInfo) {
        SkillCost invokeCost = skillInfo.getSkillData().getInvokeCost();
        if (invokeCost == null) {
            return Collections.emptySet();
        }
        Player player = character.getPlayer();
        PlayerInventory inventory = player.getInventory();
        Map<Integer, SCost> itemsToTake = new HashMap<>();
        int c = 0;
        outer:
        for (SkillItemCost skillItemCost : invokeCost.getItemCost()) {
            ItemString parsedItemType = skillItemCost.getItemType();
            int requiredAmount = skillItemCost.getAmount();
            Material material = Material.matchMaterial(parsedItemType.itemId);

            for (int i = 0; i < 9; i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null && item.getType() == material) {
                    if (item.getAmount() - requiredAmount < 0) {
                        itemsToTake.put(i, new SCost(item.getAmount(), skillItemCost.consumeItems()));
                        requiredAmount -= item.getAmount();
                    } else {
                        itemsToTake.put(i, new SCost(requiredAmount, skillItemCost.consumeItems()));
                        c++;
                        break outer;
                    }
                }
            }
        }
        if (c == invokeCost.getItemCost().size()) {
            for (Map.Entry<Integer, SCost> e : itemsToTake.entrySet()) {
                SCost result = e.getValue();
                if (result.consume) {
                    ItemStack item = inventory.getItem(e.getKey());
                    item.setAmount(result.amount);
                    inventory.setItem(e.getKey(), item);
                }
            }
        } else {
            return invokeCost.getInsufficientProcessors();
        }
        return Collections.emptySet();
    }

    @Override
    public EquipedSlot createEquipedSlot(String className, int slotId) {
        return new SpigotEquipedSlot(slotId);
    }

    public ItemStack createSkillbind(ISkill iSkill) {
        ItemStack itemStack = new ItemStack(Material.PUMPKIN_SEEDS, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(iSkill.getLocalizableName());
        itemStack.setItemMeta(itemMeta);
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setBoolean(SKILLBIND, true);
        return nbtItem.getItem();
    }

    @Override
    public void initializeCharacterInventory(ISpigotCharacter character) {
        if (inventoryHandler.handleInventoryInitializationPre(character)) {
            fillInventory(character);
            inventoryHandler.handleInventoryInitializationPost(character);
        }
    }

    private void fillInventory(ISpigotCharacter character) {
        Map<Class<?>, RpgInventory> managedInventory = character.getManagedInventory();
        Player player = character.getPlayer();
        for (RpgInventory rInv : managedInventory.values()) {
            for (Map.Entry<Integer, ManagedSlot> rInvE : rInv.getManagedSlots().entrySet()) {
                int index = rInvE.getKey();
                ItemStack itemStack = player.getInventory().getItem(index);
                if (itemStack != null) {
                    Optional<RpgItemStack> rpgItemStack = itemService.getRpgItemStack(itemStack);
                    rpgItemStack.ifPresent(stack -> rInvE.getValue().setContent(stack));
                }
            }
        }
    }

    public static final class SCost {
        final int amount;
        final boolean consume;

        public SCost(int amount, boolean consume) {
            this.amount = amount;
            this.consume = consume;
        }
    }
}
