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

package cz.neumimto.rpg.common.entity.players;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.ItemDamageProcessor;
import cz.neumimto.rpg.api.effects.*;
import cz.neumimto.rpg.api.entity.EntityHand;
import cz.neumimto.rpg.api.entity.IReservable;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.entity.players.party.IParty;
import cz.neumimto.rpg.api.inventory.RpgInventory;
import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.EquipedSlot;
import cz.neumimto.rpg.api.skills.IPlayerSkillHandler;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.preprocessors.InterruptableSkillPreprocessor;
import cz.neumimto.rpg.api.skills.tree.SkillTreeSpecialization;
import cz.neumimto.rpg.common.skills.PlayerSkillHandlers;
import cz.neumimto.rpg.common.skills.types.ItemAccessSkill;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by NeumimTo on 26.12.2014.
 */

public abstract class ActiveCharacter<T, P extends IParty> implements IActiveCharacter<T, P> {

    protected transient UUID pl;
    protected CharacterBase base;

    private Map<String, PlayerClassData> classes = new HashMap<>();

    /*
    - Needs to be recalculated on class add/remove
    - AttributeConfig change
    - Player login
    * */
    private transient float[] primaryProperties;

    /*
    - player respawn
    - effect apply/expire
    */
    private transient float[] secondaryProperties;

    private transient boolean invulnerable;

    private transient boolean isusingguimod;

    private IReservable mana;
    private IReservable health;

    private transient P party;

    private transient Map<String, IEffectContainer<Object, IEffect<Object>>> effects = new HashMap<>();
    private IPlayerSkillHandler skills;

    private transient Set<RpgItemType> allowedArmorIds = new HashSet<>();

    private transient Map<RpgItemType, Double> allowedWeapons = new HashMap<>();

    private transient Map<String, Double> projectileDamage = new HashMap<>();
    private transient Set<RpgItemType> allowedOffHandWeapons = new HashSet<>();
    private Map<String, Long> cooldowns = new HashMap<>();
    private transient WeakReference<P> pendingPartyInvite = new WeakReference<>(null);
    private transient double weaponDamage;
    private transient double armorvalue;

    private transient String preferedDamageType = null;
    private transient Map<String, Integer> transientAttributes = new HashMap<>();

    private transient List<Integer> slotsToReinitialize;

    private Set<EquipedSlot> denySlotInteractionArr;

    private Set<SkillTreeSpecialization> specs = new HashSet<>();

    private transient Map<String, Integer> attributeSession = new HashMap<>();

    private transient Map<Class<?>, RpgInventory> inventory;

    private transient int mainHandSlotId;

    private transient RpgItemStack offHand;
    private transient RpgItemStack mainHand;

    private transient PlayerClassData primaryClass;

    private boolean requiresDamageRecalculation;
    private int lastHotbarSlotInteraction = -1;
    private InterruptableSkillPreprocessor channeledSkill;

    public ActiveCharacter(UUID uuid, CharacterBase base, int propertyCount) {
        this.pl = uuid;
        this.primaryProperties = new float[propertyCount];
        this.secondaryProperties = new float[propertyCount];
        this.base = base;
        this.skills = new PlayerSkillHandlers.SHARED();
        this.slotsToReinitialize = new ArrayList<>();
        this.denySlotInteractionArr = new HashSet<>();
        this.inventory = new HashMap<>();
        this.requiresDamageRecalculation = true;
    }

    @Override
    public void setChanneledSkill(InterruptableSkillPreprocessor o) {
        this.channeledSkill = o;
    }

    @Override
    public Optional<InterruptableSkillPreprocessor> getChanneledSkill() {
        return Optional.ofNullable(channeledSkill);
    }

    @Override
    public boolean isSilenced() {
        return channeledSkill == null && hasEffectType(CommonEffectTypes.SILENCE);
    }

    @Override
    public Map<Class<?>, RpgInventory> getManagedInventory() {
        return inventory;
    }


    @SuppressWarnings("unchecked")
    public void addEffect(IEffect effect) {
        IEffectContainer IEffectContainer1 = getEffectMap().get(effect.getName());
        if (channeledSkill != null && effect.getEffectTypes().contains(CommonEffectTypes.INTERRUPTING)) {
            channeledSkill.interrupt();
        }
        if (IEffectContainer1 == null) {
            getEffectMap().put(effect.getName(), new EffectContainer<>(effect));
        } else {
            IEffectContainer1.getEffects().add(effect);
        }
    }



    @SuppressWarnings("unchecked")
    public void addEffect(IEffectContainer<Object, IEffect<Object>> iEffectContainer) {
        IEffectContainer effectContainer1 = getEffectMap().get(iEffectContainer.getName());
        if (channeledSkill != null) {
            Set<IEffect<Object>> effects = iEffectContainer.getEffects();
            for (IEffect<Object> effect : effects) {
                if (effect.getEffectTypes().contains(CommonEffectTypes.INTERRUPTING)) {
                    channeledSkill.interrupt();
                    break;
                }
            }
        }
        if (effectContainer1 == null) {
            getEffectMap().put(iEffectContainer.getName(), iEffectContainer);
        } else {
            effectContainer1.mergeWith(iEffectContainer);
        }
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
    public Map<String, IEffectContainer<Object, IEffect<Object>>> getEffectMap() {
        return effects;
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
            if (!allowedWeapons.containsKey(weapon.getType())) {
                allowedWeapons.put(weapon.getType(), weapon.getDamage());
            } else {
                ItemDamageProcessor itemDamageProcessor = Rpg.get().getPluginConfig().ITEM_DAMAGE_PROCESSOR;
                double dmg = itemDamageProcessor.get(allowedWeapons.get(weapon.getType()), weapon.getDamage());
                allowedWeapons.put(weapon.getType(), dmg);
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
    public double getBaseProjectileDamage(String id) {
        Double d = getProjectileDamages().get(id);
        if (d == null) {
            return 0;
        }
        return d;
    }

    @Override
    public IActiveCharacter updateItemRestrictions() {

        Log.info("Updating item restrictions " + getName());

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

            //might be expensive on massive Skilltrees, eventually i could cache these types of skill in an extra collection
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
            Map<String, Double> projectileDamage = configClass.getProjectileDamage();
            for (Map.Entry<String, Double> entityType : projectileDamage.entrySet()) {
                Double aDouble = getProjectileDamages().get(entityType.getKey());
                if (aDouble == null) {
                    getProjectileDamages().put(entityType.getKey(), entityType.getValue());
                } else {
                    double v = Rpg.get().getPluginConfig().ITEM_DAMAGE_PROCESSOR.get(aDouble, entityType.getValue());
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
    public boolean canUse(RpgItemType weaponItemType, EntityHand h) {
        if (h == EntityHand.MAIN) {
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
    public Map<String, Double> getProjectileDamages() {
        return projectileDamage;
    }

    @Override
    public CharacterBase getCharacterBase() {
        return base;
    }

    @Override
    public Map<String, PlayerSkillContext> getSkills() {
        return skills.getSkills();
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
        if (playerClassData.getClassDefinition().getClassType().equalsIgnoreCase(Rpg.get().getPluginConfig().PRIMARY_CLASS_TYPE)) {
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
    public P getParty() {
        return party;
    }

    @Override
    public void setParty(P party) {
        if (this.party != null) {
            this.party.removePlayer(this);
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
    public String getDamageType() {
        return preferedDamageType;
    }

    @Override
    public void setDamageType(String damageType) {
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
    public P getPendingPartyInvite() {
        return pendingPartyInvite.get();
    }

    @Override
    public void setPendingPartyInvite(P party) {
        pendingPartyInvite = new WeakReference<>(party);
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
    public boolean isFriendlyTo(IActiveCharacter character) {
        if (character == this) {
            return true;
        }
        return getParty().getPlayers().contains(character);
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
    public double getExperienceBonusFor(String name, String type) {
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
