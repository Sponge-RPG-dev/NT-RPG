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

import cz.neumimto.rpg.ClassService;
import cz.neumimto.rpg.common.utils.exceptions.MissingConfigurationException;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.api.ActionResult;
import cz.neumimto.rpg.api.IRpgElement;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillDependency;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.PropertyServiceImpl;
import cz.neumimto.rpg.configuration.DebugLevel;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.damage.SpongeDamageService;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.effects.common.def.ClickComboActionComponent;
import cz.neumimto.rpg.effects.common.def.CombatEffect;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.events.PlayerDataPreloadComplete;
import cz.neumimto.rpg.events.character.CharacterManaRegainEvent;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.inventory.SpongeInventoryService;
import cz.neumimto.rpg.inventory.UserActionType;
import cz.neumimto.rpg.persistance.CharacterClassDao;
import cz.neumimto.rpg.persistance.PlayerDao;
import cz.neumimto.rpg.persistance.model.BaseCharacterAttribute;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.persistance.model.CharacterSkill;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.players.groups.DependencyGraph;
import cz.neumimto.rpg.players.leveling.SkillTreeType;
import cz.neumimto.rpg.properties.DefaultProperties;
import cz.neumimto.rpg.properties.SpongePropertyService;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.api.skills.ISkillService;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.tree.SkillTreeSpecialization;
import cz.neumimto.rpg.sponge.utils.PermissionUtils;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cz.neumimto.core.localization.Arg.arg;
import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;
import static cz.neumimto.rpg.api.logging.Log.*;

/**
 * Created by NeumimTo on 26.12.2014.
 */
public abstract class CharacterService {

    @Inject
    private ISkillService skillService;

    @Inject
    private NtRpgPlugin plugin;

    @Inject
    private PlayerDao playerDao;

    @Inject
    private SpongeInventoryService spongeInventoryService;

    @Inject
    private ClassService classService;

    @Inject
    public EntityService entityService;

    @Inject
    private SpongeDamageService spongeDamageService;

    @Inject
    private SpongePropertyService spongePropertyService;

    @Inject
    private CharacterClassDao characterClassDao;

    private Map<UUID, IActiveCharacter> characters = new HashMap<>();

    private Map<UUID, DataPreparationStage> dataPreparationStageMap = new ConcurrentHashMap<>();

    @Inject
    private EffectService effectService;


    public void loadPlayerData(UUID id, String playerName) {
        dataPreparationStageMap.put(id, new DataPreparationStage(DataPreparationStage.Stage.LOADING));

        characters.put(id, buildDummyChar(id));
        CompletableFuture.runAsync(() -> {
            info("Loading player - " + id);
            long k = System.currentTimeMillis();
            List<CharacterBase> playerCharacters = playerDao.getPlayersCharacters(id);
            k = System.currentTimeMillis() - k;
            info("Finished loading of player data" + id + ", loaded " + playerCharacters.size() + " characters   [" + k + "]ms");

            if (playerCharacters.isEmpty() && pluginConfig.CREATE_FIRST_CHAR_AFTER_LOGIN) {
                CharacterBase cb = createCharacterBase(playerName, id);
                createAndUpdate(cb);

                playerCharacters = Collections.singletonList(cb);
                info("Automatically created character for a player " + id + ", " + playerName);
            }
            IActiveCharacter icharacter = null;
            if (pluginConfig.PLAYER_AUTO_CHOOSE_LAST_PLAYED_CHAR || playerCharacters.size() == 1) {
                icharacter = createActiveCharacter(id, playerCharacters.get(0));
                dataPreparationStageMap.put(id, new DataPreparationStage(DataPreparationStage.Stage.TO_BE_ASSIGNED));
            }
            final IActiveCharacter character = icharacter;

            if (character != null) {
                info("Finished initializing of player character " + id + ", [" + (System.currentTimeMillis() - k) + "]ms");

                addCharacterToGame(id, character, playerCharacters);
            } else {
                dataPreparationStageMap.put(id, new DataPreparationStage(DataPreparationStage.Stage.NO_ACTION, playerCharacters));
            }
        }, NtRpgPlugin.asyncExecutor);
    }

    protected abstract void addCharacterToGame(UUID id, IActiveCharacter character, List<CharacterBase> playerChars);

    protected void finalizePlayerDataPreloadStage(UUID id, IActiveCharacter character, PlayerDataPreloadComplete event, Player popt) {
        setActiveCharacter(event.getPlayer(), character);
        invalidateCaches(character);
        assignPlayerToCharacter(popt);
        dataPreparationStageMap.remove(id);
    }

    protected void playerDataPreloadStagePlayerNotReady(UUID id, IActiveCharacter character) {
        dataPreparationStageMap.put(id, new DataPreparationStage(DataPreparationStage.Stage.PLAYER_NOT_YET_READY, character));
        Log.info("Data for Player " + id + " prepared but player instance not ready yet, will attempt to initialize later");
    }

    public void checkPlayerDataStatus(Player targetEntity) {
        UUID uniqueId = targetEntity.getUniqueId();
        if (!characters.containsKey(uniqueId)) {
            return;
        }
        DataPreparationStage dataPreparationStage = dataPreparationStageMap.get(uniqueId);
        if (dataPreparationStage.stage == DataPreparationStage.Stage.PLAYER_NOT_YET_READY) {
            setActiveCharacter(uniqueId, dataPreparationStage.character);
            invalidateCaches(characters.get(targetEntity.getUniqueId()));
            assignPlayerToCharacter(targetEntity);
            dataPreparationStageMap.remove(uniqueId);
        } else if (dataPreparationStage.stage == DataPreparationStage.Stage.NO_ACTION) {
            if (!dataPreparationStage.characters.isEmpty()) {
                Gui.invokeCharacterMenu(targetEntity, dataPreparationStage.characters);
            } else {
                //todo message
            }
        }
    }

    /**
     * @param name
     * @return Initialized CharacterBase in the default state, The entity is not persisted yet
     */
    public CharacterBase createCharacterBase(String name, UUID uuid) {
        CharacterBase characterBase = new CharacterBase();
        characterBase.setName(name);
        characterBase.setUuid(uuid);
        characterBase.setAttributePoints(pluginConfig.ATTRIBUTEPOINTS_ON_START);
        return characterBase;
    }

    public void registerDummyChar(PreloadCharacter dummy) {
        characters.put(dummy.getPlayer().getUniqueId(), dummy);
    }

    public boolean assignPlayerToCharacter(Player pl) {
        if (pl == null) {
            return false;
        }
        info("Assigning player to character " + pl.getName());
        if (!characters.containsKey(pl.getUniqueId())) {
            error("Could not find any character for player " + pl.getName() + " Auth event not fired?");
            return false;
        }
        IActiveCharacter character = characters.get(pl.getUniqueId());
        if (character.isStub()) {
            return false;
        }
        if (character.getCharacterBase().getHealthScale() != null) {
            pl.offer(Keys.HEALTH_SCALE, character.getCharacterBase().getHealthScale());
        }

        spongeInventoryService.initializeCharacterInventory(character);
        return true;
    }

    public abstract void updateWeaponRestrictions(IActiveCharacter character);

    public abstract void updateArmorRestrictions(IActiveCharacter character);


    /**
     * Gets list of player's characters
     * The method should be invoked only from async task
     *
     * @param id
     * @return
     */
    public List<CharacterBase> getPlayersCharacters(UUID id) {
        return playerDao.getPlayersCharacters(id);
    }

    public void putInSaveQueue(CharacterBase base) {
        CompletableFuture.runAsync(() -> {
            long k = System.currentTimeMillis();
            info("Saving player " + base.getUuid() + " character " + base.getName());
            save(base);
            info("Saved player " + base.getUuid() + " character " + base.getName() + "[" + (System.currentTimeMillis() - k) + "]ms ");
        }, NtRpgPlugin.asyncExecutor);
    }

    /**
     * Saves player data
     *
     * @param base
     */
    public void save(CharacterBase base) {
        base.onUpdate();
        playerDao.update(base);
    }

    public void createAndUpdate(CharacterBase base) {
        base.onCreate();
        playerDao.createAndUpdate(base);
    }

    /**
     * @param uuid
     * @return character, never returns null
     */
    public IActiveCharacter getCharacter(UUID uuid) {
        return characters.get(uuid);
    }

    public IActiveCharacter getCharacter(Player player) {
        return characters.get(player.getUniqueId());
    }

    public Collection<IActiveCharacter> getCharacters() {
        return characters.values();
    }

    /**
     * Activates character for specified player, replaces old
     *
     * @param uuid
     * @param character
     * @return new character
     */
    public IActiveCharacter setActiveCharacter(UUID uuid, IActiveCharacter character) {
        info("Setting active character player " + uuid + " character " + character.getName());
        IActiveCharacter activeCharacter = getCharacter(uuid);
        if (activeCharacter == null) {
            characters.put(uuid, character);
        } else {
            deleteCharacterReferences(activeCharacter);
            character.setUsingGuiMod(activeCharacter.isUsingGuiMod());
            characters.put(uuid, character);
        }
        initActiveCharacter(character);
        return character;
    }

    public void initActiveCharacter(IActiveCharacter character) {
        info("Initializing character " + character.getCharacterBase().getId());
        character.sendMessage(Localizations.CURRENT_CHARACTER, arg("character", character.getName()));
        addDefaultEffects(character);
        Set<BaseCharacterAttribute> baseCharacterAttribute = character.getCharacterBase().getBaseCharacterAttribute();

        for (BaseCharacterAttribute at : baseCharacterAttribute) {
            Optional<Attribute> type = Sponge.getRegistry().getType(Attribute.class, at.getName());
            if (type.isPresent()) {
                assignAttribute(character, type.get(), character.getLevel());
            } else {
                warn(" - Unknown attribute stored in the database - " + at.getName());
            }
        }

        for (PlayerClassData nClass : character.getClasses().values()) {
            applyGroupEffects(character, nClass.getClassDefinition());
        }

        spongeInventoryService.initializeCharacterInventory(character);
        spongeDamageService.recalculateCharacterWeaponDamage(character);


        updateMaxHealth(character);
        entityService.updateWalkSpeed(character);
    }

    public void addDefaultEffects(IActiveCharacter character) {
        effectService.addEffect(new CombatEffect(character));
    }

    public void removeGroupEffects(IActiveCharacter character, ClassDefinition p) {
        if (p == null) {
            return;
        }
        effectService.removeGlobalEffectsAsEnchantments(p.getEffects().keySet(), character, p);
    }

    public void applyGroupEffects(IActiveCharacter character, ClassDefinition p) {
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
    public void updateMaxMana(IActiveCharacter character) {
        float max_mana = entityService.getEntityProperty(character, DefaultProperties.max_mana);
        float actreserved = entityService.getEntityProperty(character, DefaultProperties.reserved_mana);
        float reserved = entityService.getEntityProperty(character, DefaultProperties.reserved_mana_multiplier);
        float maxval = max_mana - (actreserved * reserved);
        character.getMana().setMaxValue(maxval);
    }

    /**
     * Updates maximal health from character properties
     *
     * @param character
     */
    public void updateMaxHealth(IActiveCharacter character) {
        float max_health = entityService.getEntityProperty(character, DefaultProperties.max_health)
                        - entityService.getEntityProperty(character, DefaultProperties.reserved_health);
        float actreserved = entityService.getEntityProperty(character, DefaultProperties.reserved_health);
        float reserved = entityService.getEntityProperty(character, DefaultProperties.reserved_health_multiplier);
        float maxval = max_health - (actreserved * reserved);
        if (maxval <= 0) {
            maxval = 1;
        }
        info("Setting max health " + character.getName() + " to " + maxval);
        character.getHealth().setMaxValue(maxval);
    }


    public IActiveCharacter removeCachedWrapper(UUID uuid) {
        return removeCachedCharacter(uuid);
    }

    public IActiveCharacter removeCachedCharacter(UUID uuid) {
        return deleteCharacterReferences(characters.remove(uuid));
    }

    protected IActiveCharacter deleteCharacterReferences(IActiveCharacter character) {
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
    public void removePlayerData(UUID uniqueId) {
        removeCachedWrapper(uniqueId);
        playerDao.deleteData(uniqueId);
    }

    /**
     * builds dummy character as a placeholder, Almost all actions are restricted
     *
     * @param uuid
     * @return
     */
    public PreloadCharacter buildDummyChar(UUID uuid) {
        info("Creating a dummy character for " + uuid);
        return new PreloadCharacter(uuid);
    }

    public void invalidateCaches(final IActiveCharacter activeCharacter) {
        activeCharacter.updateItemRestrictions();
        updateArmorRestrictions(activeCharacter);
        updateWeaponRestrictions(activeCharacter);
        updateAttributes(activeCharacter);
        entityService.updateWalkSpeed(activeCharacter);
        updateMaxHealth(activeCharacter);
        updateMaxMana(activeCharacter);
    }

    private void updateAttributes(IActiveCharacter activeCharacter) {
        Set<Map.Entry<String, PlayerClassData>> entries = activeCharacter.getClasses().entrySet();
        for (Map.Entry<String, PlayerClassData> entry : entries) {
            PlayerClassData value = entry.getValue();
            ClassDefinition classDefinition = value.getClassDefinition();
            Map<Attribute, Integer> attributes = classDefinition.getStartingAttributes();
            addTransientAttribtues(activeCharacter, attributes);
        }
    }



    public void recalculateProperties(IActiveCharacter character) {
        Map<Integer, Float> defaults = spongePropertyService.getDefaults();
        float[] primary = character.getPrimaryProperties();
        float[] secondary = character.getSecondaryProperties();
        float pval = 0;
        float sval = 0;
        for (int i = 0; i < primary.length; i++) {
            pval = 0;
            sval = 0;
            for (PlayerClassData cdata : character.getClasses().values()) {
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

    public void recalculateSecondaryPropertiesOnly(IActiveCharacter character) {
        float[] secondary = character.getSecondaryProperties();
        float sval = 0;
        for (int i = 0; i < secondary.length; i++) {
            for (PlayerClassData cdata : character.getClasses().values()) {
                ClassDefinition classDefinition = cdata.getClassDefinition();
                float[] propLevelBonus = classDefinition.getPropLevelBonus();
                if (propLevelBonus != null) {
                    sval += propLevelBonus[i] * cdata.getLevel();
                }
            }
            secondary[i] = sval;
        }
    }


    private Set<PlayerSkillContext> resolveSkills(CharacterBase characterBase, IActiveCharacter character) {
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
                PlayerClassData playerClassData = character.getClasses().get(name);
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

    /**
     * @param player
     * @param characterBase
     * @return
     */
    public ActiveCharacter createActiveCharacter(UUID player, CharacterBase characterBase) {
        characterBase = playerDao.fetchCharacterBase(characterBase);
        ActiveCharacter activeCharacter = new ActiveCharacter(player, characterBase, PropertyServiceImpl.LAST_ID);
        Set<String> strings = spongePropertyService.getAttributes().keySet();
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
            classService.addAllPermissions(activeCharacter, playerClassData);
        }

        spongeInventoryService.initializeManagedSlots(activeCharacter);

        Set<PlayerSkillContext> skillData = resolveSkills(characterBase, activeCharacter);
        recalculateProperties(activeCharacter);
        for (PlayerSkillContext dt : skillData) {
            dt.getSkill().onCharacterInit(activeCharacter, dt.getLevel());
        }


        return activeCharacter;
    }

    /**
     * @param uniqueId player's uuid
     * @return 1 - if player reached maximal amount of characters
     * 2 - if player has character with same name
     * 0 - ok
     */
    public int canCreateNewCharacter(UUID uniqueId, java.lang.String name) {
        //todo use db query
        List<CharacterBase> list = getPlayersCharacters(uniqueId);
        if (list.size() >= PermissionUtils.getMaximalCharacterLimit(uniqueId)) {
            return 1;
        }
        if (list.stream().anyMatch(c -> c.getName().equalsIgnoreCase(name))) {
            return 2;
        }
        return 0;
    }


    public ActionResult canUpgradeSkill(IActiveCharacter character, ClassDefinition classDef, ISkill skill) {
        CharacterClass cc = null;

        if (!character.hasClass(classDef)) {
            return ActionResult.withErrorMessage(Localizations.NO_ACCESS_TO_SKILL.toText());
        }

        Map<String, SkillData> skills = classDef.getSkillTree().getSkills();
        if (skills.containsKey(skill.getId())) {
            cc = character.getCharacterBase().getCharacterClass(classDef);
        }


        if (cc.getSkillPoints() < 1) {
            return ActionResult.withErrorMessage(Localizations.NO_SKILLPOINTS.toText(arg("skill", skill.getName())));
        }
        PlayerSkillContext playerSkillContext = character.getSkillInfo(skill);

        if (playerSkillContext == null) {
            return ActionResult.withErrorMessage(Localizations.NOT_LEARNED_SKILL.toText(arg("skill", skill.getName())));
        }
        int minlevel = playerSkillContext.getLevel() + playerSkillContext.getSkillData().getMinPlayerLevel();

        if (minlevel > character.getLevel()) {
            Map<java.lang.String, Object> map = new HashMap<>();
            map.put("skill", skill.getName());
            map.put("level", minlevel);
            return ActionResult.withErrorMessage(Localizations.SKILL_REQUIRES_HIGHER_LEVEL.toText(arg(map)));
        }
        if (playerSkillContext.getLevel() + 1 > playerSkillContext.getSkillData().getMaxSkillLevel()) {
            Map<java.lang.String, Object> map = new HashMap<>();
            map.put("skill", skill.getName());
            map.put("level", playerSkillContext.getLevel());
            return ActionResult.withErrorMessage(Localizations.SKILL_IS_ON_MAX_LEVEL.toText(arg(map)));
        }

        if (playerSkillContext.getLevel() * playerSkillContext.getSkillData().getLevelGap() > character.getLevel()) {
            Map<java.lang.String, Object> map = new HashMap<>();
            map.put("skill", skill.getName());
            map.put("level", playerSkillContext.getLevel() * playerSkillContext.getSkillData().getLevelGap());
            return ActionResult.withErrorMessage(Localizations.INSUFFICIENT_LEVEL_GAP.toText(arg(map)));
        }

        return ActionResult.ok();
    }

    public void upgradeSkill(IActiveCharacter character, PlayerSkillContext playerSkillContext, ISkill skill) {
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
    public ActionResult canLearnSkill(IActiveCharacter character, ClassDefinition classDef, ISkill skill) {
        PlayerClassData nClass = null;
        SkillTree skillTree = classDef.getSkillTree();
        for (PlayerClassData playerClassData : character.getClasses().values()) {
            if (playerClassData.getClassDefinition().getSkillTree() == skillTree) {
                nClass = playerClassData;
                break;
            }
        }

        if (skillTree == null || nClass == null)  {
            return ActionResult.withErrorMessage(Localizations.NO_ACCESS_TO_SKILL.toText());
        }

        int avalaibleSkillpoints = 0;
        CharacterClass clazz = character.getCharacterBase().getCharacterClass(nClass.getClassDefinition());
        if (clazz == null) {
            throw new MissingConfigurationException("Class=" + nClass.getClassDefinition().getName() + ". Renamed?");
        }

        //todo fetch from db
        avalaibleSkillpoints = clazz.getSkillPoints();
        if (avalaibleSkillpoints < 1) {
            return ActionResult.withErrorMessage(Localizations.NO_SKILLPOINTS.toText(arg("skill", skill.getName())));
        }

        if (character.hasSkill(skill.getId())) {
            return ActionResult.withErrorMessage(Localizations.SKILL_ALREADY_LEARNED.toText(arg("skill", skill.getName())));
        }

        SkillData info = skillTree.getSkillById(skill.getId());
        if (info == null) {
            return ActionResult.withErrorMessage(Localizations.SKILL_NOT_IN_A_TREE.toText(arg("skill", skill.getName())));
        }

        if (character.getLevel() < info.getMinPlayerLevel()) {
            Map<java.lang.String, Object> map = new HashMap<>();
            map.put("skill", skill.getName());
            map.put("level", info.getMinPlayerLevel());
            return ActionResult.withErrorMessage(Localizations.SKILL_REQUIRES_HIGHER_LEVEL.toText(arg(map)));
        }

        if (!hasHardSkillDependencies(character, info)) {
            Map<String, Object> map = new HashMap<>();
            map.put("skill", skill.getName());
            map.put("hard", info.getHardDepends().stream().map(SkillDependency::toString).collect(Collectors.joining(", ")));
            map.put("soft", info.getSoftDepends().stream().map(SkillDependency::toString).collect(Collectors.joining(", ")));
            return ActionResult.withErrorMessage(Localizations.MISSING_SKILL_DEPENDENCIES.toText(arg(map)));
        }

        if (!hasSoftSkillDependencies(character, info)) {
            Map<java.lang.String, Object> map = new HashMap<>();
            map.put("skill", skill.getName());
            map.put("hard", info.getHardDepends().stream().map(SkillDependency::toString).collect(Collectors.joining(", ")));
            map.put("soft", info.getSoftDepends().stream().map(SkillDependency::toString).collect(Collectors.joining(", ")));
            return ActionResult.withErrorMessage(Localizations.MISSING_SKILL_DEPENDENCIES.toText(arg(map)));
        }

        if (hasConflictingSkillDepedencies(character, info)) {
            Map<String, Object> map = new HashMap<>();
            map.put("skill", skill.getName());
            map.put("conflict", skill.getId());
            return ActionResult.withErrorMessage(Localizations.SKILL_CONFLICTS.toText(arg(map)));
        }

        return ActionResult.ok();
    }

    public boolean hasConflictingSkillDepedencies(IActiveCharacter character, SkillData info) {
        for (SkillData skillData : info.getConflicts()) {
            if (character.hasSkill(skillData.getSkillId())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSoftSkillDependencies(IActiveCharacter character, SkillData info) {
        for (SkillDependency dep : info.getSoftDepends()) {
            PlayerSkillContext skillInfo = character.getSkillInfo(dep.skillData.getSkill());
            if (skillInfo != null && skillInfo.getLevel() <= dep.minSkillLevel) {
                return true;
            }
        }
        return info.getSoftDepends().isEmpty();
    }

    public boolean hasHardSkillDependencies(IActiveCharacter character, SkillData info) {
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
    public ActionResult canRefundSkill(IActiveCharacter character, ClassDefinition classDefinition, ISkill skill) {
        PlayerSkillContext skillInfo = character.getSkillInfo(skill);
        if (skillInfo == null) {
            return ActionResult.withErrorMessage(Localizations.NOT_LEARNED_SKILL.toText());
        }
        SkillTree skillTree = classDefinition.getSkillTree();
        SkillData info = skillTree.getSkills().get(skill.getId());
        for (SkillData info1 : info.getDepending()) {
            PlayerSkillContext e = character.getSkill(info1.getSkill().getId());
            if (e != null) {
                return ActionResult.withErrorMessage(Localizations.REFUND_SKILLS_DEPENDING.toText());
            }
        }
        if (skill instanceof SkillTreeSpecialization && pluginConfig.PATH_NODES_SEALED) {
            return ActionResult.withErrorMessage(Localizations.UNABLE_TO_REFUND_SKILL_SEALED.toText());
        }
        return ActionResult.ok();
    }


    public CharacterSkill refundSkill(IActiveCharacter character, PlayerSkillContext playerSkillContext, ISkill skill) {
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
            if (next.getFromClass().getName().equalsIgnoreCase(playerSkillContext.getClassDefinition().getName())){
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
    public int characterResetSkills(IActiveCharacter character, boolean force) {
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
    public void characterSetMaxHealth(IActiveCharacter character, float newHealht) {
        double health = character.getHealth().getValue();
        double max = character.getHealth().getMaxValue();
        double percent = Utils.getPercentage(health, max);
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
    public void characterAddPoints(IActiveCharacter character, ClassDefinition clazz, int skillpoint, int attributepoint) {
        CharacterClass cc = character.getCharacterBase().getCharacterClass(clazz);
        cc.setSkillPoints(cc.getSkillPoints() + skillpoint);
        character.setAttributePoints(character.getAttributePoints() + attributepoint);
        character.sendMessage(Localizations.CHARACTER_GAINED_POINTS,
                arg("skillpoints", skillpoint).with("attributes", attributepoint));
        putInSaveQueue(character.getCharacterBase());
    }

    public void addExperiences(IActiveCharacter character, double exp, String source) {
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

    public void addExperiences(IActiveCharacter character, double exp, PlayerClassData aClass) {
        double[] levels = aClass.getClassDefinition().getLevelProgression().getLevelMargins();
        if (levels == null) {
            //class can`t take exp
            return;
        }

        int level = aClass.getLevel();
        exp = exp * entityService.getEntityProperty(character, DefaultProperties.experiences_mult);

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

            classService.addPermissions(character, aClass);
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
            spongeInventoryService.initializeCharacterInventory(character);
        }

        Gui.showExpChange(character, aClass.getClassDefinition().getName(), exp);

        CompletableFuture.runAsync(() -> {
            info("Saving CharacterClass " + characterClass.getId(), DebugLevel.DEVELOP);
            characterClassDao.update(characterClass);
        }, NtRpgPlugin.asyncExecutor);
    }


    public void assignAttribute(IActiveCharacter character, Attribute attribute, int levels) {
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
    public int addAttribute(IActiveCharacter character, Attribute attribute, int i) {
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

    public void addAttribute(IActiveCharacter character, Attribute attribute) {
        addAttribute(character, attribute, 1);
    }

    public void addTransientAttribtues(IActiveCharacter activeCharacter, Map<Attribute, Integer> attributes) {
        for (Map.Entry<Attribute, Integer> ae : attributes.entrySet()) {
            addTransientAttribute(activeCharacter, ae.getKey(), ae.getValue());
        }
    }

    public void addTransientAttribute(IActiveCharacter character, Attribute attribute, int amount) {
        character.getTransientAttributes().merge(attribute.getId(), amount, Integer::sum);
        if (!attribute.getPropBonus().isEmpty()) {
            applyAttributeValue(character, attribute, amount);
        }
    }

    private void applyAttributeValue(IActiveCharacter character, Attribute attribute, int amount) {
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
    public void respawnCharacter(IActiveCharacter character) {
        effectService.removeAllEffects(character);

        Set<String> strings = spongePropertyService.getAttributes().keySet();
        for (String string : strings) {
            character.getTransientAttributes().put(string, 0);
        }

        character.setRequiresDamageRecalculation(true);
        for (PlayerClassData nClass : character.getClasses().values()) {
            applyGroupEffects(character, nClass.getClassDefinition());
        }

        character.getMana().setValue(0);
        addDefaultEffects(character);

        spongeInventoryService.initializeCharacterInventory(character);
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            invalidateCaches(character);
            Double d = character.getHealth().getMaxValue();
            character.getEntity().offer(Keys.HEALTH, d);
        }).delay(1, TimeUnit.MILLISECONDS).submit(plugin);
    }

    public void setHeathscale(IActiveCharacter character, double i) {
        character.getCharacterBase().setHealthScale(i);
        character.getPlayer().offer(Keys.HEALTH_SCALE, i);
        putInSaveQueue(character.getCharacterBase());
    }

    /**
     * @param character
     * @param userActionType
     * @return true whenever root event should be cancelled
     */
    public boolean processUserAction(IActiveCharacter character, UserActionType userActionType) {
        IEffectContainer effect = character.getEffect(ClickComboActionComponent.name);
        if (effect == null) {
            return false;
        }
        ClickComboActionComponent e = (ClickComboActionComponent) effect;
        if (userActionType == UserActionType.L && e.hasStarted()) {
            e.processLMB();
            return false;
        }
        if (userActionType == UserActionType.R) {
            e.processRMB();
            return false;
        }
        if (userActionType == UserActionType.Q && pluginConfig.ENABLED_Q && e.hasStarted()) {
            e.processQ();
            return true;
        }
        if (userActionType == UserActionType.E && pluginConfig.ENABLED_E && e.hasStarted()) {
            e.processE();
            return true;
        }
        return false;
    }

    public int markCharacterForRemoval(UUID player, java.lang.String charName) {
        return playerDao.markCharacterForRemoval(player, charName);
    }

    public void gainMana(IActiveCharacter character, double manaToAdd, IRpgElement source) {
        double current = character.getMana().getValue();
        double max = character.getMana().getMaxValue();
        if (current >= max) {
            return;
        }

        CharacterManaRegainEvent event = new CharacterManaRegainEvent(character, manaToAdd, source);
        Sponge.getGame().getEventManager().post(event);
        if (event.isCancelled() || event.getAmount() <= 0) {
            return;
        }

        current += event.getAmount();
        if (current > max) current = max;

        character.getMana().setValue(current);
        Gui.displayMana(character);
    }

    public ActionResult canGainClass(IActiveCharacter character, ClassDefinition klass) {
        Map<String, PlayerClassData> classes = character.getClasses();
        if (classes.containsKey(klass.getName())) {
            return ActionResult.withErrorMessage(Localizations.ALREADY_HAS_THIS_CLASS.toText(arg("class", klass.getName())));
        }

        DependencyGraph classDependencyGraph = klass.getClassDependencyGraph();

        Set<ClassDefinition> c = character.getClasses().values().stream().map(PlayerClassData::getClassDefinition).collect(Collectors.toSet());
        boolean ok = classDependencyGraph.isValidFor(c);

        if (!ok) {
           return ActionResult.withErrorMessage(Localizations.MISSING_CLASS_DEPENDENCIES.toText());
        }

        if (NtRpgPlugin.pluginConfig.RESPECT_CLASS_SELECTION_ORDER) {
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
                    return ActionResult.withErrorMessage(Localizations.MISSING_CLASS_DEPENDENCIES.toText());
                }
                ClassDefinition classDefinition = classByType.getClassDefinition();
                if (!classDefinition.getClassDependencyGraph().isValidFor(character.getClasses()
                        .values().stream().map(PlayerClassData::getClassDefinition).collect(Collectors.toSet()))) {
                    return ActionResult.withErrorMessage(Localizations.MISSING_CLASS_DEPENDENCIES.toText());
                }
                break;
            }
        }

        return ActionResult.ok();
    }

    public ActionResult addNewClass(IActiveCharacter character, ClassDefinition klass) {
        CharacterBase characterBase = character.getCharacterBase();
        CharacterClass cc = new CharacterClass();
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
            classService.addPermissions(character, playerClassData);
            scheduleNextTick(() -> {
                recalculateSecondaryPropertiesOnly(character);
                applyGroupEffects(character, klass);
                scheduleNextTick(() -> {
                    invalidateCaches(character);
                    character.updateItemRestrictions();
                    Text message = klass.getWelcomeMessage();
                    if (message == null) {
                        message = Localizations.PLAYER_CHOOSED_CLASS.toText();
                    }
                    character.sendMessage(message);
                });
            });
        });
        return ActionResult.ok();
    }

    private void scheduleNextTick(Runnable r) {
        Sponge.getScheduler().createTaskBuilder().delay(1, TimeUnit.MILLISECONDS)
                .execute(r).submit(NtRpgPlugin.GlobalScope.plugin);
    }

    public void addSkillPoint(IActiveCharacter character, PlayerClassData playerClassData, int skillpointsPerLevel) {
        CharacterClass characterClass = character.getCharacterBase().getCharacterClass(playerClassData.getClassDefinition());
        characterClass.setSkillPoints(characterClass.getSkillPoints() + skillpointsPerLevel);
    }

    public void addPersistantSkill(IActiveCharacter character, PlayerClassData origin, CharacterSkill skill) {
        character.getCharacterBase().getCharacterSkills().add(skill);
    }

    public void addSkill(IActiveCharacter character, PlayerClassData origin, PlayerSkillContext skill) {
        character.addSkill(skill.getSkill().getId(), skill);
        character.addSkill(skill.getSkill().getName(), skill);
    }

    /**
     * Takes away one skillpoint and adds skill to player
     * Does not update the character state
     * @param character
     * @param origin
     * @param skill
     */
    public void learnSkill(IActiveCharacter character, PlayerClassData origin, ISkill skill) {
        CharacterClass clazz = origin.getCharacterClass();
        clazz.setSkillPoints(clazz.getSkillPoints() - 1);
        clazz.setUsedSkillPoints(clazz.getUsedSkillPoints() + 1);

        ClassDefinition classDef = origin.getClassDefinition();
        PlayerSkillContext einfo = new PlayerSkillContext(classDef, skill, character);
        einfo.setLevel(1);

        SkillTree skillTree = classDef.getSkillTree();
        einfo.setSkillData(skillTree.getSkills().get(skill.getId()));

        CharacterSkill skill1 = new CharacterSkill();
        skill1.setLevel(1);
        skill1.setCharacterBase(character.getCharacterBase());
        skill1.setFromClass(clazz);
        skill1.setCatalogId(skill.getId());

        addPersistantSkill(character, origin, skill1);
        addSkill(character, origin, einfo);
        skill.skillLearn(character);
        Log.info("Character " + character.getCharacterBase().getUuid() + " learned skill " + skill.getId());
    }

    public void removeTransientAttributes(Map<Attribute, Integer> bonusAttributes, IActiveCharacter character) {
        for (Map.Entry<Attribute, Integer> entry : bonusAttributes.entrySet()) {
            removeTransientAttribute(character, entry.getKey(), entry.getValue());
        }
    }

    private void removeTransientAttribute(IActiveCharacter character, Attribute key, Integer value) {
        character.getTransientAttributes().merge(key.getId(), value, (b, a) -> a - b);
    }
}

