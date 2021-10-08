package cz.neumimto.rpg.common.skills.types;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.SkillExecutionType;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.tree.SkillTree;
import cz.neumimto.rpg.common.skills.utils.SkillLoadingErrors;

import java.util.*;
import java.util.function.BiFunction;

public class CharacterAttributeSkill extends AbstractSkill<IActiveCharacter> {

    public CharacterAttributeSkill() {
        super();
    }

    @Override
    public SkillResult onPreUse(IActiveCharacter character, PlayerSkillContext esi) {
        return SkillResult.CANCELLED;
    }

    @Override
    public void onCharacterInit(IActiveCharacter c, int level, PlayerSkillContext context) {
        super.onCharacterInit(c, level, context);
        assignAll(c, 1, (integer, integer2) -> integer <= integer2);
    }

    private void assignAll(IActiveCharacter c, int i, BiFunction<Integer, Integer, Boolean> fc) {
        PlayerSkillContext skill = c.getSkill(getId());
        int totalLevel = skill.getTotalLevel();
        CharacterAttributeSkillData skillData = (CharacterAttributeSkillData) skill.getSkillData();
        for (Wrapper wrapper : skillData.wrappers) {
            if (fc.apply(wrapper.level, totalLevel)) {
                Rpg.get().getCharacterService().addTransientAttribute(c, wrapper.getCharacterAttribute(), i * wrapper.value);
            }
        }
    }

    @Override
    public void skillLearn(IActiveCharacter c, PlayerSkillContext context) {
        super.skillLearn(c, context);
        assignAll(c, 1, (integer, integer2) -> integer <= integer2);
    }

    @Override
    public void skillUpgrade(IActiveCharacter c, int level, PlayerSkillContext context) {
        super.skillUpgrade(c, level, context);
        assignAll(c, 1, Objects::equals);
    }

    @Override
    public void skillRefund(IActiveCharacter IActiveCharacter, PlayerSkillContext context) {
        super.skillRefund(IActiveCharacter, context);
        assignAll(IActiveCharacter, -1, (integer, integer2) -> integer <= integer2);
    }


    @Override
    public CharacterAttributeSkillData constructSkillData() {
        return new CharacterAttributeSkillData(getId());
    }

    @Override
    public <T extends SkillData> void loadSkillData(T skillData, SkillTree context, SkillLoadingErrors errors, Config c) {
        CharacterAttributeSkillData data = (CharacterAttributeSkillData) skillData;
        try {
            List<? extends Config> attributes = c.getConfigList("attributes");
            for (Config subc : attributes) {
                String attribute = subc.getString("attribute");
                int level = subc.getInt("skill-level");
                int val = subc.getInt("attribute-value");
                Optional<AttributeConfig> type = Rpg.get().getPropertyService().getAttributeById(attribute);
                if (!type.isPresent()) {
                    Log.warn("Unknown attribute " + attribute + " in " + context.getId());
                    continue;
                }
                AttributeConfig att = type.get();
                Wrapper wrapper = new Wrapper(att, level, val);
                data.wrappers.add(wrapper);
            }
        } catch (ConfigException ex) {

        }
    }

    @Override
    public SkillExecutionType getSkillExecutionType() {
        return SkillExecutionType.PASSIVE;
    }


    public class CharacterAttributeSkillData extends SkillData {

        Set<Wrapper> wrappers = new HashSet<>();

        public CharacterAttributeSkillData(String skill) {
            super(skill);
        }
    }

    public class Wrapper {

        private AttributeConfig characterAttribute;
        private int level;
        private int value;

        public Wrapper(AttributeConfig characterAttribute, int level, int value) {
            this.characterAttribute = characterAttribute;
            this.level = level;
            this.value = value;
        }

        public AttributeConfig getCharacterAttribute() {
            return characterAttribute;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
