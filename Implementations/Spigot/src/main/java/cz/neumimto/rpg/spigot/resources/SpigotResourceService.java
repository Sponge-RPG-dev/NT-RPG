package cz.neumimto.rpg.spigot.resources;

import com.google.inject.Singleton;
import cz.neumimto.rpg.common.entity.AbstractMob;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.resources.ResourceService;

@Singleton
public class SpigotResourceService extends ResourceService {

    @Override
    protected Resource getHpTracker(IActiveCharacter character) {
        return new Health(character.getUUID());
    }

    @Override
    protected Resource getStaminaTracker(IActiveCharacter character) {
        return null;
    }

    @Override
    public Resource initializeForAi(AbstractMob mob) {
        return new MobHealth(mob.getUUID());
    }
}
