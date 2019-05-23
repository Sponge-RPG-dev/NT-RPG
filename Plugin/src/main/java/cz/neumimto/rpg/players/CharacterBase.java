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

package cz.neumimto.rpg.players;

import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.persistance.converters.EquipedSlot2Json;
import cz.neumimto.rpg.persistance.converters.UUID2String;
import cz.neumimto.rpg.persistance.model.*;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

/**
 * Created by NeumimTo on 27.1.2015.
 */

@Entity
@Table(name = "rpg_character_base",
		indexes = {@Index(columnList = "uuid")})
public class CharacterBase extends TimestampEntity {

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

	@Enumerated(EnumType.STRING)
	private MessageType messageType;

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

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Long getGuildid() {
		return guildid;
	}

	public void setGuildid(Long guildid) {
		this.guildid = guildid;
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

	public int getUsedAttributePoints() {
		return usedAttributePoints;
	}

	public void setUsedAttributePoints(int usedAttributePoints) {
		this.usedAttributePoints = usedAttributePoints;
	}

	public Boolean isCanResetskills() {
		return canResetskills;
	}

	public void setCanResetskills(boolean canResetskills) {
		this.canResetskills = canResetskills;
	}

	public Date getLastReset() {
		return lastReset;
	}

	public void setLastReset(Date lastReset) {
		this.lastReset = lastReset;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
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

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
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
}
