package cz.neumimto.rpg.common.damage;

import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

import javax.inject.Inject;

public abstract class AbstractDamageService<T> implements DamageService<T> {

    @Inject
    protected EntityService entityService;

    @Inject
    protected ClassService classService;

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
            character.setWeaponDamage(0); //todo unarmed
        } else {
            recalculateCharacterWeaponDamage(character, mainHand.getItemType());
        }
    }

    @Override
    public void recalculateCharacterWeaponDamage(IActiveCharacter character, RpgItemType type) {
        double damage = getCharacterItemDamage(character, type);
        // damage += character.getMainHand().getDamage() + character.getOffHand().getDamage();
        character.setWeaponDamage(damage);
    }

}
