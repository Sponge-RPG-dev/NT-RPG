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

import cz.neumimto.core.localization.Arg;
import cz.neumimto.core.localization.LocalizableParametrizedText;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.inventory.RpgInventory;
import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.skills.IPlayerSkillHandler;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillTreeSpecialization;
import cz.neumimto.rpg.common.skills.PlayerSkillHandlers;
import cz.neumimto.rpg.effects.EffectSourceType;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.entities.IReservable;
import cz.neumimto.rpg.persistance.model.EquipedSlot;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.players.parties.Party;
import cz.neumimto.rpg.sponge.skills.ItemAccessSkill;
import org.jline.utils.Log;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;


/**
 * Created by NeumimTo on 26.12.2014.
 */

public class ActiveCharacter implements IActiveCharacter {

    private transient UUID pl;
    private CharacterBase base;

    private Map<String, PlayerClassData> classes = new HashMap<>();

    /*
    - Needs to be recalculated on class add/remove
    - Attribute change
    - Player login
    * */
    private transient float[] primaryProperties;

    /*
    - player respawn
    - effect apply/expire
    */
    private transient float[] secondaryProperties;

    private transient boolean invulnerable;
    private transient boolean silenced = false;
    private transient boolean isusingguimod;

    private IReservable mana = new CharacterMana(this);
    private IReservable health = new CharacterHealth(this);

    private transient Party party;

    private transient Map<String, IEffectContainer<Object, IEffect<Object>>> effects = new HashMap<>();
    private IPlayerSkillHandler skills;

    private transient Click click = new Click();
    private transient Set<RpgItemType> allowedArmorIds = new HashSet<>();

    private transient Map<RpgItemType, Double> allowedWeapons = new HashMap<>();

    private transient Map<EntityType, Double> projectileDamage = new HashMap<>();
    private transient Set<RpgItemType> allowedOffHandWeapons = new HashSet<>();
    private Map<String, Long> cooldowns = new HashMap<>();
    private transient WeakReference<Party> pendingPartyInvite = new WeakReference<>(null);
    private transient double weaponDamage;
    private transient double armorvalue;

    private transient DamageType preferedDamageType = null;
    private transient Map<String, Integer> transientAttributes = new HashMap<>();

    private transient List<Integer> slotsToReinitialize;

    private Set<EquipedSlot> denySlotInteractionArr;

    private Set<SkillTreeSpecialization> specs = new HashSet<>();

    private transient Map<String, SkillTreeViewModel> skillTreeViewLocation;
    private transient Map<String, Integer> attributeSession = new HashMap<>();

    private transient Map<Class<?>, RpgInventory> inventory;

    private transient int mainHandSlotId;

    private transient RpgItemStack offHand;
    private transient RpgItemStack mainHand;

    private transient PlayerClassData primaryClass;

    private boolean requiresDamageRecalculation;
    private int lastHotbarSlotInteraction = -1;

    public ActiveCharacter(UUID uuid, CharacterBase base, int propertyCount) {
        this.pl = uuid;
        this.primaryProperties = new float[propertyCount];
        this.secondaryProperties = new float[propertyCount];
        this.base = base;
        this.skills = new PlayerSkillHandlers.SHARED();
        this.slotsToReinitialize = new ArrayList<>();
        this.skillTreeViewLocation = new HashMap<>();
        this.denySlotInteractionArr = new HashSet<>();
        this.inventory = new HashMap<>();
        this.requiresDamageRecalculation = true;
    }

    @Override
    public boolean isSilenced() {
        return silenced;
    }

    public void setSilenced(boolean silenced) {
        this.silenced = silenced;
    }

    @Override
    public Map<Class<?>, RpgInventory> getManagedInventory() {
        return inventory;
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
    public float[] getPrimaryProperties() {
        return primaryProperties;
    }

    @Override
    public float getProperty(int index) {
        return primaryProperties[index] + secondaryProperties[index];
    }

    @Override
    public void setProperty(int index, float value) {
        primaryProperties[index] = value;
    }

    @Override
    public float[] getSecondaryProperties() {
        return secondaryProperties;
    }

    @Override
    public void setSecondaryProperties(float[] arr) {
        this.secondaryProperties = arr;
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
        secondaryProperties[index] = value;
    }

    @Override
    public float getCharacterPropertyWithoutLevel(int index) {
        return primaryProperties[index];
    }

    @Override
    public double getMaxMana() {
        return getMana().getMaxValue();
    }

    @Override
    public void setMaxMana(float mana) {
        getMana().setMaxValue(mana);
    }

    @Override
    public void setMaxHealth(float maxHealth) {
        getHealth().setMaxValue(maxHealth);
    }

    @Override
    public void setHealth(float maxHealth) {
        getHealth().setValue(maxHealth);
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
    public Player getEntity() {
        return getPlayer();
    }

    @Override
    public Map<String, IEffectContainer<Object, IEffect<Object>>> getEffectMap() {
        return effects;
    }

    @Override
    public Player getPlayer() {
        return Sponge.getServer().getPlayer(pl).orElse(null);
    }

    @Override
    public void setPlayer(Player pl) {
        this.pl = pl.getUniqueId();
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
    public int getAttributeValue(String name) {
        int i = 0;
        if (base.getAttributes().containsKey(name)) {
            i = base.getAttributes().get(name);
        }
        return i + getTransientAttributes().get(name);
    }

    @Override
    public Map<String, Long> getCooldowns() {
        return cooldowns;
    }

    @Override
    public boolean hasCooldown(String thing) {
        return cooldowns.containsKey(thing);
    }

    private void mergeWeapons(ClassDefinition g) {
        mergeWeapons(g.getWeapons());
        Set<ClassItem> offHandWeapons = g.getOffHandWeapons();
        for (ClassItem e : offHandWeapons) {
            allowedOffHandWeapons.add(e.getType());
        }
    }

    private void mergeWeapons(Set<ClassItem> weapons) {
        for (ClassItem weapon : weapons) {
            if (allowedWeapons.containsKey(weapon.getType())) {
                allowedWeapons.put(weapon.getType(), weapon.getDamage());
            } else {
                allowedWeapons.put(weapon.getType(), pluginConfig.ITEM_DAMAGE_PROCESSOR.get(allowedWeapons.get(weapon.getType()), weapon.getDamage()));
            }
        }
    }

    @Override
    public double getBaseWeaponDamage(RpgItemType weaponItemType) {
        Double aDouble = allowedWeapons.get(weaponItemType);
        if (aDouble == null)
            return 0D;
        return aDouble;
    }

    @Override
    public double getBaseProjectileDamage(EntityType id) {
        Double d = getProjectileDamages().get(id);
        if (d == null) {
            return 0;
        }
        return d;
    }

    @Override
    public IActiveCharacter updateItemRestrictions() {
        Log.debug("Updating item restrictions " + getName());

        allowedWeapons.clear();
        allowedOffHandWeapons.clear();
        allowedArmorIds.clear();
        getProjectileDamages().clear();


        Iterator<PlayerClassData> iterator = classes.values().iterator();
        //put in first
        if (iterator.hasNext()) {
            PlayerClassData next = iterator.next();
            ClassDefinition classDefinition = next.getClassDefinition();

            Set<ClassItem> items = classDefinition.getWeapons();
            for (ClassItem weapon : items) {
                allowedWeapons.put(weapon.getType(), weapon.getDamage());
            }

            items = classDefinition.getOffHandWeapons();
            for (ClassItem weapon : items) {
                allowedOffHandWeapons.add(weapon.getType());
            }

            items = classDefinition.getAllowedArmor();
            for (ClassItem weapon : items) {
                allowedArmorIds.add(weapon.getType());
            }

            getProjectileDamages().putAll(classDefinition.getProjectileDamage());
        }

        //calculate rest
        while (iterator.hasNext()) {
            PlayerClassData next = iterator.next();

            //merge weapon sets
            mergeWeapons(next.getClassDefinition());

            //might be expensive on massive skilltrees, eventually i could cache these types of skill in an extra collection
            for (PlayerSkillContext playerSkillContext : getSkills().values()) {
                if (playerSkillContext.getSkill().getType() == EffectSourceType.ITEM_ACCESS_SKILL) {
                    ItemAccessSkill.ItemAccessSkillData skillData = (ItemAccessSkill.ItemAccessSkillData) playerSkillContext.getSkillData();
                    Map<Integer, Set<ClassItem>> items = skillData.getItems();

                    for (Map.Entry<Integer, Set<ClassItem>> ent : items.entrySet()) {
                        if (ent.getKey() <= getLevel()) {
                            mergeWeapons(ent.getValue());
                        }
                    }
                }
            }
            allowedArmorIds.addAll(next
                    .getClassDefinition()
                    .getAllowedArmor()
                    .stream()
                    .map(ClassItem::getType)
                    .collect(Collectors.toSet()));
        }

        for (PlayerClassData playerClassData : getClasses().values()) {
            ClassDefinition configClass = playerClassData.getClassDefinition();
            Map<EntityType, Double> projectileDamage = configClass.getProjectileDamage();
            for (Map.Entry<EntityType, Double> entityType : projectileDamage.entrySet()) {
                Double aDouble = getProjectileDamages().get(entityType.getKey());
                if (aDouble == null) {
                    getProjectileDamages().put(entityType.getKey(), entityType.getValue());
                } else {
                    double v = pluginConfig.ITEM_DAMAGE_PROCESSOR.get(aDouble, entityType.getValue());
                    getProjectileDamages().put(entityType.getKey(), v);
                }
            }
        }
        return this;
    }

    @Override
    public Set<RpgItemType> getAllowedArmor() {
        return allowedArmorIds;
    }

    @Override
    public boolean canWear(RpgItemType armor) {
        return getAllowedArmor().contains(armor);
    }

    @Override
    public boolean canUse(RpgItemType weaponItemType, HandType h) {
        if (h == HandTypes.MAIN_HAND) {
            return allowedWeapons.containsKey(weaponItemType);
        } else {
            return allowedOffHandWeapons.contains(weaponItemType);
        }
    }


    @Override
    public Map<RpgItemType, Double> getAllowedWeapons() {
        return allowedWeapons;
    }

    @Override
    public Map<EntityType, Double> getProjectileDamages() {
        return projectileDamage;
    }

    @Override
    public CharacterBase getCharacterBase() {
        return base;
    }

    @Override
    public void sendMessage(LocalizableParametrizedText message, Arg arg) {
        getPlayer().sendMessage(message.toText(arg));
    }

    @Override
    public void sendMessage(ChatType chatType, Text message) {
        getPlayer().sendMessage(chatType, Text.of(message));
    }

    @Override
    public Map<String, PlayerSkillContext> getSkills() {
        return skills.getSkills();
    }

    @Override
    public void sendMessage(LocalizableParametrizedText message) {
        sendMessage(message, Arg.EMPTY);
    }

    @Override
    public void addSkill(String id, PlayerSkillContext info) {
        skills.add(id, info);
    }

    @Override
    public PlayerSkillContext getSkill(String id) {
        return skills.get(id);
    }

    @Override
    public void removeAllSkills() {
        getCharacterBase().getCharacterSkills().clear();
        skills.clear();
    }

    @Override
    public PlayerSkillContext getSkillInfo(ISkill skill) {
        return skills.get(skill.getId());
    }

    @Override
    public PlayerSkillContext getSkillInfo(String s) {
        return skills.get(s.toLowerCase());
    }

    @Override
    public boolean hasSkill(String name) {
        return skills.contains(name);
    }

    @Override
    public PlayerClassData getPrimaryClass() {
        return primaryClass;
    }

    @Override
    public void addClass(PlayerClassData playerClassData) {
        if (playerClassData.getClassDefinition().getClassType().equalsIgnoreCase(NtRpgPlugin.pluginConfig.PRIMARY_CLASS_TYPE)) {
            primaryClass = playerClassData;
        }
        classes.put(playerClassData.getClassDefinition().getName(), playerClassData);
    }

    @Override
    public int getLevel() {
        if (primaryClass == null) {
            return 1;
        }
        return getPrimaryClass().getLevel();
    }

    @Override
    public Map<String, PlayerClassData> getClasses() {
        return classes;
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
        pendingPartyInvite = new WeakReference<>(party);
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
    public boolean hasClass(ClassDefinition configClass) {
        String type = configClass.getClassType();
        return getClassByType(type) != null;
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
        if (character == this) {
            return true;
        }
        return getParty().getPlayers().contains(character);
    }

    @Override
    public Map<java.lang.String, SkillTreeViewModel> getSkillTreeViewLocation() {
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
        if (hasSkillTreeSpecialization(specialization)) {
            specs.remove(specialization);
        }
    }

    @Override
    public boolean hasSkillTreeSpecialization(SkillTreeSpecialization specialization) {
        return specs.contains(specialization);
    }

    @Override
    public Set<SkillTreeSpecialization> getSkillTreeSpecialization() {
        return Collections.unmodifiableSet(specs);
    }

    @Override
    public Set<EquipedSlot> getSlotsCannotBeEquiped() {
        return denySlotInteractionArr;
    }

    @Override
    public RpgItemStack getMainHand() {
        return mainHand;
    }

    @Override
    public int getMainHandSlotId() {
        return mainHandSlotId;
    }

    @Override
    public void setMainHand(RpgItemStack customItem, int slot) {
        this.mainHand = customItem;
        this.mainHandSlotId = slot;
    }

    @Override
    public RpgItemStack getOffHand() {
        return offHand;
    }

    @Override
    public void setOffHand(RpgItemStack customItem) {
        this.offHand = customItem;
    }

    @Override
    public int hashCode() {
        return getPlayer().getUniqueId().hashCode() * 37;
    }

    @Override
    public double getExperienceBonusFor(java.lang.String name, EntityType type) {
        double exp = 0;
        for (PlayerClassData playerClassData : getClasses().values()) {
            exp += playerClassData.getClassDefinition().getExperiencesBonus(name, type);
        }
        return exp;
    }

    @Override
    public void restartAttributeGuiSession() {
        attributeSession.clear();
    }

    @Override
    public boolean requiresDamageRecalculation() {
        return requiresDamageRecalculation;
    }

    @Override
    public void setRequiresDamageRecalculation(boolean k) {
        this.requiresDamageRecalculation = k;
    }

    @Override
    public int getLastHotbarSlotInteraction() {
        return lastHotbarSlotInteraction;
    }

    @Override
    public void setLastHotbarSlotInteraction(int last) {
        lastHotbarSlotInteraction = last;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ActiveCharacter that = (ActiveCharacter) o;
        return that.getCharacterBase().getId().equals(this.getCharacterBase().getId());
    }

    @Override
    public java.lang.String toString() {
        return "ActiveCharacter{" +
                "uuid=" + pl +
                " name=" + getName() +
                '}';
    }
}
