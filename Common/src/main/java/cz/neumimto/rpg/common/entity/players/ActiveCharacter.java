package cz.neumimto.rpg.common.entity.players;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.effects.*;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.entity.players.party.IParty;
import cz.neumimto.rpg.common.gui.SkillTreeViewModel;
import cz.neumimto.rpg.common.items.RpgItemType;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.persistance.model.CharacterBase;
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

public abstract class ActiveCharacter<T, P extends IParty> implements IEntity<T> {

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
    private transient final Object2DoubleOpenHashMap<String> projectileDamage = new Object2DoubleOpenHashMap<>(30);
    private final Object2LongOpenHashMap<String> cooldowns = new Object2LongOpenHashMap<>(10);

    private transient WeakReference<P> pendingPartyInvite = new WeakReference<>(null);
    private transient String preferedDamageType = null;

    private transient final Map<String, Integer> transientAttributes = new HashMap<>();

    private Set<SkillTreeSpecialization> specs = new HashSet<>();

    private transient Map<String, Integer> attributeSession = new HashMap<>();

    private transient PlayerClassData primaryClass;

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
        this.attrTransaction = new HashMap<>();
        this.skillUpgradeObserver = new SkillTreeChangeObserver(this);
    }

    @Override
    public UUID getUUID() {
        return pl;
    }

    public Optional<InterruptableSkillPreprocessor> getChanneledSkill() {
        return Optional.ofNullable(channeledSkill);
    }

    public void setChanneledSkill(InterruptableSkillPreprocessor o) {
        this.channeledSkill = o;
    }

    public boolean isSilenced() {
        return channeledSkill == null && hasEffectType(CommonEffectTypes.SILENCE);
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


    public PlayerClassData getClassByType(String type) {
        for (PlayerClassData value : getClasses().values()) {
            if (value.getClassDefinition().getClassType().equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }

    public boolean hasEffectType(EffectType effectType) {
        for (IEffectContainer<Object, IEffect<Object>> container : getEffectMap().values()) {
            for (IEffect effect : container.getEffects()) {
                if (effect.getEffectTypes().contains(effectType)) {
                    return true;
                }
            }
        }
        return false;
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

    public String getName() {
        return getCharacterBase().getName();
    }

    public boolean isStub() {
        return false;
    }

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

    public double[] getSecondaryProperties() {
        return secondaryProperties;
    }

    public void setSecondaryProperties(double[] arr) {
        this.secondaryProperties = arr;
    }

    public void updateLastKnownLocation(int x, int y, int z, String name) {
        getCharacterBase().setX(x);
        getCharacterBase().setY(y);
        getCharacterBase().setZ(z);
        getCharacterBase().setWorld(name);
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void setInvulnerable(boolean b) {
        this.invulnerable = b;
    }

    public void setCharacterLevelProperty(int index, double value) {
        secondaryProperties[index] = value;
    }

    public double getCharacterPropertyWithoutLevel(int index) {
        return primaryProperties[index];
    }

    public Map<String, IEffectContainer<Object, IEffect<Object>>> getEffectMap() {
        return effects;
    }

    public int getAttributePoints() {
        return base.getAttributePoints();
    }

    public void setAttributePoints(int attributePoints) {
        this.base.setAttributePoints(attributePoints);
    }

    public Map<String, Integer> getTransientAttributes() {
        return transientAttributes;
    }


    public int getAttributeValue(AttributeConfig name) {
        return getAttributeValue(name.getId());
    }

    public int getAttributeValue(String name) {
        int i = 0;
        if (base.getAttributes().containsKey(name)) {
            i = base.getAttributes().get(name);
        }
        return i + getTransientAttributes().get(name);
    }

    public Map<String, Long> getCooldowns() {
        return cooldowns;
    }

    public boolean hasCooldown(String thing) {
        return cooldowns.getOrDefault(thing, 0L) > System.currentTimeMillis();
    }

    public double getBaseProjectileDamage(String id) {
        return projectileDamage.getOrDefault(id, 0);
    }

    public ActiveCharacter updateItemRestrictions() {

        Log.info("Updating item restrictions " + getName());

        Rpg.get().getPermissionService().refreshPermGroups(this);

        getProjectileDamages().clear();

        for (PlayerSkillContext skillContext : getSkills().values()) {
            if (skillContext.getSkill().getType() == EffectSourceType.ITEM_ACCESS_SKILL) {
                ItemAccessSkill.ItemAccessSkillData skillData = (ItemAccessSkill.ItemAccessSkillData) skillContext.getSkillData();
                Map<Integer, Set<RpgItemType>> items = skillData.getItems();
                for (Map.Entry<Integer, Set<RpgItemType>> ent : items.entrySet()) {
                    if (ent.getKey() <= getLevel()) {
                        Collection<String> collect = ent.getValue().stream().map(RpgItemType::getPermission).toList();
                        //todo somehow distinguish, maybe in config of item access skill specify slot
                        Rpg.get().getPermissionService().addPermissions(this, collect);
                    }
                }
            }
        }
        return this;
    }

    public Map<String, Double> getProjectileDamages() {
        return projectileDamage;
    }

    public CharacterBase getCharacterBase() {
        return base;
    }

    public Map<String, PlayerSkillContext> getSkills() {
        return skills.getSkills();
    }

    public Map<String, PlayerSkillContext> getSkillsByName() {
        return skills.getSkillsByName();
    }

    public void addSkill(String id, PlayerSkillContext info) {
        skills.add(id, info);
    }

    public PlayerSkillContext getSkill(String id) {
        return skills.get(id);
    }

    public void removeAllSkills() {
        getCharacterBase().getCharacterSkills().clear();
        skills.clear();
    }

    public PlayerSkillContext getSkillInfo(ISkill skill) {
        return skills.get(skill.getId());
    }

    public PlayerSkillContext getSkillInfo(String s) {
        return skills.get(s.toLowerCase());
    }

    public boolean hasSkill(String name) {
        return skills.contains(name);
    }

    public PlayerClassData getPrimaryClass() {
        return primaryClass;
    }

    public void addClass(PlayerClassData playerClassData) {
        if (playerClassData.getClassDefinition().getClassType().equalsIgnoreCase(Rpg.get().getPluginConfig().PRIMARY_CLASS_TYPE)) {
            primaryClass = playerClassData;
        }
        classes.put(playerClassData.getClassDefinition().getName().toLowerCase(), playerClassData);
    }

    public void removeClass(ClassDefinition classDefinition) {
        classes.remove(classDefinition.getName().toLowerCase());
    }

    public int getLevel() {
        if (primaryClass == null) {
            return 1;
        }
        return getPrimaryClass().getLevel();
    }

    public Map<String, PlayerClassData> getClasses() {
        return classes;
    }

    public P getParty() {
        return party;
    }

    public void setParty(P party) {
        if (this.party != null) {
            this.party.removePlayer(this);
        }
        this.party = party;
    }

    public boolean hasParty() {
        return getParty() != null && getParty().getPlayers().size() > 1;
    }

    public boolean isInPartyWith(ActiveCharacter character) {
        return (character.hasParty() && hasParty() && character.getParty() == character.getParty());
    }

    public boolean hasPreferedDamageType() {
        return preferedDamageType != null;
    }

    public String getDamageType() {
        return preferedDamageType;
    }

    public void setDamageType(String damageType) {
        this.preferedDamageType = damageType;
    }

    public boolean isUsingGuiMod() {
        return isusingguimod;
    }

    public void setUsingGuiMod(boolean b) {
        isusingguimod = b;
    }

    public boolean isPartyLeader() {
        return hasParty() && getParty().getLeader() == this;
    }

    public P getPendingPartyInvite() {
        return pendingPartyInvite.get();
    }

    public void setPendingPartyInvite(P party) {
        pendingPartyInvite = new WeakReference<>(party);
    }

    public boolean hasClass(ClassDefinition configClass) {
        String type = configClass.getClassType();
        return getClassByType(type) != null;
    }

    public boolean isFriendlyTo(ActiveCharacter character) {
        if (character == this) {
            return true;
        }
        return getParty().getPlayers().contains(character);
    }

    public void addSkillTreeSpecialization(SkillTreeSpecialization specialization) {
        this.specs.add(specialization);
    }

    public void removeSkillTreeSpecialization(SkillTreeSpecialization specialization) {
        if (hasSkillTreeSpecialization(specialization)) {
            specs.remove(specialization);
        }
    }

    public boolean hasSkillTreeSpecialization(SkillTreeSpecialization specialization) {
        return specs.contains(specialization);
    }

    public Set<SkillTreeSpecialization> getSkillTreeSpecialization() {
        return Collections.unmodifiableSet(specs);
    }

    public double getExperienceBonusFor(String name, String type) {
        double exp = 0;
        for (PlayerClassData playerClassData : getClasses().values()) {
            exp += playerClassData.getClassDefinition().getExperiencesBonus(name, type);
        }
        return exp;
    }

    public void restartAttributeGuiSession() {
        attributeSession.clear();
    }

    public Map<String, Integer> getAttributesTransaction() {
        return attrTransaction;
    }

    public void setAttributesTransaction(HashMap<String, Integer> map) {
        attrTransaction = map;
    }

    public String getPlayerAccountName() {
        return getCharacterBase().getLastKnownPlayerName();
    }

    public SkillTreeChangeObserver getSkillUpgradeObservers() {
        return skillUpgradeObserver;
    }

    public Stack<String> getGuiCommandHistory() {
        return guiCommands;
    }

    public Resource getResource(String name) {
        return classResources.get(name);
    }

    public void removeResource(String type) {
        classResources.remove(type);
    }

    public void addResource(String name, Resource resource) {
        classResources.put(name, resource);
    }

    public String toString() {
        return "ActiveCharacter{" +
                "uuid=" + pl +
                " name=" + getName() +
                '}';
    }

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

    public abstract void sendMessage(String msg);

    public PlayerClassData getClassByName(String name) {
        return classes.values().stream().filter(a->a.getClassDefinition().getName().equals(name)).findFirst().orElse(null);
    }

    public abstract Map<String, ? extends SkillTreeViewModel> getSkillTreeViewLocation();

    public abstract SkillTreeViewModel getLastTimeInvokedSkillTreeView();

    public long getCooldown(String skillId) {
        return cooldowns.getOrDefault(skillId, 0L);
    }
}
