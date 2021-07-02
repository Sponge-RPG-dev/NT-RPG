

package cz.neumimto.rpg.api.gui;

import com.google.inject.Singleton;
import cz.neumimto.rpg.api.effects.EffectStatusType;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.skills.tree.SkillTree;

/**
 * Created by NeumimTo on 12.2.2015.
 */
@JsBinding(JsBinding.Type.CLASS)
@Singleton
public class Gui {

    private static IPlayerMessage vanilla;

    private static IPlayerMessage mod;

    public Gui() {
    }

    public Gui(IPlayerMessage vanilla) {
        Gui.vanilla = vanilla;
    }

    private static IPlayerMessage getMessageTypeOf(IActiveCharacter player) {
        if (player == null || player.isUsingGuiMod()) {
            return mod;
        }
        return vanilla;
    }

    public static void sendCooldownMessage(IActiveCharacter player, String skillname, double cooldown) {
        getMessageTypeOf(player).sendCooldownMessage(player, skillname, cooldown);
    }

    public static void openSkillTreeMenu(IActiveCharacter player) {
        getMessageTypeOf(player).openSkillTreeMenu(player);
    }

    public static void sendEffectStatus(IActiveCharacter player, EffectStatusType type, IEffect effect) {
        getMessageTypeOf(player).sendEffectStatus(player, type, effect);
    }


    public static void showCharacterInfo(IActiveCharacter player, IActiveCharacter character) {
        getMessageTypeOf(player).sendPlayerInfo(player, character);
    }

    public static void showExpChange(IActiveCharacter character, String classname, double expchange) {
        getMessageTypeOf(character).showExpChange(character, classname, expchange);
    }

    public static void showLevelChange(IActiveCharacter character, PlayerClassData aClass, int level) {
        getMessageTypeOf(character).showLevelChange(character, aClass, level);
    }

    public static void sendStatus(IActiveCharacter character) {
        getMessageTypeOf(character).sendStatus(character);
    }

    public static void sendListOfCharacters(IActiveCharacter player, CharacterBase currentlyCreated) {
        getMessageTypeOf(player).sendListOfCharacters(player, currentlyCreated);
    }

    public static void showClassInfo(IActiveCharacter character, ClassDefinition cc) {
        getMessageTypeOf(character).showClassInfo(character, cc);
    }

    public static void sendListOfRunes(IActiveCharacter character) {
        getMessageTypeOf(character).sendListOfRunes(character);
    }

    public static void displayClassArmor(ClassDefinition g, IActiveCharacter target) {
        getMessageTypeOf(target).displayGroupArmor(g, target);
    }

    public static void displayClassWeapons(ClassDefinition g, IActiveCharacter target) {
        getMessageTypeOf(target).displayGroupWeapon(g, target);
    }

    public static void displayInitialAttributes(ClassDefinition g, IActiveCharacter target) {
        getMessageTypeOf(target).displayAttributes(target, g);
    }

    public static void displayMana(IActiveCharacter character) {
        getMessageTypeOf(character).displayMana(character);
    }

    public static void displayCurrentClicks(IActiveCharacter character, String combo) {
        getMessageTypeOf(character).displayCurrentClicks(character, combo);
    }

    public static void moveSkillTreeMenu(IActiveCharacter character) {
        getMessageTypeOf(character).moveSkillTreeMenu(character);
    }

    public static void displaySkillDetailsInventoryMenu(IActiveCharacter character, SkillTree tree, String command) {
        getMessageTypeOf(character).displaySkillDetailsInventoryMenu(character, tree, command);
    }

    public static void displayInitialProperties(ClassDefinition byName, IActiveCharacter commandSource) {
        getMessageTypeOf(commandSource).displayInitialProperties(byName, commandSource);
    }

    public static void skillExecution(IActiveCharacter character, PlayerSkillContext skill) {
        getMessageTypeOf(character).skillExecution(character, skill);
    }

    public static void sendClassesByType(IActiveCharacter character, String o) {
        getMessageTypeOf(character).sendClassesByType(character, o);
    }

    public static void sendClassTypes(IActiveCharacter character) {
        getMessageTypeOf(character).sendClassTypes(character);
    }

    public static void displayCharacterMenu(IActiveCharacter character) {
        getMessageTypeOf(character).displayCharacterMenu(character);
    }

    public static void displayCharacterAttributes(IActiveCharacter character) {
        getMessageTypeOf(character).displayCharacterAttributes(character);
    }

    public static void displayCharacterArmor(IActiveCharacter character, int page) {
        getMessageTypeOf(character).displayCharacterArmor(character, page);
    }

    public static void displayCharacterWeapons(IActiveCharacter character, int page) {
        getMessageTypeOf(character).displayCharacterWeapons(character, page);
    }

    public static void displaySpellbook(IActiveCharacter character) {
        getMessageTypeOf(character).displaySpellbook(character);
    }

    public static void displayClassDependencies(IActiveCharacter character, ClassDefinition classDefinition) {
        getMessageTypeOf(character).displayClassDependencies(character, classDefinition);
    }

    public void setVanillaMessaging(IPlayerMessage instance) {
        vanilla = instance;
    }
}
