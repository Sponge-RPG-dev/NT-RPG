package cz.neumimto.rpg.api.skills.types;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.rpg.api.entity.players.attributes.AttributeConfig;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillExecutionType;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.utils.SkillLoadingErrors;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

import java.util.*;
import java.util.function.BiFunction;

import static org.jline.utils.Log.warn;

public class CharacterAttributeSkill extends AbstractSkill {

    public CharacterAttributeSkill() {
        super();
    }

    @Override
    public void onPreUse(IActiveCharacter character, SkillContext skillContext) {
        skillContext.result(SkillResult.CANCELLED);
    }

    @Override
    public void onCharacterInit(IActiveCharacter c, int level) {
        super.onCharacterInit(c, level);
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
    public void skillLearn(IActiveCharacter c) {
        super.skillLearn(c);
        assignAll(c, 1, (integer, integer2) -> integer <= integer2);
    }

    @Override
    public void skillUpgrade(IActiveCharacter c, int level) {
        super.skillUpgrade(c, level);
        assignAll(c, 1, Objects::equals);
    }

    @Override
    public void skillRefund(IActiveCharacter IActiveCharacter) {
        super.skillRefund(IActiveCharacter);
        assignAll(IActiveCharacter, -1, (integer, integer2) -> integer <= integer2);
    }


    @Override
    public CharacterAttributeSkillData constructSkillData() {
        return new CharacterAttributeSkillData(getName());
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
                    warn("Unknown attribute " + attribute + " in " + context.getId());
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
