package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.effects.EffectSourceType;
import cz.neumimto.rpg.effects.IEffectSource;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.inventory.data.CustomItemData;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 16.1.2016.
 */
public class Charm extends HotbarObject {


    private Map<ItemRestriction,Object> restrictionSet = new HashMap<>();


    public Charm(ItemStack itemStack) {
        super(itemStack);
        type = HotbarObjectTypes.CHARM;
    }


    public Map<ItemRestriction,Object> getRestrictions() {
        return restrictionSet;
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
    public void onEquip(IActiveCharacter character) {
        super.onEquip(character);
        if (effects != null) {
            NtRpgPlugin.GlobalScope.effectService.applyGlobalEffectsAsEnchantments(effects, character, this);
        }
        NtRpgPlugin.GlobalScope.damageService.recalculateCharacterWeaponDamage(character);
    }

    @Override
    public void onUnEquip(IActiveCharacter character) {
        if (effects != null) {
            NtRpgPlugin.GlobalScope.effectService.removeGlobalEffectsAsEnchantments(effects, character, this);
        }
        NtRpgPlugin.GlobalScope.damageService.recalculateCharacterWeaponDamage(character);
    }

    public Map<IGlobalEffect, String> getEffects() {
        return effects;
    }

    public void setEffects(Map<IGlobalEffect, String> effects) {
        this.effects = effects;
    }

    @Override
    public IEffectSource getType() {
        return EffectSourceType.CHARM;
    }
}
