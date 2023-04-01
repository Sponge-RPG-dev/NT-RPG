package cz.neumimto.rpg.spigot.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import cz.neumimto.rpg.common.effects.*;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.gui.IPlayerMessage;
import cz.neumimto.rpg.common.gui.SkillTreeViewModel;
import cz.neumimto.rpg.common.inventory.CannotUseItemReason;
import cz.neumimto.rpg.common.localization.Arg;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.skills.tree.SkillTree;
import cz.neumimto.rpg.spigot.effects.common.def.BossBarExpNotifier;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import cz.neumimto.rpg.spigot.gui.inventoryviews.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SpigotGui implements IPlayerMessage<SpigotCharacter> {

    @Inject
    private LocalizationService localizationService;

    @Inject
    private EffectService effectService;

    @Override
    public boolean isClientSideGui() {
        return false;
    }

    @Override
    public void sendCooldownMessage(SpigotCharacter player, String message, double cooldown) {
        player.sendMessage(localizationService.translate(LocalizationKeys.ON_COOLDOWN,
                Arg.arg("skill", message).with("time", String.format("%.2f", cooldown))));
    }

    @Override
    public void sendEffectStatus(SpigotCharacter player, EffectStatusType type, IEffect effect) {

    }


    @Override
    public void sendPlayerInfo(SpigotCharacter character, SpigotCharacter target) {

    }

    @Override
    public void showExpChange(SpigotCharacter character, String classname, double expchange) {
        IEffectContainer<Object, BossBarExpNotifier> barExpNotifier = character.getEffect(BossBarExpNotifier.name);
        BossBarExpNotifier effect = (BossBarExpNotifier) barExpNotifier;
        if (effect == null) {
            effect = new BossBarExpNotifier(character);
            effectService.addEffect(effect, InternalEffectSourceProvider.INSTANCE);
        }
        effect.notifyExpChange(character, classname, expchange);
    }

    @Override
    public void showLevelChange(SpigotCharacter character, PlayerClassData clazz, int level) {
        Player player = character.getPlayer();
        player.sendMessage("Level up: " + clazz.getClassDefinition().getName() + " - " + level);

    }

    @Override
    public void sendStatus(SpigotCharacter character) {

    }

    @Override
    public void sendListOfCharacters(SpigotCharacter player, CharacterBase currentlyCreated) {
        SpigotGuiHelper.sendcharacters(player.getPlayer(), player, currentlyCreated);
    }

    @Override
    public void showClassInfo(SpigotCharacter character, ClassDefinition cc) {
        showClassInfo(character, cc, null);
    }

    public void showClassInfo(SpigotCharacter character, ClassDefinition cc, String back) {
        Player player = character.getPlayer();
        ChestGui chestGui = ClassViewGui.get(cc.getName());
        chestGui.show(player);
    }

    @Override
    public void sendListOfRunes(SpigotCharacter character) {

    }

    @Override
    public void displayGroupArmor(ClassDefinition cc, SpigotCharacter target) {
        Player player = target.getPlayer();
        ChestGui chestGui = ClassArmorGuiView.get(cc.getName());
        chestGui.show(player);
    }

    @Override
    public void displayGroupWeapon(ClassDefinition cc, SpigotCharacter target) {
        Player player = target.getPlayer();
        ChestGui chestGui = ClassWeaponsGuiView.get(cc.getName());
        chestGui.show(player);
    }

    @Override
    public void sendCannotUseItemNotification(SpigotCharacter character, String item, CannotUseItemReason reason) {
        if (reason == CannotUseItemReason.CONFIG) {
            character.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, translate(LocalizationKeys.CANNOT_USE_ITEM_CONFIGURATION_REASON));
        } else if (reason == CannotUseItemReason.LEVEL) {
            BaseComponent translate = translate(LocalizationKeys.CANNOT_USE_ITEM_LEVEL_REASON);
            translate.setColor(ChatColor.RED);
            character.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, translate);
        } else if (reason == CannotUseItemReason.LORE) {
            BaseComponent translate = translate(LocalizationKeys.CANNOT_USE_ITEM_LORE_REASON);
            translate.setColor(ChatColor.RED);
            character.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, translate);
        }
    }

    private BaseComponent translate(String key) {
        return TextComponent.fromLegacyText(localizationService.translate(key))[0];
    }

    @Override
    public void openSkillTreeMenu(SpigotCharacter player) {
        SkillTree skillTree = player.getLastTimeInvokedSkillTreeView().getSkillTree();
        if (player.getSkillTreeViewLocation().get(skillTree.getId()) == null) {
            SpigotSkillTreeViewModel skillTreeViewModel = new SpigotSkillTreeViewModel();
            for (SkillTreeViewModel treeViewModel : player.getSkillTreeViewLocation().values()) {
                treeViewModel.setCurrent(false);
            }
            player.getSkillTreeViewLocation().put(skillTree.getId(), skillTreeViewModel);
            skillTreeViewModel.setSkillTree(skillTree);
        }
        Inventory skillTreeInventoryViewTemplate = SkillTreeViewBuilder.instance.createSkillTreeView(player, skillTree);
        SpigotGuiHelper.drawSkillTreeViewData(skillTreeInventoryViewTemplate, player);
        player.getPlayer().openInventory(skillTreeInventoryViewTemplate);
    }

    @Override
    public void moveSkillTreeMenu(SpigotCharacter character) {
        Player player = character.getPlayer();
        InventoryView openInventory = player.getOpenInventory();
        if (openInventory.getType() == InventoryType.CHEST) {
            SpigotGuiHelper.drawSkillTreeViewData(openInventory.getTopInventory(), character);
        }
    }

    @Override
    public void displaySkillDetailsInventoryMenu(SpigotCharacter character, SkillTree tree, String command) {
        SpigotGuiHelper.createSkillDetailInventoryView(character, tree, tree.getSkillById(command));
    }

    @Override
    public void displayInitialProperties(ClassDefinition byName, SpigotCharacter player) {

    }

    @Override
    public void sendClassesByType(SpigotCharacter character, String def) {
        Player player = character.getPlayer();
        ChestGui chestGui = ClassesByTypeGuiView.get(def);
        chestGui.show(player);
    }

    @Override
    public void sendClassTypes(SpigotCharacter character) {
        Player player = character.getPlayer();
        ChestGui chestGui = ClassTypesGuiView.get();
        chestGui.show(player);
    }

    @Override
    public void displayCharacterMenu(SpigotCharacter character) {
        Player player = character.getPlayer();
        ChestGui inventory = CharacterGuiView.get(player);
        inventory.show(player);
    }

    @Override
    public void displayCharacterAttributes(SpigotCharacter character) {
        Player player = character.getPlayer();
        ChestGui chestGui = CharacterAttributesGuiView.get(player);
        chestGui.show(player);
    }

    @Override
    public void displayCurrentClicks(SpigotCharacter character, String combo) {

    }

    @Override
    public void displayCharacterArmor(SpigotCharacter character, int page) {
        Player player = character.getPlayer();
        ChestGui chestGui = ArmorGuiView.get(player);
        chestGui.show(player);
    }

    @Override
    public void displayCharacterWeapons(SpigotCharacter character, int page) {
        Player player = character.getPlayer();
        ChestGui chestGui = WeaponGuiView.get(player);
        chestGui.show(player);
    }

    @Override
    public void displaySpellbook(SpigotCharacter character) {
        Inventory i = SpigotGuiHelper.createSpellbookInventory(character);
        character.getPlayer().openInventory(i);
    }

    @Override
    public void displayClassDependencies(SpigotCharacter character, ClassDefinition classDefinition) {
        Player player = character.getPlayer();
        ChestGui chestGui = ClassDepedenciesGuiView.get(classDefinition.getName());
        chestGui.show(player);
    }

    @Override
    public void displayClassAttributes(SpigotCharacter character, ClassDefinition classDefinition) {
        Player player = character.getPlayer();
        ChestGui chestGui = ClassAttributesGuiView.get(classDefinition.getName());
        chestGui.show(player);
    }
}
