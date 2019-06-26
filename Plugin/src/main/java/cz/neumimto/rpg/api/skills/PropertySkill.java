package cz.neumimto.rpg.api.skills;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.types.AbstractSkill;
import cz.neumimto.rpg.api.skills.utils.SkillLoadingErrors;
import cz.neumimto.rpg.api.utils.Console;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class PropertySkill extends AbstractSkill {

    @Inject
    private PropertyService propertyService;

    @Inject
    private DamageService damageService;

    @Inject
    private EntityService entityService;

    @Override
    public void onPreUse(IActiveCharacter character, SkillContext skillContext) {
        skillContext.result(SkillResult.CANCELLED);
    }

    @Override
    public void onCharacterInit(IActiveCharacter c, int level) {
        super.onCharacterInit(c, level);
        add(c, 1, (integer, integer2) -> integer <= integer2);
    }

    @Override
    public void skillLearn(IActiveCharacter IActiveCharacter) {
        super.skillLearn(IActiveCharacter);
        add(IActiveCharacter, 1, (integer, integer2) -> integer <= integer2);
    }

    @Override
    public void skillRefund(IActiveCharacter IActiveCharacter) {
        super.skillRefund(IActiveCharacter);
        add(IActiveCharacter, -1, (integer, integer2) -> integer <= integer2);
    }

    @Override
    public void skillUpgrade(IActiveCharacter IActiveCharacter, int level) {
        super.skillUpgrade(IActiveCharacter, level);
        add(IActiveCharacter, 1, Objects::equals);
    }

    private void add(IActiveCharacter character, int i, BiFunction<Integer, Integer, Boolean> fc) {
        PlayerSkillContext skill = character.getSkill(getId());
        PropertySkillData skillData = (PropertySkillData) skill.getSkillData();
        for (Wrapper property : skillData.properties) {
            if (fc.apply(property.level, skill.getTotalLevel())) {
                Rpg.get().getCharacterService().changePropertyValue(character, property.propertyId, property.value * i);
            }
        }
    }

    @Override
    public <T extends SkillData> void loadSkillData(T skillData, SkillTree context, SkillLoadingErrors errors, Config c) {
        PropertySkillData data = (PropertySkillData) skillData;
        List<? extends Config> properties = c.getConfigList("Properties");
        for (Config cprop : properties) {
            int level = cprop.getInt("level");
            float value = (float) cprop.getDouble("value");
            String name = cprop.getString("property-name");

            try {
                int idByName = propertyService.getIdByName(name);
                Wrapper wrapper = new Wrapper(name, idByName, level, value);
                data.properties.add(wrapper);
            } catch (NullPointerException e) {
                errors.log(Console.RED + "Unknown property name %s in %s" + Console.RESET, name, context.getId());
            }

        }
    }

    @Override
    public SkillExecutionType getSkillExecutionType() {
        return SkillExecutionType.PASSIVE;
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
