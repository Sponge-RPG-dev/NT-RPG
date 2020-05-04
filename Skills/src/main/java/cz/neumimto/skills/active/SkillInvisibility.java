package cz.neumimto.skills.active;

import cz.neumimto.effects.positive.Invisibility;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 23.12.2015.
 */
@Singleton
@ResourceLoader.ListenerClass
@ResourceLoader.Skill("ntrpg:invisibility")
public class SkillInvisibility extends ActiveSkill {

    @Inject
    private EffectService effectService;

    @Override
    public void init() {
        super.init();
        setDamageType(null);
        settings.addNode(SkillNodes.DURATION, 10, 10);
        addSkillType(SkillType.STEALTH);
        addSkillType(SkillType.MOVEMENT);
    }

    @Override
    public SkillResult cast(IActiveCharacter character, PlayerSkillContext skillContext) {
        long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
        Invisibility invisibility = new Invisibility(character, duration);
        effectService.addEffect(invisibility, this);
        return SkillResult.OK;
    }


}
