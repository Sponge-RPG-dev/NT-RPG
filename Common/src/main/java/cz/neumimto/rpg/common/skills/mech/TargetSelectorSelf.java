package cz.neumimto.rpg.common.skills.mech;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;

import javax.inject.Singleton;

@Singleton
public class TargetSelectorSelf {

    public IEntity getTarget(IActiveCharacter character) {
        return character;
    }
}
