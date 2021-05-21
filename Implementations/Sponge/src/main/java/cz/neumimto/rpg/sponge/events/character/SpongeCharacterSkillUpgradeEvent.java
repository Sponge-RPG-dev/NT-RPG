

package cz.neumimto.rpg.sponge.events.character;

import cz.neumimto.rpg.api.events.character.CharacterSkillUpgradeEvent;
import cz.neumimto.rpg.api.skills.ISkill;

/**
 * Created by NeumimTo on 26.7.2015.
 */
public class SpongeCharacterSkillUpgradeEvent extends AbstractCharacterEvent implements CharacterSkillUpgradeEvent {

    private ISkill skill;
    private String failedTranslationKey;

    @Override
    public ISkill getSkill() {
        return skill;
    }

    @Override
    public void setSkill(ISkill skill) {
        this.skill = skill;
    }

    @Override
    public String getFailedTranslationKey() {
        return failedTranslationKey;
    }

    @Override
    public void setFailedTranslationKey(String failedTranslationKey) {
        this.failedTranslationKey = failedTranslationKey;
    }

}
