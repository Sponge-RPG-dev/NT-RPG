package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.scripting.Caster;
import cz.neumimto.rpg.common.skills.scripting.Handler;
import cz.neumimto.rpg.common.skills.scripting.TargetSelector;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@TargetSelector("damage_check")
public class DamageCheck {

    @Inject
    private DamageService damageService;

    @Handler
    public boolean canDamage(@Caster IActiveCharacter character, IEntity target) {
        return damageService.canDamage(character, target);
    }
}
