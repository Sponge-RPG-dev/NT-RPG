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

import cz.neumimto.core.localization.LocalizableParametrizedText;
import cz.neumimto.rpg.api.inventory.ManagedSlot;
import cz.neumimto.rpg.api.inventory.RpgInventory;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.entities.IEntityType;
import cz.neumimto.rpg.entities.IReservable;
import cz.neumimto.rpg.persistance.model.EquipedSlot;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.players.parties.Party;
import cz.neumimto.rpg.api.skills.tree.SkillTreeSpecialization;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by NeumimTo on 23.7.2015.
 */
public interface IActiveCharacter extends IEntity<Player> {

    Map<String, PlayerClassData> getClasses();

    default PlayerClassData getClassByType(String type) {
        for (PlayerClassData value : getClasses().values()) {
            if (value.getClassDefinition().getClassType().equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }

    Map<Class<?>, RpgInventory> getManagedInventory();

    Party getParty();

    void setParty(Party party);

    String getName();

    boolean isStub();

    float[] getPrimaryProperties();

    void setCharacterLevelProperty(int index, float value);

    float getCharacterPropertyWithoutLevel(int index);

    double getMaxMana();

    void setMaxMana(float mana);

    void setMaxHealth(float maxHealth);

    void setHealth(float maxHealth);

    IReservable getMana();

    void setMana(IReservable mana);

    void setHealth(IReservable health);

    Player getPlayer();

    void setPlayer(Player pl);

    void resetRightClicks();

    int getAttributePoints();

    void setAttributePoints(int attributePoints);

    int getAttributeValue(String name);

    default Integer getAttributeValue(Attribute attribute) {
        return getAttributeValue(attribute.getId());
    }

    Map<String, Long> getCooldowns();

    boolean hasCooldown(String thing);

    double getBaseWeaponDamage(RpgItemType type);

    Set<RpgItemType> getAllowedArmor();

    boolean canWear(RpgItemType armor);

    Map<RpgItemType, Double> getAllowedWeapons();

    Map<EntityType, Double> getProjectileDamages();

    CharacterBase getCharacterBase();

    PlayerClassData getPrimaryClass();

    double getBaseProjectileDamage(EntityType id);

    IActiveCharacter updateItemRestrictions();

    Map<String, PlayerSkillContext> getSkills();

    PlayerSkillContext getSkillInfo(ISkill skill);

    boolean hasSkill(String name);

    int getLevel();

    PlayerSkillContext getSkillInfo(String s);

    boolean isSilenced();

    void addSkill(String name, PlayerSkillContext info);

    PlayerSkillContext getSkill(String skillName);

    void removeAllSkills();

    boolean hasParty();

    boolean isInPartyWith(IActiveCharacter character);

    boolean isUsingGuiMod();

    void setUsingGuiMod(boolean b);

    boolean isPartyLeader();

    Party getPendingPartyInvite();

    void setPendingPartyInvite(Party party);

    boolean canUse(RpgItemType weaponItemType, HandType h);

    double getWeaponDamage();

    void setWeaponDamage(double damage);

    double getArmorValue();

    void setArmorValue(double value);

    boolean hasPreferedDamageType();

    DamageType getDamageType();

    void setDamageType(DamageType damageType);

    void updateLastKnownLocation(int x, int y, int z, String name);

    boolean isInvulnerable();

    void setInvulnerable(boolean b);

    RpgItemStack getMainHand();

    int getMainHandSlotId();

    void setMainHand(RpgItemStack customItem, int slot);

    RpgItemStack getOffHand();

    void setOffHand(RpgItemStack customItem);

    @Override
    default IEntityType getType() {
        return IEntityType.CHARACTER;
    }

    @Override
    Player getEntity();

    void sendMessage(LocalizableParametrizedText message);

    float[] getSecondaryProperties();

    void setSecondaryProperties(float[] arr);

    Map<String, Integer> getTransientAttributes();

    MessageType getPreferedMessageType();

    void setPreferedMessageType(MessageType type);

    boolean hasClass(ClassDefinition configClass);

    List<Integer> getSlotsToReinitialize();

    void setSlotsToReinitialize(List<Integer> slotsToReinitialize);

    Map<String, SkillTreeViewModel> getSkillTreeViewLocation();

    SkillTreeViewModel getLastTimeInvokedSkillTreeView();

    void addSkillTreeSpecialization(SkillTreeSpecialization specialization);

    void removeSkillTreeSpecialization(SkillTreeSpecialization specialization);

    boolean hasSkillTreeSpecialization(SkillTreeSpecialization specialization);

    Set<SkillTreeSpecialization> getSkillTreeSpecialization();

    Set<EquipedSlot> getSlotsCannotBeEquiped();

    double getExperienceBonusFor(java.lang.String name, EntityType type);

    @Override
    default void sendMessage(Text t) {
        getPlayer().sendMessage(t);
    }

    void addClass(PlayerClassData playerClassData);

    void restartAttributeGuiSession();

    default void getMinimalInventoryRequirements(Map<Attribute, Integer> seed) {

        Map<Class<?>, RpgInventory> managedInventory = getManagedInventory();
        for (RpgInventory inv : managedInventory.values()) {

            for (ManagedSlot value : inv.getManagedSlots().values()) {
                Optional<RpgItemStack> content = value.getContent();
                if (content.isPresent()) {
                    RpgItemStack rpgItemStack = content.get();
                    Map<Attribute, Integer> minimalAttributeRequirements = rpgItemStack.getMinimalAttributeRequirements();

                    for (Map.Entry<Attribute, Integer> entry : minimalAttributeRequirements.entrySet()) {
                        seed.compute(entry.getKey(), (attribute, integer) -> Math.max(integer, entry.getValue()));
                    }
                }
            }
        }
    }

    boolean requiresDamageRecalculation();

    void setRequiresDamageRecalculation(boolean k);

    int getLastHotbarSlotInteraction();

    void setLastHotbarSlotInteraction(int last);
}
