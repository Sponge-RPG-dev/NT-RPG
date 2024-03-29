package cz.neumimto.rpg.common.skills;

import com.electronwill.nightconfig.core.conversion.Path;
import com.typesafe.config.Config;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassResource;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.common.skills.tree.SkillTree;
import cz.neumimto.rpg.common.skills.types.AbstractSkill;
import cz.neumimto.rpg.common.skills.utils.SkillLoadingErrors;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class ResourceSkill extends AbstractSkill<ActiveCharacter> {

    @Inject
    private ResourceService resourceService;

    @Override
    public SkillResult onPreUse(ActiveCharacter character, PlayerSkillContext esi) {
        return SkillResult.CANCELLED;
    }

    @Override
    public void onCharacterInit(ActiveCharacter c, int level, PlayerSkillContext context) {
        super.onCharacterInit(c, level, context);
        add(c, true, (integer, integer2) -> integer <= integer2);
    }

    @Override
    public void skillLearn(ActiveCharacter ActiveCharacter, PlayerSkillContext context) {
        super.skillLearn(ActiveCharacter, context);
        add(ActiveCharacter, true, (integer, integer2) -> integer <= integer2);
    }

    @Override
    public void skillRefund(ActiveCharacter ActiveCharacter, PlayerSkillContext context) {
        super.skillRefund(ActiveCharacter, context);
        add(ActiveCharacter, false, (integer, integer2) -> integer <= integer2);
    }

    @Override
    public void skillUpgrade(ActiveCharacter ActiveCharacter, int level, PlayerSkillContext context) {
        super.skillUpgrade(ActiveCharacter, level, context);
        add(ActiveCharacter, true, Objects::equals);
    }

    private void add(ActiveCharacter character, boolean add, BiFunction<Integer, Integer, Boolean> fc) {
        PlayerSkillContext skill = character.getSkill(getId());
        ResourceSkill.ResourceSkillData skillData = (ResourceSkill.ResourceSkillData) skill.getSkillData();
        for (Wrapper property : skillData.resources) {
            if (fc.apply(property.level, skill.getTotalLevel())) {
                if (add) {
                    resourceService.addResource(character, property, getId());
                } else {
                    resourceService.removeResource(character, property, getId());
                }
            }
        }
    }

    @Override
    public <T extends SkillData> void loadSkillData(T skillData, SkillTree context, SkillLoadingErrors errors, Config c) {
        ResourceSkill.ResourceSkillData data = (ResourceSkill.ResourceSkillData) skillData;
        List<? extends Config> properties = c.getConfigList("Resources");
        for (Config cprop : properties) {
            String type = cprop.getString("Type");
            int level = cprop.getInt("Level");
            double value = cprop.hasPath("Value") ? cprop.getDouble("Value") : 0;
            double tick = cprop.hasPath("TickChange") ? cprop.getDouble("TickChange") : 0;
            Wrapper wrapper = new Wrapper();
            wrapper.level = level;
            wrapper.type = type;
            wrapper.baseValue = value;
            wrapper.tickChange = tick;
            data.resources.add(wrapper);
        }
    }

    @Override
    public SkillExecutionType getSkillExecutionType() {
        return SkillExecutionType.PASSIVE;
    }

    @Override
    public ResourceSkill.ResourceSkillData constructSkillData() {
        return new ResourceSkill.ResourceSkillData(getId());
    }

    public static class Wrapper extends ClassResource {
        @Path("Level")
        public int level;
    }


    public class ResourceSkillData extends SkillData {

        List<Wrapper> resources = new ArrayList<>();

        public ResourceSkillData(String skill) {
            super(skill);
        }

        public List<Wrapper> getResources() {
            return resources;
        }
    }
}
