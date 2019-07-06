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

package cz.neumimto.rpg.api.gui;

import cz.neumimto.rpg.api.effects.EffectStatusType;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.inventory.CannotUseItemReason;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillTree;

import java.util.List;

/**
 * Created by NeumimTo on 6.8.2015.
 */
public interface IPlayerMessage<T extends IActiveCharacter> {

    boolean isClientSideGui();

    void sendCooldownMessage(T player, String message, double cooldown);

    void sendEffectStatus(T player, EffectStatusType type, IEffect effect);

    void invokeCharacterMenu(T player, List<CharacterBase> characterBases);

    void sendPlayerInfo(T character, List<CharacterBase> target);

    void sendPlayerInfo(T character, T target);

    void showExpChange(T character, String classname, double expchange);

    void showLevelChange(T character, PlayerClassData clazz, int level);

    void sendStatus(T character);

    void invokerDefaultMenu(T character);

    void sendListOfCharacters(T player, CharacterBase currentlyCreated);

    void showClassInfo(T character, ClassDefinition cc);

    void sendListOfRunes(T character);

    void displayGroupArmor(ClassDefinition g, T target);

    void displayGroupWeapon(ClassDefinition g, T target);

    void sendClassInfo(T target, ClassDefinition configClass);

    void displayAttributes(T target, ClassDefinition group);

    void displayHealth(T character);

    void displayMana(T character);

    void sendCannotUseItemNotification(T character, String item, CannotUseItemReason reason);

    void openSkillTreeMenu(T player);

    void moveSkillTreeMenu(T character);

    void displaySkillDetailsInventoryMenu(T character, SkillTree tree, String command);

    void displayInitialProperties(ClassDefinition byName, T player);

    void sendCannotUseItemInOffHandNotification(String futureOffHandItem, T character, CannotUseItemReason reason);

    void skillExecution(T character, PlayerSkillContext skill);

    void sendClassesByType(T character, String def);

    void sendClassTypes(T character);

    void displayCharacterMenu(T character);

    void displayCharacterAttributes(T character);

    void displayCurrentClicks(T character, String combo);

    void displayCharacterArmor(T character, int page);
}
