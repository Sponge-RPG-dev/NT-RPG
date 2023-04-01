package cz.neumimto.rpg.common.gui;

import com.google.inject.Singleton;
import cz.neumimto.rpg.common.effects.EffectStatusType;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.tree.SkillTree;

/**
 * Created by NeumimTo on 12.2.2015.
 */
@Singleton
public class Gui {

    private static IPlayerMessage vanilla;

    private static IPlayerMessage mod;

    public Gui() {
    }

    public Gui(IPlayerMessage vanilla) {
        Gui.vanilla = vanilla;
    }

    private static IPlayerMessage getMessageTypeOf(ActiveCharacter player) {
        if (player == null || player.isUsingGuiMod()) {
            return mod;
        }
        return vanilla;
    }

    public static void sendCooldownMessage(ActiveCharacter player, String skillname, double cooldown) {
        getMessageTypeOf(player).sendCooldownMessage(player, skillname, cooldown);
    }

    public static void openSkillTreeMenu(ActiveCharacter player) {
        getMessageTypeOf(player).openSkillTreeMenu(player);
    }

    public static void sendEffectStatus(ActiveCharacter player, EffectStatusType type, IEffect effect) {
        getMessageTypeOf(player).sendEffectStatus(player, type, effect);
    }


    public static void showCharacterInfo(ActiveCharacter player, ActiveCharacter character) {
        getMessageTypeOf(player).sendPlayerInfo(player, character);
    }

    public static void showExpChange(ActiveCharacter character, String classname, double expchange) {
        getMessageTypeOf(character).showExpChange(character, classname, expchange);
    }

    public static void showLevelChange(ActiveCharacter character, PlayerClassData aClass, int level) {
        getMessageTypeOf(character).showLevelChange(character, aClass, level);
    }

    public static void sendStatus(ActiveCharacter character) {
        getMessageTypeOf(character).sendStatus(character);
    }

    public static void sendListOfCharacters(ActiveCharacter player, CharacterBase currentlyCreated) {
        getMessageTypeOf(player).sendListOfCharacters(player, currentlyCreated);
    }

    public static void showClassInfo(ActiveCharacter character, ClassDefinition cc) {
        getMessageTypeOf(character).showClassInfo(character, cc);
    }

    public static void sendListOfRunes(ActiveCharacter character) {
        getMessageTypeOf(character).sendListOfRunes(character);
    }

    public static void displayClassArmor(ClassDefinition g, ActiveCharacter target) {
        getMessageTypeOf(target).displayGroupArmor(g, target);
    }

    public static void displayClassWeapons(ClassDefinition g, ActiveCharacter target) {
        getMessageTypeOf(target).displayGroupWeapon(g, target);
    }

    public static void displayCurrentClicks(ActiveCharacter character, String combo) {
        getMessageTypeOf(character).displayCurrentClicks(character, combo);
    }

    public static void moveSkillTreeMenu(ActiveCharacter character) {
        getMessageTypeOf(character).moveSkillTreeMenu(character);
    }

    public static void displaySkillDetailsInventoryMenu(ActiveCharacter character, SkillTree tree, String command) {
        getMessageTypeOf(character).displaySkillDetailsInventoryMenu(character, tree, command);
    }

    public static void displayInitialProperties(ClassDefinition byName, ActiveCharacter commandSource) {
        getMessageTypeOf(commandSource).displayInitialProperties(byName, commandSource);
    }

    public static void skillExecution(ActiveCharacter character, PlayerSkillContext skill) {
        getMessageTypeOf(character).skillExecution(character, skill);
    }

    public static void sendClassesByType(ActiveCharacter character, String o) {
        getMessageTypeOf(character).sendClassesByType(character, o);
    }

    public static void sendClassTypes(ActiveCharacter character) {
        getMessageTypeOf(character).sendClassTypes(character);
    }

    public static void displayCharacterMenu(ActiveCharacter character) {
        getMessageTypeOf(character).displayCharacterMenu(character);
    }

    public static void displayCharacterAttributes(ActiveCharacter character) {
        getMessageTypeOf(character).displayCharacterAttributes(character);
    }

    public static void displayCharacterArmor(ActiveCharacter character, int page) {
        getMessageTypeOf(character).displayCharacterArmor(character, page);
    }

    public static void displayCharacterWeapons(ActiveCharacter character, int page) {
        getMessageTypeOf(character).displayCharacterWeapons(character, page);
    }

    public static void displaySpellbook(ActiveCharacter character) {
        getMessageTypeOf(character).displaySpellbook(character);
    }

    public static void displayClassDependencies(ActiveCharacter character, ClassDefinition classDefinition) {
        getMessageTypeOf(character).displayClassDependencies(character, classDefinition);
    }

    public static void displayClassAttributes(ActiveCharacter character, ClassDefinition classDefinition) {
        getMessageTypeOf(character).displayClassAttributes(character, classDefinition);
    }

    public void setVanillaMessaging(IPlayerMessage instance) {
        vanilla = instance;
    }
}
