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

import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.effects.EffectContainer;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.inventory.RPGItemType;
import cz.neumimto.rpg.inventory.items.types.CustomItem;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.players.groups.Guild;
import cz.neumimto.rpg.players.groups.PlayerGroup;
import cz.neumimto.rpg.players.groups.Race;
import cz.neumimto.rpg.players.parties.Party;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.SkillTreeSpecialization;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;

import java.util.*;

/**
 * Created by NeumimTo on 23.7.2015.
 */
public class PreloadCharacter implements IActiveCharacter {

	static float[] characterProperties = new float[PropertyService.LAST_ID];
	IReservable mana = new Mana(this);
	UUID uuid;
	Health health = new HealthStub(this);
	private boolean isusinggui;
	private Player player;

	public PreloadCharacter(UUID uuid) {
		this.uuid = uuid;
		mana.setMaxValue(0);
	}


	@Override
	public boolean isFriendlyTo(IActiveCharacter character) {
		return false;
	}

	@Override
	public void setCharacterLevelProperty(int index, float value) {

	}

	@Override
	public float[] getCharacterLevelProperties() {
		return characterProperties;
	}

	@Override
	public void setCharacterLevelProperties(float[] arr) {

	}

	@Override
	public Map<String, Integer> getTransientAttributes() {
		return null;
	}

	@Override
	public boolean isInvulnerable() {
		return PluginConfig.ALLOW_COMBAT_FOR_CHARACTERLESS_PLAYERS;
	}

	@Override
	public void setInvulnerable(boolean b) {

	}

	@Override
	public Map<Integer, CustomItem> getEquipedInventorySlots() {
		return Collections.emptyMap();
	}

	@Override
	public float getCharacterPropertyWithoutLevel(int index) {
		return 0;
	}

	@Override
	public double getBaseWeaponDamage(RPGItemType type) {
		return 0;
	}

	@Override
	public String getName() {
		return "None";
	}

	@Override
	public int getLevel() {
		return 0;
	}

	@Override
	public boolean isStub() {
		return true;
	}

	@Override
	public float[] getCharacterProperties() {
		return characterProperties;
	}

	@Override
	public void setProperties(float[] arr) {

	}

	@Override
	public boolean canUse(RPGItemType weaponItemType) {
		return false;
	}

	@Override
	public double getWeaponDamage() {
		return 0;
	}

	@Override
	public void setWeaponDamage(double damage) {

	}

	@Override
	public double getArmorValue() {
		return 0;
	}

	@Override
	public void setArmorValue(double value) {

	}

	@Override
	public boolean hasPreferedDamageType() {
		return false;
	}

	@Override
	public DamageType getDamageType() {
		return DamageTypes.ATTACK;
	}

	@Override
	public void setDamageType(DamageType damageType) {

	}

	@Override
	public void updateLastKnownLocation(int x, int y, int z, String name) {

	}

	@Override
	public Map<String, IEffectContainer<Object, IEffect<Object>>> getEffectMap() {
		return Collections.emptyMap();
	}

	@Override
	public float getProperty(int index) {
		if (index == DefaultProperties.walk_speed) { //let player move around even without character
			return 0.2f;
		}
		return 0;
	}

	@Override
	public void setProperty(int index, float value) {

	}

	@Override
	public double getMaxMana() {
		return 0;
	}

	@Override
	public void setMaxMana(float mana) {

	}

	@Override
	public void setMaxHealth(float maxHealth) {

	}

	@Override
	public void setHealth(float mana) {

	}

	@Override
	public IReservable getMana() {
		return mana;
	}

	@Override
	public void setMana(IReservable mana) {

	}


	@Override
	public void setHealth(IReservable health) {

	}

	@Override
	public IReservable getHealth() {
		return health;
	}

	@Override
	public double getExperiencs() {
		return 0;
	}

	@Override
	public void addExperiences(double exp, ExperienceSource source) {

	}

	@Override
	public Player getPlayer() {
		if (this.player == null) {
			Optional<Player> player = Sponge.getServer().getPlayer(uuid);
			if (player.isPresent()) {
				this.player = player.get();
			} else {
				throw new PlayerNotInGameException(String.format("Player object with uuid=%s has not been constructed yet. Calling PreloadCharacter.getCharacter in a wrong state"), this);
			}
		}
		return this.player;
	}

	@Override
	public void setPlayer(Player pl) {

	}

	@Override
	public void resetRightClicks() {

	}

	@Override
	public int getAttributePoints() {
		return 0;
	}

	@Override
	public void setAttributePoints(int attributePoints) {

	}

	@Override
	public Integer getAttributeValue(String name) {
		return 0;
	}

	@Override
	public ExtendedNClass getPrimaryClass() {
		return ExtendedNClass.Default;
	}

	@Override
	public void setPrimaryClass(ConfigClass clazz) {

	}

	@Override
	public Map<String, Long> getCooldowns() {
		return Collections.emptyMap();
	}

	@Override
	public boolean hasCooldown(String thing) {
		return true;
	}


	@Override
	public Set<RPGItemType> getAllowedArmor() {
		return Collections.emptySet();
	}

	@Override
	public boolean canWear(RPGItemType armor) {
		return false;
	}

	@Override
	public Map<ItemType, RPGItemWrapper> getAllowedWeapons() {
		return Collections.emptyMap();
	}

	@Override
	public Map<EntityType, Double> getProjectileDamages() {
		return Collections.emptyMap();
	}

	@Override
	public Set<ExtendedNClass> getClasses() {
		return Collections.emptySet();
	}

	@Override
	public ConfigClass getNClass(int index) {
		return ConfigClass.Default;
	}

	@Override
	public Race getRace() {
		return Race.Default;
	}

	@Override
	public void setRace(Race race) {

	}

	@Override
	public Guild getGuild() {
		return null;
	}

	@Override
	public void setGuild(Guild guild) {

	}

	@Override
	public IActiveCharacter updateItemRestrictions() {
		return this;
	}

	@Override
	public CharacterBase getCharacterBase() {
		return new CharacterBase();
	}

	@Override
	public void setClass(ConfigClass nclass, int slot) {

	}

	@Override
	public double getBaseProjectileDamage(EntityType id) {
		return 0;
	}

	@Override
	public Collection<IEffectContainer<Object, IEffect<Object>>> getEffects() {
		return Collections.emptySet();
	}

	@Override
	public EffectContainer getEffect(String cl) {
		return null;
	}

	@Override
	public boolean hasEffect(String cl) {
		return false;
	}

	@Override
	public void addEffect(IEffect effect) {

	}

	@Override
	public void removeEffect(String cl) {

	}

	@Override
	public void addPotionEffect(PotionEffectType p, int amplifier, long duration) {

	}

	@Override
	public void addPotionEffect(PotionEffectType p, int amplifier, long duration, boolean partciles) {

	}

	@Override
	public void removePotionEffect(PotionEffectType type) {

	}

	@Override
	public boolean hasPotionEffect(PotionEffectType type) {
		return false;
	}

	@Override
	public void addPotionEffect(PotionEffect e) {

	}

	@Override
	public void sendMessage(String message) {

	}

	@Override
	public void sendMessage(ChatType chatType, Text message) {

	}

	@Override
	public Map<String, ExtendedSkillInfo> getSkills() {
		return Collections.emptyMap();
	}

	@Override
	public ExtendedSkillInfo getSkillInfo(ISkill skill) {
		return ExtendedSkillInfo.Empty;
	}

	@Override
	public boolean hasSkill(String name) {
		return false;
	}

	@Override
	public ExtendedSkillInfo getSkillInfo(String s) {
		return ExtendedSkillInfo.Empty;
	}

	@Override
	public boolean isSilenced() {
		return true;
	}

	@Override
	public void addSkill(String name, ExtendedSkillInfo info) {

	}

	@Override
	public ExtendedSkillInfo getSkill(String skillName) {
		return ExtendedSkillInfo.Empty;
	}

	@Override
	public void getRemoveAllSkills() {

	}

	@Override
	public boolean hasParty() {
		return false;
	}

	@Override
	public boolean isInPartyWith(IActiveCharacter character) {
		return false;
	}


	@Override
	public Party getParty() {
		return new Party(this);
	}

	@Override
	public void setParty(Party party) {

	}

	@Override
	public Party getPendingPartyInvite() {
		return null;
	}

	@Override
	public void setPendingPartyInvite(Party party) {

	}

	@Override
	public boolean isUsingGuiMod() {
		return isusinggui;
	}

	@Override
	public void setUsingGuiMod(boolean b) {
		isusinggui = b;
	}

	@Override
	public boolean isPartyLeader() {
		return false;
	}

	@Override
	public double getHp() {
		return health.getValue();
	}

	@Override
	public void setHp(double d) {
		health.setValue(d);
	}

	@Override
	public Player getEntity() {
		return getPlayer();
	}


	@Override
	public MessageType getPreferedMessageType() {
		return MessageType.CHAT;
	}

	@Override
	public void setPreferedMessageType(MessageType type) {

	}

	@Override
	public boolean hasClass(PlayerGroup configClass) {
		return false;
	}

	@Override
	public List<Integer> getSlotsToReinitialize() {
		return Collections.emptyList();
	}

	@Override
	public void setSlotsToReinitialize(List<Integer> slotsToReinitialize) {

	}


	@Override
	public boolean isDetached() {
		return true;
	}

	@Override
	public Map<String, SkillTreeViewModel> getSkillTreeViewLocation() {
		return Collections.emptyMap();
	}

	@Override
	public SkillTreeViewModel getLastTimeInvokedSkillTreeView() {
		return null;
	}

	@Override
	public void addSkillTreeSpecialization(SkillTreeSpecialization specialization) {

	}

	@Override
	public void removeSkillTreeSpecialization(SkillTreeSpecialization specialization) {

	}

	@Override
	public boolean hasSkillTreeSpecialization(SkillTreeSpecialization specialization) {
		return false;
	}

	@Override
	public Set<Integer> getSlotsCannotBeEquiped() {
		return Collections.emptySet();
	}

	@Override
	public CustomItem getMainHand() {
		return null;
	}

	@Override
	public void setMainHand(CustomItem customItem, int slot) {

	}

	@Override
	public CustomItem getOffHand() {
		return null;
	}

	@Override
	public void setOffHand(CustomItem customItem) {

	}
}
