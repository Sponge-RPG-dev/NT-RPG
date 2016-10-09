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

import cz.neumimto.rpg.TimestampEntity;
import cz.neumimto.rpg.persistance.converters.MapSL2Json;
import cz.neumimto.rpg.persistance.converters.UUID2String;
import cz.neumimto.rpg.persistance.model.BaseCharacterAttribute;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.persistance.model.CharacterSkill;
import cz.neumimto.rpg.players.groups.NClass;
import cz.neumimto.rpg.skills.ISkill;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_id")
    private Long characterId;
    //todo locking
    //@Version
    private long version;

    @Convert(converter = UUID2String.class)
    private UUID uuid;

    @Column(length = 40)
    private String name;

    private String info;

    private int usedAttributePoints;

    protected int attributePoints;

    private boolean canResetskills;

    private String race, primaryClass;

    //TODO ehcache!
    private int guildid;

    @Column(length = 16)
    private String lastKnownPlayerName;

    @Column(name = "last_reset_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastReset;

    @Column(columnDefinition="TEXT")
    @Convert(converter = MapSL2Json.class)
    private Map<String,Long> characterCooldowns;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL,mappedBy = "characterBase")
    private Set<CharacterSkill> characterSkills = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL,mappedBy = "characterBase")
    private Set<CharacterClass> characterClasses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL,mappedBy = "characterBase")
    @Access(AccessType.FIELD)
    private Set<BaseCharacterAttribute> baseCharacterAttribute = new HashSet<>();

    @Transient
    private Map<String,Integer> cachedAttributes = new HashMap<>();

    private int X;

    private int Y;

    private int Z;

    private String world;

    public Map<String,Integer> getAttributes() {
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

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public int getGuildid() {
        return guildid;
    }

    public void setGuildid(int guildid) {
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

    public boolean isCanResetskills() {
        return canResetskills;
    }

    public void setCanResetskills(boolean canResetskills) {
        this.canResetskills = canResetskills;
    }

    public String getPrimaryClass() {
        return primaryClass;
    }

    public void setPrimaryClass(String primaryClass) {
        this.primaryClass = primaryClass;
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

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public int getZ() {
        return Z;
    }

    public void setZ(int z) {
        Z = z;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }


    public int getAttributePoints() {
        return attributePoints;
    }

    public void setAttributePoints(int attributePoints) {
        this.attributePoints = attributePoints;
    }

    public Map<String,Long> getCharacterCooldowns() {
        return characterCooldowns;
    }

    public void setCharacterCooldowns(Map<String,Long> characterCooldowns) {
        this.characterCooldowns = characterCooldowns;
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
            cachedAttributes.put(attribute.getName(),attribute.getLevel());
        }
    }

    public CharacterClass getCharacterClass(NClass nClass) {
        for (CharacterClass characterClass : characterClasses) {
            if (characterClass.getName().equalsIgnoreCase(nClass.getName())) {
                return characterClass;
            }
        }
        return null;
    }

    public CharacterSkill getCharacterSkill(ISkill skill) {
        for (CharacterSkill characterSkill : characterSkills) {
            if (characterSkill.getName().equalsIgnoreCase(skill.getName())) {
                return characterSkill;
            }
        }
        return null;
    }
}
