package cz.neumimto.rpg.spigot.inventory;

import cz.neumimto.rpg.api.persistance.model.EquipedSlot;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;
import cz.neumimto.rpg.common.inventory.AbstractInventoryService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.persistance.SpigotEquipedSlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import javax.inject.Singleton;
import java.util.Set;

@Singleton
public class SpigotInventoryService extends AbstractInventoryService<ISpigotCharacter> {

    @Override
    public void load() {

    }

    @Override
    public Set<ActiveSkillPreProcessorWrapper> processItemCost(ISpigotCharacter character, PlayerSkillContext info) {
        return null;
    }

    @Override
    public void initializeCharacterInventory(ISpigotCharacter character) {
        Player entity = character.getEntity();
        PlayerInventory inventory = entity.getInventory();

    }

    @Override
    public EquipedSlot createEquipedSlot(String className, int slotId) {
        return new SpigotEquipedSlot(slotId);
    }

    @Override
    public String getItemIconForSkill(ISkill iSkill) {
        return null;
    }
}
