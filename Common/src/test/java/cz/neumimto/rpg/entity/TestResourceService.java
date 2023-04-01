package cz.neumimto.rpg.entity;

import cz.neumimto.rpg.common.entity.AbstractMob;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.resources.ResourceDefinition;
import cz.neumimto.rpg.common.resources.ResourceService;

import javax.inject.Singleton;

@Singleton
public class TestResourceService extends ResourceService {
    @Override
    protected Resource getHpTracker(ActiveCharacter character, ResourceDefinition resourceDefinition) {
        return new Resource(resourceDefinition);
    }

    @Override
    protected Resource getStaminaTracker(ActiveCharacter character, ResourceDefinition resourceDefinition) {
        return new Resource(resourceDefinition);
    }

    @Override
    public Resource initializeForAi(AbstractMob mob) {
        return null;
    }
}
