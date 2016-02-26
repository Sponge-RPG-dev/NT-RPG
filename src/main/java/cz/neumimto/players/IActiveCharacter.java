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

package cz.neumimto.players;

import cz.neumimto.IEntity;
import cz.neumimto.IEntityType;
import cz.neumimto.effects.IEffect;
import cz.neumimto.inventory.HotbarObject;
import cz.neumimto.inventory.Weapon;
import cz.neumimto.players.groups.Guild;
import cz.neumimto.players.groups.NClass;
import cz.neumimto.players.groups.Race;
import cz.neumimto.players.parties.Party;
import cz.neumimto.players.properties.attributes.ICharacterAttribute;
import cz.neumimto.skills.ExtendedSkillInfo;
import cz.neumimto.skills.ISkill;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypeWorn;

import java.util.Map;
import java.util.Set;

/**
 * Created by NeumimTo on 23.7.2015.
 */
public interface IActiveCharacter extends IEntity<Player> {

    Party getParty();

    void setParty(Party party);

    String getName();

    boolean isStub();

    float[] getCharacterProperties();

    float getCharacterProperty(int index);

    void setCharacterProperty(int index, float value);

    void setCharacterLevelProperty(int index, float value);

    float getCharacterPropertyWithoutLevel(int index);

    double getMaxMana();

    void setMaxMana(float mana);

    void setMaxHealth(float maxHealth);

    void setHealth(float mana);

    IReservable getMana();

    void setMana(IReservable mana);

    Health getHealth();

    void setHealth(Health health);

    Map<EquipmentTypeWorn, Weapon> getEquipedArmor();

    double getExperiencs();

    void addExperiences(double exp, ExperienceSource source);

    Player getPlayer();

    void setPlayer(Player pl);

    void resetRightClicks();

    int getSkillPoints();

    void setSkillPoints(int skillPoints);

    int getAttributePoints();

    void setAttributePoints(int attributePoints);

    Integer getAttributeValue(String name);

    default Integer getAttributeValue(ICharacterAttribute attribute) {
        return getAttributeValue(attribute.getName());
    }

    ExtendedNClass getPrimaryClass();

    void setPrimaryClass(NClass clazz);

    Map<String, Long> getCooldowns();

    boolean hasCooldown(String thing);

    double getBaseWeaponDamage(ItemType id);

    Set<ItemType> getAllowedArmor();

    boolean canWear(ItemStack armor);

    Map<ItemType, Double> getAllowedWeapons();

    Set<ExtendedNClass> getClasses();

    NClass getNClass(int index);

    Race getRace();

    void setRace(Race race);

    Guild getGuild();

    void setGuild(Guild guild);

    CharacterBase getCharacterBase();

    void setClass(NClass nclass, int slot);

    public IActiveCharacter updateItemRestrictions();

    public Map<String, ExtendedSkillInfo> getSkills();

    ExtendedSkillInfo getSkillInfo(ISkill skill);

    public boolean hasSkill(String name);

    int getLevel();

    ExtendedSkillInfo getSkillInfo(String s);

    boolean isSilenced();

    void addSkill(String name, ExtendedSkillInfo info);

    ExtendedSkillInfo getSkill(String skillName);

    void getRemoveAllSkills();

    boolean hasParty();

    boolean isInPartyWith(IActiveCharacter character);

    boolean isUsingGuiMod();

    void setUsingGuiMod(boolean b);

    boolean isPartyLeader();

    void setPendingPartyInvite(Party party);

    Party getPendingPartyInvite();

    Weapon getMainHand();

    void setMainHand(Weapon mainHand);

    Weapon getOffHand();

    void setOffHand(Weapon offHand);

    boolean canUse(ItemType weaponItemType);

    void setWeaponDamage(double damage);

    double getWeaponDamage();

    void setArmorValue(double value);

    double getArmorValue();

    boolean hasPreferedDamageType();

    DamageType getDamageType();

    void setDamageType(DamageType damageType);

    void setCharacterProperties(float[] arr);

    void setCharacterLevelProperties(float[] arr);

    void updateLastKnownLocation(int x, int y, int z, String name);

    boolean isInvulnerable();

    void setInvulnerable(boolean b);

    HotbarObject[] getHotbar();

    void setHotbarSlot(int i,HotbarObject o);

    default boolean isSocketing() {
        return getCurrentRune() > 0;
    }

    void setCurrentRune(int slot);

    int getCurrentRune();

    @Override
    double getHp();

    @Override
    void setHp(double d);

    @Override
    default IEntityType getType() {
        return IEntityType.CHARACTER;
    }

    @Override
    Player getEntity();

    @Override
    Map<Class<? extends IEffect>, IEffect> getEffectMap();

    @Override
    void sendMessage(String message);

    float[] getCharacterLevelProperties();

    Map<String,Integer> getTransientAttributes();
}
