package cz.neumimto.rpg.common.entity.players;

import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.effects.EffectType;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.effects.IEffectContainer;
import cz.neumimto.rpg.common.entity.EntityHand;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.IEntityType;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.entity.players.party.IParty;
import cz.neumimto.rpg.common.gui.SkillTreeViewModel;
import cz.neumimto.rpg.common.inventory.ManagedSlot;
import cz.neumimto.rpg.common.inventory.RpgInventory;
import cz.neumimto.rpg.common.items.RpgItemStack;
import cz.neumimto.rpg.common.items.RpgItemType;
import cz.neumimto.rpg.common.model.EquipedSlot;
import cz.neumimto.rpg.common.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.preprocessors.InterruptableSkillPreprocessor;
import cz.neumimto.rpg.common.skills.tree.SkillTreeSpecialization;

import java.util.*;

/**
 * Deprecated will be replaced with ActiveCharacter as base reference - soon in valve time units
 */
@Deprecated
public interface IActiveCharacter<T, P extends IParty> extends IEntity<T> {

    Map<String, PlayerClassData> getClasses();

    UUID getUUID();

    default PlayerClassData getClassByType(String type) {
        for (PlayerClassData value : getClasses().values()) {
            if (value.getClassDefinition().getClassType().equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }

    default boolean hasEffectType(EffectType effectType) {
        for (IEffectContainer<Object, IEffect<Object>> container : getEffectMap().values()) {
            for (IEffect effect : container.getEffects()) {
                if (effect.getEffectTypes().contains(effectType)) {
                    return true;
                }
            }
        }
        return false;
    }

    Map<Class<?>, RpgInventory> getManagedInventory();

    P getParty();

    void setParty(P party);

    String getName();

    boolean isStub();

    double[] getPrimaryProperties();

    void setCharacterLevelProperty(int index, double value);

    double getCharacterPropertyWithoutLevel(int index);

    Resource getResource(String name);

    void addResource(String name, Resource resource);

    int getAttributePoints();

    void setAttributePoints(int attributePoints);

    int getAttributeValue(String name);

    default Integer getAttributeValue(AttributeConfig attribute) {
        return getAttributeValue(attribute.getId());
    }

    Map<String, Long> getCooldowns();

    default Long getCooldown(String action) {
        return getCooldowns().get(action);
    }

    boolean hasCooldown(String thing);

    Set<RpgItemType> getAllowedArmor();

    boolean canWear(RpgItemType armor);

    Set<RpgItemType> getAllowedWeapons();

    Map<String, Double> getProjectileDamages();

    CharacterBase getCharacterBase();

    PlayerClassData getPrimaryClass();

    double getBaseProjectileDamage(String id);

    IActiveCharacter updateItemRestrictions();

    Map<String, PlayerSkillContext> getSkills();

    PlayerSkillContext getSkillInfo(ISkill skill);

    boolean hasSkill(String name);

    void removeClass(ClassDefinition classDefinition);

    int getLevel();

    PlayerSkillContext getSkillInfo(String s);

    boolean isSilenced();

    Map<String, PlayerSkillContext> getSkillsByName();

    void addSkill(String name, PlayerSkillContext info);

    PlayerSkillContext getSkill(String skillName);

    void removeAllSkills();

    boolean hasParty();

    boolean isInPartyWith(IActiveCharacter character);

    boolean isUsingGuiMod();

    void setUsingGuiMod(boolean b);

    boolean isPartyLeader();

    P getPendingPartyInvite();

    void setPendingPartyInvite(P party);

    boolean canUse(RpgItemType weaponItemType, EntityHand h);

    boolean hasPreferedDamageType();

    String getDamageType();

    void setDamageType(String damageType);

    void updateLastKnownLocation(int x, int y, int z, String name);

    boolean isInvulnerable();

    void setInvulnerable(boolean b);


    @Override
    default IEntityType getType() {
        return IEntityType.CHARACTER;
    }

    void sendMessage(String message);

    double[] getSecondaryProperties();

    void setSecondaryProperties(double[] arr);

    Map<String, Integer> getTransientAttributes();

    boolean hasClass(ClassDefinition configClass);

    List<Integer> getSlotsToReinitialize();

    void setSlotsToReinitialize(List<Integer> slotsToReinitialize);

    void addSkillTreeSpecialization(SkillTreeSpecialization specialization);

    void removeSkillTreeSpecialization(SkillTreeSpecialization specialization);

    boolean hasSkillTreeSpecialization(SkillTreeSpecialization specialization);

    Set<SkillTreeSpecialization> getSkillTreeSpecialization();

    Set<EquipedSlot> getSlotsCannotBeEquiped();

    double getExperienceBonusFor(String dimmension, String type);

    void addClass(PlayerClassData playerClassData);

    void restartAttributeGuiSession();

    default void getMinimalInventoryRequirements(Map<AttributeConfig, Integer> seed) {

        Map<Class<?>, RpgInventory> managedInventory = getManagedInventory();
        for (RpgInventory inv : managedInventory.values()) {

            for (ManagedSlot value : inv.getManagedSlots().values()) {
                Optional<RpgItemStack> content = value.getContent();
                if (content.isPresent()) {
                    RpgItemStack rpgItemStack = content.get();
                    Map<AttributeConfig, Integer> minimalAttributeRequirements = rpgItemStack.getMinimalAttributeRequirements();

                    for (Map.Entry<AttributeConfig, Integer> entry : minimalAttributeRequirements.entrySet()) {
                        seed.compute(entry.getKey(), (attribute, integer) -> Math.max(integer, entry.getValue()));
                    }
                }
            }
        }
    }

    void sendNotification(String message);

    default PlayerClassData getClassByName(String name) {
        return getClasses().get(name.toLowerCase());
    }

    void setChanneledSkill(InterruptableSkillPreprocessor o);

    Optional<InterruptableSkillPreprocessor> getChanneledSkill();

    Map<String, Integer> getAttributesTransaction();

    void setAttributesTransaction(HashMap<String, Integer> map);

    String getPlayerAccountName();

    Map<String, ? extends SkillTreeViewModel> getSkillTreeViewLocation();

    SkillTreeViewModel getLastTimeInvokedSkillTreeView();

    SkillTreeChangeObserver getSkillUpgradeObservers();

    Stack<String> getGuiCommandHistory();

    void removeResource(String type);

    void updateResourceUIHandler();

}
