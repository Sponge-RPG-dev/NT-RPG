package cz.neumimto.inventory;

import cz.neumimto.NtRpgPlugin;
import cz.neumimto.configuration.Localization;
import cz.neumimto.effects.IGlobalEffect;
import cz.neumimto.gui.Gui;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.utils.ItemStackUtils;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Map;

/**
 * Created by NeumimTo on 16.1.2016.
 */
public class Charm extends HotbarObject {

    private Map<IGlobalEffect,Integer> effects;

    public Charm() {
        type = HotbarObjectTypes.CHARM;
    }

    @Override
    public void onRightClick(IActiveCharacter character) {
        Gui.sendMessage(character, Localization.CHARM_INFO);
    }

    @Override
    public void onLeftClick(IActiveCharacter character) {
        onRightClick(character);
    }

    @Override
    public void onEquip(ItemStack is, IActiveCharacter character) {
        if (effects == null) {
            effects = ItemStackUtils.getItemEffects(is);
        }
        NtRpgPlugin.GlobalScope.effectService.applyGlobalEffectsAsEnchantments(effects,character);
    }

    @Override
    public void onUnEquip(IActiveCharacter character) {
        if (effects != null)
            NtRpgPlugin.GlobalScope.effectService.removeGlobalEffectsAsEnchantments(effects,character);
    }

    public Map<IGlobalEffect, Integer> getEffects() {
        return effects;
    }

    public void setEffects(Map<IGlobalEffect, Integer> effects) {
        this.effects = effects;
    }
}
