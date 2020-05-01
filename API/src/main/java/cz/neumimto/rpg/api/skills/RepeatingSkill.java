package cz.neumimto.rpg.api.skills;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillExecutorCallback;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.skills.utils.SkillLoadingErrors;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RepeatingSkill extends ActiveSkill {

    @Inject
    private EffectService effectService;

    @Override
    public WrappedSkill.WrappedSkillData constructSkillData() {
        return new RepeatingSkillData(getId());
    }

    @Override
    public <T extends SkillData> void loadSkillData(T skillData, SkillTree context, SkillLoadingErrors errors, Config c) {
        RepeatingSkillData data = (RepeatingSkillData) skillData;
        data.period = c.getLong("Repeat-period");
        try {
            data.maxDuartion = c.getLong("Repeat-maxDuration");
        } catch (ConfigException ignored) {
            data.maxDuartion = -1;
        }

        Config inner = c.getConfig("Parent");
        data.getSkill().loadSkillData(skillData, context, errors, inner);
    }

    @Override
    public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
        RepeatingSkillData skillData = (RepeatingSkillData) info.getSkillData();

        RepeatingSkillEffect repeatingSkillEffect = new RepeatingSkillEffect(skillData, character);
        effectService.addEffect(repeatingSkillEffect, this);

        skillContext.next(character, info, skillContext.result(SkillResult.OK));
    }


    public static class RepeatingSkillData extends WrappedSkill.WrappedSkillData {
        public long period;
        public long maxDuartion;

        public RepeatingSkillData(String skill) {
            super(skill);
        }
    }

    public class RepeatingSkillEffect extends EffectBase implements IEffectContainer {

        private final RepeatingSkillData skillData;
        private final IActiveCharacter character;
        private final PlayerSkillContext info;
        private final RepeatingNotificationSkillExecutor executor;
        private final SkillContext skillContext;

        public RepeatingSkillEffect(RepeatingSkillData skillData, IActiveCharacter character) {
            super("repeating_" + skillData.getSkillId(), character);
            this.skillData = skillData;
            this.character = character;
            this.info = character.getSkillInfo(skillData.getWrapped().getSkill());
            setDuration(skillData.maxDuartion);
            setPeriod(skillData.period);
            executor = new RepeatingNotificationSkillExecutor(this);
            skillContext = new SkillContext(RepeatingSkill.this, info);
        }

        public RepeatingSkillData getSkillData() {
            return skillData;
        }

        @Override
        public void onTick(IEffect self) {
            Rpg.get().getSkillService().executeSkill(character, info, skillContext, executor);
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


    static class RepeatingNotificationSkillExecutor extends SkillExecutorCallback {

        private RepeatingSkillEffect repeatingSkillEffect;

        public RepeatingNotificationSkillExecutor(RepeatingSkillEffect repeatingSkillEffect) {
            this.repeatingSkillEffect = repeatingSkillEffect;
        }

        @Override
        public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
            switch (skillResult.getResult()) {
                case NO_MANA:
                case NO_HP:
                    repeatingSkillEffect.setDuration(0);
                    break;
                default:
                    skillResult.resetCursor();
            }
        }
    }

}
