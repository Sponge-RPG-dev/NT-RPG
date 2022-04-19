package cz.neumimto.rpg.spigot.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.*;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.gui.IPlayerMessage;
import cz.neumimto.rpg.common.gui.SkillTreeViewModel;
import cz.neumimto.rpg.common.inventory.CannotUseItemReason;
import cz.neumimto.rpg.common.localization.Arg;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.model.CharacterBase;
import cz.neumimto.rpg.common.skills.tree.SkillTree;
import cz.neumimto.rpg.spigot.effects.common.def.BossBarExpNotifier;
import cz.neumimto.rpg.spigot.effects.common.def.ManaBar;
import cz.neumimto.rpg.spigot.effects.common.def.ManaBarBossBar;
import cz.neumimto.rpg.spigot.effects.common.def.ManaBarText;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
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
public class SpigotGui implements IPlayerMessage<ISpigotCharacter> {

    @Inject
    private LocalizationService localizationService;

    @Inject
    private EffectService effectService;

    @Override
    public boolean isClientSideGui() {
        return false;
    }

    @Override
    public void sendCooldownMessage(ISpigotCharacter player, String message, double cooldown) {
        player.sendMessage(localizationService.translate(LocalizationKeys.ON_COOLDOWN,
                Arg.arg("skill", message).with("time", String.format("%.2f", cooldown))));
    }

    @Override
    public void sendEffectStatus(ISpigotCharacter player, EffectStatusType type, IEffect effect) {

    }


    @Override
    public void sendPlayerInfo(ISpigotCharacter character, ISpigotCharacter target) {

    }

    @Override
    public void showExpChange(ISpigotCharacter character, String classname, double expchange) {
        IEffectContainer<Object, BossBarExpNotifier> barExpNotifier = character.getEffect(BossBarExpNotifier.name);
        BossBarExpNotifier effect = (BossBarExpNotifier) barExpNotifier;
        if (effect == null) {
            effect = new BossBarExpNotifier(character);
            effectService.addEffect(effect, InternalEffectSourceProvider.INSTANCE);
        }
        effect.notifyExpChange(character, classname, expchange);
    }

    @Override
    public void showLevelChange(ISpigotCharacter character, PlayerClassData clazz, int level) {
        Player player = character.getPlayer();
        player.sendMessage("Level up: " + clazz.getClassDefinition().getName() + " - " + level);

    }

    @Override
    public void sendStatus(ISpigotCharacter character) {

    }

    @Override
    public void sendListOfCharacters(ISpigotCharacter player, CharacterBase currentlyCreated) {
        SpigotGuiHelper.sendcharacters(player.getPlayer(), player, currentlyCreated);
    }

    @Override
    public void showClassInfo(ISpigotCharacter character, ClassDefinition cc) {
        showClassInfo(character, cc, null);
    }

    public void showClassInfo(ISpigotCharacter character, ClassDefinition cc, String back) {
        Player player = character.getPlayer();
        ChestGui chestGui = ClassViewGui.get(cc.getName());
        chestGui.show(player);
    }

    @Override
    public void sendListOfRunes(ISpigotCharacter character) {

    }

    @Override
    public void displayGroupArmor(ClassDefinition cc, ISpigotCharacter target) {
        Player player = target.getPlayer();
        ChestGui chestGui = ClassArmorGuiView.get(cc.getName());
        chestGui.show(player);
    }

    @Override
    public void displayGroupWeapon(ClassDefinition cc, ISpigotCharacter target) {
        Player player = target.getPlayer();
        ChestGui chestGui = ClassWeaponsGuiView.get(cc.getName());
        chestGui.show(player);
    }

    @Override
    public void sendCannotUseItemNotification(ISpigotCharacter character, String item, CannotUseItemReason reason) {
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
    public void openSkillTreeMenu(ISpigotCharacter player) {
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
    public void moveSkillTreeMenu(ISpigotCharacter character) {
        Player player = character.getPlayer();
        InventoryView openInventory = player.getOpenInventory();
        if (openInventory.getType() == InventoryType.CHEST) {
            SpigotGuiHelper.drawSkillTreeViewData(openInventory.getTopInventory(), character);
        }
    }

    @Override
    public void displaySkillDetailsInventoryMenu(ISpigotCharacter character, SkillTree tree, String command) {
        SpigotGuiHelper.createSkillDetailInventoryView(character, tree, tree.getSkillById(command));
    }

    @Override
    public void displayInitialProperties(ClassDefinition byName, ISpigotCharacter player) {

    }

    @Override
    public void sendClassesByType(ISpigotCharacter character, String def) {
        Player player = character.getPlayer();
        ChestGui chestGui = ClassesByTypeGuiView.get(def);
        chestGui.show(player);
    }

    @Override
    public void sendClassTypes(ISpigotCharacter character) {
        Player player = character.getPlayer();
        ChestGui chestGui = ClassTypesGuiView.get();
        chestGui.show(player);
    }

    @Override
    public void displayCharacterMenu(ISpigotCharacter character) {
        Player player = character.getPlayer();
        ChestGui inventory = CharacterGuiView.get(player);
        inventory.show(player);
    }

    @Override
    public void displayCharacterAttributes(ISpigotCharacter character) {
        Player player = character.getPlayer();
        ChestGui chestGui = CharacterAttributesGuiView.get(player);
        chestGui.show(player);
    }

    @Override
    public void displayCurrentClicks(ISpigotCharacter character, String combo) {

    }

    @Override
    public void displayCharacterArmor(ISpigotCharacter character, int page) {
        Player player = character.getPlayer();
        ChestGui chestGui = WeaponGuiView.get(player);
        chestGui.show(player);
    }

    @Override
    public void displayCharacterWeapons(ISpigotCharacter character, int page) {
        Player player = character.getPlayer();
        ChestGui chestGui = ArmorGuiView.get(player);
        chestGui.show(player);
    }

    @Override
    public void displaySpellbook(ISpigotCharacter character) {
        Inventory i = SpigotGuiHelper.createSpellbookInventory(character);
        character.getPlayer().openInventory(i);
    }

    @Override
    public void displayClassDependencies(ISpigotCharacter character, ClassDefinition classDefinition) {
        Player player = character.getPlayer();
        ChestGui chestGui = ClassDepedenciesGuiView.get(classDefinition.getName());
        chestGui.show(player);
    }

    @Override
    public void displayClassAttributes(ISpigotCharacter character, ClassDefinition classDefinition) {
        Player player = character.getPlayer();
        ChestGui chestGui = ClassAttributesGuiView.get(classDefinition.getName());
        chestGui.show(player);
    }
}
