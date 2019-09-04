/*
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.neumimto.rpg.persistence.model;

import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.persistance.model.*;
import cz.neumimto.rpg.api.skills.ISkill;

import java.util.*;

/**
 * Created by NeumimTo on 27.1.2015.
 */
public class CharacterBaseImpl extends TimestampEntityImpl implements CharacterBase {

    private Long characterId;
    private UUID uuid;
    private String name;
    private String info;
    private Integer attributePoints;
    private Boolean canResetskills;
    private Double healthScale;
    private String lastKnownPlayerName;
    private Date lastReset;
    private Set<CharacterSkill> characterSkills = new HashSet<>();
    private Set<CharacterClass> characterClasses = new HashSet<>();
    private Set<BaseCharacterAttribute> baseCharacterAttribute = new HashSet<>();
    private List<EquipedSlot> inventoryEquipSlotOrder = new ArrayList<>();
    private Boolean markedForRemoval;
    private Integer attributePointsSpent;
    private Integer X;
    private Integer Y;
    private Integer Z;
    private String world;

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
    public String getInfo() {
        return info;
    }

    @Override
    public void setInfo(String info) {
        this.info = info;
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
    public Boolean isCanResetskills() {
        return canResetskills;
    }

    @Override
    public void setCanResetskills(boolean canResetskills) {
        this.canResetskills = canResetskills;
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
    public Integer getAttributePointsSpent() {
        return attributePointsSpent;
    }

    @Override
    public void setAttributePointsSpent(Integer attributePointsSpent) {
        this.attributePointsSpent = attributePointsSpent;
    }


    public void postLoad() {
        for (BaseCharacterAttribute characterAttribute : baseCharacterAttribute) {
            cachedAttributes.put(characterAttribute.getName(), characterAttribute.getLevel());
        }
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
