

package cz.neumimto.rpg.common.persistance.model;

import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.model.*;
import cz.neumimto.rpg.common.skills.ISkill;

import java.util.*;

public class CharacterBaseImpl extends TimestampEntityImpl implements CharacterBase {

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

    @Override
    public Map<String, Integer> getAttributes() {
        return cachedAttributes;
    }

    @Override
    public Long getId() {
        return characterId;
    }

    @Override
    public void setId(Long id) {
        this.characterId = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLastKnownPlayerName() {
        return lastKnownPlayerName;
    }

    @Override
    public void setLastKnownPlayerName(String lastKnownPlayerName) {
        this.lastKnownPlayerName = lastKnownPlayerName;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public Boolean canResetSkills() {
        return canResetSkills;
    }

    @Override
    public void setCanResetSkills(boolean canResetSkills) {
        this.canResetSkills = canResetSkills;
    }

    @Override
    public Date getLastReset() {
        return lastReset;
    }

    @Override
    public void setLastReset(Date lastReset) {
        this.lastReset = lastReset;
    }

    @Override
    public Integer getX() {
        return X;
    }

    @Override
    public void setX(Integer x) {
        X = x;
    }

    @Override
    public Integer getY() {
        return Y;
    }

    @Override
    public void setY(Integer y) {
        Y = y;
    }

    @Override
    public Integer getZ() {
        return Z;
    }

    @Override
    public void setZ(Integer z) {
        Z = z;
    }

    @Override
    public String getWorld() {
        return world;
    }

    @Override
    public void setWorld(String world) {
        this.world = world;
    }

    @Override
    public Integer getAttributePoints() {
        return attributePoints;
    }

    @Override
    public void setAttributePoints(Integer attributePoints) {
        this.attributePoints = attributePoints;
    }

    @Override
    public Set<CharacterSkill> getCharacterSkills() {
        return characterSkills;
    }

    @Override
    public void setCharacterSkills(Set<CharacterSkill> characterSkills) {
        this.characterSkills = characterSkills;
    }

    @Override
    public Set<CharacterClass> getCharacterClasses() {
        return characterClasses;
    }

    @Override
    public void setCharacterClasses(Set<CharacterClass> characterClasses) {
        this.characterClasses = characterClasses;
    }

    @Override
    public Set<BaseCharacterAttribute> getBaseCharacterAttribute() {
        return baseCharacterAttribute;
    }

    @Override
    public void setBaseCharacterAttribute(Set<BaseCharacterAttribute> baseCharacterAttribute) {
        this.baseCharacterAttribute = baseCharacterAttribute;
        for (BaseCharacterAttribute attribute : baseCharacterAttribute) {
            cachedAttributes.put(attribute.getName(), attribute.getLevel());
        }
    }

    @Override
    public void addBaseCharacterAttribute(BaseCharacterAttribute attribute) {
        this.baseCharacterAttribute.add(attribute);
        if (cachedAttributes.containsKey(attribute.getName())) {
            cachedAttributes.put(attribute.getName(), cachedAttributes.get(attribute.getName()) + attribute.getLevel());
        } else {
            cachedAttributes.put(attribute.getName(), attribute.getLevel());
        }
    }

    @Override
    public CharacterClass getCharacterClass(ClassDefinition configClass) {
        for (CharacterClass characterClass : characterClasses) {
            if (configClass.getName().equalsIgnoreCase(characterClass.getName())) {
                return characterClass;
            }
        }
        return null;
    }

    @Override
    public CharacterSkill getCharacterSkill(ISkill skill) {
        for (CharacterSkill characterSkill : characterSkills) {
            if (characterSkill.getCatalogId().equalsIgnoreCase(skill.getId())) {
                return characterSkill;
            }
        }
        return null;
    }

    @Override
    public List<EquipedSlot> getInventoryEquipSlotOrder() {
        return inventoryEquipSlotOrder;
    }

    @Override
    public void setInventoryEquipSlotOrder(List<EquipedSlot> inventoryEquipSlotOrder) {
        this.inventoryEquipSlotOrder = inventoryEquipSlotOrder;
    }

    @Override
    public Double getHealthScale() {
        return healthScale;
    }

    @Override
    public void setHealthScale(Double healthScale) {
        this.healthScale = healthScale;
    }

    @Override
    public Boolean getMarkedForRemoval() {
        return markedForRemoval;
    }

    @Override
    public void setMarkedForRemoval(Boolean markedForRemoval) {
        this.markedForRemoval = markedForRemoval;
    }

    @Override
    public int getAttributePointsSpent() {
        return attributePointsSpent;
    }

    @Override
    public void setAttributePointsSpent(Integer attributePointsSpent) {
        this.attributePointsSpent = attributePointsSpent;
    }

    @Override
    public Map<String, Set<DateKeyPair>> getUniqueSkillpoints() {
        return uniqueSkillpoints;
    }

    @Override
    public void setUniqueSkillpoints(Map<String, Set<DateKeyPair>> uniqueSkillpoints) {
        this.uniqueSkillpoints = uniqueSkillpoints;
    }

    public void postLoad() {
        for (BaseCharacterAttribute characterAttribute : baseCharacterAttribute) {
            cachedAttributes.put(characterAttribute.getName(), characterAttribute.getLevel());
        }
    }

    @Override
    public String[][] getSpellbookPages() {
        return spellBookpages;
    }

    @Override
    public void setSpellbookPages(String[][] spellbookPages) {
        this.spellBookpages = spellbookPages;
    }

    @Override
    public String toString() {
        return "CharacterBaseImpl{" +
                "characterId=" + characterId +
                ", uuid=" + uuid +
                ", name='" + name + '\'' +
                '}';
    }
}
