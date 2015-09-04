package cz.neumimto.skills;

import cz.neumimto.configuration.Localization;
import cz.neumimto.effects.EffectService;
import cz.neumimto.ioc.Inject;
import cz.neumimto.players.IActiveCharacter;

/**
 * Created by NeumimTo on 6.8.2015.
 */
public abstract class PassiveSkill extends AbstractSkill {

    @Inject
    protected EffectService effectService;

    @Override
    public SkillResult onPreUse(IActiveCharacter character) {
        character.sendMessage(Localization.CANT_USE_PASSIVE_SKILL);
        return SkillResult.FAIL;
    }
}
