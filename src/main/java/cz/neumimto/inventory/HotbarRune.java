package cz.neumimto.inventory;

import cz.neumimto.NtRpgPlugin;
import cz.neumimto.inventory.runewords.Rune;
import cz.neumimto.players.IActiveCharacter;

/**
 * Created by NeumimTo on 2.1.2016.
 */
public class HotbarRune extends HotbarObject {

    protected Rune r;

    public HotbarRune() {
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
}
