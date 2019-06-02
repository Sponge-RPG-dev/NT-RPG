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

package cz.neumimto.rpg.sponge.gui;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.core.localization.LocalizableParametrizedText;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.entity.players.CharacterBase;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.attributes.AttributeConfig;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.gui.IPlayerMessage;
import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.api.skills.ISkillService;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.reloading.Reload;
import cz.neumimto.rpg.common.reloading.ReloadService;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.utils.model.CharacterListModel;
import cz.neumimto.rpg.sponge.damage.SpongeDamageService;
import cz.neumimto.rpg.api.effects.EffectStatusType;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.common.effects.InternalEffectSourceProvider;
import cz.neumimto.rpg.sponge.effects.common.def.BossBarExpNotifier;
import cz.neumimto.rpg.sponge.effects.common.def.ManaBarNotifier;
import cz.neumimto.rpg.sponge.inventory.CannotUseItemReason;
import cz.neumimto.rpg.sponge.inventory.data.*;
import cz.neumimto.rpg.sponge.inventory.data.manipulators.SkillTreeNode;
import cz.neumimto.rpg.sponge.inventory.runewords.ItemUpgrade;
import cz.neumimto.rpg.sponge.inventory.runewords.RWService;
import cz.neumimto.rpg.sponge.inventory.runewords.Rune;
import cz.neumimto.rpg.sponge.inventory.runewords.RuneWord;
import cz.neumimto.rpg.sponge.items.SpongeRpgItemType;
import cz.neumimto.rpg.common.persistance.dao.PlayerDao;
import cz.neumimto.rpg.common.persistance.model.CharacterClass;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.sponge.commands.InfoCommand;
import cz.neumimto.rpg.sponge.utils.ItemStackUtils;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.Color;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.sponge.NtRpgPlugin.pluginConfig;
import static cz.neumimto.rpg.sponge.gui.GuiHelper.*;

/**
 * Created by NeumimTo on 6.8.2015.
 */
@Singleton
@ResourceLoader.ListenerClass
public class VanillaMessaging implements IPlayerMessage {

    private static final String skillname = "sk";
    public static Map<SkillTreeControllsButton, SkillTreeInterfaceModel> controlls;
    @Inject
    private Game game;
    @Inject
    private ClassService classService;
    @Inject
    private EffectService effectService;
    @Inject
    private NtRpgPlugin plugin;
    @Inject
    private RWService rwService;
    @Inject
    private InfoCommand infoCommand;
    @Inject
    private SpongeDamageService spongeDamageService;
    @Inject
    private CharacterService characterService;
    @Inject
    private ISkillService skillService;
    @Inject
    private PlayerDao playerDao;

    @Reload(on = ReloadService.PLUGIN_CONFIG)
    public void load() {
        controlls = new HashMap<>();
        for (String a : pluginConfig.SKILLTREE_BUTTON_CONTROLLS) {
            String[] split = a.split(",");

            SkillTreeControllsButton key = SkillTreeControllsButton.valueOf(split[0].toUpperCase());
            ItemType type = Sponge.getRegistry().getType(ItemType.class, split[1]).orElse(ItemTypes.BARRIER);

            controlls.put(key, new SkillTreeInterfaceModel(Integer.parseInt(split[3]), type, split[2], (short) 0));

        }
    }

    @Override
    public boolean isClientSideGui() {
        return false;
    }

    @Override
    public void invokerDefaultMenu(IActiveCharacter character) {

    }

    @Override
    public void sendMessage(IActiveCharacter player, LocalizableParametrizedText message, Arg arg) {
        player.sendMessage(message, arg);
    }

    @Override
    public void sendCooldownMessage(IActiveCharacter player, String message, double cooldown) {
        player.sendMessage(Localizations.ON_COOLDOWN, Arg.arg("skill", message).with("time", cooldown));
    }

    @Override
    public void sendEffectStatus(IActiveCharacter player, EffectStatusType type, IEffect effect) {

    }

    @Override
    public void invokeCharacterMenu(IActiveCharacter player, List<CharacterBase> characterBases) {
        ItemStack.Builder b = ItemStack.builder();
        List<ItemStack> list = new ArrayList<>();
        //todo
    }


    @Override
    public void sendPlayerInfo(IActiveCharacter character, List<CharacterBase> target) {
        PaginationService paginationService = game.getServiceManager().provide(PaginationService.class).get();
        PaginationList.Builder builder = paginationService.builder();
        builder.padding(Text.builder("=").color(TextColors.GREEN).build());
        List<Text> content = new ArrayList<>();
        for (CharacterBase characterBase : target) {
            String name = characterBase.getName();
            int level = character.getPrimaryClass().getLevel();
            Text.Builder b = Text.builder();
            b.append(Text.builder(" [").color(TextColors.DARK_GRAY).build())
                    .append(Text.builder("SELECT").color(TextColors.GREEN).build())
                    .append(Text.builder("] - ").color(TextColors.DARK_GRAY).build());
            b.append(Text.of(name));
            if (character.getPrimaryClass() != null) {
                b.append(Text.builder(" ").build()).append(Text.of(level));
            }
            content.add(b.build());
        }
        builder.contents(content);
        builder.sendTo(character.getPlayer());
    }


    private Text getDetailedCharInfo(IActiveCharacter character) {
        Text text = Text.builder("Level").color(TextColors.YELLOW).append(
                Text.builder("Race").color(TextColors.RED).append(
                        Text.builder("Guild").color(TextColors.AQUA).append(
                                Text.builder("Class").color(TextColors.GOLD).build()
                        ).build()).build()).build();
        return text;
    }

    @Override
    public void sendPlayerInfo(IActiveCharacter character, IActiveCharacter target) {
        character.getPlayer().sendMessage(getDetailedCharInfo(target));
    }

    @Override
    public void showExpChange(IActiveCharacter character, String classname, double expchange) {
        IEffectContainer<Object, BossBarExpNotifier> barExpNotifier = character.getEffect(BossBarExpNotifier.name);
        BossBarExpNotifier effect = (BossBarExpNotifier) barExpNotifier;
        if (effect == null) {
            effect = new BossBarExpNotifier(character);
            effectService.addEffect(effect, InternalEffectSourceProvider.INSTANCE);
        }
        effect.notifyExpChange(character, classname, expchange);
    }

    @Override
    public void showLevelChange(IActiveCharacter character, PlayerClassData clazz, int level) {
        Player player = character.getPlayer();
        player.sendMessage(Text.of("Level up: " + clazz.getClassDefinition().getName() + " - " + level));
    }

    @Override
    public void sendStatus(IActiveCharacter character) {
        CharacterBase base = character.getCharacterBase();

        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        PaginationList.Builder builder = paginationService.builder();
        builder.title(Text.of(character.getName(), Color.YELLOW));
        builder.padding(Text.of("═", Color.GRAY));

        List<Text> content = new ArrayList<>();
        Set<CharacterClass> characterClasses = base.getCharacterClasses();
        for (CharacterClass cc : characterClasses) {
            Text t = Text.builder().append(Text.of(Utils.capitalizeFirst(cc.getName()), Color.GREEN))
                    .append(Text.of(" - ", TextColors.GRAY))
                    .append(Text.of(cc.getSkillPoints(), TextColors.BLUE))
                    .append(Text.of(String.format("(%s)", cc.getUsedSkillPoints()), TextColors.GRAY))

                    .toText();
            content.add(t);
        }
        content.add(Text.builder().append(Text.of("AttributeConfig points: ", TextColors.GREEN))
                .append(Text.of(character.getCharacterBase().getAttributePoints(), TextColors.AQUA))
                .append(Text.of(String.format("(%s)", character.getCharacterBase().getUsedAttributePoints(), TextColors.GRAY))).toText());

        builder.contents(content);
        builder.sendTo(character.getPlayer());
    }


    @Override
    public void showClassInfo(IActiveCharacter character, ClassDefinition cc) {
        Inventory i = createMenuInventoryClassDefView(cc);

        ItemStack of = GuiHelper.itemStack(ItemTypes.DIAMOND);
        of.offer(new InventoryCommandItemMenuData("character set class " + cc.getName()));
        of.offer(Keys.DISPLAY_NAME, Localizations.CONFIRM.toText());
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 1))).offer(of);

        ItemStack tree = GuiHelper.itemStack(ItemTypes.SAPLING);
        tree.offer(Keys.DISPLAY_NAME, Localizations.SKILLTREE.toText());
        tree.offer(new InventoryCommandItemMenuData("skilltree " + cc.getName()));
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(4, 3))).offer(tree);

        character.getPlayer().openInventory(i);
    }

    @Override
    public void sendListOfCharacters(final IActiveCharacter player, CharacterBase currentlyCreated) {
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        PaginationList.Builder builder = paginationService.builder();
        NtRpgPlugin.asyncExecutor.execute(() -> {

            List<CharacterBase> playersCharacters = characterService.getPlayersCharacters(player.getPlayer().getUniqueId());
            List<CharacterListModel> list = new ArrayList<>();
            for (CharacterBase playersCharacter : playersCharacters) {
                playerDao.fetchCharacterBase(playersCharacter);
                Set<CharacterClass> characterClasses = playersCharacter.getCharacterClasses();
                Integer pcExp = 0;
                for (CharacterClass characterClass : characterClasses) {
                    ClassDefinition classDefinitionByName = classService.getClassDefinitionByName(characterClass.getName());
                    if (classDefinitionByName == null) {
                        continue;
                    }
                    if (classDefinitionByName.getClassType().equalsIgnoreCase(NtRpgPlugin.pluginConfig.PRIMARY_CLASS_TYPE)) {
                        pcExp = characterClass.getLevel();
                        break;
                    }
                }
                String collect = playersCharacter.getCharacterClasses().stream().map(CharacterClass::getName).collect(Collectors.joining(", "));
                list.add(new CharacterListModel(
                        playersCharacter.getName(),
                        collect,
                        pcExp
                ));
            }


            List<Text> content = new ArrayList<>();
            builder.linesPerPage(10);
            builder.padding(Text.builder("=").color(TextColors.DARK_GRAY).build());

            String current = player.getName();

            list.forEach(a -> {
                Text.Builder b = Text.builder(" -")
                        .color(TextColors.GRAY);
                if (!a.getCharacterName().equalsIgnoreCase(current)) {
                    b.append(Text.builder(" [").color(TextColors.DARK_GRAY).build())
                            .append(Text.builder("SELECT").color(TextColors.GREEN)
                                    .onClick(TextActions.runCommand("/character switch " + a.getCharacterName())).build())
                            .append(Text.builder("] - ").color(TextColors.DARK_GRAY).build());
                } else {
                    b.append(Text.builder(" [").color(TextColors.DARK_GRAY).build())
                            .append(Text.builder("*").color(TextColors.RED).build())
                            .append(Text.builder("] - ").color(TextColors.DARK_GRAY).build());
                }
                b.append(Text.builder(a.getCharacterName()).color(TextColors.GRAY).append(Text.of(" ")).build());

                int level = a.getPrimaryClassLevel();
                int m = 0;

                b.append(Text.builder(a.getConcatClassNames()).color(TextColors.AQUA).append(Text.of(" ")).build());

                b.append(Text.builder("Level: ").color(TextColors.DARK_GRAY).append(
                        Text.builder(level + "").color(level == m ? TextColors.RED : TextColors.DARK_PURPLE).build()).build());

                content.add(b.build());
            });
            builder.title(Text.of("Characters", TextColors.WHITE))
                    .contents(content);
            builder.sendTo(player.getEntity());

        });
    }

    @Override
    public void sendListOfRunes(IActiveCharacter character) {
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        PaginationList.Builder builder = paginationService.builder();

        List<Text> content = new ArrayList<>();
        List<Rune> r = new ArrayList<>(rwService.getRunes().values());
        for (Rune rune : r) {
            LiteralText.Builder b = Text.builder(rune.getName()).color(TextColors.GOLD);
            content.add(b.build());
        }
        builder.contents(content);
        builder.linesPerPage(10);
        builder.padding(Text.builder("=").color(TextColors.DARK_GRAY).build());
        builder.sendTo(character.getPlayer());


    }

    private void displayCommonMenu(IActiveCharacter character, Collection<? extends ClassDefinition> g, ClassDefinition default_, Text invHeader) {
        Inventory i = Inventory.builder()
                .of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(invHeader))
                .build(plugin);
        Player player = character.getPlayer();
        for (ClassDefinition cc : g) {
            if (cc == default_) {
                continue;
            }
            if (!cc.isShowsInMenu() && !player.hasPermission("ntrpg.admin")) {
                continue;
            }
            i.offer(createItemRepresentingGroup(cc));
        }
        player.openInventory(i);
    }

    private ItemStack createItemRepresentingGroup(ClassDefinition p) {
        ItemStack s = GuiHelper.itemStack(p.getItemType());
        s.offer(new MenuInventoryData(true));
        s.offer(Keys.DISPLAY_NAME, Text.of(p.getName(), TextColors.DARK_PURPLE));
        s.offer(Keys.ITEM_LORE, getItemLore(p.getDescription()));
        String l = "race ";
        s.offer(new InventoryCommandItemMenuData(l + p.getName()));
        return s;
    }

    @Override
    public void displayGroupArmor(ClassDefinition g, IActiveCharacter c) {
        Player target = c.getPlayer();
        Inventory i = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).build(plugin);
        List<List<SpongeRpgItemType>> rows = new ArrayList<>(5);
        for (int ki = 0; ki <= 5; ki++) {
            rows.add(new ArrayList<>());
        }
        for (ClassItem ci : g.getAllowedArmor()) {
            SpongeRpgItemType type = (SpongeRpgItemType) ci.getType();
            if (ItemStackUtils.isHelmet(type.getItemType())) {
                rows.get(0).add(type);
            } else if (ItemStackUtils.isChestplate(type.getItemType())) {
                rows.get(1).add(type);
            } else if (ItemStackUtils.isLeggings(type.getItemType())) {
                rows.get(2).add(type);
            } else if (ItemStackUtils.isBoots(type.getItemType())) {
                rows.get(3).add(type);
            } else {
                rows.get(4).add(type);
            }
        }

        ItemStack of = GuiHelper.back(g);
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, 0))).offer(of);

        int x = 2;
        int y = 0;
        for (List<SpongeRpgItemType> row : rows) {
            y = 0;
            for (SpongeRpgItemType type : row) {
                ItemStack armor = GuiHelper.itemStack(type.getItemType());
                if (type.getModelId() != null) {
                    armor.offer(Keys.DISPLAY_NAME, Text.of(type.getModelId()));
                }
                armor.offer(new MenuInventoryData(true));
                i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(x, y))).offer(armor);
                y++;
            }
            x++;
        }
        target.openInventory(i);
    }

    @Override
    public void displayGroupWeapon(ClassDefinition g, IActiveCharacter c) {
        Player target = c.getPlayer();
        Inventory i = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).build(plugin);


        ItemStack of = back(g);
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, 0))).offer(of);

        TreeSet<ClassItem> treeSet = new TreeSet<>(Comparator.comparingInt(o -> (int) o.getDamage()));
        Set<ClassItem> weapons = g.getWeapons();
        treeSet.addAll(weapons);

        for (ClassItem configRPGItemType : treeSet) {
            SpongeRpgItemType type = (SpongeRpgItemType) configRPGItemType.getType();
            ItemStack q = GuiHelper.rpgItemTypeToItemStack(type);
            i.offer(q);
        }

        target.openInventory(i);
    }


    @Override
    public void sendClassInfo(IActiveCharacter target, ClassDefinition configClass) {
        Inventory i = createMenuInventoryClassDefView(configClass);
        target.getPlayer().openInventory(i);
    }

    @Override
    public void displayAttributes(IActiveCharacter target, ClassDefinition cls) {

        Inventory.Builder builder = Inventory
                .builder();
        Text invName = Localizations.ATTRIBUTES.toText();
        Inventory i = builder.of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(invName))
                .build(plugin);

        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, 0))).offer(back(cls));

        int x = 1;
        int y = 1;
        for (Map.Entry<AttributeConfig, Integer> a : cls.getStartingAttributes().entrySet()) {
            AttributeConfig key = a.getKey();
            Integer value = a.getValue();
            i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(x, y)))
                    .offer(createAttributeItem(key, value));
            //somehow format them in square-like structure
            if (x == 7) {
                x = 1;
                y++;
            } else {
                x++;
            }
        }
        target.getPlayer().openInventory(i);
    }

    @Override
    public void displayRuneword(IActiveCharacter character, RuneWord rw, boolean linkToRWList) {
        Inventory i = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).build(plugin);
        String cmd = infoCommand.getAliases().get(0);
        if (linkToRWList) {
            if (character.getPlayer().hasPermission("ntrpg.runewords.list")) {
                i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, 0))).offer(back("runes", Localizations.RUNE_LIST.toText()));
            }
        }

        List<ItemStack> commands = new ArrayList<>();
        if (!rw.getAllowedItems().isEmpty()) {
            ItemStack is = GuiHelper.itemStack(ItemTypes.IRON_PICKAXE);
            is.offer(Keys.DISPLAY_NAME, Localizations.RUNEWORD_ITEMS_MENU.toText());
            is.offer(Keys.ITEM_LORE,
                    Collections.singletonList(
                            Localizations.RUNEWORD_ITEMS_MENU_TOOLTIP.toText(Arg.arg("runeword", rw.getName()))
                    )
            );
            is.offer(new InventoryCommandItemMenuData("runeword " + rw.getName() + " allowed-items"));
            commands.add(is);
        }

        if (!rw.getAllowedGroups().isEmpty()) {
            ItemStack is = GuiHelper.itemStack(ItemTypes.LEATHER_HELMET);
            is.offer(Keys.DISPLAY_NAME, Localizations.RUNEWORD_ALLOWED_GROUPS_MENU.toText());
            is.offer(Keys.ITEM_LORE,
                    Collections.singletonList(
                            Localizations.RUNEWORD_ALLOWED_GROUPS_MENU_TOOLTIP.toText(Arg.arg("runeword", rw.getName()))
                    )
            );
            is.offer(Keys.HIDE_ATTRIBUTES, true);
            is.offer(new InventoryCommandItemMenuData("runeword " + rw.getName() + " allowed-classes"));
            commands.add(is);
        }

        if (!rw.getAllowedGroups().isEmpty()) {
            ItemStack is = GuiHelper.itemStack(ItemTypes.REDSTONE);
            is.offer(Keys.DISPLAY_NAME, Localizations.RUNEWORD_BLOCKED_GROUPS_MENU.toText());
            is.offer(Keys.ITEM_LORE,
                    Collections.singletonList(
                            Localizations.RUNEWORD_BLOCKED_GROUPS_MENU_TOOLTIP.toText(Arg.arg("runeword", rw.getName()))
                    )
            );
            is.offer(new InventoryCommandItemMenuData("runeword " + rw.getName() + " blocked-classes"));
            commands.add(is);
        }

        for (int q = 0; q < commands.size(); q++) {
            i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(q + 2, 2))).offer(commands.get(q));
        }

        if (character.getPlayer().hasPermission("ntrpg.runewords.combination.list")) {
            int x = 1;
            int y = 4;
            if (rw.getRunes().size() <= 7) {
                for (ItemUpgrade rune : rw.getRunes()) {
					/*ItemStack is = rwService.createRune(rune);
					is.offer(new MenuInventoryData(true));
					i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(x, y))).offer(is);
					x++;
					*/
                }
            } else {
                ItemStack is = ItemStack.of(rwService.getAllowedRuneItemTypes().get(0), rw.getRunes().size());
                is.offer(new MenuInventoryData(true));
                StringBuilder s = null;
                for (ItemUpgrade rune : rw.getRunes()) {
                    s.append(rune.getName());
                }
                is.offer(Keys.DISPLAY_NAME, Text.of(s.toString(), TextColors.GOLD));
                i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(x, y))).offer(is);
            }
        }

        character.getPlayer().openInventory(i);
    }


    @Override
    public void displayRunewordBlockedGroups(IActiveCharacter character, RuneWord rw) {
        character.getPlayer().openInventory(displayGroupRequirements(character, rw, rw.getAllowedGroups()));
    }

    @Override
    public void displayRunewordRequiredGroups(IActiveCharacter character, RuneWord rw) {

    }

    @Override
    public void displayRunewordAllowedGroups(IActiveCharacter character, RuneWord rw) {

    }

    @Override
    public void displayRunewordAllowedItems(IActiveCharacter character, RuneWord rw) {
        Inventory i = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).build(plugin);
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, 0)))
                .offer(back("runeword " + rw.getName(), Localizations.RUNEWORD_DETAILS_MENU.toText()));
        int x = 1;
        int y = 2;
        for (String type : rw.getAllowedItems()) {
            ItemType itemType = Sponge.getRegistry().getType(ItemType.class, type).orElse(ItemTypes.STONE);
            i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(x, y))).offer(GuiHelper.itemStack(itemType));
            if (x == 7) {
                x = 1;
                y++;
            } else {
                x++;
            }
        }

    }

    private Inventory displayGroupRequirements(IActiveCharacter character, RuneWord rw, Set<ClassDefinition> groups) {
        Inventory i = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).build(plugin);
        String cmd = infoCommand.getAliases().get(0);
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, 0)))
                .offer(back("runeword " + rw.getName(), Localizations.RUNEWORD_DETAILS_MENU.toText()));

        List<ItemStack> list = new ArrayList<>();
        for (ClassDefinition classDefinition : groups) {
            list.add(runewordRequirementsToItemStack(character, classDefinition));
        }
        int x = 1;
        int y = 2;
        for (ItemStack itemStack : list) {
            i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(x, y))).offer(itemStack);
            if (x == 7) {
                x = 1;
                y++;
            } else {
                x++;
            }
        }
        return i;
    }

    private ItemStack runewordRequirementsToItemStack(IActiveCharacter character, ClassDefinition classDefinition) {
        ItemStack is = createItemRepresentingGroup(classDefinition);
        TextColor color = hasGroup(character, classDefinition);
        is.offer(Keys.DISPLAY_NAME, Text.of(color, classDefinition.getName()));
        return is;
    }

    private TextColor hasGroup(IActiveCharacter character, ClassDefinition classDefinition) {
        return character.hasClass(classDefinition) ? TextColors.GREEN : TextColors.RED;
    }

    private ItemStack createAttributeItem(AttributeConfig key, Integer value) {
        ItemStack of = GuiHelper.itemStack(key.getItemType());
        of.offer(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_RED, key.getName()));

        of.offer(new MenuInventoryData(true));
        List<Text> lore = new ArrayList<>();
        lore.add(Localizations.INITIAL_VALUE.toText(Arg.arg("value", value)));
        if (key.getDescription() != null) {
            lore.addAll(getItemLore(key.getDescription()));
        }
        of.offer(Keys.ITEM_LORE, lore);
        return of;
    }


    @Override
    public void displayHealth(IActiveCharacter character) {
        double value = character.getHealth().getValue();
        double maxValue = character.getHealth().getMaxValue();
        //todo implement
        //double reservedAmount = character.getHealth().getReservedAmount();

        Text a = Localizations.HEALTH.toText(Arg.arg("current", value).with("maxValue", maxValue));
        character.getPlayer().sendMessage(a);
    }

    @Override
    public void displayMana(IActiveCharacter character) {
        IEffectContainer<Object, ManaBarNotifier> barExpNotifier = character.getEffect(ManaBarNotifier.name);
        ManaBarNotifier effect = (ManaBarNotifier) barExpNotifier;
        if (effect == null) {
            effect = new ManaBarNotifier(character);
            effectService.addEffect(effect, InternalEffectSourceProvider.INSTANCE);
        }
        effect.notifyManaChange();

    }

    @Override
    public void sendCannotUseItemNotification(IActiveCharacter character, String item, CannotUseItemReason reason) {
        if (reason == CannotUseItemReason.CONFIG) {
            character.getPlayer()
                    .sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, Localizations.CANNOT_USE_ITEM_CONFIGURATION_REASON.toText()));
        } else if (reason == CannotUseItemReason.LEVEL) {
            character.getPlayer().sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, Localizations.CANNOT_USE_ITEM_LEVEL_REASON.toText()));
        } else if (reason == CannotUseItemReason.LORE) {
            character.getPlayer().sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, Localizations.CANNOT_USE_ITEM_LORE_REASON.toText()));
        }
    }


    @Override
    public void openSkillTreeMenu(IActiveCharacter player) {
        SkillTree skillTree = player.getLastTimeInvokedSkillTreeView().getSkillTree();
        if (player.getSkillTreeViewLocation().get(skillTree.getId()) == null) {
            SkillTreeViewModel skillTreeViewModel = new SkillTreeViewModel();
            for (SkillTreeViewModel treeViewModel : player.getSkillTreeViewLocation().values()) {
                treeViewModel.setCurrent(false);
            }
            player.getSkillTreeViewLocation().put(skillTree.getId(), skillTreeViewModel);
            skillTreeViewModel.setSkillTree(skillTree);
        }
        Inventory skillTreeInventoryViewTemplate = GuiHelper.createSkillTreeInventoryViewTemplate(player, skillTree);
        createSkillTreeView(player, skillTreeInventoryViewTemplate);
        player.getPlayer().openInventory(skillTreeInventoryViewTemplate);

    }


    @Override
    public void moveSkillTreeMenu(IActiveCharacter character) {
        Optional<Container> openInventory = character.getPlayer().getOpenInventory();
        if (openInventory.isPresent()) {
            createSkillTreeView(character, openInventory.get().query(GridInventory.class).first());
        }
    }

    @Override
    public void displaySkillDetailsInventoryMenu(IActiveCharacter character, SkillTree tree, String command) {
        Inventory skillDetailInventoryView = GuiHelper.createSkillDetailInventoryView(character, tree, tree.getSkillById(command));
        character.getPlayer().openInventory(skillDetailInventoryView);
    }

    @Override
    public void displayInitialProperties(ClassDefinition byName, IActiveCharacter player) {
        ItemStack back = GuiHelper.back(byName);

    }

    @Override
    public void sendCannotUseItemInOffHandNotification(String futureOffHandItem, IActiveCharacter character, CannotUseItemReason reason) {
        if (reason == CannotUseItemReason.CONFIG) {
            character.getPlayer().sendMessage(ChatTypes.ACTION_BAR, Localizations.CANNOT_USE_ITEM_CONFIGURATION_REASON_OFFHAND.toText());
        } else if (reason == CannotUseItemReason.LEVEL) {
            character.getPlayer().sendMessage(ChatTypes.ACTION_BAR, Localizations.CANNOT_USE_ITEM_LEVEL_REASON.toText());
        } else if (reason == CannotUseItemReason.LORE) {
            character.getPlayer().sendMessage(ChatTypes.ACTION_BAR, Localizations.CANNOT_USE_ITEM_LORE_REASON.toText());
        }
    }

    private void createSkillTreeView(IActiveCharacter character, Inventory skillTreeInventoryViewTemplate) {

        SkillTreeViewModel skillTreeViewModel = character.getLastTimeInvokedSkillTreeView();
        SkillTree skillTree = skillTreeViewModel.getSkillTree();
        short[][] skillTreeMap = skillTreeViewModel.getSkillTree().getSkillTreeMap();
        int y = skillTree.getCenter().value + skillTreeViewModel.getLocation().value; //y
        int x = skillTree.getCenter().key + skillTreeViewModel.getLocation().key; //x

        if (skillTreeMap == null) {
            throw new IllegalStateException("No AsciiMap defined for skilltree: " + skillTree.getId());
        }

        int columns = skillTreeMap[0].length;
        int rows = skillTreeMap.length;

        SkillTreeViewModel.InteractiveMode interactiveMode = skillTreeViewModel.getInteractiveMode();
        ItemStack md = GuiHelper.interactiveModeToitemStack(character, interactiveMode);
        skillTreeInventoryViewTemplate.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, 1))).clear();
        skillTreeInventoryViewTemplate.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, 1))).offer(md);


        for (int k = -3; k <= 3; k++) { //x
            for (int l = -3; l <= 3; l++) { //y
                Inventory query = skillTreeInventoryViewTemplate
                        .query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(l + 3, k + 3)));
                query.clear();
                if (x + k >= 0 && x + k < rows) {
                    if (l + y >= 0 && l + y < columns) {

                        short id = skillTreeMap[x + k][l + y];
                        ItemStack itemStack = null;
                        if (id > 0) {
                            SkillTreeInterfaceModel guiModelById = skillService.getGuiModelById(id);
                            if (guiModelById != null) {
                                itemStack = guiModelById.toItemStack();
                            } else {
                                SkillData skillById = skillTree.getSkillById(id);

                                if (skillById == null) {
                                    itemStack = GuiHelper.itemStack(ItemTypes.BARRIER);
                                    itemStack.offer(Keys.DISPLAY_NAME, Text.of("UNKNOWN SKILL ID: " + id));
                                    itemStack.offer(new MenuInventoryData(true));
                                } else {
                                    itemStack = GuiHelper.skillToItemStack(character, skillById, skillTree);
                                    itemStack.offer(new SkillTreeInventoryViewControllsData(SkillTreeControllsButton.NODE));
                                    itemStack.offer(new MenuInventoryData(true));
                                    itemStack.offer(new SkillTreeNode(skillById.getSkill().getId()));
                                }
                            }
                        }
                        if (itemStack == null) {
                            itemStack = ItemStack.of(ItemTypes.STAINED_GLASS_PANE, 1);
                            itemStack.offer(Keys.DISPLAY_NAME, Text.EMPTY);
                            itemStack.offer(Keys.DYE_COLOR, DyeColors.GRAY);
                            itemStack.offer(Keys.HIDE_MISCELLANEOUS, true);
                            itemStack.offer(new MenuInventoryData(true));
                        }
                        query.offer(itemStack);
                    } else {
                        //	SlotPos slotPos = new SlotPos(l + 3, k + 3);
                        query.offer(GuiHelper.createSkillTreeInventoryMenuBoundary());
                    }
                } else {
                    query.offer(GuiHelper.createSkillTreeInventoryMenuBoundary());
                }
            }
        }
    }

    @Override
    public void skillExecution(IActiveCharacter character, PlayerSkillContext skill) {
        character.sendMessage(ChatTypes.ACTION_BAR,
                Text.builder(skill.getSkill().getName())
                        .style(TextStyles.BOLD)
                        .color(TextColors.GOLD)
                        .build()
        );
    }

    @Override
    public void sendClassesByType(IActiveCharacter character, String type) {
        Inventory i = GuiHelper.createMenuInventoryClassTypeView(type);

        character.getPlayer().openInventory(i);
    }

    @Override
    public void sendClassTypes(IActiveCharacter character) {
        Inventory i = GuiHelper.createMenuInventoryClassTypesView();

        character.getPlayer().openInventory(i);
    }

    @Override
    public void displayCharacterMenu(IActiveCharacter character) {
        Inventory i = GuiHelper.createCharacterEmptyInventory(character).build(NtRpgPlugin.GlobalScope.plugin);

        ItemStack itemStack = GuiHelper.itemStack(ItemTypes.BOOK);
        itemStack.offer(Keys.DISPLAY_NAME, Localizations.ATTRIBUTES.toText());
        itemStack.offer(new InventoryCommandItemMenuData("character attributes "));
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(1, 1))).offer(itemStack);

        //todo more

        character.getPlayer().openInventory(i);
    }

    @Override
    public void displayCharacterAttributes(IActiveCharacter character) {
        Inventory i = GuiHelper.createCharacterEmptyInventory(character)
                .listener(ClickInventoryEvent.Primary.class, event -> {
                    Player root = (Player) event.getCause().root();
                    Iterator<SlotTransaction> iterator = event.getTransactions().iterator();
                    while (iterator.hasNext()) {
                        SlotTransaction next = iterator.next();
                        Optional<String> s = next.getFinal().get(NKeys.ATTRIBUTE_REF);
                        if (s.isPresent()) {
                            Slot slot = next.getSlot();
                            Optional<SlotPos> properties = slot.transform().getInventoryProperty(SlotPos.class);
                            SlotPos slotPos = properties.get();
                            slotPos = new SlotPos(slotPos.getX(), slotPos.getY() - 1);

                        }
                        break;
                    }
                })
                .build(NtRpgPlugin.GlobalScope.plugin);

        i.offer(back("character ", Text.of("back")));

        ItemStack commit = GuiHelper.itemStack(ItemTypes.DIAMOND);
        commit.offer(Keys.DISPLAY_NAME, Text.builder("Commit").color(TextColors.GREEN).build());
        commit.offer(new InventoryCommandItemMenuData("character attributes tx-commit "));

        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 0)))
                .offer(commit);


        Collection<AttributeConfig> allOf = Sponge.getRegistry().getAllOf(AttributeConfig.class);

        int q = 0;
        for (AttributeConfig attribute : allOf) {

            ItemStack itemStack = GuiHelper.itemStack(attribute.getItemType());
            itemStack.offer(Keys.DISPLAY_NAME, Text.of(attribute.getName()));
            List<Text> text = TextHelper.splitStringByDelimiter(attribute.getDescription());
            Integer amount = character.getCharacterBase().getAttributes().get(attribute.getId());
            text.add(Text.builder("Val: " + (amount == null ? 0 : amount) + " / " + attribute.getMaxValue()).build());
            itemStack.offer(Keys.ITEM_LORE, text);

            ItemStack btn = GuiHelper.itemStack(ItemTypes.SUGAR);
            btn.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "+"));
            btn.offer(new AttributeRefMenuData(attribute.getId()));
            i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(q, 2))).offer(btn);
            i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(q, 3))).offer(itemStack);
            q++;
        }
    }

    @Override
    public void displayCurrentClicks(IActiveCharacter character, String combo) {
        String split = combo.replaceAll(".", "$0 ");
        LiteralText build = Text.builder(split).color(TextColors.GREEN).style(TextStyles.UNDERLINE)
                .append(Text.builder("_").color(TextColors.GRAY).build()).build();

        character.getPlayer().sendMessage(ChatTypes.ACTION_BAR, build);
    }
}

