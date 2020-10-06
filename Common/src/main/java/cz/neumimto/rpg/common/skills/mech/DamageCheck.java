package cz.neumimto.rpg.common.skills.mech;


import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.scripting.Caster;
import cz.neumimto.rpg.common.skills.scripting.Handler;
import cz.neumimto.rpg.common.skills.scripting.SkillMechanic;
import cz.neumimto.rpg.common.skills.scripting.Target;

import javax.inject.Singleton;

@Singleton
@SkillMechanic("damage_check")
public class DamageCheck {

    @Handler
    public boolean check(@Caster IActiveCharacter caster, @Target IEntity entity) {
        return true;
    }

}
