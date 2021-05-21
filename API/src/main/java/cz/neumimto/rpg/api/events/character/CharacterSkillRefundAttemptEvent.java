

package cz.neumimto.rpg.api.events.character;

import cz.neumimto.rpg.api.events.skill.SkillEvent;

/**
 * Created by NeumimTo on 27.7.2015.
 */
public interface CharacterSkillRefundAttemptEvent extends TargetCharacterEvent, SkillEvent {

    String getFailedTranslationKey();

    void setFailedTranslationKey(String failedMessage);

}
