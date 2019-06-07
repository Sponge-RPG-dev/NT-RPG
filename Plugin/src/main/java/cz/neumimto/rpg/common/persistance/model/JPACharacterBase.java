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

package cz.neumimto.rpg.common.persistance.model;

import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.persistance.model.*;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.common.persistance.converters.EquipedSlot2Json;
import cz.neumimto.rpg.common.persistance.converters.UUID2String;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

/**
 * Created by NeumimTo on 27.1.2015.
 */

@Entity
@Table(name = "rpg_character_base",
        indexes = {@Index(columnList = "uuid")})
public class JPACharacterBase extends JPATimestampEntity implements CharacterBase {

    @Id
    @GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "character_id")
    private Long characterId;
    //todo locking
    //@Version
    private Long version;

    @Convert(converter = UUID2String.class)
    private UUID uuid;

    @Column(length = 40)
    private String name;

    private String info;

    @Column(name = "used_attribute_points")
    private Integer usedAttributePoints;

    @Column(name = "attribute_points")
    private Integer attributePoints;

    @Column(name = "can_reset_skills")
    private Boolean canResetskills;

    @Column(name = "guild_id")
    private Long guildid;

    @Column(name = "health_scale")
    private Double healthScale;

    @Column(name = "last_known_player_name", length = 16)
    private String lastKnownPlayerName;

    @Column(name = "last_reset_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastReset;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "characterBase")
    private Set<CharacterSkill> characterSkills = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "characterBase")
    private Set<CharacterClass> characterClasses = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "characterBase")
    @Access(AccessType.FIELD)
    private Set<BaseCharacterAttribute> baseCharacterAttribute = new HashSet<>();

    @Transient
    private Map<String, Integer> cachedAttributes = new HashMap<>();

    @Convert(converter = EquipedSlot2Json.class)
    @Column(name = "inventory_equip_slot_order", columnDefinition = "TEXT")
    private List<EquipedSlot> inventoryEquipSlotOrder = new ArrayList<>();

    @Column(name = "marked_for_removal")
    private Boolean markedForRemoval;

    private Integer X;

    private Integer Y;

    private Integer Z;

    private String world;

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
    public Long getGuildid() {
        return guildid;
    }

    @Override
    public void setGuildid(Long guildid) {
        this.guildid = guildid;
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
    public int getUsedAttributePoints() {
        return usedAttributePoints;
    }

    @Override
    public void setUsedAttributePoints(int usedAttributePoints) {
        this.usedAttributePoints = usedAttributePoints;
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
    public long getVersion() {
        return version;
    }

    @Override
    public void setVersion(long version) {
        this.version = version;
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
}
