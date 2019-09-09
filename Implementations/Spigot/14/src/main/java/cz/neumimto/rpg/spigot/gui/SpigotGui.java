package cz.neumimto.rpg.spigot.gui;

import cz.neumimto.rpg.api.effects.EffectStatusType;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.gui.IPlayerMessage;
import cz.neumimto.rpg.api.inventory.CannotUseItemReason;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class SpigotGui implements IPlayerMessage<ISpigotCharacter> {
    @Override
    public boolean isClientSideGui() {
        return false;
    }

    @Override
    public void sendCooldownMessage(ISpigotCharacter player, String message, double cooldown) {

    }

    @Override
    public void sendEffectStatus(ISpigotCharacter player, EffectStatusType type, IEffect effect) {

    }

    @Override
    public void invokeCharacterMenu(ISpigotCharacter player, List<CharacterBase> characterBases) {

    }

    @Override
    public void sendPlayerInfo(ISpigotCharacter character, List<CharacterBase> target) {

    }

    @Override
    public void sendPlayerInfo(ISpigotCharacter character, ISpigotCharacter target) {

    }

    @Override
    public void showExpChange(ISpigotCharacter character, String classname, double expchange) {

    }

    @Override
    public void showLevelChange(ISpigotCharacter character, PlayerClassData clazz, int level) {

    }

    @Override
    public void sendStatus(ISpigotCharacter character) {

    }

    @Override
    public void invokerDefaultMenu(ISpigotCharacter character) {

    }

    @Override
    public void sendListOfCharacters(ISpigotCharacter player, CharacterBase currentlyCreated) {

    }

    @Override
    public void showClassInfo(ISpigotCharacter character, ClassDefinition cc) {

    }

    @Override
    public void sendListOfRunes(ISpigotCharacter character) {

    }

    @Override
    public void displayGroupArmor(ClassDefinition g, ISpigotCharacter target) {

    }

    @Override
    public void displayGroupWeapon(ClassDefinition g, ISpigotCharacter target) {

    }

    @Override
    public void sendClassInfo(ISpigotCharacter target, ClassDefinition configClass) {

    }

    @Override
    public void displayAttributes(ISpigotCharacter target, ClassDefinition group) {

    }

    @Override
    public void displayHealth(ISpigotCharacter character) {

    }

    @Override
    public void displayMana(ISpigotCharacter character) {

    }

    @Override
    public void sendCannotUseItemNotification(ISpigotCharacter character, String item, CannotUseItemReason reason) {

    }

    @Override
    public void openSkillTreeMenu(ISpigotCharacter player) {

    }

    @Override
    public void moveSkillTreeMenu(ISpigotCharacter character) {

    }

    @Override
    public void displaySkillDetailsInventoryMenu(ISpigotCharacter character, SkillTree tree, String command) {

    }

    @Override
    public void displayInitialProperties(ClassDefinition byName, ISpigotCharacter player) {

    }

    @Override
    public void sendCannotUseItemInOffHandNotification(String futureOffHandItem, ISpigotCharacter character, CannotUseItemReason reason) {

    }

    @Override
    public void skillExecution(ISpigotCharacter character, PlayerSkillContext skill) {

    }

    @Override
    public void sendClassesByType(ISpigotCharacter character, String def) {

    }

    @Override
    public void sendClassTypes(ISpigotCharacter character) {

    }

    @Override
    public void displayCharacterMenu(ISpigotCharacter character) {

    }

    @Override
    public void displayCharacterAttributes(ISpigotCharacter character) {

    }

    @Override
    public void displayCurrentClicks(ISpigotCharacter character, String combo) {

    }

    @Override
    public void displayCharacterArmor(ISpigotCharacter character, int page) {

    }

    @Override
    public void displayCharacterWeapons(ISpigotCharacter character, int page) {

    }
}
