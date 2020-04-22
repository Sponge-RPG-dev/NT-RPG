package cz.neumimto.rpg.common.damage;

import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;

import javax.inject.Inject;

public abstract class AbstractDamageService<W extends IActiveCharacter, T> implements DamageService<W, T> {

    @Inject
    protected EntityService entityService;

    @Inject
    protected ClassService classService;

    @Inject
    protected PluginConfig pluginConfig;

    protected DamageHandler<W, T> damageHandler;

    @Override
    public double getCharacterItemDamage(IActiveCharacter character, RpgItemType type) {
        if (type == null) {
            return 1; //todo unarmed
        }
        double base = character.getBaseWeaponDamage(type);

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

    @Override
    public void recalculateCharacterWeaponDamage(IActiveCharacter character) {
        if (character.isStub()) {
            return;
        }
        RpgItemStack mainHand = character.getMainHand();
        recalculateCharacterWeaponDamage(character, mainHand);
    }

    @Override
    public void recalculateCharacterWeaponDamage(IActiveCharacter character, RpgItemStack mainHand) {
        if (mainHand == null) {
            character.setWeaponDamage(1); //todo unarmed
        } else {
            recalculateCharacterWeaponDamage(character, mainHand.getItemType());
            Double damage = mainHand.getItemData().get(ItemService.DAMAGE_KEY);
            if (damage != null) {
                double damageF = pluginConfig.ITEM_DAMAGE_PROCESSOR.get(character.getWeaponDamage(), damage);
                character.setWeaponDamage(damageF);
            }
        }
    }

    @Override
    public void recalculateCharacterWeaponDamage(IActiveCharacter character, RpgItemType type) {
        double damage = getCharacterItemDamage(character, type);
        // damage += character.getMainHand().getDamage() + character.getOffHand().getDamage();
        character.setWeaponDamage(damage);
    }

    @Override
    public boolean canDamage(W caster, T l) {
        return damageHandler.canDamage(caster, l);
    }

    public DamageHandler<W, T> getDamageHandler() {
        return damageHandler;
    }

    public void setDamageHandler(DamageHandler<W, T> damageHandler) {
        this.damageHandler = damageHandler;
    }

    public abstract static class DamageHandler<W extends IActiveCharacter, T> {

        public abstract boolean canDamage(W damager, T damaged);

        public abstract double getEntityResistance(IEntity entity, String damageType);

        public abstract double getEntityDamageMult(IEntity entity, String damageType);

    }

}
