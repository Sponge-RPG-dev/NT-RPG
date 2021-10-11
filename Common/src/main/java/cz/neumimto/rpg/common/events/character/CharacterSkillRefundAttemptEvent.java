

package cz.neumimto.rpg.common.events.character;

import cz.neumimto.rpg.common.events.skill.SkillEvent;

/**
 * Created by NeumimTo on 27.7.2015.
 */
public interface CharacterSkillRefundAttemptEvent extends TargetCharacterEvent, SkillEvent {

    String getFailedTranslationKey();

    void setFailedTranslationKey(String failedMessage);

}
