package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

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
