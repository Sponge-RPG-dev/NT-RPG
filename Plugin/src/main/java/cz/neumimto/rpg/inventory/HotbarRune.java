package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.EffectSourceType;
import cz.neumimto.rpg.effects.IEffectSource;
import cz.neumimto.rpg.inventory.runewords.Rune;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by NeumimTo on 2.1.2016.
 */
public class HotbarRune extends HotbarObject {

    protected Rune r;

    public HotbarRune(ItemStack itemStack) {
        super(itemStack);
        type = HotbarObjectTypes.RUNE;
    }

    @Override
    public void onRightClick(IActiveCharacter character) {
        NtRpgPlugin.GlobalScope.inventorySerivce.startSocketing(character);
    }

    @Override
    public void onLeftClick(IActiveCharacter character) {
        onRightClick(character);
    }


    public Rune getRune() {
        return r;
    }

    public void setRune(Rune r) {
        this.r = r;
    }

    @Override
    public IEffectSource getType() {
        return EffectSourceType.CHARM;
    }
}
