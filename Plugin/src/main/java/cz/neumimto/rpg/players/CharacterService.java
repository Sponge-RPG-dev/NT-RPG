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

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.GroupService;
import cz.neumimto.rpg.MissingConfigurationException;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.Pair;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.effects.*;
import cz.neumimto.rpg.effects.common.def.ClickComboActionEvent;
import cz.neumimto.rpg.effects.common.def.CombatEffect;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.events.*;
import cz.neumimto.rpg.events.character.CharacterWeaponUpdateEvent;
import cz.neumimto.rpg.events.character.EventCharacterArmorPostUpdate;
import cz.neumimto.rpg.events.character.PlayerDataPreloadComplete;
import cz.neumimto.rpg.events.character.WeaponEquipEvent;
import cz.neumimto.rpg.events.party.PartyInviteEvent;
import cz.neumimto.rpg.events.skills.SkillLearnEvent;
import cz.neumimto.rpg.events.skills.SkillRefundEvent;
import cz.neumimto.rpg.events.skills.SkillUpgradeEvent;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.inventory.*;
import cz.neumimto.rpg.persistance.PlayerDao;
import cz.neumimto.rpg.persistance.model.BaseCharacterAttribute;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.persistance.model.CharacterSkill;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.players.groups.Guild;
import cz.neumimto.rpg.players.groups.PlayerGroup;
import cz.neumimto.rpg.players.groups.Race;
import cz.neumimto.rpg.players.parties.Party;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.utils.SkillTreeActionResult;
import cz.neumimto.rpg.utils.Utils;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.text.Text;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by NeumimTo on 26.12.2014.
 */
@Singleton
public class CharacterService {

	@Inject
	private SkillService skillService;

	@Inject
	private Game game;

	@Inject
	private NtRpgPlugin plugin;

	@Inject
	private PlayerDao playerDao;

	@Inject
	private InventoryService inventoryService;

	@Inject
	private GroupService groupService;

	@Inject
	private EntityService entityService;

	@Inject
	private DamageService damageService;

	@Inject
	private PropertyService propertyService;

	@Inject
	private CauseStackManager causeStackManager;

	@Inject
	private Logger logger;

	private Map<UUID, NPlayer> playerWrappers = new ConcurrentHashMap<>();
	private Map<UUID, IActiveCharacter> characters = new HashMap<>();


	@Inject
	private EffectService effectService;

	public void loadPlayerData(UUID id) {
		characters.put(id, buildDummyChar(id));
		game.getScheduler().createTaskBuilder().name("PlayerDataLoad-" + id).async().execute(() -> {
			logger.info("Loading player - " + id);
			long k = System.currentTimeMillis();
			final List<CharacterBase> playerCharacters = playerDao.getPlayersCharacters(id);
			logger.info("Finished loading of player " + id + ", loaded " + playerCharacters.size() + " characters   [" + (System.currentTimeMillis() - k) + "]ms");
			game.getScheduler().createTaskBuilder().name("Callback-PlayerDataLoad" + id).execute(() -> {
				PlayerDataPreloadComplete event = new PlayerDataPreloadComplete(id, playerCharacters);
				game.getEventManager().post(event);

			}).submit(plugin);
		}).submit(plugin);
	}


	public boolean assignPlayerToCharacter(Player pl) {
		if (pl == null) {
			return false;
		}
		if (!characters.containsKey(pl.getUniqueId())) {
			return false;
		}
		IActiveCharacter character = characters.get(pl.getUniqueId());
		if (character.isStub())
			return false;
		character.setPlayer(pl);
		if (character.getCharacterBase().getHealthScale() != null) {
			pl.offer(Keys.HEALTH_SCALE, character.getCharacterBase().getHealthScale());
		}
		inventoryService.initializeHotbar(character);
		return true;
	}

	public void updateWeaponRestrictions(IActiveCharacter character) {
		Map<ItemType, RPGItemWrapper> weapons = character.updateItemRestrictions().getAllowedWeapons();


		CharacterWeaponUpdateEvent event = new CharacterWeaponUpdateEvent(character, weapons);
		game.getEventManager().post(event);

	}

	public void updateArmorRestrictions(IActiveCharacter character) {
		Set<ItemType> allowedArmor = character.updateItemRestrictions().getAllowedArmor();

		EventCharacterArmorPostUpdate event = new EventCharacterArmorPostUpdate(character, allowedArmor);
		game.getEventManager().post(event);

	}


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
		game.getScheduler().createTaskBuilder().async().name("PlayerDataTaskSave").execute(() -> {
			Long k = System.currentTimeMillis();
			logger.info("Saving player " + base.getUuid() + " character " + base.getName());
			save(base);
			logger.info("Saved player " + base.getUuid() + " character " + base.getName() + "[" + (System.currentTimeMillis() - k) + "]ms ");
		}).submit(plugin);
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

	public NPlayer getPlayerWrapper(UUID uuid) {
		return playerWrappers.get(uuid);
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
		logger.info("Setting active character player " + uuid + " character " + character.getName());
		IActiveCharacter activeCharacter = getCharacter(uuid);
		if (activeCharacter == null) {
			characters.put(uuid, character);
		} else {
			deleteCharacterReferences(activeCharacter);
			character.setUsingGuiMod(activeCharacter.isUsingGuiMod());
			characters.put(uuid, character);
			initActiveCharacter(character);
		}
		return character;
	}

	/**
	 * Creates party
	 *
	 * @param character
	 */
	public void createParty(ActiveCharacter character) {
		CancellableEvent event = new PartyCreateEvent(character);
		game.getEventManager().post(event);
		if (!event.isCancelled()) {
			Party party = new Party(character);
			party.addPlayer(character);
		}
	}

	/**
	 * Changes leader of a party. The old leader is not removed from the party
	 *
	 * @param party
	 * @param newleader
	 */
	public void changePartyLeader(Party party, ActiveCharacter newleader) {
		party.setLeader(newleader);
	}

	/**
	 * @param party
	 * @param character
	 * @return 1 - if player is already in party, 2 player has not been invited
	 */
	public int partyJoin(Party party, ActiveCharacter character) {
		if (party.getPlayers().contains(character)) {
			return 1;
		}
		if (party.getInvites().contains(character.getPlayer().getUniqueId())) {
			party.getInvites().remove(character.getPlayer().getUniqueId());
			party.addPlayer(character);
			return 0;
		}
		return 2;
	}

	/**
	 * Invites character to a party
	 *
	 * @param party
	 * @param character
	 * @return 1 - if character is already in the party
	 * 0 - ok
	 */
	public int inviteToParty(Party party, ActiveCharacter character) {
		if (party.getPlayers().contains(character)) {
			return 1;
		}
		PartyInviteEvent event = new PartyInviteEvent(party, character);
		if (!event.isCancelled()) {
			party.getInvites().add(character.getPlayer().getUniqueId());
			return 0;
		}
		return 1;
	}

	public void initActiveCharacter(IActiveCharacter character) {
		logger.info("Initializing character " + character.getCharacterBase().getId());
		character.getPlayer().sendMessage(Text.of(Localization.CURRENT_CHARACTER.replaceAll("%1", character.getName())));
		addDefaultEffects(character);
		Set<BaseCharacterAttribute> baseCharacterAttribute = character.getCharacterBase().getBaseCharacterAttribute();
		for (BaseCharacterAttribute at : baseCharacterAttribute) {
			ICharacterAttribute attribute = propertyService.getAttribute(at.getName());
			if (attribute != null) {
				assignAttribute(character, attribute, character.getLevel());
			}
		}

		for (ExtendedNClass nClass : character.getClasses()) {
			applyGroupEffects(character, nClass.getConfigClass());
		}
		applyGroupEffects(character, character.getRace());

		updateMaxHealth(character);
		updateMaxHealth(character);
		updateWalkSpeed(character);

		damageService.recalculateCharacterWeaponDamage(character);

		inventoryService.initializeHotbar(character);
		CharacterInitializedEvent event = new CharacterInitializedEvent(character);
		game.getEventManager().post(event);

	}


	public void addDefaultEffects(IActiveCharacter character) {
		effectService.addEffect(new CombatEffect(character), character, InternalEffectSourceProvider.INSTANCE);
	}

	/**
	 * Does nothing if character is PreloadChar.
	 * If group is null nothing is changed
	 * character is saved
	 * Updates item restriction
	 *
	 * @param character
	 * @param configClass
	 * @param slot        defines primary/Secondary/... class
	 * @param race
	 * @param guild
	 */
	public void updatePlayerGroups(IActiveCharacter character, ConfigClass configClass, int slot, Race race, Guild guild) {
		if (character.isStub())
			return;
		logger.info("Initializing character " + character.getCharacterBase().getId());
		boolean k = false;
		if (configClass != null) {
			CharacterChangeGroupEvent e = new CharacterChangeClassEvent(character, configClass, slot, character.getNClass(slot));

			game.getEventManager().post(e);
			if (!e.isCancelled()) {
				k = true;
				logger.info("Processing class change - " + e);
				Map<String, String> args = new HashMap<>();
				args.put("player", character.getPlayer().getName());
				args.put("uuid", character.getPlayer().getUniqueId().toString());
				args.put("class", character.getRace().getName());
				if (character.getNClass(slot).getExitCommands() != null)
					Utils.executeCommandBatch(args, character.getNClass(slot).getExitCommands(), character.getPlayer());


				removeGroupEffects(character, character.getNClass(slot));
				character.setClass(configClass, slot);
				applyGroupEffects(character, configClass);

				args.put("class", character.getRace().getName());
				if (character.getNClass(slot).getExitCommands() != null)
					Utils.executeCommandBatch(args, character.getNClass(slot).getEnterCommands(), character.getPlayer());
			}
		}

		if (race != null) {
			CharacterChangeGroupEvent ev = new CharacterChangeRaceEvent(character, race, character.getRace());
			game.getEventManager().post(ev);
			if (!ev.isCancelled()) {
				k = true;
				logger.info("Processing race change - " + ev);
				Map<String, String> args = new HashMap<>();
				args.put("player", character.getPlayer().getName());
				args.put("uuid", character.getPlayer().getUniqueId().toString());
				args.put("race", character.getRace().getName());
				if (character.getRace().getExitCommands() != null)
					Utils.executeCommandBatch(args, character.getRace().getExitCommands(), character.getPlayer());

				removeGroupEffects(character, character.getRace());
				character.setRace(race);
				applyGroupEffects(character, race);


				args.put("race", character.getRace().getName());
				if (character.getRace().getExitCommands() != null)
					Utils.executeCommandBatch(args, character.getRace().getEnterCommands(), character.getPlayer());
			}
		}
		if (guild != null) {
			k = true;

			character.setGuild(guild);


		}
		if (k) {
			putInSaveQueue(character.getCharacterBase());
			recalculateProperties(character);
			updateArmorRestrictions(character);
			updateWeaponRestrictions(character);
			updateWalkSpeed(character);
			updateMaxHealth(character);
			updateMaxMana(character);
		}
	}

	public void removeGroupEffects(IActiveCharacter character, PlayerGroup p) {
		if (p == null) return;
		effectService.removeGlobalEffectsAsEnchantments(p.getEffects(), character, p);
	}

	public void applyGroupEffects(IActiveCharacter character, PlayerGroup p) {
		if (p == null) return;
		effectService.applyGlobalEffectsAsEnchantments(p.getEffects(), character, p);
	}

	/**
	 * updates maximal mana from character properties
	 *
	 * @param character
	 */
	public void updateMaxMana(IActiveCharacter character) {
		IReservable mana = character.getMana();
		float max_mana = getCharacterProperty(character, DefaultProperties.max_mana) - getCharacterProperty(character, DefaultProperties.reserved_mana);
		float actreserved = getCharacterProperty(character, DefaultProperties.reserved_mana);
		float reserved = getCharacterProperty(character, DefaultProperties.reserved_mana_multiplier);
		float maxval = max_mana - (actreserved * reserved);
		character.getMana().setMaxValue(maxval);
	}

	/**
	 * Updates maximal health from character properties
	 *
	 * @param character
	 */
	public void updateMaxHealth(IActiveCharacter character) {
		float max_health = getCharacterProperty(character, DefaultProperties.max_health) - getCharacterProperty(character, DefaultProperties.reserved_health);
		float actreserved = getCharacterProperty(character, DefaultProperties.reserved_health);
		float reserved = getCharacterProperty(character, DefaultProperties.reserved_health_multiplier);
		float maxval = max_health - (actreserved * reserved);
		if (maxval <= 0) {
			maxval = 1;
		}
		character.getHealth().setMaxValue(maxval);
	}


	public IActiveCharacter removeCachedWrapper(UUID uuid) {
		return removeCachedCharacter(uuid);
	}

	public IActiveCharacter removeCachedCharacter(UUID uuid) {
		return deleteCharacterReferences(characters.remove(uuid));
	}

	protected IActiveCharacter deleteCharacterReferences(IActiveCharacter character) {
		Collection<IEffectContainer<Object, IEffect<Object>>> effects = character.getEffects();
		effects.stream()
				.map(IEffectContainer::getEffects)
				.forEach(a -> a.stream().forEach(e -> effectService.stopEffect(e)));
		if (character.hasParty())
			character.getParty().removePlayer(character);
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
		return new PreloadCharacter(uuid);
	}

	private Runnable updateAll(final IActiveCharacter activeCharacter) {
		return () -> {
			updateArmorRestrictions(activeCharacter);
			updateWeaponRestrictions(activeCharacter);
			updateWalkSpeed(activeCharacter);
			updateMaxHealth(activeCharacter);
			updateMaxMana(activeCharacter);
		};
	}

	public void recalculateProperties(IActiveCharacter character) {
		Map<Integer, Float> defaults = propertyService.getDefaults();
		float[] arr = character.getCharacterProperties();
		float[] lvl = character.getCharacterLevelProperties();
		float val = 0;
		for (int i = 0; i < arr.length; i++) {
			if (character.getPrimaryClass().getConfigClass().getPropBonus().containsKey(i)) {
				val += character.getPrimaryClass().getConfigClass().getPropBonus().get(i);
			}
			if (character.getRace().getPropBonus().containsKey(i)) {
				val += character.getRace().getPropBonus().get(i);
			}
			if (val == 0 && defaults.containsKey(i)) {
				val = defaults.get(i);
			}
			arr[i] = val;
			val = 0;
			if (character.getPrimaryClass().getConfigClass().getPropLevelBonus().containsKey(i)) {
				val += character.getPrimaryClass().getConfigClass().getPropLevelBonus().get(i);
			}
			if (character.getRace().getPropLevelBonus().containsKey(i)) {
				val += character.getRace().getPropLevelBonus().get(i);
			}
			lvl[i] = val;
		}
		//todo
	}

	private void initSkills(ActiveCharacter activeCharacter) {
		for (ExtendedSkillInfo extendedSkillInfo : activeCharacter.getSkills().values()) {
			extendedSkillInfo.getSkill().onCharacterInit(activeCharacter, extendedSkillInfo.getLevel());
		}
	}


	private void resolveSkillsCds(CharacterBase characterBase, IActiveCharacter character) {
		character.getCooldowns();


		Map<String, Integer> skills = new HashMap<>();
		Set<CharacterSkill> characterSkills = characterBase.getCharacterSkills();
		characterSkills.stream().forEach(characterSkill -> skills.put(characterSkill.getName(), characterSkill.getLevel()));
		Map<String, Long> cooldowns = characterBase.getCharacterCooldowns();

		long l = System.currentTimeMillis();
		cooldowns.entrySet().removeIf(next -> next.getValue() <= l);

		characterBase.getCharacterCooldowns().clear();
		for (Map.Entry<String, Integer> stringIntegerEntry : skills.entrySet()) {
			ExtendedSkillInfo info = new ExtendedSkillInfo();
			ISkill skill = skillService.getSkill(stringIntegerEntry.getKey());
			if (skill != null) {
				info.setLevel(stringIntegerEntry.getValue());
				info.setSkill(skill);
				SkillData info1 = character.getPrimaryClass().getConfigClass().getSkillTree().getSkills().get(skill.getName());
				if (info1 != null) {

					info.setSkillData(info1);
					character.addSkill(info.getSkill().getName(), info);
				}
			}
		}

	}

	//todo merge

	/**
	 * @param player
	 * @param characterBase
	 * @return
	 */
	public ActiveCharacter buildActiveCharacterAsynchronously(Player player, CharacterBase characterBase) {
		characterBase = playerDao.fetchCharacterBase(characterBase);
		ActiveCharacter activeCharacter = new ActiveCharacter(player, characterBase);
		activeCharacter.setRace(groupService.getRace(characterBase.getRace()));

		activeCharacter.setPrimaryClass(groupService.getNClass(characterBase.getPrimaryClass()));
		groupService.addAllPermissions(activeCharacter, activeCharacter.getRace());
		groupService.addAllPermissions(activeCharacter, activeCharacter.getPrimaryClass().getConfigClass());
		String s = activeCharacter.getPrimaryClass().getConfigClass().getName();
		Optional<CharacterClass> first = characterBase.getCharacterClasses()
				.stream()
				.filter(a -> s.equalsIgnoreCase(a.getName()))
				.findFirst();
		if (first.isPresent()) {
			CharacterClass cc = first.get();
			Double d = cc.getExperiences();
			if (d != null) {
				activeCharacter.getPrimaryClass().setExperiences(d);
			}
			recalculateProperties(activeCharacter);
			resolveSkillsCds(characterBase, activeCharacter);
			initSkills(activeCharacter);
			Double exp = d;
			if (exp != null) {
				addExperiences(activeCharacter, exp, activeCharacter.getPrimaryClass(), true);
			}
		}
		game.getScheduler().createTaskBuilder().name("FetchCharBaseDataCallback-" + player.getUniqueId())
				.execute(updateAll(activeCharacter)
				).submit(plugin);
		return activeCharacter;

	}

	/**
	 * @param uniqueId player's uuid
	 * @return 1 - if player reached maximal amount of characters
	 * 2 - if player has character with same name
	 * 0 - ok
	 */
	public int canCreateNewCharacter(UUID uniqueId, String name) {
		//todo use db query
		List<CharacterBase> list = getPlayersCharacters(uniqueId);
		if (list.size() >= PluginConfig.PLAYER_MAX_CHARS) {
			return 1;
		}
		if (list.stream().anyMatch(c -> c.getName().equalsIgnoreCase(name))) {
			return 2;
		}
		return 0;
	}

	/**
	 * Invokes synchronous task which activates character for player
	 *
	 * @param uuid
	 * @param character
	 */
	public void setActiveCharacterSynchronously(UUID uuid, IActiveCharacter character) {
		game.getScheduler().createTaskBuilder().name("SyncTaskActivateCharOf" + uuid).execute(() -> {
			setActiveCharacter(uuid, character);
		}).submit(plugin);
	}

	/**
	 * @param character
	 * @param skill
	 * @return 1 if character has no skillpoints,
	 * 2 if character has not learned the skill yet
	 * 3 if skill requires higher level. The formula is MinPlayerLevel + skillLevel > characterlevel
	 * (this restricts abusing when player might rush certain skills in a skilltree and spending all skillpoints on a single skill).
	 * 4 if skill is on max level
	 * 5 - if event is cancelled
	 * 0 - ok
	 */
	public Pair<SkillTreeActionResult, SkillTreeActionResult.Data> upgradeSkill(IActiveCharacter character, ISkill skill) {
		Pair<SkillTreeActionResult, SkillTreeActionResult.Data> p = new Pair<>();

		CharacterClass cc = null;
		Set<ExtendedNClass> classes = character.getClasses();

		for (ExtendedNClass aClass : classes) {
			Map<String, SkillData> skills = aClass.getConfigClass().getSkillTree().getSkills();
			if (skills.containsKey(skill.getName())) {
				cc = character.getCharacterBase().getCharacterClass(aClass.getConfigClass());
				break;
			}
		}


		if (cc.getSkillPoints() < 1) {
			p.key = SkillTreeActionResult.NO_SKILLPOINTS;
			p.value = new SkillTreeActionResult.Data(skill.getName());
			return p;
		}
		ExtendedSkillInfo extendedSkillInfo = character.getSkillInfo(skill);
		if (extendedSkillInfo == null) {
			p.key = SkillTreeActionResult.NOT_LEARNED_SKILL;
			p.value = new SkillTreeActionResult.Data(skill.getName());
			return p;
		}
		int minlevel = extendedSkillInfo.getLevel() + extendedSkillInfo.getSkillData().getMinPlayerLevel();
		if (minlevel > character.getLevel()) {
			p.key = SkillTreeActionResult.SKILL_REQUIRES_HIGHER_LEVEL;
			p.value = new SkillTreeActionResult.Data(skill.getName(), minlevel + "");
			return p;
		}
		if (extendedSkillInfo.getLevel() + 1 > extendedSkillInfo.getSkillData().getMaxSkillLevel()) {
			p.key = SkillTreeActionResult.SKILL_ON_MAX_LEVEL;
			p.value = new SkillTreeActionResult.Data(skill.getName(), extendedSkillInfo.getLevel() + "");
			return p;
		}
		SkillUpgradeEvent event = new SkillUpgradeEvent(character, skill, extendedSkillInfo.getLevel() + 1);
		game.getEventManager().post(event);
		if (event.isCancelled()) {
			p.key = SkillTreeActionResult.UNKNOWN;
			p.value = new SkillTreeActionResult.Data("");
			return p;
		}
		extendedSkillInfo.setLevel(event.getLevel());
		int s = cc.getSkillPoints();
		cc.setSkillPoints(s - 1);
		cc.setUsedSkillPoints(s + 1);
		CharacterSkill characterSkill = character.getCharacterBase().getCharacterSkill(skill);
		characterSkill.setLevel(extendedSkillInfo.getLevel());
		skill.skillUpgrade(character, event.getLevel());
		p.key = SkillTreeActionResult.UPGRADED;
		p.value = new SkillTreeActionResult.Data(skill.getName(), event.getLevel() + "");
		return p;
	}

	/**
	 * @param character
	 * @param skill
	 */
	public Pair<SkillTreeActionResult, SkillTreeActionResult.Data> characterLearnskill(IActiveCharacter character, ISkill skill, SkillTree skillTree) {
		Pair<SkillTreeActionResult, SkillTreeActionResult.Data> p = new Pair<>();

		ExtendedNClass nClass = null;
		for (ExtendedNClass extendedNClass : character.getClasses()) {
			if (extendedNClass.getConfigClass().getSkillTree() == skillTree) {
				nClass = extendedNClass;
				break;
			}
		}

		if (nClass == null) {
			p.key = SkillTreeActionResult.NO_ACCESS_TO_SKILL;
			return p;
		}
		int avalaibleSkillpoints = 0;
		CharacterClass clazz = character.getCharacterBase().getCharacterClass(nClass.getConfigClass());
		if (clazz == null) {
			throw new MissingConfigurationException("Class=" + nClass.getConfigClass().getName() + ". Renamed?");
		}
		//todo fetch from db
		avalaibleSkillpoints = clazz.getSkillPoints();
		if (avalaibleSkillpoints < 1) {
			p.key = SkillTreeActionResult.NO_SKILLPOINTS;
			p.value = new SkillTreeActionResult.Data(skill.getName());
			return p;
		}
		SkillData info = skillTree.getSkills().get(skill.getName());
		if (info == null) {
			p.key = SkillTreeActionResult.SKILL_IS_NOT_IN_A_TREE;
			p.value = new SkillTreeActionResult.Data(skill.getName());
			return p;
		}
		if (character.getLevel() < info.getMinPlayerLevel()) {
			p.key = SkillTreeActionResult.SKILL_REQUIRES_HIGHER_LEVEL;
			p.value = new SkillTreeActionResult.Data(skill.getName(), info.getMinPlayerLevel() + "");
			return p;
		}
		for (SkillData skillData : info.getHardDepends()) {
			if (!character.hasSkill(skillData.getSkillName())) {
				p.key = SkillTreeActionResult.DOES_NOT_MATCH_CHAIN;
				//todo
				p.value = new SkillTreeActionResult.Data(skill.getName(), info.getMinPlayerLevel() + "");
				return p;
			}
		}
		boolean hasSkill = info.getSoftDepends().isEmpty();
		for (SkillData skillData : info.getSoftDepends()) {
			if (character.hasSkill(skillData.getSkillName())) {
				hasSkill = true;
				break;
			}
		}
		if (!hasSkill) {
			p.key = SkillTreeActionResult.DOES_NOT_MATCH_CHAIN;
			//todo
			p.value = new SkillTreeActionResult.Data(skill.getName());
			return p;
		}
		for (SkillData skillData : info.getConflicts()) {
			if (character.hasSkill(skillData.getSkillName())) {
				p.value = new SkillTreeActionResult.Data(skill.getName());
				p.key = SkillTreeActionResult.SKILL_CONFCLITS;
			}
		}

		if (character.hasSkill(skill.getName())) {
			p.key = SkillTreeActionResult.ALREADY_LEARNED;

			p.value = new SkillTreeActionResult.Data(skill.getName());
			return p;
		}
		SkillLearnEvent event = new SkillLearnEvent(character, skill);
		game.getEventManager().post(event);
		if (event.isCancelled()) {
			p.key = SkillTreeActionResult.UNKNOWN;
			p.value = new SkillTreeActionResult.Data("");
			return p;
		}


		clazz.setSkillPoints(avalaibleSkillpoints - 1);
		clazz.setUsedSkillPoints(avalaibleSkillpoints + 1);

		ExtendedSkillInfo einfo = new ExtendedSkillInfo();
		einfo.setLevel(1);
		einfo.setSkill(skill);
		einfo.setSkillData(skillTree.getSkills().get(skill.getName()));
		character.addSkill(skill.getName().toLowerCase(), einfo);

		CharacterSkill skill1 = new CharacterSkill();
		skill1.setLevel(1);
		skill1.setCharacterBase(character.getCharacterBase());
		skill1.setFromClass(clazz);
		skill1.setName(skill.getName());
		character.getCharacterBase().getCharacterSkills().add(skill1);

		putInSaveQueue(character.getCharacterBase());
		skill.skillLearn(character);
		p.key = SkillTreeActionResult.LEARNED;
		p.value = new SkillTreeActionResult.Data(skill.getName());
		return p;
	}

	/**
	 * @param character
	 * @param skill
	 * @param configClass
	 * @return 1 - if character has not a single skillpoint in the skill
	 * 2 - if one or more skills are on a path in a skilltree after the skill.
	 * 3 - SkillRefundEvent was cancelled
	 * 4 - Cant refund skill-tree path
	 * 0 - ok
	 */
	public int refundSkill(IActiveCharacter character, ISkill skill, ConfigClass configClass) {
		ExtendedSkillInfo skillInfo = character.getSkillInfo(skill);
		if (skillInfo == null)
			return 1;
		SkillTree skillTree = configClass.getSkillTree();
		SkillData info = skillTree.getSkills().get(skill.getName());
		for (SkillData info1 : info.getDepending()) {
			ExtendedSkillInfo e = character.getSkill(info1.getSkillName());
			if (e != null)
				return 2;
		}
		CancellableEvent event = new SkillRefundEvent(character, skill);
		game.getEventManager().post(event);
		if (event.isCancelled()) {
			return 3;
		}
		if (skill instanceof SkillTreeSpecialization && PluginConfig.PATH_NODES_SEALED) {
			return 4;
		}
		int level = skillInfo.getLevel();
		skill.skillRefund(character);
		CharacterClass cc = character.getCharacterBase().getCharacterClass(configClass);
		int skillPoints = cc.getSkillPoints();
		cc.setSkillPoints(skillPoints + level);
		cc.setUsedSkillPoints(skillPoints - level);
		putInSaveQueue(character.getCharacterBase());
		return 0;
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
			character.getRemoveAllSkills();
			characterBase.getCharacterCooldowns().clear();
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
	 * Updates character walkspeed to match DefaultProperties.walk_speed property
	 *
	 * @param entity
	 */
	public void updateWalkSpeed(IEffectConsumer entity) {
		double speed = entityService.getEntityProperty(entity, DefaultProperties.walk_speed);
		entity.getEntity().offer(Keys.WALKING_SPEED, speed);
		if (PluginConfig.DEBUG)
			logger.info(entity + " setting walk speed to " + speed);
	}

	/**
	 * Adds stats/attribute points to the character and saves the character
	 *
	 * @param character      - character object
	 * @param skillpoint     - skillpoints to be added
	 * @param attributepoint - attribute points to be added
	 */
	public void characterAddPoints(IActiveCharacter character, ConfigClass clazz, int skillpoint, int attributepoint) {
		CharacterClass cc = character.getCharacterBase().getCharacterClass(clazz);
		cc.setSkillPoints(cc.getSkillPoints() + skillpoint);
		character.setAttributePoints(character.getAttributePoints() + attributepoint);
		Gui.sendMessage(character, Localization.CHARACTER_GAINED_POINTS.replaceAll("%1", skillpoint + "").replaceAll("%2", "" + attributepoint));
		putInSaveQueue(character.getCharacterBase());
	}

	public int equipWeapon(IActiveCharacter character, Weapon weapon, boolean isOffhand) {
		if (!character.canUse(weapon.getItemType())) {
			return 1;
		}
		WeaponEquipEvent event = null;
		if (isOffhand) {
			event = new WeaponEquipEvent(character, weapon, character.getOffHand());
		} else {
			event = new WeaponEquipEvent(character, weapon, character.getMainHand());
		}
		game.getEventManager().post(event);
		if (event.isCancelled())
			return 2;
		Set<IGlobalEffect> effects = event.getLastItem().getEffects().keySet();
		effects.forEach(g -> effectService.removeEffect(g.getName(), character, weapon));
		Map<IGlobalEffect, String> toadd = event.getNewItem().getEffects();
		effectService.applyGlobalEffectsAsEnchantments(toadd, character, weapon);
		return 0;
	}

	public int changeEquipedArmor(IActiveCharacter character, EquipmentType type, Weapon armor) {
		/*if (!character.canWear(armor.getItemType())) {
            return 1;
        }*/

		Weapon armor1 = character.getEquipedArmor().get(type);
		if (armor1 != null) {
			armor1.getEffects().keySet().forEach(g -> effectService.removeEffect(g.getName(), character, armor));
			character.getEquipedArmor().remove(type);
		}
		if (armor != null) {
			effectService.applyGlobalEffectsAsEnchantments(armor.getEffects(), character, armor);
		}

		return 0;
	}

	public boolean canUseItemType(IActiveCharacter character, RPGItemType type) {
		return character.canUse(type);
	}

	public void addExperiences(IActiveCharacter character, double exp, ExperienceSource source) {
		Set<ExtendedNClass> classes = character.getClasses();
		for (ExtendedNClass aClass : classes) {
			ConfigClass configClass = aClass.getConfigClass();
			if (configClass.hasExperienceSource(source)) {
				int maxlevel = configClass.getLevels().length - 1;
				if (aClass.getLevel() > maxlevel)
					continue;
				addExperiences(character, exp, aClass, false);
			}
		}
	}


	public void addExperiences(IActiveCharacter character, double exp, ExtendedNClass aClass, boolean onlyinit) {
		if (!aClass.takesExp() && !onlyinit) {
			return;
		}

		int level = aClass.getLevel();

		if (!onlyinit)
			exp = exp * getCharacterProperty(character, DefaultProperties.experiences_mult);
		double total = aClass.getExperiences();
		double lvlexp = aClass.getExperiencesFromLevel();
		double[] levels = aClass.getConfigClass().getLevels();
		if (levels == null)
			return;
		double levellimit = levels[level];

		double newcurrentexp = lvlexp + exp;

		while (newcurrentexp > levellimit) {
			level++;
			if (!onlyinit) {
				Gui.showLevelChange(character, aClass, level);
				CharacterGainedLevelEvent event = new CharacterGainedLevelEvent(character, aClass, level, aClass.getConfigClass().getSkillpointsperlevel(), aClass.getConfigClass().getAttributepointsperlevel());
				event.getaClass().setLevel(event.getLevel());
				game.getEventManager().post(event);
				characterAddPoints(character, aClass.getConfigClass(), event.getSkillpointsPerLevel(), event.getAttributepointsPerLevel());
			}
			groupService.addPermissions(character, character.getRace());
			groupService.addPermissions(character, character.getPrimaryClass().getConfigClass());
			aClass.setExperiencesFromLevel(0);
			if (!aClass.takesExp()) {
				break;
			}
			if (level > levels.length - 1) {
				break;
			}
			newcurrentexp = newcurrentexp - levellimit;
			levellimit = levels[level];

		}

		if (!onlyinit) {
			aClass.setExperiences(newcurrentexp);
			aClass.setExperiencesFromLevel(newcurrentexp);
		} else {
			aClass.setExperiencesFromLevel(newcurrentexp);
		}
		Gui.showExpChange(character, aClass.getConfigClass().getName(), exp);
		aClass.setLevel(level);
	}


	public void assignAttribute(IActiveCharacter character, ICharacterAttribute attribute, int levels) {
		Map<Integer, Float> integerFloatMap = attribute.affectsProperties();
		for (Map.Entry<Integer, Float> entry : integerFloatMap.entrySet()) {
			character.getCharacterProperties()[entry.getKey()] = character
					.getCharacterProperties()[entry.getKey()] + entry.getValue() * levels;
		}
	}

	/**
	 * @param character
	 * @param attribute
	 * @param i
	 * @return
	 */
	public int addAttribute(IActiveCharacter character, ICharacterAttribute attribute, int i) {
		int attributePoints = character.getCharacterBase().getAttributePoints();
		if (attributePoints - i <= 0) {
			return 1;
		}
		CharacterEvent event = new CharacterAttributeChange(character, i);
		game.getEventManager().post(event);
		if (event.isCancelled()) {
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
		return 0;
	}

	public void addAttribute(IActiveCharacter character, ICharacterAttribute attribute) {
		addAttribute(character, attribute, 1);
	}

	public void addTemporalAttribute(IActiveCharacter character, ICharacterAttribute attribute, int amount) {
		character.getTransientAttributes().merge(attribute.getId(), amount, (a, b) -> a + b);
	}

	/**
	 * sponge is creating new player object each time a player is (re)spawned @link https://github.com/SpongePowered/SpongeCommon/commit/384180f372fa233bcfc110a7385f43df2a85ef76
	 * character object is heavy, lets do not recreate its instance just reasign player and effects
	 */
	public void respawnCharacter(IActiveCharacter character, Player pl) {
		Iterator<IEffectContainer<Object, IEffect<Object>>> iterator = character.getEffects().iterator();
		while (iterator.hasNext()) {
			IEffectContainer<Object, IEffect<Object>> next = iterator.next();
			next.forEach(q -> effectService.stopEffect(q));
			iterator.remove();
		}
		assignPlayerToCharacter(pl);

		for (ExtendedNClass nClass : character.getClasses()) {
			applyGroupEffects(character, nClass.getConfigClass());
		}

		applyGroupEffects(character, character.getRace());


		character.updateSelectedHotbarSlot();
		character.getMana().setValue(0);
		inventoryService.cancelSocketing(character);
		addDefaultEffects(character);
		updateMaxHealth(character);
		updateAll(character).run();
		inventoryService.initializeHotbar(character);
		inventoryService.initializeArmor(character);
		damageService.recalculateCharacterWeaponDamage(character);

		Sponge.getScheduler().createTaskBuilder().execute(() -> {
			Double d = character.getEntity().get(Keys.MAX_HEALTH).get();
			character.getEntity().offer(Keys.MAX_HEALTH, d);
		}).delay(3, TimeUnit.SECONDS).submit(plugin);
	}

	/**
	 * Unlike ActiveCharacter#getProperty this method checks for maximal allowed value, defined in configfile.
	 *
	 * @see PropertyService#loadMaximalServerPropertyValues()
	 */
	public float getCharacterProperty(IActiveCharacter character, int index) {
		return Math.min(propertyService.getMaxPropertyValue(index), character.getProperty(index));
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
		IEffectContainer effect = character.getEffect(ClickComboActionEvent.name);
		if (effect == null)
			return false;
		ClickComboActionEvent e = (ClickComboActionEvent) effect;
		if (userActionType == UserActionType.L && e.hasStarted()) {
			e.processLMB();
			return false;
		}
		if (userActionType == UserActionType.R) {
			e.processRMB();
			return false;
		}
		if (userActionType == UserActionType.Q && PluginConfig.ENABLED_Q && e.hasStarted()) {
			e.processQ();
			return true;
		}
		if (userActionType == UserActionType.E && PluginConfig.ENABLED_E && e.hasStarted()) {
			e.processE();
			return true;
		}
		return false;
	}
}

