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

import cz.neumimto.rpg.TextHelper;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.effects.EffectSourceType;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.inventory.ConfigRPGItemType;
import cz.neumimto.rpg.inventory.RPGItemType;
import cz.neumimto.rpg.inventory.items.types.CustomItem;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.players.groups.Guild;
import cz.neumimto.rpg.players.groups.PlayerGroup;
import cz.neumimto.rpg.players.groups.Race;
import cz.neumimto.rpg.players.parties.Party;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.skills.*;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by NeumimTo on 26.12.2014.
 */

public class ActiveCharacter implements IActiveCharacter {

	private transient Player pl;
	private CharacterBase base;

	private transient float[] characterProperties;
	private transient float[] characterPropertiesLevel;

	private transient boolean invulnerable;
	private transient boolean silenced = false;
	private transient boolean isusingguimod;

	private IReservable mana = new Mana(this);
	private IReservable health = new Health(this);

	private Race race = Race.Default;
	private Guild guild;

	private transient Party party;

	private transient Map<String, IEffectContainer<Object, IEffect<Object>>> effects = new HashMap<>();
	private Map<String, ExtendedSkillInfo> skills = new HashMap<>();
	private transient Set<ExtendedNClass> classes = new HashSet<>();

	private transient ExtendedNClass primary;

	private transient Click click = new Click();
	private transient Set<RPGItemType> allowedArmorIds = new HashSet<>();
	private transient Map<ItemType, RPGItemWrapper> allowedWeapons = new HashMap<>();
	private transient Map<EntityType, Double> projectileDamage = new HashMap<>();


	private transient WeakReference<Party> pendingPartyInvite = new WeakReference<Party>(null);
	private transient double weaponDamage;
	private transient double armorvalue;

	private transient DamageType preferedDamageType = null;
	private transient Map<String, Integer> transientAttributes = new HashMap<>();

	private transient List<Integer> slotsToReinitialize;
	private transient Map<Integer, CustomItem> equipedArmor;
	private Set<Integer> denySlotInteractionArr;

	private Set<SkillTreeSpecialization> specs = new HashSet<>();

	private transient Map<String, SkillTreeViewModel> skillTreeViewLocation;
	private CustomItem offHand;
	private int mainHandSlotId;
	private CustomItem mainHand;

	public ActiveCharacter(Player pl, CharacterBase base) {
		this.pl = pl;
		characterProperties = new float[PropertyService.LAST_ID];
		characterPropertiesLevel = new float[PropertyService.LAST_ID];
		equipedArmor = new HashMap<>();
		ExtendedNClass cl = new ExtendedNClass(this);
		cl.setPrimary(true);
		cl.setConfigClass(ConfigClass.Default);
		this.base = base;
		classes.add(cl);
		slotsToReinitialize = new ArrayList<>();
		skillTreeViewLocation = new HashMap<>();
		denySlotInteractionArr = new HashSet<>();
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
		this.characterPropertiesLevel = arr;
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
	public IReservable getHealth() {
		return health;
	}

	@Override
	public void setHealth(IReservable health) {
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
	public Map<Integer, CustomItem> getEquipedInventorySlots() {
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
			primary = new ExtendedNClass(this);
			primary.setConfigClass(nclass);
			primary.setPrimary(true);
			classes.add(primary);
		}
		CharacterClass cc = getCharacterBase().getCharacterClass(nclass);
		if (cc == null) {
			cc = new CharacterClass();
			cc.setCharacterBase(getCharacterBase());
			cc.setName(nclass.getName());
			getCharacterBase().getCharacterClasses().add(cc);
		}
		Double aDouble = cc.getExperiences();
		if (aDouble == null) {
			primary.setExperiences(0D);
			primary.setLevel(0);
			if (slot == 0) {
				cc.setSkillPoints(PluginConfig.SKILLPOINTS_ON_START);
			}
			cc.setExperiences(0D);
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
		mergeWeapons(g.getWeapons());
		for (Map.Entry<EntityType, Double> e : g.getProjectileDamage().entrySet()) {
			if (getBaseProjectileDamage(e.getKey()) < e.getValue()) {
				projectileDamage.put(e.getKey(), e.getValue());
			}
		}
	}

	private void mergeWeapons(Map<ItemType, Set<ConfigRPGItemType>> map) {
		for (Map.Entry<ItemType, Set<ConfigRPGItemType>> toAdd : map.entrySet()) {
			RPGItemWrapper wrapper = allowedWeapons.get(toAdd.getKey());
			if (wrapper == null) {
				wrapper = RPGItemWrapper.createFromSet(toAdd.getValue());
				allowedWeapons.put(toAdd.getKey(), wrapper);
			} else {
				wrapper.addItems(toAdd.getValue());
			}
		}
	}

	@Override
	public double getBaseWeaponDamage(RPGItemType weaponItemType) {
		RPGItemWrapper wrapper = getAllowedWeapons().get(weaponItemType.getItemType());
		if (wrapper == null)
			return 0D;
		for (ConfigRPGItemType configRPGItemType : wrapper.getItems()) {
			if (weaponItemType.getDisplayName() == null) {
				if (configRPGItemType.getRpgItemType().getDisplayName() == null) {
					//todo check if the displayname is reserved
					return wrapper.getDamage(); //null is first, if both null => can use unnamed item
				}
			} else {
				if (weaponItemType.getDisplayName().equalsIgnoreCase(configRPGItemType.getRpgItemType().getDisplayName())) {
					return wrapper.getDamage();
				}
			}
		}
		return 0D;
	}

	@Override
	public double getBaseProjectileDamage(EntityType id) {
		Double d = getProjectileDamages().get(id);
		if (d == null)
			return 0;
		return d;
	}

	public IActiveCharacter updateItemRestrictions() {
		allowedWeapons.clear();

		Map<ItemType, Set<ConfigRPGItemType>> weapons = getRace().getWeapons();
		allowedWeapons.putAll(weapons.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> RPGItemWrapper.createFromSet(e.getValue()))));




		//mergeWeapons(getGuild());
		mergeWeapons(getPrimaryClass().getConfigClass());
		//could be problematic, but its not called too often
		for (ExtendedSkillInfo extendedSkillInfo : getSkills().values()) {
			if (extendedSkillInfo.getSkill().getType() == EffectSourceType.ITEM_ACCESS_SKILL) {
				ItemAccessSkill.ItemAccessSkillData skillData = (ItemAccessSkill.ItemAccessSkillData) extendedSkillInfo.getSkillData();
				Map<Integer, Map<ItemType,Set<ConfigRPGItemType>>> items = skillData.getItems();
				for (Map.Entry<Integer, Map<ItemType, Set<ConfigRPGItemType>>> ent : items.entrySet()) {
					if (ent.getKey() <= getLevel()) {
						mergeWeapons(ent.getValue());
					}
				}

			}
		}
		//mergeWeapons(getRace());
		allowedArmorIds.clear();

		allowedArmorIds.addAll(getRace().getAllowedArmor());

		//allowedArmorIds.addAll(getGuild().getAllowedArmor());

		allowedArmorIds.addAll(getPrimaryClass().getConfigClass().getAllowedArmor());
		
		getProjectileDamages().clear();
		
		getProjectileDamages().putAll(getRace().getProjectileDamage());

		for (ExtendedNClass extendedNClass : getClasses()) {
			ConfigClass configClass = extendedNClass.getConfigClass();
			Map<EntityType, Double> projectileDamage = configClass.getProjectileDamage();
			for (Map.Entry<EntityType, Double> entityType : projectileDamage.entrySet()) {
				Double aDouble = getProjectileDamages().get(entityType.getKey());
				if (aDouble == null) {
					getProjectileDamages().put(entityType.getKey(), entityType.getValue());
				} else if (PluginConfig.WEAPON_MERGE_STRATEGY == 1) {
					getProjectileDamages().put(entityType.getKey(), aDouble + entityType.getValue());
				} else if (PluginConfig.WEAPON_MERGE_STRATEGY == 2) {
					getProjectileDamages().put(entityType.getKey(), Math.max(aDouble, entityType.getValue()));
				}
			}
		}
		
		return this;
	}

	@Override
	public Set<RPGItemType> getAllowedArmor() {
		return allowedArmorIds;
	}

	@Override
	public boolean canWear(RPGItemType armor) {
		return getAllowedArmor().contains(armor);
	}

	@Override
	public boolean canUse(RPGItemType weaponItemType) {
		RPGItemWrapper set = getAllowedWeapons().get(weaponItemType.getItemType());
		return set != null && set.containsItem(weaponItemType);
	}


	@Override
	public Map<ItemType, RPGItemWrapper> getAllowedWeapons() {
		return allowedWeapons;
	}

	@Override
	public Map<EntityType, Double> getProjectileDamages() {
		return projectileDamage;
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
		this.race = race;
		getCharacterBase().setRace(race.getName());
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

	@Override
	public CharacterBase getCharacterBase() {
		return base;
	}

	@Override
	public void sendMessage(String message) {
		pl.sendMessage(TextHelper.parse(message));
	}

	@Override
	public void sendMessage(ChatType chatType, Text message) {
		pl.sendMessage(chatType, Text.of(message));
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
		for (ExtendedNClass aClass : classes) {
			if (aClass.getSlot() == index)
				return aClass.getConfigClass();
		}
		return null;
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
	public boolean isDetached() {
		return getPlayer() == null;
	}


	@Override
	public boolean isFriendlyTo(IActiveCharacter character) {
		if (character == this) return true;
		for (IActiveCharacter iActiveCharacter : getParty().getPlayers()) {
			if (iActiveCharacter == character) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Map<String, SkillTreeViewModel> getSkillTreeViewLocation() {
		return skillTreeViewLocation;
	}

	@Override
	public SkillTreeViewModel getLastTimeInvokedSkillTreeView() {
		for (SkillTreeViewModel skillTreeViewModel : skillTreeViewLocation.values()) {
			if (skillTreeViewModel.isCurrent()) {
				return skillTreeViewModel;
			}
		}
		return null;
	}

	@Override
	public void addSkillTreeSpecialization(SkillTreeSpecialization specialization) {
		this.specs.add(specialization);
	}

	@Override
	public void removeSkillTreeSpecialization(SkillTreeSpecialization specialization) {
		if (hasSkillTreeSpecialization(specialization))
			specs.remove(specialization);
	}

	@Override
	public boolean hasSkillTreeSpecialization(SkillTreeSpecialization specialization) {
		return specs.contains(specialization);
	}

	@Override
	public Set<Integer> getSlotsCannotBeEquiped() {
		return denySlotInteractionArr;
	}

	@Override
	public CustomItem getMainHand() {
		return mainHand;
	}

	@Override
	public void setMainHand(CustomItem customItem, int slot) {
		this.mainHand = customItem;
		this.mainHandSlotId = slot;
	}

	@Override
	public CustomItem getOffHand() {
		return offHand;
	}

	@Override
	public void setOffHand(CustomItem customItem) {
		this.offHand = customItem;
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

	@Override
	public String toString() {
		return "ActiveCharacter{" +
				"uuid=" + pl.getUniqueId() +
				" name=" + getName() +
				'}';
	}
}
