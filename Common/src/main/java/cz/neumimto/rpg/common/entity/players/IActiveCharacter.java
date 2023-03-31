package cz.neumimto.rpg.common.entity.players;

import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.effects.EffectType;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.effects.IEffectContainer;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.IEntityType;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.entity.players.party.IParty;
import cz.neumimto.rpg.common.gui.SkillTreeViewModel;
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

    void addSkillTreeSpecialization(SkillTreeSpecialization specialization);

    void removeSkillTreeSpecialization(SkillTreeSpecialization specialization);

    boolean hasSkillTreeSpecialization(SkillTreeSpecialization specialization);

    Set<SkillTreeSpecialization> getSkillTreeSpecialization();

    double getExperienceBonusFor(String dimmension, String type);

    void addClass(PlayerClassData playerClassData);

    void restartAttributeGuiSession();

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
