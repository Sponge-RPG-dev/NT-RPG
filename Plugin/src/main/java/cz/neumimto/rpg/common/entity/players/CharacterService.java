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

import static cz.neumimto.rpg.api.localization.Arg.arg;
import static cz.neumimto.rpg.api.logging.Log.info;
import static cz.neumimto.rpg.api.logging.Log.warn;
import cz.neumimto.rpg.api.IRpgElement;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.ICharacterService;
import cz.neumimto.rpg.api.entity.players.attributes.AttributeConfig;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.DependencyGraph;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.entity.players.leveling.SkillTreeType;
import cz.neumimto.rpg.api.events.character.*;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.inventory.InventoryService;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.api.persistance.model.*;
import cz.neumimto.rpg.api.skills.*;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.tree.SkillTreeSpecialization;
import cz.neumimto.rpg.api.utils.ActionResult;
import cz.neumimto.rpg.api.utils.DebugLevel;
import cz.neumimto.rpg.api.utils.MathUtils;
import cz.neumimto.rpg.common.persistance.dao.CharacterClassDao;
import cz.neumimto.rpg.common.persistance.dao.PlayerDao;
import cz.neumimto.rpg.common.persistance.model.JPACharacterClass;
import cz.neumimto.rpg.common.persistance.model.JPACharacterSkill;
import cz.neumimto.rpg.common.utils.exceptions.MissingConfigurationException;
import cz.neumimto.rpg.sponge.events.PlayerDataPreloadComplete;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 * Created by NeumimTo on 26.12.2014.
 */
public abstract class CharacterService<T extends IActiveCharacter> implements ICharacterService<T> {

    @Inject
    private SkillService skillService;

    @Inject
    protected PlayerDao playerDao;

    @Inject
    protected InventoryService inventoryService;

    @Inject
    private ClassService classService;

    @Inject
    private EntityService entityService;

    @Inject
    private DamageService damageService;

    @Inject
    private PropertyService propertyService;

    @Inject
    private LocalizationService localizationService;

    @Inject
    private CharacterClassDao characterClassDao;

    private Map<UUID, DataPreparationStage> dataPreparationStageMap = new ConcurrentHashMap<>();

    @Inject
    protected EffectService effectService;

    @Inject
    private PermissionService permissionService;


    protected abstract void addCharacterToGame(UUID id, T character, List<CharacterBase> playerChars);

    protected abstract boolean hasCharacter(UUID uniqueId);

    @Override
    public void loadPlayerData(UUID id, String playerName) {
        dataPreparationStageMap.put(id, new DataPreparationStage(DataPreparationStage.Stage.LOADING));

        addCharacter(id, buildDummyChar(id));
        CompletableFuture.runAsync(() -> {
            info("Loading player - " + id);
            long k = System.currentTimeMillis();
            List<CharacterBase> playerCharacters = playerDao.getPlayersCharacters(id);
            k = System.currentTimeMillis() - k;
            info("Finished loading of player data" + id + ", loaded " + playerCharacters.size() + " characters   [" + k + "]ms");
            PluginConfig pluginConfig = Rpg.get().getPluginConfig();
            if (playerCharacters.isEmpty() && pluginConfig.CREATE_FIRST_CHAR_AFTER_LOGIN) {
                CharacterBase cb = createCharacterBase(playerName, id);
                createAndUpdate(cb);

                playerCharacters = Collections.singletonList(cb);
                info("Automatically created character for a player " + id + ", " + playerName);
            }
            T icharacter = null;
            if (pluginConfig.PLAYER_AUTO_CHOOSE_LAST_PLAYED_CHAR || playerCharacters.size() == 1) {
                icharacter = createActiveCharacter(id, playerCharacters.get(0));
                dataPreparationStageMap.put(id, new DataPreparationStage(DataPreparationStage.Stage.TO_BE_ASSIGNED));
            }
            final T character = icharacter;

            if (character != null) {
                info("Finished initializing of player character " + id + ", [" + (System.currentTimeMillis() - k) + "]ms");
                addCharacterToGame(id, character, playerCharacters);
            } else {
                dataPreparationStageMap.put(id, new DataPreparationStage(DataPreparationStage.Stage.NO_ACTION, playerCharacters));
            }
        }, Rpg.get().getAsyncExecutor());
    }


    protected void finalizePlayerDataPreloadStage(UUID id, T character, PlayerDataPreloadComplete event) {
        setActiveCharacter(event.getPlayer(), character);
        invalidateCaches(character);
        dataPreparationStageMap.remove(id);
    }

    protected void playerDataPreloadStagePlayerNotReady(UUID id, T character) {
        dataPreparationStageMap.put(id, new DataPreparationStage(DataPreparationStage.Stage.PLAYER_NOT_YET_READY, character));
        Log.info("Data for Player " + id + " prepared but player instance not ready yet, will attempt to initialize later");
    }

    @Override
    public void checkPlayerDataStatus(UUID uniqueId) {
        if (!hasCharacter(uniqueId)) {
            return;
        }
        DataPreparationStage dataPreparationStage = dataPreparationStageMap.get(uniqueId);
        if (dataPreparationStage.stage == DataPreparationStage.Stage.PLAYER_NOT_YET_READY) {
            setActiveCharacter(uniqueId, (T) dataPreparationStage.character);
            invalidateCaches(getCharacter(uniqueId));
            assignPlayerToCharacter(uniqueId);
            dataPreparationStageMap.remove(uniqueId);
        } else if (dataPreparationStage.stage == DataPreparationStage.Stage.NO_ACTION) {
            if (!dataPreparationStage.characters.isEmpty()) {
                Gui.invokeCharacterMenu(getCharacter(uniqueId), dataPreparationStage.characters);
            } else {
                //todo message
            }
        }
    }

    /**
     * @param name
     * @return Initialized CharacterBase in the default state, The entity is not persisted yet
     */
    @Override
    public CharacterBase createCharacterBase(String name, UUID uuid) {
        CharacterBase characterBase = createCharacterBase();
        characterBase.setName(name);
        characterBase.setUuid(uuid);
        PluginConfig pluginConfig = Rpg.get().getPluginConfig();
        characterBase.setAttributePoints(pluginConfig.ATTRIBUTEPOINTS_ON_START);
        return characterBase;
    }

    protected abstract CharacterBase createCharacterBase();

    @Override
    public void updateWeaponRestrictions(T character) {
        Map weapons = character.getAllowedWeapons();
        CharacterWeaponUpdateEvent event = Rpg.get().getEventFactory().createEventInstance(CharacterWeaponUpdateEvent.class);
        event.setWeapons(weapons);
        event.setTarget(character);
        Rpg.get().postEvent(event);
    }

    @Override
    public void updateArmorRestrictions(T character) {
        Set allowedArmor = character.getAllowedArmor();
        EventCharacterArmorPostUpdate event = Rpg.get().getEventFactory().createEventInstance(EventCharacterArmorPostUpdate.class);
        event.setArmor(allowedArmor);
        event.setTarget(character);
        Rpg.get().postEvent(event);
    }


    /**
     * Gets list of player's characters
     * The method should be invoked only from async task
     *
     * @param id
     * @return
     */
    @Override
    public List<CharacterBase> getPlayersCharacters(UUID id) {
        return playerDao.getPlayersCharacters(id);
    }

    @Override
    public void putInSaveQueue(CharacterBase base) {
        CompletableFuture.runAsync(() -> {
            long k = System.currentTimeMillis();
            info("Saving player " + base.getUuid() + " character " + base.getName());
            save(base);
            info("Saved player " + base.getUuid() + " character " + base.getName() + "[" + (System.currentTimeMillis() - k) + "]ms ");
        }, Rpg.get().getAsyncExecutor());
    }

    /**
     * Saves player data
     *
     * @param base
     */
    @Override
    public void save(CharacterBase base) {
        base.onUpdate();
        playerDao.update(base);
    }

    @Override
    public void createAndUpdate(CharacterBase base) {
        base.onCreate();
        playerDao.createAndUpdate(base);
    }


    /**
     * Activates character for specified player, replaces old
     *
     * @param uuid
     * @param character
     * @return new character
     */
    @Override
    public IActiveCharacter setActiveCharacter(UUID uuid, T character) {
        info("Setting active character player " + uuid + " character " + character.getName());
        T activeCharacter = getCharacter(uuid);
        if (activeCharacter == null) {
            addCharacter(uuid, character);
        } else {
            deleteCharacterReferences(activeCharacter);
            character.setUsingGuiMod(activeCharacter.isUsingGuiMod());
            addCharacter(uuid, character);
        }
        initActiveCharacter(character);
        return character;
    }

    @Override
    public void initActiveCharacter(T character) {
        info("Initializing character " + character.getCharacterBase().getId());
        String msg = localizationService.translate(LocalizationKeys.CHARACTER_INITIALIZED, arg("character", character.getName()));
        character.sendMessage(msg);
        addDefaultEffects(character);
        Set<BaseCharacterAttribute> baseCharacterAttribute = character.getCharacterBase().getBaseCharacterAttribute();


        for (BaseCharacterAttribute at : baseCharacterAttribute) {
            Optional<AttributeConfig> type = propertyService.getAttributeById(at.getName());
            if (type.isPresent()) {
                assignAttribute(character, type.get(), character.getLevel());
            } else {
                warn(" - Unknown attribute stored in the database - " + at.getName());
            }
        }

        Map<String, PlayerClassData> classes = character.getClasses();
        for (PlayerClassData nClass : classes.values()) {
            applyGroupEffects(character, nClass.getClassDefinition());
        }

        inventoryService.initializeCharacterInventory(character);
        damageService.recalculateCharacterWeaponDamage(character);


        updateMaxHealth(character);
        entityService.updateWalkSpeed(character);

        CharacterInitializedEvent event = Rpg.get().getEventFactory().createEventInstance(CharacterInitializedEvent.class);
        event.setTarget(character);
        Rpg.get().postEvent(event);
    }


    @Override
    public void removeGroupEffects(T character, ClassDefinition p) {
        if (p == null) {
            return;
        }
        effectService.removeGlobalEffectsAsEnchantments(p.getEffects().keySet(), character, p);
    }

    @Override
    public void applyGroupEffects(T character, ClassDefinition p) {
        if (p == null) {
            return;
        }
        effectService.applyGlobalEffectsAsEnchantments(p.getEffects(), character, p);
    }

    /**
     * updates maximal mana from character properties
     *
     * @param character
     */
    @Override
    public void updateMaxMana(T character) {
        float max_mana = entityService.getEntityProperty(character, CommonProperties.max_mana);
        float actreserved = entityService.getEntityProperty(character, CommonProperties.reserved_mana);
        float reserved = entityService.getEntityProperty(character, CommonProperties.reserved_mana_multiplier);
        float maxval = max_mana - (actreserved * reserved);
        character.getMana().setMaxValue(maxval);
    }

    /**
     * Updates maximal health from character properties
     *
     * @param character
     */
    @Override
    public void updateMaxHealth(T character) {
        float max_health = entityService.getEntityProperty(character, CommonProperties.max_health)
                - entityService.getEntityProperty(character, CommonProperties.reserved_health);
        float actreserved = entityService.getEntityProperty(character, CommonProperties.reserved_health);
        float reserved = entityService.getEntityProperty(character, CommonProperties.reserved_health_multiplier);
        float maxval = max_health - (actreserved * reserved);
        if (maxval <= 0) {
            maxval = 1;
        }
        info("Setting max health " + character.getName() + " to " + maxval);
        character.getHealth().setMaxValue(maxval);
    }

    @Override
    public IActiveCharacter removeCachedWrapper(UUID uuid) {
        return removeCachedCharacter(uuid);
    }

    @Override
    public IActiveCharacter removeCachedCharacter(UUID uuid) {
        return deleteCharacterReferences(removeCharacter(uuid));
    }

    protected abstract T removeCharacter(UUID uuid);

    protected IActiveCharacter deleteCharacterReferences(T character) {
        effectService.removeAllEffects(character);
        if (character.hasParty()) {
            character.getParty().removePlayer(character);
        }
        character.setParty(null);
        return character;
    }

    /**
     * Removes all player's data from database.
     * Only way how to get back deleted data is backup your database.
     *
     * @param uniqueId player's uuid
     */
    @Override
    public void removePlayerData(UUID uniqueId) {
        removeCachedWrapper(uniqueId);
        playerDao.deleteData(uniqueId);
    }

    @Override
    public void invalidateCaches(final T activeCharacter) {
        activeCharacter.updateItemRestrictions();
        updateArmorRestrictions(activeCharacter);
        updateWeaponRestrictions(activeCharacter);
        updateAttributes(activeCharacter);
        entityService.updateWalkSpeed(activeCharacter);
        updateMaxHealth(activeCharacter);
        updateMaxMana(activeCharacter);
    }

    private void updateAttributes(T activeCharacter) {
        Set<Map.Entry<String, PlayerClassData>> entries = activeCharacter.getClasses().entrySet();
        for (Map.Entry<String, PlayerClassData> entry : entries) {
            PlayerClassData value = entry.getValue();
            ClassDefinition classDefinition = value.getClassDefinition();
            Map<AttributeConfig, Integer> attributes = classDefinition.getStartingAttributes();
            addTransientAttribtues(activeCharacter, attributes);
        }
    }


    @Override
    public void recalculateProperties(T character) {
        Map<Integer, Float> defaults = propertyService.getDefaults();
        float[] primary = character.getPrimaryProperties();
        float[] secondary = character.getSecondaryProperties();
        float pval = 0;
        float sval = 0;
        for (int i = 0; i < primary.length; i++) {
            pval = 0;
            sval = 0;
            Map<String, PlayerClassData> classes = character.getClasses();
            for (PlayerClassData cdata : classes.values()) {
                ClassDefinition classDefinition = cdata.getClassDefinition();
                float[] propBonus = classDefinition.getPropBonus();
                if (propBonus != null) {
                    pval += propBonus[i];
                }

                float[] propLevelBonus = classDefinition.getPropLevelBonus();
                if (propLevelBonus != null) {
                    sval += propLevelBonus[i] * cdata.getLevel();
                }
            }

            if (defaults.containsKey(i)) {
                pval += defaults.get(i);
            }
            primary[i] = pval;
            secondary[i] = sval;
        }
    }

    @Override
    public void recalculateSecondaryPropertiesOnly(T character) {
        float[] secondary = character.getSecondaryProperties();
        float sval = 0;
        for (int i = 0; i < secondary.length; i++) {
            Map<String, PlayerClassData> classes = character.getClasses();
            for (PlayerClassData cdata : classes.values()) {
                ClassDefinition classDefinition = cdata.getClassDefinition();
                float[] propLevelBonus = classDefinition.getPropLevelBonus();
                if (propLevelBonus != null) {
                    sval += propLevelBonus[i] * cdata.getLevel();
                }
            }
            secondary[i] = sval;
        }
    }


    private Set<PlayerSkillContext> resolveSkills(CharacterBase characterBase, T character) {
        Set<CharacterSkill> characterSkills1 = characterBase.getCharacterSkills();
        final long l = System.currentTimeMillis();
        Set<PlayerSkillContext> toInit = new HashSet<>();
        for (CharacterSkill characterSkill : characterSkills1) {
            Optional<ISkill> byId = skillService.getById(characterSkill.getCatalogId());
            if (byId.isPresent()) {
                ISkill iSkill = byId.get();
                CharacterClass fromClass = characterSkill.getFromClass();
                String name = fromClass.getName();
                ClassDefinition classDefinitionByName = classService.getClassDefinitionByName(name);
                if (classDefinitionByName == null) {
                    Log.warn("Character Base [" + characterBase.getId() + "] CharacterSkill [" + characterSkill.getId() + "] CharacterClass [" + fromClass.getId() + "] Unknown class name [" + fromClass.getName() + "]");
                    continue;
                }

                PlayerSkillContext info = new PlayerSkillContext(classDefinitionByName, iSkill, character);
                info.setLevel(characterSkill.getLevel());
                Map<String, PlayerClassData> classes = character.getClasses();
                PlayerClassData playerClassData = classes.get(name);
                SkillData info1 = playerClassData.getClassDefinition().getSkillTree().getSkills().get(iSkill.getId());
                if (info1 != null) {
                    toInit.add(info);
                    info.setSkillData(info1);
                    addSkill(character, playerClassData, info);
                }

                if (characterSkill.getCooldown() == null) {
                    continue;
                }
                if (characterSkill.getCooldown() <= l) {
                    continue;
                }
                character.getCooldowns().put(characterSkill.getCatalogId(), characterSkill.getCooldown());
            } else {
                Log.warn("Character Base [" + characterBase.getId() + "] CharacterSkill [" + characterSkill.getId() + "] Unknown Skill id [" + characterSkill.getCatalogId() + "]");
            }
        }
        return toInit;
    }

    @Override
    public T createActiveCharacter(UUID player, CharacterBase characterBase) {
        characterBase = playerDao.fetchCharacterBase(characterBase);
        T activeCharacter = createCharacter(player, characterBase);
        Set<String> strings = propertyService.getAttributes().keySet();
        for (String string : strings) {
            activeCharacter.getTransientAttributes().put(string, 0);
        }
        Set<CharacterClass> characterClasses = characterBase.getCharacterClasses();

        for (CharacterClass characterClass : characterClasses) {
            ClassDefinition classDef = classService.getClassDefinitionByName(characterClass.getName());
            if (classDef == null) {
                warn(" Character " + characterBase.getUuid() + " had persisted class " + characterClass.getName() + " but the class is missing class definition configuration");
                continue;
            }
            PlayerClassData playerClassData = new PlayerClassData(classDef, characterClass);
            activeCharacter.addClass(playerClassData);
            permissionService.addAllPermissions(activeCharacter, playerClassData);
        }

        inventoryService.initializeManagedSlots(activeCharacter);

        Set<PlayerSkillContext> skillData = resolveSkills(characterBase, activeCharacter);
        recalculateProperties(activeCharacter);
        for (PlayerSkillContext dt : skillData) {
            dt.getSkill().onCharacterInit(activeCharacter, dt.getLevel());
        }


        return activeCharacter;
    }


    protected abstract T createCharacter(UUID player, CharacterBase characterBase);

    /**
     * @param uniqueId player's uuid
     * @return 1 - if player reached maximal amount of characters
     * 2 - if player has character with same name
     * 0 - ok
     */
    @Override
    public abstract int canCreateNewCharacter(UUID uniqueId, String name);

    @Override
    public ActionResult canUpgradeSkill(T character, ClassDefinition classDef, ISkill skill) {
        CharacterClass cc = null;

        if (!character.hasClass(classDef)) {
            String text = localizationService.translate(LocalizationKeys.NO_ACCESS_TO_SKILL);
            return ActionResult.withErrorMessage(text);
        }

        Map<String, SkillData> skills = classDef.getSkillTree().getSkills();
        if (skills.containsKey(skill.getId())) {
            cc = character.getCharacterBase().getCharacterClass(classDef);
        }


        if (cc.getSkillPoints() < 1) {
            String text = localizationService.translate(LocalizationKeys.NO_SKILLPOINTS, arg("skill", skill.getName()));
            return ActionResult.withErrorMessage(text);
        }
        PlayerSkillContext playerSkillContext = character.getSkillInfo(skill);

        if (playerSkillContext == null) {
            String text = localizationService.translate(LocalizationKeys.NOT_LEARNED_SKILL, arg("skill", skill.getName()));
            return ActionResult.withErrorMessage(text);
        }
        int minlevel = playerSkillContext.getLevel() + playerSkillContext.getSkillData().getMinPlayerLevel();

        if (minlevel > character.getLevel()) {
            Map<String, Object> map = new HashMap<>();
            map.put("skill", skill.getName());
            map.put("level", minlevel);
            String text = localizationService.translate(LocalizationKeys.SKILL_REQUIRES_HIGHER_LEVEL, arg(map));
            return ActionResult.withErrorMessage(text);
        }
        if (playerSkillContext.getLevel() + 1 > playerSkillContext.getSkillData().getMaxSkillLevel()) {
            Map<String, Object> map = new HashMap<>();
            map.put("skill", skill.getName());
            map.put("level", playerSkillContext.getLevel());
            String text = localizationService.translate(LocalizationKeys.SKILL_IS_ON_MAX_LEVEL, arg(map));
            return ActionResult.withErrorMessage(text);
        }

        if (playerSkillContext.getLevel() * playerSkillContext.getSkillData().getLevelGap() > character.getLevel()) {
            Map<String, Object> map = new HashMap<>();
            map.put("skill", skill.getName());
            map.put("level", playerSkillContext.getLevel() * playerSkillContext.getSkillData().getLevelGap());
            String text = localizationService.translate(LocalizationKeys.INSUFFICIENT_LEVEL_GAP, arg(map));
            return ActionResult.withErrorMessage(text);
        }

        CharacterSkillUpgradeEvent event = Rpg.get().getEventFactory().createEventInstance(CharacterSkillUpgradeEvent.class);
        event.setTarget(character);
        event.setSkill(skill);

        if (Rpg.get().postEvent(event)) {
            return ActionResult.withErrorMessage(event.getFailedTranslationKey());
        }

        return ActionResult.ok();
    }

    @Override
    public void upgradeSkill(T character, PlayerSkillContext playerSkillContext, ISkill skill) {
        ClassDefinition classDefinition = playerSkillContext.getClassDefinition();
        CharacterClass cc = character.getCharacterBase().getCharacterClass(classDefinition);
        int s = cc.getSkillPoints();
        playerSkillContext.setLevel(playerSkillContext.getLevel() + 1);
        cc.setSkillPoints(s - 1);
        cc.setUsedSkillPoints(s + 1);
        CharacterSkill characterSkill = character.getCharacterBase().getCharacterSkill(skill);
        characterSkill.setLevel(playerSkillContext.getLevel());

        skill.skillUpgrade(character, playerSkillContext.getLevel());
    }

    /**
     * @param character
     * @param skill
     */
    @Override
    public ActionResult canLearnSkill(T character, ClassDefinition classDef, ISkill skill) {
        PlayerClassData nClass = null;
        SkillTree skillTree = classDef.getSkillTree();
        Map<String, PlayerClassData> classes = character.getClasses();
        for (PlayerClassData playerClassData : classes.values()) {
            if (playerClassData.getClassDefinition().getSkillTree() == skillTree) {
                nClass = playerClassData;
                break;
            }
        }

        if (skillTree == null || nClass == null) {
            String text = localizationService.translate(LocalizationKeys.NO_ACCESS_TO_SKILL);
            return ActionResult.withErrorMessage(text);
        }

        int avalaibleSkillpoints = 0;
        CharacterClass clazz = character.getCharacterBase().getCharacterClass(nClass.getClassDefinition());
        if (clazz == null) {
            throw new MissingConfigurationException("Class=" + nClass.getClassDefinition().getName() + ". Renamed?");
        }

        //todo fetch from db
        avalaibleSkillpoints = clazz.getSkillPoints();
        if (avalaibleSkillpoints < 1) {
            String text = localizationService.translate(LocalizationKeys.NO_SKILLPOINTS, arg("skill", skill.getName()));
            return ActionResult.withErrorMessage(text);
        }

        if (character.hasSkill(skill.getId())) {
            String text = localizationService.translate(LocalizationKeys.SKILL_ALREADY_LEARNED, arg("skill", skill.getName()));
            return ActionResult.withErrorMessage(text);
        }

        SkillData info = skillTree.getSkillById(skill.getId());
        if (info == null) {
            String text = localizationService.translate(LocalizationKeys.SKILL_NOT_IN_A_TREE, arg("skill", skill.getName()));
            return ActionResult.withErrorMessage(text);
        }

        if (character.getLevel() < info.getMinPlayerLevel()) {
            Map<java.lang.String, Object> map = new HashMap<>();
            map.put("skill", skill.getName());
            map.put("level", info.getMinPlayerLevel());
            String text = localizationService.translate(LocalizationKeys.SKILL_REQUIRES_HIGHER_LEVEL, arg(map));
            return ActionResult.withErrorMessage(text);

        }

        if (!hasHardSkillDependencies(character, info)) {
            Map<String, Object> map = new HashMap<>();
            map.put("skill", skill.getName());
            map.put("hard", info.getHardDepends().stream().map(SkillDependency::toString).collect(Collectors.joining(", ")));
            map.put("soft", info.getSoftDepends().stream().map(SkillDependency::toString).collect(Collectors.joining(", ")));
            String text = localizationService.translate(LocalizationKeys.MISSING_SKILL_DEPENDENCIES, arg(map));
            return ActionResult.withErrorMessage(text);

        }

        if (!hasSoftSkillDependencies(character, info)) {
            Map<java.lang.String, Object> map = new HashMap<>();
            map.put("skill", skill.getName());
            map.put("hard", info.getHardDepends().stream().map(SkillDependency::toString).collect(Collectors.joining(", ")));
            map.put("soft", info.getSoftDepends().stream().map(SkillDependency::toString).collect(Collectors.joining(", ")));
            String text = localizationService.translate(LocalizationKeys.MISSING_SKILL_DEPENDENCIES, arg(map));
            return ActionResult.withErrorMessage(text);
        }

        if (hasConflictingSkillDepedencies(character, info)) {
            Map<String, Object> map = new HashMap<>();
            map.put("skill", skill.getName());
            map.put("conflict", skill.getId());
            String text = localizationService.translate(LocalizationKeys.SKILL_CONFLICTS, arg(map));
            return ActionResult.withErrorMessage(text);
        }

        CharacterSkillLearnAttemptEvent event = Rpg.get().getEventFactory().createEventInstance(CharacterSkillLearnAttemptEvent.class);
        event.setTarget(character);
        event.setSkill(skill);

        if (Rpg.get().postEvent(event)) {
            return ActionResult.withErrorMessage(event.getFailedTranslationKey());
        }

        return ActionResult.ok();
    }

    @Override
    public boolean hasConflictingSkillDepedencies(T character, SkillData info) {
        for (SkillData skillData : info.getConflicts()) {
            if (character.hasSkill(skillData.getSkillId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasSoftSkillDependencies(T character, SkillData info) {
        for (SkillDependency dep : info.getSoftDepends()) {
            PlayerSkillContext skillInfo = character.getSkillInfo(dep.skillData.getSkill());
            if (skillInfo != null && skillInfo.getLevel() <= dep.minSkillLevel) {
                return true;
            }
        }
        return info.getSoftDepends().isEmpty();
    }

    @Override
    public boolean hasHardSkillDependencies(T character, SkillData info) {
        for (SkillDependency skillData : info.getHardDepends()) {
            PlayerSkillContext skillInfo = character.getSkillInfo(skillData.skillData.getSkill());
            if (skillInfo == null || skillInfo.getLevel() < skillData.minSkillLevel) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param character
     * @param skill
     * @param classDefinition
     * @return 1 - if character has not a single skillpoint in the skill
     * 2 - if one or more skill are on a path in a skilltree after the skill.
     * 3 - CharacterSkillRefundEvent was cancelled
     * 4 - Cant refund skill-tree path
     * 0 - ok
     */
    @Override
    public ActionResult canRefundSkill(T character, ClassDefinition classDefinition, ISkill skill) {
        PlayerSkillContext skillInfo = character.getSkillInfo(skill);
        if (skillInfo == null) {
            String text = localizationService.translate(LocalizationKeys.NOT_LEARNED_SKILL);
            return ActionResult.withErrorMessage(text);
        }
        SkillTree skillTree = classDefinition.getSkillTree();
        SkillData info = skillTree.getSkills().get(skill.getId());
        for (SkillData info1 : info.getDepending()) {
            PlayerSkillContext e = character.getSkill(info1.getSkill().getId());
            if (e != null) {
                String text = localizationService.translate(LocalizationKeys.REFUND_SKILLS_DEPENDING);
                return ActionResult.withErrorMessage(text);
            }
        }
        PluginConfig pluginConfig = Rpg.get().getPluginConfig();
        if (skill instanceof SkillTreeSpecialization && pluginConfig.PATH_NODES_SEALED) {
            String text = localizationService.translate(LocalizationKeys.UNABLE_TO_REFUND_SKILL_SEALED);
            return ActionResult.withErrorMessage(text);
        }

        CharacterSkillRefundAttemptEvent event = Rpg.get().getEventFactory().createEventInstance(CharacterSkillRefundAttemptEvent.class);
        event.setTarget(character);
        event.setSkill(skill);

        if (Rpg.get().postEvent(event)) {
            return ActionResult.withErrorMessage(event.getFailedTranslationKey());
        }

        return ActionResult.ok();
    }

    @Override
    public CharacterSkill refundSkill(T character, PlayerSkillContext playerSkillContext, ISkill skill) {
        int level = playerSkillContext.getLevel();
        skill.skillRefund(character);
        CharacterBase characterBase = character.getCharacterBase();

        CharacterClass cc = characterBase.getCharacterClass(playerSkillContext.getClassDefinition());

        int skillPoints = cc.getSkillPoints();
        cc.setSkillPoints(skillPoints + level);
        cc.setUsedSkillPoints(skillPoints - level);

        Iterator<CharacterSkill> iterator = characterBase.getCharacterSkills().iterator();
        while (iterator.hasNext()) {
            CharacterSkill next = iterator.next();
            if (next.getFromClass().getName().equalsIgnoreCase(playerSkillContext.getClassDefinition().getName())) {
                if (next.getCatalogId().equalsIgnoreCase(skill.getId())) {
                    iterator.remove();
                    return next;
                }
            }
        }
        return null;
    }


    /**
     * Resets character's skilltrees, and gives back all allocated skillpoints.
     *
     * @param character to be reseted
     * @param force
     * @return 1 - if character cant be reseted or force argument is false
     * 0 - ok;
     */
    @Override
    public int characterResetSkills(T character, boolean force) {
        CharacterBase characterBase = character.getCharacterBase();
        if (characterBase.isCanResetskills() || force) {
            characterBase.setCanResetskills(false);
            characterBase.setLastReset(new Date(System.currentTimeMillis()));
            characterBase.getCharacterSkills().clear();
            character.removeAllSkills();
            Set<CharacterClass> characterClasses = character.getCharacterBase().getCharacterClasses();
            character.getCharacterBase().getCharacterSkills().clear();
            for (CharacterClass characterClass : characterClasses) {
                int usedSkillPoints = characterClass.getUsedSkillPoints();
                characterClass.setSkillPoints(characterClass.getSkillPoints() + usedSkillPoints);
            }

            putInSaveQueue(characterBase);
            return 0;
        }
        return 1;
    }

    /**
     * Sets new max hp value
     *
     * @param character
     * @param newHealht
     */
    @Override
    public void characterSetMaxHealth(T character, float newHealht) {
        double health = character.getHealth().getValue();
        double max = character.getHealth().getMaxValue();
        double percent = MathUtils.getPercentage(health, max);
        character.getHealth().setMaxValue(newHealht);
        character.getHealth().setValue(newHealht / percent);
    }

    /**
     * Adds stats/attribute points to the character and saves the character
     *
     * @param character      - character object
     * @param skillpoint     - skillpoints to be added
     * @param attributepoint - attribute points to be added
     */
    @Override
    public void characterAddPoints(T character, ClassDefinition clazz, int skillpoint, int attributepoint) {
        CharacterClass cc = character.getCharacterBase().getCharacterClass(clazz);
        cc.setSkillPoints(cc.getSkillPoints() + skillpoint);
        character.setAttributePoints(character.getAttributePoints() + attributepoint);
        String msg = localizationService.translate(LocalizationKeys.CHARACTER_GAINED_POINTS,
                arg("skillpoints", skillpoint).with("attributes", attributepoint));
        character.sendMessage(msg);
        putInSaveQueue(character.getCharacterBase());
    }

    @Override
    public void addExperiences(T character, double exp, String source) {
        Map<String, PlayerClassData> classes = character.getClasses();
        for (Map.Entry<String, PlayerClassData> entry : classes.entrySet()) {
            PlayerClassData value = entry.getValue();
            ClassDefinition classDefinition = value.getClassDefinition();
            if (classDefinition.hasExperienceSource(source)) {
                if (value.takesExp()) {
                    addExperiences(character, exp, entry.getValue());
                }
            }
        }
    }

    @Override
    public void addExperiences(T character, double exp, PlayerClassData aClass) {
        double[] levels = aClass.getClassDefinition().getLevelProgression().getLevelMargins();
        if (levels == null) {
            //class can`t take exp
            return;
        }

        int level = aClass.getLevel();
        exp = exp * entityService.getEntityProperty(character, CommonProperties.experiences_mult);

        double lvlexp = aClass.getExperiencesFromLevel();

        double levellimit = levels[level];

        double newcurrentexp = lvlexp + exp;

        boolean gotLevel = false;
        while (newcurrentexp > levellimit) {
            level++;
            aClass.setLevel(level);
            Gui.showLevelChange(character, aClass, level);
            SkillTreeType skillTreeType = aClass.getClassDefinition().getSkillTreeType();
            if (skillTreeType != null) {
                skillTreeType.processClassLevelUp(character, aClass, level);
            }
            gotLevel = true;

            permissionService.addPermissions(character, aClass);
            if (!aClass.takesExp()) {
                break;
            }
            if (level > levels.length - 1) {
                break;
            }
            newcurrentexp = newcurrentexp - levellimit;
            levellimit = levels[level];
        }
        CharacterClass characterClass = aClass.getCharacterClass();
        characterClass.setExperiences(newcurrentexp);

        if (gotLevel) {
            inventoryService.initializeCharacterInventory(character);
        }

        Gui.showExpChange(character, aClass.getClassDefinition().getName(), exp);

        CompletableFuture.runAsync(() -> {
            info("Saving CharacterClass " + characterClass.getId(), DebugLevel.DEVELOP);
            characterClassDao.update(characterClass);
        }, Rpg.get().getAsyncExecutor());
    }

    @Override
    public void assignAttribute(T character, AttributeConfig attribute, int levels) {
        Map<Integer, Float> integerFloatMap = attribute.getPropBonus();
        for (Map.Entry<Integer, Float> entry : integerFloatMap.entrySet()) {
            character.getPrimaryProperties()[entry.getKey()] = character
                    .getPrimaryProperties()[entry.getKey()] + entry.getValue() * levels;
        }
    }

    /**
     * @param character
     * @param attribute
     * @param i
     * @return
     */
    @Override
    public int addAttribute(T character, AttributeConfig attribute, int i) {
        CharacterAttributeChange event = Rpg.get().getEventFactory().createEventInstance(CharacterAttributeChange.class);

        event.setTarget(character);
        event.setAttribute(attribute);

        if (Rpg.get().postEvent(event)) {
            return 1;
        }

        int attributePoints = character.getCharacterBase().getAttributePoints();
        if (attributePoints - i <= 0) {
            return 1;
        }
        Set<BaseCharacterAttribute> ap = character.getCharacterBase().getBaseCharacterAttribute();
        for (BaseCharacterAttribute a : ap) {
            if (a.getName().equalsIgnoreCase(attribute.getName())) {
                a.setLevel(a.getLevel() + i);
            }
        }
        character.getCharacterBase().setAttributePoints(attributePoints - i);
        assignAttribute(character, attribute, i);
        recalculateProperties(character);
        character.setRequiresDamageRecalculation(true);
        return 0;
    }

    @Override
    public void addAttribute(T character, AttributeConfig attribute) {
        addAttribute(character, attribute, 1);
    }

    @Override
    public void addTransientAttribtues(T activeCharacter, Map<AttributeConfig, Integer> attributes) {
        for (Map.Entry<AttributeConfig, Integer> ae : attributes.entrySet()) {
            addTransientAttribute(activeCharacter, ae.getKey(), ae.getValue());
        }
    }

    @Override
    public void addTransientAttribute(T character, AttributeConfig attribute, int amount) {
        Map<String, Integer> i = character.getTransientAttributes();
        i.merge(attribute.getId(), amount, Integer::sum);
        if (!attribute.getPropBonus().isEmpty()) {
            applyAttributeValue(character, attribute, amount);
        }
    }

    private void applyAttributeValue(T character, AttributeConfig attribute, int amount) {
        float[] primaryProperties = character.getPrimaryProperties();
        for (Map.Entry<Integer, Float> entry : attribute.getPropBonus().entrySet()) {
            int key = entry.getKey();
            float val = entry.getValue() * amount;
            float currentVal = primaryProperties[key];
            primaryProperties[key] = val + currentVal;
        }
    }

    /**
     * sponge is creating new player object each time a player is (re)spawned @link https://github
     * .com/SpongePowered/SpongeCommon/commit/384180f372fa233bcfc110a7385f43df2a85ef76
     * character object is heavy, lets do not recreate its instance just reasign player and effect
     */
    @Override
    public void respawnCharacter(T character) {
        effectService.removeAllEffects(character);

        Set<String> strings = propertyService.getAttributes().keySet();
        for (String string : strings) {
            character.getTransientAttributes().put(string, 0);
        }

        character.setRequiresDamageRecalculation(true);
        Map<String, PlayerClassData> classes = character.getClasses();
        for (PlayerClassData nClass : classes.values()) {
            applyGroupEffects(character, nClass.getClassDefinition());
        }

        character.getMana().setValue(0);
        addDefaultEffects(character);

        inventoryService.initializeCharacterInventory(character);
    }


    @Override
    public int markCharacterForRemoval(UUID player, java.lang.String charName) {
        return playerDao.markCharacterForRemoval(player, charName);
    }

    @Override
    public void gainMana(T character, double manaToAdd, IRpgElement source) {
        double current = character.getMana().getValue();
        double max = character.getMana().getMaxValue();
        if (current >= max) {
            return;
        }

        CharacterManaRegainEvent event = Rpg.get().getEventFactory().createEventInstance(CharacterManaRegainEvent.class);

        event.setAmount(manaToAdd);
        event.setTarget(character);
        event.setSource(source);

        if (Rpg.get().postEvent(event)) {
            return;
        }
        if (event.getAmount() <= 0) {
            return;
        }

        current += event.getAmount();
        if (current > max) current = max;

        character.getMana().setValue(current);
        Gui.displayMana(character);
    }

    @Override
    public ActionResult canGainClass(T character, ClassDefinition klass) {
        Map<String, PlayerClassData> classes = character.getClasses();
        if (classes.containsKey(klass.getName())) {
            String text = localizationService.translate(LocalizationKeys.ALREADY_HAS_THIS_CLASS, "class", klass.getName());
            return ActionResult.withErrorMessage(text);
        }

        DependencyGraph classDependencyGraph = klass.getClassDependencyGraph();

        Set<ClassDefinition> c = classes.values().stream().map(PlayerClassData::getClassDefinition).collect(Collectors.toSet());
        boolean ok = classDependencyGraph.isValidFor(c);

        if (!ok) {
            String text = localizationService.translate(LocalizationKeys.MISSING_CLASS_DEPENDENCIES);
            return ActionResult.withErrorMessage(text);
        }

        PluginConfig pluginConfig = Rpg.get().getPluginConfig();
        if (pluginConfig.RESPECT_CLASS_SELECTION_ORDER) {
            Set<String> classTypes = pluginConfig.CLASS_TYPES.keySet();
            Iterator<String> ctype = classTypes.iterator();
            String first = classTypes.iterator().next();

            while (ctype.hasNext()) {
                String classType = ctype.next();
                if (first.equalsIgnoreCase(classType) && first.equalsIgnoreCase(klass.getClassType())) {
                    break;
                }
                PlayerClassData classByType = character.getClassByType(classType);
                if (classByType == null) {
                    String text = localizationService.translate(LocalizationKeys.MISSING_CLASS_DEPENDENCIES);
                    return ActionResult.withErrorMessage(text);
                }
                ClassDefinition classDefinition = classByType.getClassDefinition();

                if (!classDefinition.getClassDependencyGraph().isValidFor(classes
                        .values().stream().map(PlayerClassData::getClassDefinition).collect(Collectors.toSet()))) {
                    String text = localizationService.translate(LocalizationKeys.MISSING_CLASS_DEPENDENCIES);
                    return ActionResult.withErrorMessage(text);
                }
                break;
            }
        }

        return ActionResult.ok();
    }

    @Override
    public ActionResult addNewClass(T character, ClassDefinition klass) {
        CharacterBase characterBase = character.getCharacterBase();
        CharacterClass cc = new JPACharacterClass();
        cc.setName(klass.getName());
        cc.setCharacterBase(characterBase);
        cc.setExperiences(0D);
        cc.setSkillPoints(0);
        cc.setUsedSkillPoints(0);
        cc.setLevel(0);

        characterBase.getCharacterClasses().add(cc);
        putInSaveQueue(characterBase);

        PlayerClassData playerClassData = new PlayerClassData(klass, cc);
        character.addClass(playerClassData);


        scheduleNextTick(() -> {
            recalculateProperties(character);
            permissionService.addPermissions(character, playerClassData);
            scheduleNextTick(() -> {
                recalculateSecondaryPropertiesOnly(character);
                applyGroupEffects(character, klass);
                scheduleNextTick(() -> {
                    invalidateCaches(character);
                    character.updateItemRestrictions();
                    String message = klass.getWelcomeMessage();
                    if (message == null) {
                        message = localizationService.translate(LocalizationKeys.CLASS_WELCOME_MESSAGE,
                                arg("class", klass.getName()));
                    }
                    character.sendMessage(message);
                });
            });
        });
        return ActionResult.ok();
    }

    protected abstract void scheduleNextTick(Runnable r);

    @Override
    public void addSkillPoint(T character, PlayerClassData playerClassData, int skillpointsPerLevel) {
        CharacterClass characterClass = character.getCharacterBase().getCharacterClass(playerClassData.getClassDefinition());
        characterClass.setSkillPoints(characterClass.getSkillPoints() + skillpointsPerLevel);
    }

    @Override
    public void addPersistantSkill(T character, PlayerClassData origin, CharacterSkill skill) {
        character.getCharacterBase().getCharacterSkills().add(skill);
    }

    @Override
    public void addSkill(T character, PlayerClassData origin, PlayerSkillContext skill) {
        character.addSkill(skill.getSkill().getId(), skill);
        character.addSkill(skill.getSkill().getName(), skill);
    }

    /**
     * Takes away one skillpoint and adds skill to player
     * Does not update the character state
     *
     * @param character
     * @param origin
     * @param skill
     */
    @Override
    public void learnSkill(T character, PlayerClassData origin, ISkill skill) {
        CharacterClass clazz = origin.getCharacterClass();
        clazz.setSkillPoints(clazz.getSkillPoints() - 1);
        clazz.setUsedSkillPoints(clazz.getUsedSkillPoints() + 1);

        ClassDefinition classDef = origin.getClassDefinition();
        PlayerSkillContext einfo = new PlayerSkillContext(classDef, skill, character);
        einfo.setLevel(1);

        SkillTree skillTree = classDef.getSkillTree();
        einfo.setSkillData(skillTree.getSkills().get(skill.getId()));

        CharacterSkill skill1 = new JPACharacterSkill();
        skill1.setLevel(1);
        skill1.setCharacterBase(character.getCharacterBase());
        skill1.setFromClass(clazz);
        skill1.setCatalogId(skill.getId());

        addPersistantSkill(character, origin, skill1);
        addSkill(character, origin, einfo);
        skill.skillLearn(character);
        Log.info("Character " + character.getCharacterBase().getUuid() + " learned skill " + skill.getId());
    }

    @Override
    public void removeTransientAttributes(Map<AttributeConfig, Integer> bonusAttributes, T character) {
        for (Map.Entry<AttributeConfig, Integer> entry : bonusAttributes.entrySet()) {
            removeTransientAttribute(character, entry.getKey(), entry.getValue());
        }
    }

    private void removeTransientAttribute(T character, AttributeConfig key, Integer value) {
        Map<String, Integer> transientAttributes = character.getTransientAttributes();
        transientAttributes.merge(key.getId(), value, (b, a) -> a - b);
    }

    @Override
    public void changePropertyValue(T character, int propertyId, float value) {
        character.addProperty(propertyId, value);
        if (propertyId == CommonProperties.max_health) {
            updateMaxHealth(character);
        } else if (propertyId == CommonProperties.max_mana) {
            updateMaxMana(character);
        } else if (propertyService.updatingRequiresDamageRecalc(propertyId)) {
            damageService.recalculateCharacterWeaponDamage(character);
        }
    }

}

