package cz.neumimto.rpg.spigot.events.character;

import cz.neumimto.rpg.common.events.character.CharacterSkillLearnAttemptEvent;
import cz.neumimto.rpg.common.skills.ISkill;
import org.bukkit.event.HandlerList;

/**
 * Created by NeumimTo on 26.7.2015.
 */
public class SpigotCharacterSkillLearnAttemptEvent extends AbstractCharacterEvent implements CharacterSkillLearnAttemptEvent {

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

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
