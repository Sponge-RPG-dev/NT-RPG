package cz.neumimto.rpg.common.skills.mech;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.scripting.Caster;
import cz.neumimto.rpg.common.skills.scripting.Handler;
import cz.neumimto.rpg.common.skills.scripting.TargetSelector;

import javax.inject.Singleton;

@Singleton
@TargetSelector("self")
public class TargetSelectorSelf {

    @Handler
    public IEntity getTarget(@Caster IActiveCharacter character) {
        return character;
    }
}
