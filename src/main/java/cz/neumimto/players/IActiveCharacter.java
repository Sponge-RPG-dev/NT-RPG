package cz.neumimto.players;

import cz.neumimto.Weapon;
import cz.neumimto.effects.IEffectConsumer;
import cz.neumimto.players.groups.Guild;
import cz.neumimto.players.groups.NClass;
import cz.neumimto.players.groups.Race;
import cz.neumimto.players.parties.Party;
import cz.neumimto.skills.ExtendedSkillInfo;
import cz.neumimto.skills.ISkill;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypeWorn;

import java.util.Map;
import java.util.Set;

/**
 * Created by NeumimTo on 23.7.2015.
 */
public interface IActiveCharacter extends IEffectConsumer {

    Party getParty();

    void setParty(Party party);

    String getName();

    boolean isStub();

    float[] getCharacterProperties();

    float getCharacterProperty(int index);

    void setCharacterProperty(int index, float value);

    public void setCharacterLevelProperty(int index, float value);

    public float getCharacterPropertyWithoutLevel(int index);

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

    void addExperiences(float exp);

    Player getPlayer();

    void setPlayer(Player pl);

    void onRightClickBlock(int slotId);

    void resetRightClicks();

    short getSkillPoints();

    void setSkillPoints(short skillPoints);

    short getAttributePoints();

    void setAttributePoints(short attributePoints);

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
}
