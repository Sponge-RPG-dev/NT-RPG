

package cz.neumimto.rpg.api.skills;

import com.typesafe.config.Config;
import cz.neumimto.rpg.api.IRpgElement;
import cz.neumimto.rpg.api.effects.EffectSourceType;
import cz.neumimto.rpg.api.effects.IEffectSource;
import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.utils.SkillLoadingErrors;

import java.util.Set;

/**
 * Created by NeumimTo on 1.1.2015.
 */
public interface ISkill<T> extends IEffectSourceProvider, IRpgElement {

    String getId();

    void init();

    void skillLearn(IActiveCharacter character, PlayerSkillContext context);

    void skillUpgrade(IActiveCharacter character, int level, PlayerSkillContext context);

    void skillRefund(IActiveCharacter character, PlayerSkillContext context);

    SkillSettings getDefaultSkillSettings();

    void onCharacterInit(IActiveCharacter c, int level, PlayerSkillContext context);

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

    SkillExecutionType getSkillExecutionType();
}
