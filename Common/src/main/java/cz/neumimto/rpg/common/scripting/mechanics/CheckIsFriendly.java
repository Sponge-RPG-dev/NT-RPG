package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.scripting.Caster;
import cz.neumimto.rpg.common.skills.scripting.Handler;
import cz.neumimto.rpg.common.skills.scripting.SkillMechanic;
import cz.neumimto.rpg.common.skills.scripting.Target;

import javax.inject.Singleton;

@Singleton
@SkillMechanic("is_ally")
public class CheckIsFriendly {

    @Handler
    public boolean removeEffect(@Target IEntity target, @Caster IActiveCharacter caster) {
        return target.isFriendlyTo(caster);
    }
}
