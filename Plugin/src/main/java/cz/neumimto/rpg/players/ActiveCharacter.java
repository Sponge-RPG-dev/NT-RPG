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

import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.inventory.Armor;
import cz.neumimto.rpg.inventory.HotbarObject;
import cz.neumimto.rpg.inventory.Weapon;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.players.groups.Guild;
import cz.neumimto.rpg.players.groups.PlayerGroup;
import cz.neumimto.rpg.players.groups.Race;
import cz.neumimto.rpg.players.parties.Party;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.SkillData;
import cz.neumimto.rpg.skills.StartingPoint;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tristate;

import java.lang.ref.WeakReference;
import java.util.*;


/**
 * Created by NeumimTo on 26.12.2014.
 */

public class ActiveCharacter implements IActiveCharacter {
    private transient float[] characterProperties;
    private transient float[] characterPropertiesLevel;
    private transient boolean invulnerable;
    private IReservable mana = new Mana(this);
    private Health health = new Health(this);
    private transient Player pl;
    private transient Map<String, IEffectContainer<Object, IEffect<Object>>> effects = new HashMap<>();
    private transient Click click = new Click();
    private transient Set<ItemType> allowedArmorIds = new HashSet<>();
    private transient Map<ItemType, Double> allowedWeapons = new HashMap<>();
    private transient Party party;
    private Map<String, ExtendedSkillInfo> skills = new HashMap<>();
    private Race race = Race.Default;
    private Guild guild;
    private transient Set<ExtendedNClass> classes = new HashSet<>();
    private transient ExtendedNClass primary;
    private transient Weapon mainHand = Weapon.EmptyHand;
    private transient Weapon offHand = Weapon.EmptyHand;
    private CharacterBase base;
    private transient boolean silenced = false;
    private transient boolean isusingguimod;
    private transient WeakReference<Party> pendingPartyInvite = new WeakReference<Party>(null);
    private transient double weaponDamage;
    private transient double armorvalue;
    private transient DamageType preferedDamageType = null;
    private transient HotbarObject[] hotbar = new HotbarObject[9];
    private transient int socketing;
    private transient Map<String, Integer> transientAttributes = new HashMap<>();
    private transient boolean openedinv = false;
    private transient List<Integer> slotsToReinitialize;
    private transient Map<EquipmentType, Armor> equipedArmor;
    private transient int selected;
    public ActiveCharacter(Player pl, CharacterBase base) {
        this.pl = pl;
        characterProperties = new float[PropertyService.LAST_ID];
        characterPropertiesLevel = new float[PropertyService.LAST_ID];
        equipedArmor = new HashMap<>();
        ExtendedNClass cl = new ExtendedNClass();
        cl.setPrimary(true);
        cl.setConfigClass(ConfigClass.Default);
        cl.setExperiences(0);
        this.base = base;
        classes.add(cl);
        slotsToReinitialize = new ArrayList<>();
    }

    @Override
    public int getCurrentRune() {
        return socketing;
    }

    @Override
    public void setCurrentRune(int is) {
        socketing = is;
    }

    @Override
    public HotbarObject[] getHotbar() {
        return hotbar;
    }

    @Override
    public void setHotbarSlot(int i, HotbarObject o) {
        hotbar[i] = o;
    }

    public boolean isSilenced() {
        return silenced;
    }

    public void setSilenced(boolean silenced) {
        this.silenced = silenced;
    }

    @Override
    public String getName() {
        return getCharacterBase().getName();
    }

    @Override
    public boolean isStub() {
        return false;
    }

    @Override
    public float[] getCharacterProperties() {
        return characterProperties;
    }

    @Override
    public void setProperties(float[] arr) {
        characterProperties = arr;
    }

    @Override
    public float getProperty(int index) {
        return characterProperties[index] + characterPropertiesLevel[index] * getPrimaryClass().getLevel();
    }

    @Override
    public void setProperty(int index, float value) {
        characterProperties[index] = value;
    }

    @Override
    public float[] getCharacterLevelProperties() {
        return characterPropertiesLevel;
    }

    @Override
    public void setCharacterLevelProperties(float[] arr) {

    }

    @Override
    public void updateLastKnownLocation(int x, int y, int z, String name) {
        getCharacterBase().setX(x);
        getCharacterBase().setY(y);
        getCharacterBase().setZ(z);
        getCharacterBase().setWorld(name);
    }

    @Override
    public boolean isInvulnerable() {
        return invulnerable;
    }

    @Override
    public void setInvulnerable(boolean b) {
        this.invulnerable = b;
    }


    @Override
    public void setCharacterLevelProperty(int index, float value) {
        characterPropertiesLevel[index] = value;
    }

    @Override
    public float getCharacterPropertyWithoutLevel(int index) {
        return characterProperties[index];
    }

    @Override
    public double getMaxMana() {
        return getProperty(DefaultProperties.max_mana);
    }

    @Override
    public void setMaxMana(float mana) {
        setProperty(DefaultProperties.max_mana, mana);
    }

    @Override
    public void setMaxHealth(float maxHealth) {
        setProperty(DefaultProperties.max_health, maxHealth);
    }

    @Override
    public void setHealth(float mana) {
        setProperty(DefaultProperties.max_mana, mana);
    }

    @Override
    public IReservable getMana() {
        return mana;
    }

    @Override
    public void setMana(IReservable mana) {
        this.mana = mana;
    }

    @Override
    public Health getHealth() {
        return health;
    }

    @Override
    public void setHealth(Health health) {
        this.health = health;
    }


    @Override
    public double getHp() {
        return getHealth().getValue();
    }

    @Override
    public void setHp(double d) {
        setHealth((float) d);
    }

    @Override
    public Player getEntity() {
        return getPlayer();
    }

    @Override
    public Map<EquipmentType, Armor> getEquipedArmor() {
        return equipedArmor;
    }


    @Override
    public Map<String, IEffectContainer<Object, IEffect<Object>>> getEffectMap() {
        return effects;
    }

    @Override
    public double getExperiencs() {
        return getPrimaryClass().getExperiences();
    }

    @Override
    public void addExperiences(double exp, ExperienceSource source) {
        for (ExtendedNClass nClass : classes) {
            if (nClass.getConfigClass().hasExperienceSource(source)) {
                CharacterClass c = getCharacterBase().getCharacterClass(nClass.getConfigClass());
                Double nClass1 = c.getExperiences() == null ? 0 : c.getExperiences();
                if (nClass1 == null) {
                    c.setExperiences(exp);
                }
            }
        }
    }

    @Override
    public Player getPlayer() {
        return pl;
    }

    @Override
    public void setPlayer(Player pl) {
        this.pl = pl;
    }

    @Override
    public void resetRightClicks() {
        click.setTimes(0);
        click.setLastTime(0);
    }

    @Override
    public int getAttributePoints() {
        return base.getAttributePoints();
    }

    @Override
    public void setAttributePoints(int attributePoints) {
        this.base.setAttributePoints(attributePoints);
    }

    @Override
    public Map<String, Integer> getTransientAttributes() {
        return transientAttributes;
    }

    @Override
    public Integer getAttributeValue(String name) {
        return base.getAttributes().get(name) + getTransientAttributes().get(name);
    }

    @Override
    public ExtendedNClass getPrimaryClass() {
        return primary;
    }

    @Override
    public void setPrimaryClass(ConfigClass clazz) {
        setClass(clazz, 0);
    }

    public void setClass(ConfigClass nclass, int slot) {
        if (primary != null) {
            //      fixPropertyValues(getPrimaryClass().getConfigClass().getPropBonus(), -1);
            //      fixPropertyLevelValues(getPrimaryClass().getConfigClass().getPropLevelBonus(), -1);
            skills.clear();
        }

        //classes.clear();

        if (slot == 0) {
            primary = new ExtendedNClass();
            primary.setConfigClass(nclass);
            primary.setPrimary(true);
            classes.add(primary);
        }
        CharacterClass cc = getCharacterBase().getCharacterClass(nclass);
        if (cc == null) {
            cc = new CharacterClass();
            cc.setCharacterBase(getCharacterBase());
            cc.setName(nclass.getName());
        }
        Double aDouble = cc.getExperiences();
        if (aDouble == null) {
            primary.setExperiences(0D);
            primary.setLevel(0);
            if (slot == 0) {
                cc.setSkillPoints(PluginConfig.SKILLPOINTS_ON_START);
            }
            cc.setExperiences(0D);
            getCharacterBase().getCharacterClasses().add(cc);
        } else {
            //  primary.setStacks(getCharacterBase().getStacks());
            primary.setExperiences(aDouble);
        }
        base.setPrimaryClass(nclass.getName());
        //   fixPropertyValues(nclass.getPropBonus(), 1);
        //  fixPropertyLevelValues(getPrimaryClass().getConfigClass().getPropLevelBonus(), 1);
        SkillData skillData = nclass.getSkillTree().getSkills().get(StartingPoint.name);
        if (skillData != null) {
            ExtendedSkillInfo info = new ExtendedSkillInfo();
            info.setLevel(0);
            info.setSkill(null);
            info.setSkillData(skillData);
        }
        updateItemRestrictions();
    }

    @Override
    public Map<String, Long> getCooldowns() {
        return getCharacterBase().getCharacterCooldowns();
    }

    @Override
    public boolean hasCooldown(String thing) {
        return getCharacterBase().getCharacterCooldowns().containsKey(thing);
    }

    private void mergeWeapons(PlayerGroup g) {
        for (Map.Entry<ItemType, Double> entries : g.getWeapons().entrySet()) {
            if (getBaseWeaponDamage(entries.getKey()) < entries.getValue()) {
                allowedWeapons.put(entries.getKey(), entries.getValue());
            }
        }
    }

    @Override
    public double getBaseWeaponDamage(ItemType id) {
        Double d = getAllowedWeapons().get(id);
        if (d == null)
            return 0;
        return d;
    }

    public IActiveCharacter updateItemRestrictions() {
        allowedWeapons.clear();
        allowedWeapons.putAll(getRace().getWeapons());
        //   mergeWeapons(getGuild());
        mergeWeapons(getPrimaryClass().getConfigClass());
        mergeWeapons(getRace());
        allowedArmorIds.clear();
        allowedArmorIds.addAll(getRace().getAllowedArmor());
        //   allowedArmorIds.addAll(getGuild().getAllowedArmor());
        allowedArmorIds.addAll(getPrimaryClass().getConfigClass().getAllowedArmor());
        return this;
    }

    @Override
    public Set<ItemType> getAllowedArmor() {
        return allowedArmorIds;
    }

    @Override
    public boolean canWear(ItemStack armor) {
        return getAllowedArmor().contains(armor.getItem());
    }

    @Override
    public boolean canUse(ItemType weaponItemType) {
        return getAllowedWeapons().containsKey(weaponItemType);
    }


    @Override
    public Map<ItemType, Double> getAllowedWeapons() {
        return allowedWeapons;
    }

    @Override
    public Set<ExtendedNClass> getClasses() {
        return classes;
    }

    @Override
    public Race getRace() {
        return race;
    }

    @Override
    public void setRace(Race race) {
        if (this.race != Race.Default) {
            removePermissions(race.getPermissions());
        }
        this.race = race;
        getCharacterBase().setRace(race.getName());
        addPermissions(race.getPermissions());
    }

    @Override
    public Guild getGuild() {
        return guild;
    }

    @Override
    public void setGuild(Guild guild) {
        if (this.guild != Guild.Default) {
            //     fixPropertyValues(this.guild.getPropBonus(), -1);
            //       removePermissions(guild.getPermissions());
        }
        this.guild = guild;
        //  fixPropertyValues(guild.getPropBonus(), 1);
        //   addPermissions(guild.getPermissions());*/
    }

    public void addPermissions(Collection<String> perms) {
        SubjectData subjectData = pl.getTransientSubjectData();
        perms.stream().forEach(s -> {
            subjectData.setPermission(SubjectData.GLOBAL_CONTEXT, s, Tristate.TRUE);
        });
    }

    public void removePermissions(Collection<String> perms) {
        SubjectData subjectData = pl.getTransientSubjectData();
        perms.stream().forEach(s -> {
            subjectData.setPermission(SubjectData.GLOBAL_CONTEXT, s, Tristate.FALSE);
        });
    }

    @Override
    public CharacterBase getCharacterBase() {
        return base;
    }

    @Override
    public void sendMessage(String message) {
        pl.sendMessage(Text.of(message));
    }

    @Override
    public Map<String, ExtendedSkillInfo> getSkills() {
        return Collections.unmodifiableMap(skills); //lets use wrapper class instaed of guava's immutable
    }

    @Override
    public void addSkill(String name, ExtendedSkillInfo info) {
        skills.put(name.toLowerCase(), info);
    }

    @Override
    public ExtendedSkillInfo getSkill(String skillName) {
        return skills.get(skillName.toLowerCase());
    }

    @Override
    public void getRemoveAllSkills() {
        getCharacterBase().getCharacterSkills().clear();
        skills.clear();
    }

    @Override
    public ExtendedSkillInfo getSkillInfo(ISkill skill) {
        return skills.get(skill.getName().toLowerCase());
    }

    @Override
    public ExtendedSkillInfo getSkillInfo(String s) {
        return skills.get(s.toLowerCase());
    }

    @Override
    public boolean hasSkill(String name) {
        return skills.containsKey(name.toLowerCase());
    }

    @Override
    public int getLevel() {
        return getPrimaryClass().getLevel();
    }

    @Override
    public ConfigClass getNClass(int index) {
        return getPrimaryClass().getConfigClass();
    }

    @Override
    public Party getParty() {
        return party;
    }

    @Override
    public void setParty(Party party) {
        if (this.party != null) {
            party.removePlayer(this);
        }
        this.party = party;
    }

    @Override
    public boolean hasParty() {
        return getParty() != null && getParty().getPlayers().size() > 1;
    }

    @Override
    public boolean isInPartyWith(IActiveCharacter character) {
        return (character.hasParty() && hasParty() && character.getParty() == character.getParty());
    }

    @Override
    public double getWeaponDamage() {
        return weaponDamage;
    }

    @Override
    public void setWeaponDamage(double damage) {
        weaponDamage = damage;
    }

    @Override
    public double getArmorValue() {
        return armorvalue;
    }

    @Override
    public void setArmorValue(double value) {
        armorvalue = value;
    }

    @Override
    public boolean hasPreferedDamageType() {
        return preferedDamageType != null;
    }

    @Override
    public DamageType getDamageType() {
        return preferedDamageType;
    }

    @Override
    public void setDamageType(DamageType damageType) {
        this.preferedDamageType = damageType;
    }

    @Override
    public boolean isUsingGuiMod() {
        return isusingguimod;
    }

    @Override
    public void setUsingGuiMod(boolean b) {
        isusingguimod = b;
    }

    @Override
    public boolean isPartyLeader() {
        return hasParty() && getParty().getLeader() == this;
    }

    @Override
    public Party getPendingPartyInvite() {
        return pendingPartyInvite.get();
    }

    @Override
    public void setPendingPartyInvite(Party party) {
        pendingPartyInvite = new WeakReference<Party>(party);
    }

    @Override
    public Weapon getMainHand() {
        return mainHand;
    }

    @Override
    public void setMainHand(Weapon mainHand) {
        this.mainHand = mainHand;
    }

    @Override
    public Weapon getOffHand() {
        return offHand;
    }

    @Override
    public void setOffHand(Weapon offHand) {
        this.offHand = offHand;
    }

    @Override
    public void setOpenInventory(boolean b) {
        this.openedinv = b;
    }

    @Override
    public boolean hasOpenInventory() {
        return openedinv;
    }

    @Override
    public MessageType getPreferedMessageType() {
        return getCharacterBase().getMessageType();
    }

    @Override
    public void setPreferedMessageType(MessageType type) {
        this.getCharacterBase().setMessageType(type);
    }

    @Override
    public boolean hasClass(PlayerGroup configClass) {
        for (ExtendedNClass aClass : classes) {
            if (aClass.getConfigClass() == configClass) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Integer> getSlotsToReinitialize() {
        return slotsToReinitialize;
    }

    @Override
    public void setSlotsToReinitialize(List<Integer> slotsToReinitialize) {
        this.slotsToReinitialize = slotsToReinitialize;
    }

    @Override
    public int getSelectedHotbarSlot() {
        return selected;
    }

    @Override
    public void updateSelectedHotbarSlot() {
        Hotbar hotbar = getPlayer().getInventory().query(Hotbar.class);
        selected = hotbar.getSelectedSlotIndex();
    }

    @Override
    public int hashCode() {
        return getPlayer().getUniqueId().hashCode() * 37;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveCharacter that = (ActiveCharacter) o;
        return that.getCharacterBase().getId().equals(this.getCharacterBase().getId());
    }
}
