package cz.neumimto.rpg.spigot.inventory;

import cz.neumimto.rpg.api.inventory.ManagedSlot;
import cz.neumimto.rpg.api.inventory.RpgInventory;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.persistance.model.EquipedSlot;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;
import cz.neumimto.rpg.common.inventory.AbstractInventoryService;
import cz.neumimto.rpg.common.inventory.InventoryHandler;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.persistance.SpigotEquipedSlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Singleton
public class SpigotInventoryService extends AbstractInventoryService<ISpigotCharacter> {

    @Inject
    private InventoryHandler inventoryHandler;

    @Inject
    private SpigotItemService itemService;

    @Override
    public Set<ActiveSkillPreProcessorWrapper> processItemCost(ISpigotCharacter character, PlayerSkillContext info) {
        return null;
    }

    @Override
    public EquipedSlot createEquipedSlot(String className, int slotId) {
        return new SpigotEquipedSlot(slotId);
    }

    public ItemStack createSkillbind(ISkill skill) {
        return null;
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


}
