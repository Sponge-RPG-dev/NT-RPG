package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

import javax.inject.Singleton;

@Singleton
public class IsFriendly {

    public boolean to(IEntity target, IActiveCharacter caster) {
        return target.isFriendlyTo(caster);
    }
}
