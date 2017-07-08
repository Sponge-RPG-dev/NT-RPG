package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.common.positive.Invisibility;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;

/**
 * Created by NeumimTo on 23.12.2015.
 */
@ResourceLoader.Skill
@ResourceLoader.ListenerClass
public class SkillInvisibility extends ActiveSkill {

    @Inject
    private EffectService effectService;

    public SkillInvisibility() {
        setName("Invisibility");
        setDamageType(null);
        SkillSettings settings = new SkillSettings();
        settings.addNode(SkillNodes.DURATION, 10, 10);
        setSettings(settings);
        getSkillTypes().add(SkillType.CANT_CAST_WHILE_SILENCED);
    }

    @Override
    public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info,SkillModifier skillModifier) {
        long duration = (long) settings.getLevelNodeValue(SkillNodes.DURATION,info.getTotalLevel());
        Invisibility invisibility = new Invisibility(character, duration);
        effectService.addEffect(invisibility,character, this);
        return SkillResult.OK;
    }


}
