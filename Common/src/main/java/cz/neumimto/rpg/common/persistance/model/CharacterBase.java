package cz.neumimto.rpg.common.persistance.model;

import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.model.DateKeyPair;
import cz.neumimto.rpg.common.model.EquipedSlot;
import cz.neumimto.rpg.common.skills.ISkill;

import java.util.*;

public class CharacterBase extends TimestampEntity {

    private Long characterId;
    private UUID uuid;
    private String name;
    private Integer attributePoints;
    private Boolean canResetSkills;
    private Double healthScale;
    private String lastKnownPlayerName;
    private Date lastReset;
    private Set<CharacterSkill> characterSkills = new HashSet<>();
    private Set<CharacterClass> characterClasses = new HashSet<>();
    private Set<BaseCharacterAttribute> baseCharacterAttribute = new HashSet<>();
    private List<EquipedSlot> inventoryEquipSlotOrder = new ArrayList<>();
    private Boolean markedForRemoval;
    private int attributePointsSpent;
    private int X;
    private int Y;
    private int Z;
    private String world;
    private Map<String, Set<DateKeyPair>> uniqueSkillpoints = new HashMap<>();

    private String[][] spellBookpages;

    private transient Map<String, Integer> cachedAttributes = new HashMap<>();

    public Map<String, Integer> getAttributes() {
        return cachedAttributes;
    }

    public Long getId() {
        return characterId;
    }

    public void setId(Long id) {
        this.characterId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getLastKnownPlayerName() {
        return lastKnownPlayerName;
    }


    public void setLastKnownPlayerName(String lastKnownPlayerName) {
        this.lastKnownPlayerName = lastKnownPlayerName;
    }


    public UUID getUuid() {
        return uuid;
    }


    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


    public Boolean canResetSkills() {
        return canResetSkills;
    }


    public void setCanResetSkills(boolean canResetSkills) {
        this.canResetSkills = canResetSkills;
    }


    public Date getLastReset() {
        return lastReset;
    }


    public void setLastReset(Date lastReset) {
        this.lastReset = lastReset;
    }


    public Integer getX() {
        return X;
    }


    public void setX(Integer x) {
        X = x;
    }


    public Integer getY() {
        return Y;
    }


    public void setY(Integer y) {
        Y = y;
    }


    public Integer getZ() {
        return Z;
    }


    public void setZ(Integer z) {
        Z = z;
    }


    public String getWorld() {
        return world;
    }


    public void setWorld(String world) {
        this.world = world;
    }


    public Integer getAttributePoints() {
        return attributePoints;
    }


    public void setAttributePoints(Integer attributePoints) {
        this.attributePoints = attributePoints;
    }


    public Set<CharacterSkill> getCharacterSkills() {
        return characterSkills;
    }


    public void setCharacterSkills(Set<CharacterSkill> characterSkills) {
        this.characterSkills = characterSkills;
    }


    public Set<CharacterClass> getCharacterClasses() {
        return characterClasses;
    }


    public void setCharacterClasses(Set<CharacterClass> characterClasses) {
        this.characterClasses = characterClasses;
    }


    public Set<BaseCharacterAttribute> getBaseCharacterAttribute() {
        return baseCharacterAttribute;
    }


    public void setBaseCharacterAttribute(Set<BaseCharacterAttribute> baseCharacterAttribute) {
        this.baseCharacterAttribute = baseCharacterAttribute;
        for (BaseCharacterAttribute attribute : baseCharacterAttribute) {
            cachedAttributes.put(attribute.getName(), attribute.getLevel());
        }
    }


    public void addBaseCharacterAttribute(BaseCharacterAttribute attribute) {
        this.baseCharacterAttribute.add(attribute);
        if (cachedAttributes.containsKey(attribute.getName())) {
            cachedAttributes.put(attribute.getName(), cachedAttributes.get(attribute.getName()) + attribute.getLevel());
        } else {
            cachedAttributes.put(attribute.getName(), attribute.getLevel());
        }
    }


    public CharacterClass getCharacterClass(ClassDefinition configClass) {
        for (CharacterClass characterClass : characterClasses) {
            if (configClass.getName().equalsIgnoreCase(characterClass.getName())) {
                return characterClass;
            }
        }
        return null;
    }


    public CharacterSkill getCharacterSkill(ISkill skill) {
        for (CharacterSkill characterSkill : characterSkills) {
            if (characterSkill.getCatalogId().equalsIgnoreCase(skill.getId())) {
                return characterSkill;
            }
        }
        return null;
    }


    public List<EquipedSlot> getInventoryEquipSlotOrder() {
        return inventoryEquipSlotOrder;
    }


    public void setInventoryEquipSlotOrder(List<EquipedSlot> inventoryEquipSlotOrder) {
        this.inventoryEquipSlotOrder = inventoryEquipSlotOrder;
    }


    public Double getHealthScale() {
        return healthScale;
    }


    public void setHealthScale(Double healthScale) {
        this.healthScale = healthScale;
    }


    public Boolean getMarkedForRemoval() {
        return markedForRemoval;
    }


    public void setMarkedForRemoval(Boolean markedForRemoval) {
        this.markedForRemoval = markedForRemoval;
    }


    public int getAttributePointsSpent() {
        return attributePointsSpent;
    }


    public void setAttributePointsSpent(Integer attributePointsSpent) {
        this.attributePointsSpent = attributePointsSpent;
    }


    public Map<String, Set<DateKeyPair>> getUniqueSkillpoints() {
        return uniqueSkillpoints;
    }


    public void setUniqueSkillpoints(Map<String, Set<DateKeyPair>> uniqueSkillpoints) {
        this.uniqueSkillpoints = uniqueSkillpoints;
    }

    public void postLoad() {
        for (BaseCharacterAttribute characterAttribute : baseCharacterAttribute) {
            cachedAttributes.put(characterAttribute.getName(), characterAttribute.getLevel());
        }
    }


    public String[][] getSpellbookPages() {
        return spellBookpages;
    }


    public void setSpellbookPages(String[][] spellbookPages) {
        this.spellBookpages = spellbookPages;
    }


    public String toString() {
        return "CharacterBaseImpl{" + "characterId=" + characterId + ", uuid=" + uuid + ", name='" + name + '\'' + '}';
    }
}
