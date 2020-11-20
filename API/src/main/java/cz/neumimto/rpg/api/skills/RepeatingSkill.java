package cz.neumimto.rpg.api.skills;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.skills.utils.SkillLoadingErrors;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RepeatingSkill extends ActiveSkill<IActiveCharacter> {

    @Inject
    private EffectService effectService;

    @Override
    public WrappedSkill.WrappedSkillData constructSkillData() {
        return new RepeatingSkillData(getId());
    }

    @Override
    public <T extends SkillData> void loadSkillData(T skillData, SkillTree context, SkillLoadingErrors errors, Config c) {
        RepeatingSkillData data = (RepeatingSkillData) skillData;
        data.period = c.getLong("Repeat-Period");
        try {
            data.countRemaining = c.getInt("Repeat-Count");
        } catch (ConfigException ignored) {
            data.countRemaining = 1;
        }

        Config inner = c.getConfig("Parent");
        data.getSkill().loadSkillData(skillData, context, errors, inner);
    }

    @Override
    public SkillResult cast(IActiveCharacter character, PlayerSkillContext info) {
        RepeatingSkillData skillData = (RepeatingSkillData) info.getSkillData();

        RepeatingSkillEffect repeatingSkillEffect = new RepeatingSkillEffect(skillData, character, info);
        effectService.addEffect(repeatingSkillEffect, this);

        return SkillResult.OK;
    }


    public static class RepeatingSkillData extends WrappedSkill.WrappedSkillData {
        public long period;
        public int countRemaining;

        public RepeatingSkillData(String skill) {
            super(skill);
        }
    }

    public class RepeatingSkillEffect extends EffectBase implements IEffectContainer {

        private final RepeatingSkillData skillData;
        private final IActiveCharacter character;
        private PlayerSkillContext info;
        private int countRemaining;

        public RepeatingSkillEffect(RepeatingSkillData skillData, IActiveCharacter character, PlayerSkillContext info) {
            super("repeating_" + skillData.getSkillId(), character);
            this.skillData = skillData;
            this.character = character;
            this.info = new PlayerSkillContext(info.getClassDefinition(), skillData.getWrapped().getSkill(), character);
            this.info.setSkillData(skillData.wrapped);
            countRemaining = skillData.countRemaining;
            setDuration(-1);
            setPeriod(skillData.period);
        }

        public RepeatingSkillData getSkillData() {
            return skillData;
        }

        @Override
        public void onTick(IEffect self) {
            if (countRemaining-- < 0) {
                setDuration(0);
            } else {
                Rpg.get().getSkillService().executeSkill(character, info);
            }
        }


        @Override
        public Object getStackedValue() {
            return null;
        }

        @Override
        public void setStackedValue(Object o) {

        }

        @Override
        public Set<RepeatingSkillEffect> getEffects() {
            return new HashSet<>(Collections.singletonList(this));
        }


        @Override
        public RepeatingSkillEffect constructEffectContainer() {
            return this;
        }

    }

}
