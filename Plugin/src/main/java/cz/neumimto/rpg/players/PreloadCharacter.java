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

import cz.neumimto.core.localization.Arg;
import cz.neumimto.core.localization.LocalizableParametrizedText;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.inventory.ManagedSlot;
import cz.neumimto.rpg.api.inventory.RpgInventory;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.common.entity.PropertyServiceImpl;
import cz.neumimto.rpg.common.inventory.RpgInventoryImpl;
import cz.neumimto.rpg.effects.EffectContainer;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.entities.IReservable;
import cz.neumimto.rpg.persistance.model.EquipedSlot;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.players.parties.Party;
import cz.neumimto.rpg.properties.DefaultProperties;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.tree.SkillTreeSpecialization;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;

import java.util.*;

import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;

/**
 * Created by NeumimTo on 23.7.2015.
 */
public class PreloadCharacter implements IActiveCharacter {

	private static float[] characterProperties = new float[PropertyServiceImpl.LAST_ID];
	private IReservable mana = new CharacterMana(this);
	private UUID uuid;
	private CharacterHealth health = new CharacterHealthStub(this);
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
	public float[] getSecondaryProperties() {
		return characterProperties;
	}

	@Override
	public void setSecondaryProperties(float[] arr) {

	}

	@Override
	public Map<java.lang.String, Integer> getTransientAttributes() {
		return null;
	}

	@Override
	public boolean isInvulnerable() {
		return pluginConfig.ALLOW_COMBAT_FOR_CHARACTERLESS_PLAYERS;
	}

	@Override
	public void setInvulnerable(boolean b) {

	}

	@Override
	public float getCharacterPropertyWithoutLevel(int index) {
		return 0;
	}

	@Override
	public double getBaseWeaponDamage(RpgItemType type) {
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
	public float[] getPrimaryProperties() {
		return characterProperties;
	}

	@Override
	public boolean canUse(RpgItemType weaponItemType, HandType type) {
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
	public void updateLastKnownLocation(int x, int y, int z, java.lang.String name) {

	}

	@Override
	public Map<java.lang.String, IEffectContainer<Object, IEffect<Object>>> getEffectMap() {
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
	public IReservable getHealth() {
		return health;
	}

	@Override
	public void setHealth(IReservable health) {

	}

	@Override
	public Player getPlayer() {
		if (this.player == null) {
			Optional<Player> player = Sponge.getServer().getPlayer(uuid);
			if (player.isPresent()) {
				this.player = player.get();
			} else {
				throw new PlayerNotInGameException(String.format(
						"Player object with uuid=%s has not been constructed yet. Calling PreloadCharacter.getCharacter in a wrong state"), this);
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
	public Integer getAttributeValue(java.lang.String name) {
		return 0;
	}

	@Override
	public Map<java.lang.String, Long> getCooldowns() {
		return Collections.emptyMap();
	}

	@Override
	public boolean hasCooldown(java.lang.String thing) {
		return true;
	}


	@Override
	public Set<RpgItemType> getAllowedArmor() {
		return Collections.emptySet();
	}

	@Override
	public boolean canWear(RpgItemType armor) {
		return false;
	}

	@Override
	public Map<RpgItemType, Double> getAllowedWeapons() {
		return Collections.emptyMap();
	}

	@Override
	public Map<EntityType, Double> getProjectileDamages() {
		return Collections.emptyMap();
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
	public PlayerClassData getPrimaryClass() {
		return null;
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
	public EffectContainer getEffect(java.lang.String cl) {
		return null;
	}

	@Override
	public boolean hasEffect(java.lang.String cl) {
		return false;
	}

	@Override
	public void addEffect(IEffect effect) {

	}

	@Override
	public void removeEffect(java.lang.String cl) {

	}

	@Override
	public void addPotionEffect(PotionEffectType p, int amplifier, long duration) {

	}

	@Override
	public void addPotionEffect(PotionEffectType p, int amplifier, long duration, boolean particles) {

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
	public void sendMessage(LocalizableParametrizedText message, Arg arg) {
		getPlayer().sendMessage(message.toText(arg));
	}

	@Override
	public void sendMessage(ChatType chatType, Text message) {

	}

	@Override
	public Map<java.lang.String, PlayerSkillContext> getSkills() {
		return Collections.emptyMap();
	}

	@Override
	public PlayerSkillContext getSkillInfo(ISkill skill) {
		return PlayerSkillContext.Empty;
	}

	@Override
	public boolean hasSkill(java.lang.String name) {
		return false;
	}

	@Override
	public PlayerSkillContext getSkillInfo(java.lang.String s) {
		return PlayerSkillContext.Empty;
	}

	@Override
	public boolean isSilenced() {
		return true;
	}

	@Override
	public void addSkill(java.lang.String name, PlayerSkillContext info) {

	}

	@Override
	public PlayerSkillContext getSkill(java.lang.String skillName) {
		return PlayerSkillContext.Empty;
	}

	@Override
	public void removeAllSkills() {

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
	public Map<String, PlayerClassData> getClasses() {
		return Collections.EMPTY_MAP;
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
	public Player getEntity() {
		return getPlayer();
	}

	@Override
	public void sendMessage(LocalizableParametrizedText message) {
		getPlayer().sendMessage(message.toText());
	}


	@Override
	public MessageType getPreferedMessageType() {
		return MessageType.CHAT;
	}

	@Override
	public void setPreferedMessageType(MessageType type) {

	}

	@Override
	public boolean hasClass(ClassDefinition configClass) {
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
	public Map<java.lang.String, SkillTreeViewModel> getSkillTreeViewLocation() {
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
	public double getExperienceBonusFor(java.lang.String name, EntityType type) {
		return 0;
	}

	@Override public void addClass(PlayerClassData playerClassData) {

	}

	@Override
	public void removeSkillTreeSpecialization(SkillTreeSpecialization specialization) {

	}

	@Override
	public boolean hasSkillTreeSpecialization(SkillTreeSpecialization specialization) {
		return false;
	}

	@Override
	public Set<SkillTreeSpecialization> getSkillTreeSpecialization() {
		return Collections.emptySet();
	}

	@Override
	public Set<EquipedSlot> getSlotsCannotBeEquiped() {
		return Collections.emptySet();
	}

	@Override
	public RpgItemStack getMainHand() {
		return null;
	}

	@Override
	public void setMainHand(RpgItemStack customItem, int slot) {

	}

	@Override
	public int getMainHandSlotId() {
		return -1;
	}

	@Override
	public RpgItemStack getOffHand() {
		return null;
	}

	@Override
	public void setOffHand(RpgItemStack customItem) {

	}

	@Override
	public void restartAttributeGuiSession() {

	}

	@Override
	public RpgInventory getManagedInventory() {
		return new RpgInventoryImpl() {
			@Override
			public Map<Integer, ManagedSlot> getManagedSlots() {
				return Collections.emptyMap();
			}
		};
	}
}
