package cz.neumimto.rpg.common.damage;

import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.configuration.PluginConfig;
import cz.neumimto.rpg.common.entity.EntityService;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.items.RpgItemType;

import javax.inject.Inject;

public abstract class DamageService<W extends ActiveCharacter, T, E extends IEntity<T>> {

    @Inject
    protected EntityService entityService;

    @Inject
    protected ClassService classService;

    @Inject
    protected PluginConfig pluginConfig;

    protected DamageHandler<W, T> damageHandler;

    public double getCharacterItemDamage(ActiveCharacter character, RpgItemType type) {
        if (type == null) {
            return 1; //todo unarmed
        }
        double base = 0;

        for (Integer i : type.getItemClass().getProperties()) {
            base += entityService.getEntityProperty(character, i);
        }

        if (!type.getItemClass().getPropertiesMults().isEmpty()) {
            double totalMult = 1;
            for (Integer integer : type.getItemClass().getPropertiesMults()) {
                totalMult += entityService.getEntityProperty(character, integer) - 1;
            }
            base *= totalMult;
        }
        return base;
    }


    public boolean canDamage(W caster, T l) {
        return damageHandler.canDamage(caster, l);
    }

    public DamageHandler<W, T> getDamageHandler() {
        return damageHandler;
    }

    public void setDamageHandler(DamageHandler<W, T> damageHandler) {
        this.damageHandler = damageHandler;
    }

    public abstract void damageEntity(E entity, double value);

    public abstract void init();

    public abstract static class DamageHandler<W extends ActiveCharacter, T> {

        public abstract boolean canDamage(W damager, T damaged);

        public abstract double getEntityResistance(IEntity entity, String damageType);

        public abstract double getEntityDamageMult(IEntity entity, String damageType);

    }

}
