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

    String getInfo();

    void setInfo(String info);

    Long getGuildid();

    void setGuildid(Long guildid);

    String getLastKnownPlayerName();

    void setLastKnownPlayerName(String lastKnownPlayerName);

    UUID getUuid();

    void setUuid(UUID uuid);

    int getUsedAttributePoints();

    void setUsedAttributePoints(int usedAttributePoints);

    Boolean isCanResetskills();

    void setCanResetskills(boolean canResetskills);

    Date getLastReset();

    void setLastReset(Date lastReset);

    long getVersion();

    void setVersion(long version);

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

    CharacterClass getCharacterClass(ClassDefinition configClass);

    CharacterSkill getCharacterSkill(ISkill skill);

    List<EquipedSlot> getInventoryEquipSlotOrder();

    void setInventoryEquipSlotOrder(List<EquipedSlot> inventoryEquipSlotOrder);

    Double getHealthScale();

    void setHealthScale(Double healthScale);

    Boolean getMarkedForRemoval();

    void setMarkedForRemoval(Boolean markedForRemoval);
}
