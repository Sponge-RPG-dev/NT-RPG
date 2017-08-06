package cz.neumimto.skills.active;

import cz.neumimto.SkillLocalization;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;

/**
 * Created by NeumimTo on 6.8.2017.
 */
@ResourceLoader.Skill
public class BattleCharge extends ActiveSkill {

    public BattleCharge() {
        setName(SkillLocalization.SKILL_BATTLECHARGE_NAME);
        setDescription(SkillLocalization.SKILL_BATTLECHARGE_DESC);
        SkillSettings settings = new SkillSettings();
        settings.addNode(SkillNodes.DURATION, 7500, 100);
        settings.addNode(SkillNodes.RADIUS, 7500, 100);
        setSettings(settings);
    }

    @Override
    public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
        return null;
    }
}
