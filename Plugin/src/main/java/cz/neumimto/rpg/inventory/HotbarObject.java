package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by NeumimTo on 31.12.2015.
 */
public abstract class HotbarObject {

    public static HotbarObject EMPTYHAND_OR_CONSUMABLE = null;

    private int slot;
    protected IHotbarObjectType type;

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public IHotbarObjectType getType() {
        return type;
    }

    public abstract void onRightClick(IActiveCharacter character);

    public abstract void onLeftClick(IActiveCharacter character);

    public void onEquip(ItemStack is, IActiveCharacter character) {
        if (PluginConfig.DEBUG) {
            character.sendMessage("Equiped "+is.getItem().getName());
        }
    }

    public void onUnEquip(IActiveCharacter character) {
        if (PluginConfig.DEBUG) {
            character.sendMessage("Unequiped item");
        }
    }
}
