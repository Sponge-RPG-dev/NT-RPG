package cz.neumimto.rpg.common.skills.mech;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;

import javax.inject.Singleton;

@Singleton
public class TargetSelectorSelf {

    public IEntity getTarget(ActiveCharacter character) {
        return character;
    }
}
