package cz.neumimto.rpg.api.entity.players;

import cz.neumimto.rpg.api.utils.ActionResult;
import cz.neumimto.rpg.api.IRpgElement;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.entity.players.CharacterBase;
import cz.neumimto.rpg.common.entity.players.attributes.AttributeConfig;
import cz.neumimto.rpg.common.persistance.model.CharacterSkill;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.common.entity.players.UserActionType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ICharacterService<T> {
    T buildDummyChar(UUID uuid);

    void registerDummyChar(T dummy);

    /**
     * @param uuid
     * @return character, never returns null
     */
    T getCharacter(UUID uuid);

    void addCharacter(UUID uuid, T character);

    Collection<T> getCharacters();

    boolean assignPlayerToCharacter(UUID uniqueId);

    void loadPlayerData(UUID id, String playerName);

    void checkPlayerDataStatus(UUID uniqueId);

    CharacterBase createCharacterBase(String name, UUID uuid);

    void updateWeaponRestrictions(T character);

    void updateArmorRestrictions(T character);

    List<CharacterBase> getPlayersCharacters(UUID id);

    void putInSaveQueue(CharacterBase base);

    void save(CharacterBase base);

    void createAndUpdate(CharacterBase base);

    IActiveCharacter setActiveCharacter(UUID uuid, T character);

    void initActiveCharacter(T character);

    void addDefaultEffects(T character);

    void removeGroupEffects(T character, ClassDefinition p);

    void applyGroupEffects(T character, ClassDefinition p);

    void updateMaxMana(T character);

    void updateMaxHealth(T character);

    IActiveCharacter removeCachedWrapper(UUID uuid);

    IActiveCharacter removeCachedCharacter(UUID uuid);

    void removePlayerData(UUID uniqueId);

    void invalidateCaches(T activeCharacter);

    void recalculateProperties(T character);

    void recalculateSecondaryPropertiesOnly(T character);

    T createActiveCharacter(UUID player, CharacterBase characterBase);

    int canCreateNewCharacter(UUID uniqueId, String name);

    ActionResult canUpgradeSkill(T character, ClassDefinition classDef, ISkill skill);

    void upgradeSkill(T character, PlayerSkillContext playerSkillContext, ISkill skill);

    ActionResult canLearnSkill(T character, ClassDefinition classDef, ISkill skill);

    boolean hasConflictingSkillDepedencies(T character, SkillData info);

    boolean hasSoftSkillDependencies(T character, SkillData info);

    boolean hasHardSkillDependencies(T character, SkillData info);

    ActionResult canRefundSkill(T character, ClassDefinition classDefinition, ISkill skill);

    CharacterSkill refundSkill(T character, PlayerSkillContext playerSkillContext, ISkill skill);

    int characterResetSkills(T character, boolean force);

    void characterSetMaxHealth(T character, float newHealht);

    void characterAddPoints(T character, ClassDefinition clazz, int skillpoint, int attributepoint);

    void addExperiences(T character, double exp, String source);

    void addExperiences(T character, double exp, PlayerClassData aClass);

    void assignAttribute(T character, AttributeConfig attribute, int levels);

    int addAttribute(T character, AttributeConfig attribute, int i);

    void addAttribute(T character, AttributeConfig attribute);

    void addTransientAttribtues(T activeCharacter, Map<AttributeConfig, Integer> attributes);

    void addTransientAttribute(T character, AttributeConfig attribute, int amount);

    void respawnCharacter(T character);

    boolean processUserAction(T character, UserActionType userActionType);

    int markCharacterForRemoval(UUID player, String charName);

    void gainMana(T character, double manaToAdd, IRpgElement source);

    ActionResult canGainClass(T character, ClassDefinition klass);

    ActionResult addNewClass(T character, ClassDefinition klass);

    void addSkillPoint(T character, PlayerClassData playerClassData, int skillpointsPerLevel);

    void addPersistantSkill(T character, PlayerClassData origin, CharacterSkill skill);

    void addSkill(T character, PlayerClassData origin, PlayerSkillContext skill);

    void learnSkill(T character, PlayerClassData origin, ISkill skill);

    void removeTransientAttributes(Map<AttributeConfig, Integer> bonusAttributes, T character);
}
