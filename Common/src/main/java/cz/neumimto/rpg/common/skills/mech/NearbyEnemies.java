package cz.neumimto.rpg.common.skills.mech;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.scripting.Caster;
import cz.neumimto.rpg.common.skills.scripting.Handler;
import cz.neumimto.rpg.common.skills.scripting.SkillArgument;
import cz.neumimto.rpg.common.skills.scripting.TargetSelector;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

@Singleton
@TargetSelector("nearby_enemies")
public class NearbyEnemies {

    @Handler
    public List<IEntity> getTargets(@Caster IActiveCharacter character, @SkillArgument("settings.range") float range) {
        return Collections.emptyList();
    }
}
