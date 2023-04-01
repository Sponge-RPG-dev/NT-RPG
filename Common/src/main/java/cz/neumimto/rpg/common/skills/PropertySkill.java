package cz.neumimto.rpg.common.skills;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.entity.EntityService;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.skills.tree.SkillTree;
import cz.neumimto.rpg.common.skills.types.AbstractSkill;
import cz.neumimto.rpg.common.skills.utils.SkillLoadingErrors;
import cz.neumimto.rpg.common.utils.Console;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class PropertySkill extends AbstractSkill<ActiveCharacter> {

    @Inject
    private PropertyService propertyService;

    @Inject
    private DamageService damageService;

    @Inject
    private EntityService entityService;

    @Override
    public SkillResult onPreUse(ActiveCharacter character, PlayerSkillContext esi) {
        return SkillResult.CANCELLED;
    }

    @Override
    public void onCharacterInit(ActiveCharacter c, int level, PlayerSkillContext context) {
        super.onCharacterInit(c, level, context);
        add(c, 1, (integer, integer2) -> integer <= integer2);
    }

    @Override
    public void skillLearn(ActiveCharacter ActiveCharacter, PlayerSkillContext context) {
        super.skillLearn(ActiveCharacter, context);
        add(ActiveCharacter, 1, (integer, integer2) -> integer <= integer2);
    }

    @Override
    public void skillRefund(ActiveCharacter ActiveCharacter, PlayerSkillContext context) {
        super.skillRefund(ActiveCharacter, context);
        add(ActiveCharacter, -1, (integer, integer2) -> integer <= integer2);
    }

    @Override
    public void skillUpgrade(ActiveCharacter ActiveCharacter, int level, PlayerSkillContext context) {
        super.skillUpgrade(ActiveCharacter, level, context);
        add(ActiveCharacter, 1, Objects::equals);
    }

    private void add(ActiveCharacter character, int i, BiFunction<Integer, Integer, Boolean> fc) {
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
        return new PropertySkillData(getId());
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
