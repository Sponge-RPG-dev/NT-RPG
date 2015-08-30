package cz.neumimto.skills;

import cz.neumimto.configuration.Localization;
import cz.neumimto.players.IActiveCharacter;

/**
 * Created by NeumimTo on 26.7.2015.
 */
public abstract class ActiveSkill extends AbstractSkill {

    @Override
    public SkillResult onPreUse(IActiveCharacter character) {
        ExtendedSkillInfo info = character.getSkillInfo(this);
        if (character.isSilenced() && getSkillTypes().contains(SkillType.CANT_CAST_WHILE_SILENCED)) {
            character.sendMessage(Localization.PLAYER_IS_SILENCED);
            return SkillResult.CASTER_SILENCED;
        }
        return cast(character, info);
    }

    public abstract SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info);
}
