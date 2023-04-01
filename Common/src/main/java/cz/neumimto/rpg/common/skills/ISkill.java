package cz.neumimto.rpg.common.skills;

import com.typesafe.config.Config;
import cz.neumimto.rpg.common.IRpgElement;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.EffectSourceType;
import cz.neumimto.rpg.common.effects.IEffectSource;
import cz.neumimto.rpg.common.effects.IEffectSourceProvider;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.skills.tree.SkillTree;
import cz.neumimto.rpg.common.skills.utils.SkillLoadingErrors;

import java.util.Set;

/**
 * Created by NeumimTo on 1.1.2015.
 */
public interface ISkill<T> extends IEffectSourceProvider, IRpgElement {

    String getId();

    void init();

    void skillLearn(ActiveCharacter character, PlayerSkillContext context);

    void skillUpgrade(ActiveCharacter character, int level, PlayerSkillContext context);

    void skillRefund(ActiveCharacter character, PlayerSkillContext context);

    SkillSettings getDefaultSkillSettings();

    void onCharacterInit(ActiveCharacter c, int level, PlayerSkillContext context);

    SkillResult onPreUse(T character, PlayerSkillContext esi);

    Set<ISkillType> getSkillTypes();

    SkillSettings getSettings();

    void setSettings(SkillSettings settings);

    String getDamageType();

    void setDamageType(String type);

    @Override
    default IEffectSource getType() {
        return EffectSourceType.SKILL;
    }

    default <T extends SkillData> T constructSkillData() {
        return (T) new SkillData(getId());
    }

    default <T extends SkillData> void loadSkillData(T skillData, SkillTree context, SkillLoadingErrors errors, Config c) {

    }

    default void delay(long millis, Runnable action) {
        Rpg.get().scheduleSyncLater(millis, action);
    }

    SkillExecutionType getSkillExecutionType();
}
