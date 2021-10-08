package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DamageCheck {

    @Inject
    private DamageService damageService;

    public boolean canDamage(IActiveCharacter character, IEntity target) {
        return damageService.canDamage(character, target);
    }
}
