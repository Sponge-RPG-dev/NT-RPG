package cz.neumimto.rpg.api.skills;

import com.typesafe.config.Config;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.utils.SkillLoadingErrors;

import java.util.List;
import java.util.Set;

public class WrappedSkill implements ISkill {

    private ISkill inner;
    private String catalogId;

    public WrappedSkill() {
        ResourceLoader.Skill sk = this.getClass().getAnnotation(ResourceLoader.Skill.class);
        if (sk != null) {
            catalogId = sk.value().toLowerCase();
        }
    }

    public ISkill getInner() {
        return inner;
    }

    public void setInner(ISkill inner) {
        this.inner = inner;
    }

    @Override
    public WrappedSkillData constructSkillData() {
        return new WrappedSkillData(getId(), inner.constructSkillData());
    }

    @Override
    public <T extends SkillData> void loadSkillData(T skillData, SkillTree context, SkillLoadingErrors errors, Config c) {
        Config inner = c.getConfig("Parent");
        loadSkillData(skillData, context, errors, inner);
    }

    @Override
    public String getId() {
        return catalogId;
    }

    @Override
    public void init() {
        inner.init();
    }

    @Override
    public void skillLearn(IActiveCharacter character, PlayerSkillContext context) {
        inner.skillLearn(character, context);
    }

    @Override
    public void skillUpgrade(IActiveCharacter character, int level, PlayerSkillContext context) {
        inner.skillUpgrade(character, level, context);
    }

    @Override
    public void skillRefund(IActiveCharacter character, PlayerSkillContext context) {
        inner.skillRefund(character, context);
    }

    @Override
    public SkillSettings getDefaultSkillSettings() {
        return inner.getDefaultSkillSettings();
    }

    @Override
    public void onCharacterInit(IActiveCharacter c, int level, PlayerSkillContext context) {
        inner.onCharacterInit(c, level, context);
    }

    @Override
    public void onPreUse(IActiveCharacter character, SkillContext skillContext) {
        inner.onPreUse(character, skillContext);
    }

    @Override
    public Set<ISkillType> getSkillTypes() {
        return inner.getSkillTypes();
    }

    @Override
    public SkillSettings getSettings() {
        return inner.getSettings();
    }

    @Override
    public void setSettings(SkillSettings settings) {
        inner.setSettings(settings);
    }

    @Override
    public String getDamageType() {
        return inner.getDamageType();
    }

    @Override
    public void setDamageType(String type) {
        inner.setDamageType(type);
    }

    @Override
    public SkillExecutionType getSkillExecutionType() {
        return inner.getSkillExecutionType();
    }

    public static class WrappedSkillData extends SkillData {

        public SkillData wrapped;

        public WrappedSkillData(String skill, SkillData wrapped) {
            super(skill);
            this.wrapped = wrapped;
        }

        public SkillData getWrapped() {
            return wrapped;
        }

        public void setWrapped(SkillData wrapped) {
            this.wrapped = wrapped;
        }
    }
}
