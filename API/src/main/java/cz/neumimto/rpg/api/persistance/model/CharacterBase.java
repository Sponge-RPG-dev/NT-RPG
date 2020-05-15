package cz.neumimto.rpg.api.persistance.model;

import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.skills.ISkill;

import java.util.*;

public interface CharacterBase extends TimestampEntity {
    Map<String, Integer> getAttributes();

    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    String getLastKnownPlayerName();

    void setLastKnownPlayerName(String lastKnownPlayerName);

    UUID getUuid();

    void setUuid(UUID uuid);

    Boolean canResetSkills();

    void setCanResetSkills(boolean canResetSkills);

    Date getLastReset();

    void setLastReset(Date lastReset);

    Integer getX();

    void setX(Integer x);

    Integer getY();

    void setY(Integer y);

    Integer getZ();

    void setZ(Integer z);

    String getWorld();

    void setWorld(String world);

    Integer getAttributePoints();

    void setAttributePoints(Integer attributePoints);

    Set<CharacterSkill> getCharacterSkills();

    void setCharacterSkills(Set<CharacterSkill> characterSkills);

    Set<CharacterClass> getCharacterClasses();

    void setCharacterClasses(Set<CharacterClass> characterClasses);

    Set<BaseCharacterAttribute> getBaseCharacterAttribute();

    void setBaseCharacterAttribute(Set<BaseCharacterAttribute> baseCharacterAttribute);

    void addBaseCharacterAttribute(BaseCharacterAttribute baseCharacterAttribute);

    CharacterClass getCharacterClass(ClassDefinition configClass);

    CharacterSkill getCharacterSkill(ISkill skill);

    List<EquipedSlot> getInventoryEquipSlotOrder();

    void setInventoryEquipSlotOrder(List<EquipedSlot> inventoryEquipSlotOrder);

    Double getHealthScale();

    void setHealthScale(Double healthScale);

    Boolean getMarkedForRemoval();

    void setMarkedForRemoval(Boolean markedForRemoval);

    int getAttributePointsSpent();

    void setAttributePointsSpent(Integer attributePointsSpent);

    Map<String, Set<DateKeyPair>> getUniqueSkillpoints();

    void setUniqueSkillpoints(Map<String, Set<DateKeyPair>> uniqueSkillpoints);
}
