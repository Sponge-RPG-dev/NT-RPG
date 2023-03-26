package cz.neumimto.rpg.common.gui;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.EffectStatusType;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.inventory.CannotUseItemReason;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.tree.SkillTree;

/**
 * Created by NeumimTo on 6.8.2015.
 */
public interface IPlayerMessage<T extends IActiveCharacter> {

    boolean isClientSideGui();

    void sendCooldownMessage(T player, String message, double cooldown);

    void sendEffectStatus(T player, EffectStatusType type, IEffect effect);


    void sendPlayerInfo(T character, T target);

    void showExpChange(T character, String classname, double expchange);

    void showLevelChange(T character, PlayerClassData clazz, int level);

    void sendStatus(T character);

    void sendListOfCharacters(T player, CharacterBase currentlyCreated);

    void showClassInfo(T character, ClassDefinition cc);

    void sendListOfRunes(T character);

    void displayGroupArmor(ClassDefinition g, T target);

    void displayGroupWeapon(ClassDefinition g, T target);

    void sendCannotUseItemNotification(T character, String item, CannotUseItemReason reason);

    void openSkillTreeMenu(T player);

    void moveSkillTreeMenu(T character);

    void displaySkillDetailsInventoryMenu(T character, SkillTree tree, String command);

    void displayInitialProperties(ClassDefinition byName, T player);

    default void skillExecution(T character, PlayerSkillContext skill) {
        String msg = Rpg.get().getLocalizationService().translate(LocalizationKeys.SKILL_EXECUTED_MESSAGE,
                "skill",
                skill.getSkillData().getSkillName());
        character.sendNotification(msg);
    }

    void sendClassesByType(T character, String def);

    void sendClassTypes(T character);

    void displayCharacterMenu(T character);

    void displayCharacterAttributes(T character);

    void displayCurrentClicks(T character, String combo);

    void displayCharacterArmor(T character, int page);

    void displayCharacterWeapons(T character, int page);

    void displaySpellbook(T character);

    void displayClassDependencies(T character, ClassDefinition classDefinition);

    void displayClassAttributes(T character, ClassDefinition classDefinition);
}
