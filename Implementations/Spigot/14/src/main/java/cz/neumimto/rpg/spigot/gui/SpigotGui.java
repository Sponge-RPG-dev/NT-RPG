package cz.neumimto.rpg.spigot.gui;

import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.EffectStatusType;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.gui.IPlayerMessage;
import cz.neumimto.rpg.api.gui.SkillTreeViewModel;
import cz.neumimto.rpg.api.inventory.CannotUseItemReason;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.common.effects.InternalEffectSourceProvider;
import cz.neumimto.rpg.common.gui.ConfigInventory;
import cz.neumimto.rpg.common.inventory.runewords.RuneWord;
import cz.neumimto.rpg.spigot.effects.common.def.BossBarExpNotifier;
import cz.neumimto.rpg.spigot.effects.common.def.ManaBarNotifier;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
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
import java.util.List;

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
        player.sendMessage(localizationService.translate(LocalizationKeys.ON_COOLDOWN, Arg.arg("skill", message).with("time", cooldown)));
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
        Inventory i = SpigotGuiHelper.CACHED_MENUS.get("class_template" + cc.getName());
        player.openInventory(i);
    }

    @Override
    public void sendListOfRunes(ISpigotCharacter character) {

    }

    @Override
    public void displayGroupArmor(ClassDefinition cc, ISpigotCharacter target) {
        String key = "class_allowed_items_armor_" + cc.getName();
        Inventory i = SpigotGuiHelper.CACHED_MENUS.get(key);
        Player player = target.getPlayer();
        player.openInventory(i);
    }

    @Override
    public void displayGroupWeapon(ClassDefinition cc, ISpigotCharacter target) {
        String key = "class_allowed_items_weapons_" + cc.getName();
        Inventory i = SpigotGuiHelper.CACHED_MENUS.get(key);
        Player player = target.getPlayer();
        player.openInventory(i);
    }

    @Override
    public void displayAttributes(ISpigotCharacter target, ClassDefinition group) {
        Player player = target.getPlayer();
        Inventory i = SpigotGuiHelper.createClassAttributesView(player, group);
        player.openInventory(i);
    }

    @Override
    public void displayMana(ISpigotCharacter character) {
        IEffectContainer<Object, ManaBarNotifier> barExpNotifier = character.getEffect(ManaBarNotifier.name);
        ManaBarNotifier effect = (ManaBarNotifier) barExpNotifier;
        if (effect == null) {
            effect = new ManaBarNotifier(character);
            effectService.addEffect(effect, InternalEffectSourceProvider.INSTANCE);
        }
        effect.notifyManaChange();
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
        Inventory skillTreeInventoryViewTemplate = SpigotGuiHelper.createSkillTreeView(player, skillTree);
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

    }

    @Override
    public void displayInitialProperties(ClassDefinition byName, ISpigotCharacter player) {

    }

    @Override
    public void sendClassesByType(ISpigotCharacter character, String def) {
        Player player = character.getPlayer();
        Inventory inventory = SpigotGuiHelper.createMenuInventoryClassesByTypeView(player, def);
        player.openInventory(inventory);
    }

    @Override
    public void sendClassTypes(ISpigotCharacter character) {
        Player player = character.getPlayer();
        Inventory inventory = SpigotGuiHelper.createMenuInventoryClassTypesView(player);
        player.openInventory(inventory);
    }

    @Override
    public void displayCharacterMenu(ISpigotCharacter character) {
        Player player = character.getPlayer();
        Inventory inventory = SpigotGuiHelper.createCharacterMenu(character);
        player.openInventory(inventory);
    }

    @Override
    public void displayCharacterAttributes(ISpigotCharacter character) {
            //todo
    }

    @Override
    public void displayCurrentClicks(ISpigotCharacter character, String combo) {

    }

    @Override
    public void displayCharacterArmor(ISpigotCharacter character, int page) {
        Inventory i = SpigotGuiHelper.getCharacterAllowedArmor(character, page);
        character.getPlayer().openInventory(i);
    }

    @Override
    public void displayCharacterWeapons(ISpigotCharacter character, int page) {
        Inventory i = SpigotGuiHelper.getCharacterAllowedWeapons(character, page);
        character.getPlayer().openInventory(i);
    }

    public void displayCharacterAttributes(Player player, ISpigotCharacter character) {
        Inventory inventory = SpigotGuiHelper.createCharacterAttributeView(player, character);
        player.openInventory(inventory);
    }


    public void refreshAttributeView(Player player, ISpigotCharacter character, int slotMod, AttributeConfig a) {
        SpigotGuiHelper.refreshCharacterAttributeView(player, character, player.getOpenInventory().getTopInventory(), slotMod, a);
    }
}
