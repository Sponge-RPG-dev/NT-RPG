package cz.neumimto.rpg.skills;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.attributes.CharacterAttribute;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CharacterAttributeSkill extends AbstractSkill {

    public CharacterAttributeSkill(String name) {
        super();
        setName(name);
    }

    @Override
    public SkillResult onPreUse(IActiveCharacter character) {
        return SkillResult.CANCELLED;
    }

    @Override
    public void onCharacterInit(IActiveCharacter c, int level) {
        super.onCharacterInit(c, level);
        assignAll(c, 1, (integer, integer2) -> integer <= integer2);
    }

    private void assignAll(IActiveCharacter c, int i, BiFunction<Integer, Integer, Boolean> fc) {
        ExtendedSkillInfo skill = c.getSkill(getName());
        int totalLevel = skill.getTotalLevel();
        CharacterAttributeSkillData skillData = (CharacterAttributeSkillData) skill.getSkillData();
        for (Wrapper wrapper : skillData.wrappers) {
            if (fc.apply(wrapper.level, totalLevel)) {
                NtRpgPlugin.GlobalScope.characterService.addTemporalAttribute(c, wrapper.getCharacterAttribute(), i * wrapper.value);
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
                Wrapper wrapper = new Wrapper(NtRpgPlugin.GlobalScope.propertyService.getAttribute(attribute), level, val);
                if (wrapper.characterAttribute == null) {
                    errors.log("Unknown attribute %s in %s", attribute, context.getId());
                } else {
                    data.wrappers.add(wrapper);
                }
            }
        } catch (ConfigException ex) {

        }


    }

    public class CharacterAttributeSkillData extends SkillData {

        Set<Wrapper> wrappers = new HashSet<>();

        public CharacterAttributeSkillData(String skill) {
            super(skill);
        }
    }

    public class Wrapper  {

        private ICharacterAttribute characterAttribute;
        private int level;
        private int value;

        public Wrapper(ICharacterAttribute characterAttribute, int level, int value) {
            this.characterAttribute = characterAttribute;
            this.level = level;
            this.value = value;
        }

        public ICharacterAttribute getCharacterAttribute() {
            return characterAttribute;
        }

        public void setCharacterAttribute(ICharacterAttribute characterAttribute) {
            this.characterAttribute = characterAttribute;
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
