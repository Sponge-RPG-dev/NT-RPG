

package cz.neumimto.rpg.sponge.entities.players;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.IReservable;
import org.spongepowered.api.data.key.Keys;

/**
 * Created by NeumimTo on 30.12.2014.
 */
class SpongeCharacterHealth implements IReservable {

    private final SpongeCharacter activeCharacter;

    SpongeCharacterHealth(SpongeCharacter activeCharacter) {
        this.activeCharacter = activeCharacter;
    }

    @Override
    public double getMaxValue() {
        return activeCharacter.getPlayer().get(Keys.MAX_HEALTH).get();
    }

    @Override
    public void setMaxValue(double f) {
        activeCharacter.getPlayer().offer(Keys.MAX_HEALTH, f);
    }

    //todo useservice instead
    //todo implement reserved amounts
    @Override
    public void setReservedAmnout(float f) {
        activeCharacter.setProperty(CommonProperties.reserved_health, f);
    }

    @Override
    public double getReservedAmount() {
        return Rpg.get().getEntityService().getEntityProperty(activeCharacter, CommonProperties.reserved_health);
    }

    @Override
    public double getValue() {
        return activeCharacter.getPlayer().get(Keys.HEALTH).get();
    }

    @Override
    public void setValue(double f) {
        activeCharacter.getPlayer().offer(Keys.HEALTH, f);
    }

    @Override
    public double getRegen() {
        return Rpg.get().getEntityService().getEntityProperty(activeCharacter, CommonProperties.health_regen);
    }

    @Override
    public void setRegen(float f) {
        activeCharacter.setProperty(CommonProperties.health_regen, f);
    }
}
