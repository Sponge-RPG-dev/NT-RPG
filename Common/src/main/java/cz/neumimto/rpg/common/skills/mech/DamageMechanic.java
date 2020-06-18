package cz.neumimto.rpg.common.skills.mech;

import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.scripting.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@SkillMechanic("damage")
public class DamageMechanic {

    @Inject
    private DamageService damageService;

    @Handler
    public void damage(@Caster IActiveCharacter character, @Target IEntity entity, @SkillArgument("settings.damage") float damage) {
        if (damageService.canDamage(character, entity)) {
            damageService.damageEntity(character, damage);
        }
    }
}
