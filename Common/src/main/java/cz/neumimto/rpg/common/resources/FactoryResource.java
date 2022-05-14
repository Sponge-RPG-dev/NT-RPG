package cz.neumimto.rpg.common.resources;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;

public interface FactoryResource {
    Resource createFor(IActiveCharacter character);
}
