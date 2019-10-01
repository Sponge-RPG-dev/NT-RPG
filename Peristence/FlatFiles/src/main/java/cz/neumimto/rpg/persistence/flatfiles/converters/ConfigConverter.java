package cz.neumimto.rpg.persistence.flatfiles.converters;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import cz.neumimto.rpg.api.persistance.model.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConfigConverter {

    private static final String UUID = "UUID";
    private static final String NAME = "Name";
    private static final String INFO = "Info";
    private static final String LASTNAME = "LastPlayerName";
    private static final String RESET_SKILL = "CanResetSkills";
    private static final String LAST_RESET = "LastReset";
    private static final String LAST_POSITION = "LastPosition";
    private static final String AttributePoints = "AttributePoints";
    private static final String AttributePointsSpent = "AttributePointsSpent";
    private static final String SKILLS = "LearnedSkills";
    private static final String CLASSES = "Classes";
    private static final String ATTRIBUTES = "Attributes";
    private static final String INVENTOY_EQUIP_ORDER = "InventoryEquipOrder";
    public static final String MARKED_FOR_REMOVAL = "Remove";
    private static final String HEALTH_SCALING = "HEALT_SCALE";

    public static Config toConfig(CharacterBase c, FileConfig config) {

        UUID uuid = c.getUuid();
        config.set(UUID, uuid);

        String name = c.getName();
        config.set(NAME, name);

        String info = c.getInfo();
        config.set(INFO, info);

        name = c.getLastKnownPlayerName();
        config.set(LASTNAME, name);

        boolean canResetSkills = c.isCanResetskills();
        config.set(RESET_SKILL, canResetSkills);

        Date lastReset = c.getLastReset();
        config.set(LAST_RESET, lastReset);
        
        String lastPosition = String.format("%s;%s;%s;%s", c.getWorld(), c.getX(), c.getY(), c.getZ());
        config.set(LAST_POSITION, lastPosition);

        Integer attributePoints = c.getAttributePoints();
        config.set(AttributePoints, attributePoints);

        attributePoints = c.getAttributePointsSpent();
        config.set(AttributePointsSpent, attributePoints);

        List<Config> characterSkills = c.getCharacterSkills().stream().map(ConfigConverter::toConfig).collect(Collectors.toList());
        config.set(SKILLS, characterSkills);

        List<Config> characterClasses = c.getCharacterClasses().stream().map(ConfigConverter::toConfig).collect(Collectors.toList());
        config.set(CLASSES, characterClasses);

        List<Config> characterAttributes = c.getBaseCharacterAttribute().stream().map(ConfigConverter::toConfig).collect(Collectors.toList());
        config.set(ATTRIBUTES, characterAttributes);

        List<EquipedSlot> inventoryEquipSlotOrder = c.getInventoryEquipSlotOrder();
        String collect = inventoryEquipSlotOrder.stream().map(equipedSlot -> equipedSlot.toString()).collect(Collectors.joining(";"));
        config.set(INVENTOY_EQUIP_ORDER, collect);

        Double healthScale = c.getHealthScale();
        config.set(HEALTH_SCALING, healthScale);

        Boolean markedForRemoval = c.getMarkedForRemoval();
        config.set(MARKED_FOR_REMOVAL, markedForRemoval);
        return config;
    }

    private static final String ATTRIBUTE_NAME = "Name";
    private static final String ATTRIBUTE_LEVEL = "Level";
    private static Config toConfig(BaseCharacterAttribute a) {
        Config config = Config.inMemory();

        String name = a.getName();
        config.set(ATTRIBUTE_NAME, name);

        int level = a.getLevel();
        config.set(ATTRIBUTE_LEVEL, level);

        return config;
    }

    private static final String CLASS_NAME = "Name";
    private static final String CLASS_EXPERIENCES = "Experiences";
    private static final String CLASS_LEVEL = "Level";
    private static final String CLASS_SKILLPOINTS = "SkillPoints";
    private static final String CLASS_SKILLPOINTS_SPENT = "SkillPointsSpent";
    private static final String CLASS_CREATED = "Created";
    private static final String CLASS_UPDATED = "Updated";
    private static Config toConfig(CharacterClass c) {
        Config config = Config.inMemory();

        String name = c.getName();
        config.set(CLASS_NAME, name);

        double exp = c.getExperiences();
        config.set(CLASS_EXPERIENCES, exp);

        int level = c.getLevel();
        config.set(CLASS_LEVEL, level);

        int sp = c.getSkillPoints();
        config.set(CLASS_SKILLPOINTS, sp);

        int usp = c.getUsedSkillPoints();
        config.set(CLASS_SKILLPOINTS_SPENT, usp);

        Date created = c.getCreated();
        config.set(CLASS_CREATED, created);

        Date updated = c.getUpdated();
        config.set(CLASS_UPDATED, updated);

        return config;
    }

    private static final String SKILL_ID = "Skill";
    private static final String SKILL_CD = "Cooldown";
    private static final String SKILL_FROM_CLASS = "FromClass";
    private static final String SKILL_LEVEL = "Level";
    private static final String SKILL_CREATED = "Created";
    private static final String SKILL_UPDATED = "Updated";
    private static Config toConfig(CharacterSkill characterSkill) {
        Config config = Config.inMemory();

        String catalogId = characterSkill.getCatalogId();
        config.set(SKILL_ID, catalogId);

        Long cooldown = characterSkill.getCooldown();
        config.set(SKILL_CD, cooldown);

        CharacterClass fromClass = characterSkill.getFromClass();
        if (fromClass != null) {
            config.set(SKILL_FROM_CLASS, fromClass.getName());
        }

        int level = characterSkill.getLevel();
        config.set(SKILL_LEVEL, level);

        Date created = characterSkill.getCreated();
        config.set(SKILL_CREATED, created);

        Date updated = characterSkill.getUpdated();
        config.set(SKILL_UPDATED, updated);

        return config;
    }
}
