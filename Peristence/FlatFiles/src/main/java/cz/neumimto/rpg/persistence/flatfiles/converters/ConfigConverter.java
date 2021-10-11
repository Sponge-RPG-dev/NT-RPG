package cz.neumimto.rpg.persistence.flatfiles.converters;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.model.*;
import cz.neumimto.rpg.common.persistance.model.BaseCharacterAttributeImpl;
import cz.neumimto.rpg.common.persistance.model.CharacterBaseImpl;
import cz.neumimto.rpg.common.persistance.model.CharacterClassImpl;
import cz.neumimto.rpg.common.persistance.model.CharacterSkillImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigConverter {


    public static final String MARKED_FOR_REMOVAL = "Remove";

    private static final String UUID_ = "UUID";
    private static final String NAME = "Name";
    private static final String LASTNAME = "LastPlayerName";
    private static final String RESET_SKILL = "CanResetSkills";
    private static final String LAST_RESET = "LastReset";
    private static final String CHAR_CREATED = "Created";
    private static final String CHAR_UPDATED = "Updated";
    private static final String LAST_POSITION = "LastPosition";
    private static final String ATTRIBUTE_POINTS = "AttributePoints";
    private static final String ATTRIBUTE_POINTS_SPENT = "AttributePointsSpent";
    private static final String INVENTORY_EQUIP_ORDER = "InventoryEquipOrder";
    private static final String HEALTH_SCALING = "HealthScale";

    private static final String ATTRIBUTES = "Attributes";
    private static final String ATTRIBUTE_NAME = "Name";
    private static final String ATTRIBUTE_LEVEL = "Level";

    private static final String CLASSES = "Classes";
    private static final String CLASS_NAME = "Name";
    private static final String CLASS_EXPERIENCES = "Experiences";
    private static final String CLASS_LEVEL = "Level";
    private static final String CLASS_SKILLPOINTS = "SkillPoints";
    private static final String CLASS_SKILLPOINTS_SPENT = "SkillPointsSpent";

    private static final String SKILLS = "LearnedSkills";
    private static final String SKILL_ID = "Skill";
    private static final String SKILL_CD = "Cooldown";
    private static final String SKILL_FROM_CLASS = "FromClass";
    private static final String SKILL_LEVEL = "Level";

    private static final String UNIQUE_SKILLPOINTS = "UniqueSkillPoints";
    private static final String DATE_PAIR_DATE = "Date";
    private static final String DATE_PAIR_KEY = "SourceKey";

    private static final String SPELLBOOK = "PersonalSkillbook";

    public static Config toConfig(CharacterBase c, FileConfig config) {

        UUID uuid = c.getUuid();
        config.set(UUID_, uuid.toString());

        String name = c.getName();
        config.set(NAME, name);

        name = c.getLastKnownPlayerName();
        config.set(LASTNAME, name);

        Boolean canResetSkills = c.canResetSkills();
        config.set(RESET_SKILL, canResetSkills);

        Date lastReset = c.getLastReset();
        config.set(LAST_RESET, dateToText(lastReset));

        Date created = c.getCreated();
        config.set(CHAR_CREATED, dateToText(created));

        Date updated = c.getUpdated();
        config.set(CHAR_UPDATED, dateToText(updated));

        String lastPosition = String.format("%s;%s;%s;%s", c.getWorld(), c.getX(), c.getY(), c.getZ());
        config.set(LAST_POSITION, lastPosition);

        Integer attributePoints = c.getAttributePoints();
        config.set(ATTRIBUTE_POINTS, attributePoints);

        attributePoints = c.getAttributePointsSpent();
        config.set(ATTRIBUTE_POINTS_SPENT, attributePoints);

        List<Config> characterSkills = c.getCharacterSkills().stream().map(ConfigConverter::toConfig).collect(Collectors.toList());
        config.set(SKILLS, characterSkills);

        List<Config> characterClasses = c.getCharacterClasses().stream().map(ConfigConverter::toConfig).collect(Collectors.toList());
        config.set(CLASSES, characterClasses);

        List<Config> characterAttributes = c.getBaseCharacterAttribute().stream().map(ConfigConverter::toConfig).collect(Collectors.toList());
        config.set(ATTRIBUTES, characterAttributes);

        List<EquipedSlot> inventoryEquipSlotOrder = c.getInventoryEquipSlotOrder();
        String collect = inventoryEquipSlotOrder.stream().map(Object::toString).collect(Collectors.joining(";"));
        config.set(INVENTORY_EQUIP_ORDER, collect);

        Double healthScale = c.getHealthScale();
        config.set(HEALTH_SCALING, healthScale);

        Boolean markedForRemoval = c.getMarkedForRemoval();
        config.set(MARKED_FOR_REMOVAL, markedForRemoval);

        Map<String, Set<DateKeyPair>> uniqueSkillpoints = c.getUniqueSkillpoints();
        config.set(UNIQUE_SKILLPOINTS, toConfig(uniqueSkillpoints));

        String[][] spellbookPages = c.getSpellbookPages();
        config.set(SPELLBOOK, spellbookPages);

        return config;
    }

    private static Config toConfig(BaseCharacterAttribute a) {
        Config config = Config.inMemory();

        String name = a.getName();
        config.set(ATTRIBUTE_NAME, name);

        int level = a.getLevel();
        config.set(ATTRIBUTE_LEVEL, level);

        return config;
    }

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

        return config;
    }

    private static Config toConfig(Map<String, Set<DateKeyPair>> uniquePoints) {
        Config config = Config.inMemory();
        for (Map.Entry<String, Set<DateKeyPair>> entry : uniquePoints.entrySet()) {
            config.set(entry.getKey(), toConfig(entry.getValue()));
        }
        return config;
    }

    private static List<Config> toConfig(Collection<DateKeyPair> uniquePoints) {
        List<Config> configList = new ArrayList<>();
        for (DateKeyPair uniquePoint : uniquePoints) {
            Config config = Config.inMemory();
            config.set(DATE_PAIR_DATE, dateToText(uniquePoint.getDateReceived()));
            config.set(DATE_PAIR_KEY, uniquePoint.getSourceKey());
            configList.add(config);
        }
        return configList;
    }

    private static Map<String, Set<DateKeyPair>> uniqueSkillpointsFromConfig(Config config) {
        Map<String, Set<DateKeyPair>> uniquePoints = new HashMap<>();
        if (config == null) {
            return uniquePoints;
        }
        Map<String, Object> stringObjectMap = config.valueMap();
        for (Map.Entry<String, Object> entry : stringObjectMap.entrySet()) {
            List<Config> value = (List<Config>) entry.getValue();
            String key = entry.getKey();

            for (Config c : value) {
                Set<DateKeyPair> dateKeyPairs = uniquePoints.computeIfAbsent(key, k -> new HashSet<>());
                dateKeyPairs.add(new DateKeyPair(textToDate(c.get(DATE_PAIR_DATE)), c.get(DATE_PAIR_KEY)));
            }


        }

        return uniquePoints;
    }


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

        return config;
    }

    public static CharacterBase fromConfig(FileConfig config) {
        CharacterBase characterBase = new CharacterBaseImpl();
        characterBase.setUuid(UUID.fromString(config.get(UUID_)));

        characterBase.setName(config.get(NAME));
        characterBase.setLastKnownPlayerName(config.get(LASTNAME));

        characterBase.setCanResetSkills(config.get(RESET_SKILL));

        characterBase.setLastReset(textToDate(config.get(LAST_RESET)));
        characterBase.setCreated(textToDate(config.get(CHAR_CREATED)));
        characterBase.setUpdated(textToDate(config.get(CHAR_UPDATED)));

        String position = config.get(LAST_POSITION);
        String[] split = position.split(";");
        characterBase.setWorld(split[0]);
        characterBase.setX(Integer.parseInt(split[1]));
        characterBase.setY(Integer.parseInt(split[2]));
        characterBase.setZ(Integer.parseInt(split[3]));

        characterBase.setAttributePoints(config.get(ATTRIBUTE_POINTS));
        characterBase.setAttributePointsSpent(config.get(ATTRIBUTE_POINTS_SPENT));

        List<Config> classes = config.get(CLASSES);
        characterBase.setCharacterClasses(classesFromConfig(classes, characterBase));

        List<Config> skills = config.get(SKILLS);
        characterBase.setCharacterSkills(skillsFromConfig(skills, characterBase));


        List<Config> attributes = config.get(ATTRIBUTES);
        characterBase.setBaseCharacterAttribute(attributesFromConfig(attributes, characterBase));


        List<EquipedSlot> iso = new ArrayList<>();
        String o = config.get(INVENTORY_EQUIP_ORDER);
        if (!o.trim().isEmpty()) {
            for (String s : o.split(";")) {
                String[] split1 = s.split("@");
                if (split1.length == 1) {
                    iso.add(Rpg.get().getInventoryService().createEquipedSlot(null, Integer.parseInt(split1[0])));
                } else {
                    iso.add(Rpg.get().getInventoryService().createEquipedSlot(split1[0], Integer.parseInt(split1[1])));
                }
            }
        }
        characterBase.setInventoryEquipSlotOrder(iso);

        characterBase.setHealthScale(((Number) config.get(HEALTH_SCALING)).doubleValue());
        characterBase.setMarkedForRemoval(config.get(MARKED_FOR_REMOVAL));


        Config uniqueSkillpoints = config.get(UNIQUE_SKILLPOINTS);
        Map<String, Set<DateKeyPair>> stringSetMap = uniqueSkillpointsFromConfig(uniqueSkillpoints);
        characterBase.getUniqueSkillpoints().putAll(stringSetMap);


        List<List<String>> spellbookPages = config.getOrElse(SPELLBOOK,  new ArrayList<>());
        String[][] pages = spellbookPages
                .stream()
                .map((l) -> l.toArray(new String[l.size()]))
                .collect(Collectors.toList())
                .toArray(new String[spellbookPages.size()][]);
        characterBase.setSpellbookPages(pages);
        return characterBase;
    }

    private static Set<BaseCharacterAttribute> attributesFromConfig(List<Config> attributes, CharacterBase c) {
        Set<BaseCharacterAttribute> attributeSet = new HashSet<>();
        for (Config config : attributes) {
            attributeSet.add(attributeFromConfig(config, c));
        }
        return attributeSet;
    }

    private static Set<CharacterClass> classesFromConfig(List<Config> classes, CharacterBase c) {
        Set<CharacterClass> classSet = new HashSet<>();
        for (Config config : classes) {
            classSet.add(classFromConfig(config, c));
        }
        return classSet;
    }

    private static Set<CharacterSkill> skillsFromConfig(List<Config> configs, CharacterBase c) {
        Set<CharacterSkill> skills = new HashSet<>();
        for (Config config : configs) {
            skills.add(skillFromConfig(config, c));
        }
        return skills;
    }

    private static BaseCharacterAttribute attributeFromConfig(Config config, CharacterBase character) {
        BaseCharacterAttribute attribute = new BaseCharacterAttributeImpl();

        attribute.setName(config.get(ATTRIBUTE_NAME));
        attribute.setLevel(config.get(ATTRIBUTE_LEVEL));

        attribute.setCharacterBase(character);

        return attribute;
    }

    private static CharacterClass classFromConfig(Config config, CharacterBase character) {
        CharacterClass characterClass = new CharacterClassImpl();

        characterClass.setName(config.get(CLASS_NAME));
        characterClass.setExperiences(((Number) config.get(CLASS_EXPERIENCES)).doubleValue());
        characterClass.setLevel(config.get(CLASS_LEVEL));
        characterClass.setSkillPoints(config.get(CLASS_SKILLPOINTS));
        characterClass.setUsedSkillPoints(config.get(CLASS_SKILLPOINTS_SPENT));

        characterClass.setCharacterBase(character);

        return characterClass;
    }

    private static CharacterSkill skillFromConfig(Config config, CharacterBase character) {
        CharacterSkill characterSkill = new CharacterSkillImpl();
        characterSkill.setCharacterBase(character);

        characterSkill.setCatalogId(config.get(SKILL_ID));
        characterSkill.setCooldown(config.getLong(SKILL_CD));

        String o = config.get(SKILL_FROM_CLASS);
        if (o != null) {
            for (CharacterClass characterClass : character.getCharacterClasses()) {
                if (characterClass.getName().equalsIgnoreCase(o)) {
                    characterSkill.setFromClass(characterClass);
                    break;
                }
            }
        }

        characterSkill.setLevel(config.getInt(SKILL_LEVEL));

        return characterSkill;
    }

    private static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    }

    private static String dateToText(Date created) {
        return getDateFormat().format(created);
    }

    private static Date textToDate(String text) {
        try {
            return getDateFormat().parse(text);
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse date " + text);
        }
    }
}
