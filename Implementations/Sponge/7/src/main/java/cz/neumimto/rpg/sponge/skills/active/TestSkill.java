package cz.neumimto.rpg.sponge.skills.active;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.skills.NDamageType;
import cz.neumimto.rpg.sponge.skills.types.Targeted;

import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:test")
public class TestSkill extends Targeted {

    @Override
    public void init() {
        super.init();
        setDamageType(NDamageType.ICE.getId());
    }

    @Override
    public SkillResult castOn(IEntity target, ISpongeCharacter source, PlayerSkillContext skillContext) {
        return SkillResult.OK;
    }
}
