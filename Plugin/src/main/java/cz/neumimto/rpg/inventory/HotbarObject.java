package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.effects.IEffectSourceProvider;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by NeumimTo on 31.12.2015.
 */
public abstract class HotbarObject extends CustomItem implements IEffectSourceProvider {

    public static HotbarObject EMPTYHAND_OR_CONSUMABLE = null;
    protected IHotbarObjectType type;


    public IHotbarObjectType getHotbarObjectType() {
        return type;
    }

    public abstract void onRightClick(IActiveCharacter character);

    public abstract void onLeftClick(IActiveCharacter character);

    public void onEquip(ItemStack is, IActiveCharacter character) {
        if (PluginConfig.DEBUG) {
            character.sendMessage("Equiped slot " + getSlot());
        }
    }

    public void onUnEquip(IActiveCharacter character) {
        if (PluginConfig.DEBUG) {
            character.sendMessage("Unequiped slot " + getSlot());
        }
    }
}
