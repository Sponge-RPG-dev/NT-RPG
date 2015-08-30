package cz.neumimto.players;

import org.spongepowered.api.data.key.Keys;

import static cz.neumimto.players.properties.DefaultProperties.*;

/**
 * Created by NeumimTo on 30.12.2014.
 */
public class Health implements IReservable {
    private final IActiveCharacter activeCharacter;

    public Health(IActiveCharacter activeCharacter) {
        this.activeCharacter = activeCharacter;
    }

    @Override
    public double getMaxValue() {
        return activeCharacter.getPlayer().get(Keys.MAX_HEALTH).get();
    }

    @Override
    public void setMaxValue(double f) {
        activeCharacter.getPlayer().offer(Keys.MAX_HEALTH,f);
    }
    //todo implement reserved amounts
    @Override
    public void setReservedAmnout(float f) {
        activeCharacter.setCharacterProperty(reserved_health, f);
    }

    @Override
    public double getReservedAmount() {
        return activeCharacter.getCharacterProperty(reserved_health);
    }

    @Override
    public double getValue() {
        return activeCharacter.getPlayer().get(Keys.HEALTH).get();
    }

    @Override
    public void setValue(double f) {
        activeCharacter.getPlayer().offer(Keys.HEALTH,f);
    }

    @Override
    public double getRegen() {
        return activeCharacter.getCharacterProperty(health_regen);
    }

    @Override
    public void setRegen(float f) {
        activeCharacter.setCharacterProperty(health_regen, f);
    }
}
