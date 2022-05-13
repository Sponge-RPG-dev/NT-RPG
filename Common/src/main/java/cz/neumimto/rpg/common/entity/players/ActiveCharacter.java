package cz.neumimto.rpg.common.entity.players;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.*;
import cz.neumimto.rpg.common.entity.EntityHand;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.entity.players.party.IParty;
import cz.neumimto.rpg.common.inventory.RpgInventory;
import cz.neumimto.rpg.common.items.RpgItemType;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.model.CharacterBase;
import cz.neumimto.rpg.common.model.EquipedSlot;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.skills.IPlayerSkillHandler;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.PlayerSkillHandlers;
import cz.neumimto.rpg.common.skills.preprocessors.InterruptableSkillPreprocessor;
import cz.neumimto.rpg.common.skills.tree.SkillTreeSpecialization;
import cz.neumimto.rpg.common.skills.types.ItemAccessSkill;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import java.lang.ref.WeakReference;
import java.util.*;


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
    private transient double[] primaryProperties;

    /*
    - player respawn
    - effect apply/expire
    */
    private transient double[] secondaryProperties;

    private transient boolean invulnerable;

    private transient boolean isusingguimod;

    private transient P party;

    private transient Map<String, IEffectContainer<Object, IEffect<Object>>> effects = new HashMap<>();
    private final IPlayerSkillHandler skills;

    private transient final Set<RpgItemType> allowedArmorIds = new HashSet<>();

    private transient final Set<RpgItemType> allowedWeapons = new HashSet<>();
    private transient final Object2DoubleOpenHashMap<String> projectileDamage = new Object2DoubleOpenHashMap<>(30);
    private final Object2LongOpenHashMap<String> cooldowns = new Object2LongOpenHashMap<>(10);

    private transient final Set<RpgItemType> allowedOffHandWeapons = new HashSet<>();

    private transient WeakReference<P> pendingPartyInvite = new WeakReference<>(null);
    private transient String preferedDamageType = null;

    private transient final Map<String, Integer> transientAttributes = new HashMap<>();

    private transient List<Integer> slotsToReinitialize;

    private Set<EquipedSlot> denySlotInteractionArr;

    private Set<SkillTreeSpecialization> specs = new HashSet<>();

    private transient Map<String, Integer> attributeSession = new HashMap<>();

    private transient Map<Class<?>, RpgInventory> inventory;

    private transient PlayerClassData primaryClass;

    private int lastHotbarSlotInteraction = -1;
    private InterruptableSkillPreprocessor channeledSkill;

    private Map<String, Integer> attrTransaction;

    private SkillTreeChangeObserver skillUpgradeObserver;

    private Map<String, Resource> classResources = new HashMap<>();

    private transient Stack<String> guiCommands = new Stack<>();

    public ActiveCharacter(UUID uuid, CharacterBase base, int propertyCount) {
        this.pl = uuid;
        this.primaryProperties = new double[propertyCount];
        this.secondaryProperties = new double[propertyCount];
        this.base = base;
        this.skills = new PlayerSkillHandlers.SHARED();
        this.slotsToReinitialize = new ArrayList<>();
        this.denySlotInteractionArr = new HashSet<>();
        this.inventory = new HashMap<>();
        this.attrTransaction = new HashMap<>();
        this.skillUpgradeObserver = new SkillTreeChangeObserver(this);
    }

    @Override
    public UUID getUUID() {
        return pl;
    }

    @Override
    public Optional<InterruptableSkillPreprocessor> getChanneledSkill() {
        return Optional.ofNullable(channeledSkill);
    }

    @Override
    public void setChanneledSkill(InterruptableSkillPreprocessor o) {
        this.channeledSkill = o;
    }

    @Override
    public boolean isSilenced() {
        return channeledSkill == null && hasEffectType(CommonEffectTypes.SILENCE);
    }

    @Override
    public Map<Class<?>, RpgInventory> getManagedInventory() {
        return inventory;
    }


    @Override
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


    @Override
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
    public double[] getPrimaryProperties() {
        return primaryProperties;
    }

    @Override
    public double getProperty(int index) {
        return primaryProperties[index] + secondaryProperties[index];
    }

    @Override
    public void setProperty(int index, double value) {
        primaryProperties[index] = value;
    }

    @Override
    public double[] getSecondaryProperties() {
        return secondaryProperties;
    }

    @Override
    public void setSecondaryProperties(double[] arr) {
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
    public void setCharacterLevelProperty(int index, double value) {
        secondaryProperties[index] = value;
    }

    @Override
    public double getCharacterPropertyWithoutLevel(int index) {
        return primaryProperties[index];
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
        return cooldowns.getOrDefault(thing, 0L) > System.currentTimeMillis();
    }


    @Override
    public double getBaseProjectileDamage(String id) {
        return projectileDamage.getOrDefault(id, 0);
    }

    @Override
    public IActiveCharacter updateItemRestrictions() {

        Log.info("Updating item restrictions " + getName());

        allowedWeapons.clear();
        allowedOffHandWeapons.clear();
        allowedArmorIds.clear();
        getProjectileDamages().clear();


        for (PlayerClassData clazz : classes.values()) {
            ClassDefinition classDefinition = clazz.getClassDefinition();
            allowedOffHandWeapons.addAll(classDefinition.getOffHandWeapons());
            allowedArmorIds.addAll(classDefinition.getAllowedArmor());
            allowedWeapons.addAll(classDefinition.getWeapons());
        }

        for (PlayerSkillContext skillContext : getSkills().values()) {
            if (skillContext.getSkill().getType() == EffectSourceType.ITEM_ACCESS_SKILL) {
                ItemAccessSkill.ItemAccessSkillData skillData = (ItemAccessSkill.ItemAccessSkillData) skillContext.getSkillData();
                Map<Integer, Set<RpgItemType>> items = skillData.getItems();
                for (Map.Entry<Integer, Set<RpgItemType>> ent : items.entrySet()) {
                    if (ent.getKey() <= getLevel()) {
                        for (RpgItemType rpgItemType : ent.getValue()) {
                            //todo somehow distinguish, maybe in config of item access skill specify slot
                            allowedWeapons.add(rpgItemType);
                            allowedOffHandWeapons.add(rpgItemType);
                            allowedArmorIds.add(rpgItemType);
                        }
                    }
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
            return allowedWeapons.contains(weaponItemType);
        } else {
            return allowedOffHandWeapons.contains(weaponItemType);
        }
    }


    @Override
    public Set<RpgItemType> getAllowedWeapons() {
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
    public Map<String, PlayerSkillContext> getSkillsByName() {
        return skills.getSkillsByName();
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
        classes.put(playerClassData.getClassDefinition().getName().toLowerCase(), playerClassData);
    }

    @Override
    public void removeClass(ClassDefinition classDefinition) {
        classes.remove(classDefinition.getName().toLowerCase());
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
    public Map<String, Integer> getAttributesTransaction() {
        return attrTransaction;
    }

    @Override
    public void setAttributesTransaction(HashMap<String, Integer> map) {
        attrTransaction = map;
    }

    @Override
    public String getPlayerAccountName() {
        return getCharacterBase().getLastKnownPlayerName();
    }

    @Override
    public SkillTreeChangeObserver getSkillUpgradeObservers() {
        return skillUpgradeObserver;
    }

    @Override
    public Stack<String> getGuiCommandHistory() {
        return guiCommands;
    }

    @Override
    public Resource getResource(String name) {
        return classResources.get(name);
    }

    @Override
    public void removeResource(String type) {
        classResources.remove(type);
    }

    @Override
    public void addResource(String name, Resource resource) {
        classResources.put(name, resource);
    }

    @Override
    public String toString() {
        return "ActiveCharacter{" +
                "uuid=" + pl +
                " name=" + getName() +
                '}';
    }
}
