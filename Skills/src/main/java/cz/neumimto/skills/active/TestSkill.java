package cz.neumimto.skills.active;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
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
    public void castOn(IEntity target, ISpongeCharacter source, PlayerSkillContext info, SkillContext skillContext) {

    }
}
