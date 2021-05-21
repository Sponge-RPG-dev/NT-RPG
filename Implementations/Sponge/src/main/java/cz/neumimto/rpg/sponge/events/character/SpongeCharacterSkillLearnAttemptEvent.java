

package cz.neumimto.rpg.sponge.events.character;

import cz.neumimto.rpg.api.events.character.CharacterSkillLearnAttemptEvent;
import cz.neumimto.rpg.api.skills.ISkill;

/**
 * Created by NeumimTo on 26.7.2015.
 */
public class SpongeCharacterSkillLearnAttemptEvent extends AbstractCharacterEvent implements CharacterSkillLearnAttemptEvent {

    private String failedTranslationKey;
    private ISkill skill;

    @Override
    public String getFailedTranslationKey() {
        return failedTranslationKey;
    }

    @Override
    public void setFailedTranslationKey(String failedTranslationKey) {
        this.failedTranslationKey = failedTranslationKey;
    }

    @Override
    public ISkill getSkill() {
        return skill;
    }

    @Override
    public void setSkill(ISkill skill) {
        this.skill = skill;
    }

}
