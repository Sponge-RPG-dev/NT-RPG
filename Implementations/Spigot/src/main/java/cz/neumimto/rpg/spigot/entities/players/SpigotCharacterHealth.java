package cz.neumimto.rpg.spigot.entities.players;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.CommonProperties;
import cz.neumimto.rpg.common.entity.IReservable;

class SpigotCharacterHealth implements IReservable {

    private final ISpigotCharacter activeCharacter;

    SpigotCharacterHealth(ISpigotCharacter activeCharacter) {
        this.activeCharacter = activeCharacter;
    }

    @Override
    public double getMaxValue() {
        return activeCharacter.getEntity().getMaxHealth();
    }

    @Override
    public void setMaxValue(double f) {
        activeCharacter.getEntity().setMaxHealth(f);
    }

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
        return activeCharacter.getEntity().getHealth();
    }

    @Override
    public void setValue(double f) {
        activeCharacter.getEntity().setHealth(f);
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
