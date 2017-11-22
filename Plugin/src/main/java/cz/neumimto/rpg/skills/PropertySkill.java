package cz.neumimto.rpg.skills;

import antlr.ASdebug.IASDebugStream;
import com.typesafe.config.Config;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.IActiveCharacter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class PropertySkill extends AbstractSkill{

    public PropertySkill(String name) {
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
        add(c,1, (integer, integer2) -> integer <= integer2);
    }

    @Override
    public void skillLearn(IActiveCharacter IActiveCharacter) {
        super.skillLearn(IActiveCharacter);
        add(IActiveCharacter,1, (integer, integer2) -> integer <= integer2);
    }

    @Override
    public void skillRefund(IActiveCharacter IActiveCharacter) {
        super.skillRefund(IActiveCharacter);
        add(IActiveCharacter,-1, (integer, integer2) -> integer <= integer2);
    }

    @Override
    public void skillUpgrade(IActiveCharacter IActiveCharacter, int level) {
        super.skillUpgrade(IActiveCharacter, level);
        add(IActiveCharacter,1, Objects::equals);
    }

    private void add(IActiveCharacter character,  int i, BiFunction<Integer, Integer, Boolean> fc) {
        ExtendedSkillInfo skill = character.getSkill(getName());
        PropertySkillData skillData = (PropertySkillData) skill.getSkillData();
        for (Wrapper property : skillData.properties) {
            if (fc.apply(property.level, skill.getTotalLevel())) {
                character.addProperty(property.level, property.value * i);
            }
        }

    }


    @Override
    public <T extends SkillData> void loadSkillData(T skillData, SkillTree context, SkillLoadingErrors errors, Config c) {
        PropertySkillData data = (PropertySkillData) skillData;
        List<? extends Config> properties = c.getConfigList("properties");
        for (Config cprop : properties) {
            int level = cprop.getInt("level");
            float value = (float) cprop.getDouble("value");
            String name = cprop.getString("property-name");

            try {
                int idByName = NtRpgPlugin.GlobalScope.propertyService.getIdByName(name);
                Wrapper wrapper = new Wrapper(name,idByName, level, value);
                data.properties.add(wrapper);
            } catch (NullPointerException e) {
                errors.log("Unknown property name %s in %s", name, context.getId());
                continue;
            }

        }
    }

    @Override
    public PropertySkillData constructSkillData() {
        return new PropertySkillData(getName());
    }


    public class Wrapper {
        final String propertyName;
        final int propertyId;
        final int level;
        final float value;

        public Wrapper(String propertyName, int propertyId, int level, float value) {
            this.propertyName = propertyName;
            this.propertyId = propertyId;
            this.level = level;
            this.value = value;
        }
    }

    public class PropertySkillData extends SkillData {

        List<Wrapper> properties = new ArrayList<>();

        public PropertySkillData(String skill) {
            super(skill);
        }

        public List<Wrapper> getProperties() {
            return properties;
        }
    }
}
