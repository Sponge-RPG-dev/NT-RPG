package cz.neumimto.players;

import static cz.neumimto.players.properties.DefaultProperties.*;

/**
 * Created by NeumimTo on 30.12.2014.
 */
public class Mana implements IReservable {
    private final IActiveCharacter activeCharacter;

    public Mana(IActiveCharacter activeCharacter) {
        this.activeCharacter = activeCharacter;
    }

    @Override
    public double getMaxValue() {
        return activeCharacter.getCharacterProperty(max_mana);
    }

    @Override
    public void setMaxValue(double f) {
        activeCharacter.setCharacterProperty(max_mana, (float) f);
    }

    @Override
    public void setReservedAmnout(float f) {
        activeCharacter.setCharacterProperty(reserved_mana, f);
    }

    @Override
    public double getReservedAmount() {
        return activeCharacter.getCharacterProperty(reserved_mana);
    }

    @Override
    public double getValue() {
        return activeCharacter.getCharacterProperty(mana);
    }

    @Override
    public void setValue(double f) {
        if (activeCharacter.getMana().getMaxValue() < f)
            f = activeCharacter.getMana().getMaxValue();
        activeCharacter.getMana().setValue(f);
    }

    @Override
    public double getRegen() {
        return activeCharacter.getCharacterProperty(mana_regen);
    }

    @Override
    public void setRegen(float f) {
        activeCharacter.setCharacterProperty(mana_regen, f);
    }
}
