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

package cz.neumimto.rpg.gui;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.GroupService;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.commands.CommandChoose;
import cz.neumimto.rpg.commands.InfoCommand;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.EffectStatusType;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.common.def.BossBarExpNotifier;
import cz.neumimto.rpg.inventory.InventoryMenu;
import cz.neumimto.rpg.inventory.data.InventoryItemMenuData;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.runewords.RWService;
import cz.neumimto.rpg.inventory.runewords.Rune;
import cz.neumimto.rpg.persistance.DirectAccessDao;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.players.groups.PlayerGroup;
import cz.neumimto.rpg.players.groups.Race;
import cz.neumimto.rpg.skills.SkillData;
import cz.neumimto.rpg.skills.SkillTree;
import cz.neumimto.rpg.utils.ItemStackUtils;
import cz.neumimto.rpg.utils.Utils;
import cz.neumimto.rpg.utils.model.CharacterListModel;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.Color;
import org.spongepowered.common.item.inventory.custom.CustomContainer;
import org.spongepowered.common.item.inventory.custom.CustomInventory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by NeumimTo on 6.8.2015.
 */
@Singleton
@ResourceLoader.ListenerClass
public class VanilaMessaging implements IPlayerMessage {

    @Inject
    private Game game;

    @Inject
    private GroupService groupService;

    @Inject
    private EffectService effectService;

    @Inject
    private NtRpgPlugin plugin;

    @Inject
    private RWService rwService;

    @Override
    public boolean isClientSideGui() {
        return false;
    }

    @Override
    public void invokerDefaultMenu(IActiveCharacter character) {

    }

    @Override
    public void sendMessage(IActiveCharacter player, String message) {
        player.sendMessage(message);
    }

    @Override
    public void sendCooldownMessage(IActiveCharacter player, String message, double cooldown) {
        sendMessage(player, Localization.ON_COOLDOWN.replaceAll("%1", message).replace("%2", String.valueOf(cooldown)));
    }

    @Override
    public void openSkillTreeMenu(IActiveCharacter player, SkillTree skillTree, SkillData skillData) {
        Sponge.getScheduler().createTaskBuilder().async().execute(() -> {

        }).submit(plugin);
    }

    @Override
    public void moveSkillTreeMenu(IActiveCharacter player, SkillTree skillTree, Map<String, Integer> learnedSkill, SkillData center) {

    }

    @Override
    public void sendEffectStatus(IActiveCharacter player, EffectStatusType type, IEffect effect) {
        sendMessage(player, type.toMessage(effect));
    }

    @Override
    public void invokeCharacterMenu(Player player, List<CharacterBase> characterBases) {
        ItemStack.Builder b = ItemStack.builder();
        List<ItemStack> list = new ArrayList<>();
        //todo
    }

    @Override
    public void sendManaStatus(IActiveCharacter character, double currentMana, double maxMana, double reserved) {
        Text.Builder b = Text.builder("Mana: " + currentMana).color(TextColors.BLUE);
        if (reserved != 0) {
            b.append(Text.builder(" / " + (maxMana - reserved)).color(TextColors.DARK_RED).build());
        }
        b.append(Text.builder(" | " + maxMana).color(TextColors.GRAY).build());
        character.getPlayer().sendMessage(b.build());
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
            String name1 = character.getRace().getName();
            Text.Builder b = Text.builder();
            b.append(Text.builder(" [").color(TextColors.DARK_GRAY).build())
                    .append(Text.builder("SELECT").color(TextColors.GREEN).onClick(TextActions.runCommand("/" + "show" + " character " + name)).build())
                    .append(Text.builder("] - ").color(TextColors.DARK_GRAY).build());
            b.append(Text.of(name)).append(Text.builder(" ").build()).append(Text.of(level));
            b.append(Text.of(name1));
            content.add(b.build());
        }
        builder.contents(content);
        builder.sendTo(character.getPlayer());
    }


    private String getDetailedCharInfo(IActiveCharacter character) {
        Text text = Text.builder("Level").color(TextColors.YELLOW).append(
                Text.builder("Race").color(TextColors.RED).append(
                        Text.builder("Guild").color(TextColors.AQUA).append(
                                Text.builder("Class").color(TextColors.GOLD).build()
                        ).build()).build()).build();
        return text.toString();
    }

    @Override
    public void sendPlayerInfo(IActiveCharacter character, IActiveCharacter target) {
        character.sendMessage(getDetailedCharInfo(target));
    }

    @Override
    public void showExpChange(IActiveCharacter character, String classname, double expchange) {
        IEffect effect = character.getEffect(BossBarExpNotifier.class);
        if (effect == null) {
            effect = new BossBarExpNotifier(character);
            effectService.addEffect(effect, character);
        }
        BossBarExpNotifier bossbar = (BossBarExpNotifier) effect;
        bossbar.setLevel(character.getPrimaryClass().getLevel());
        bossbar.notifyExpChange(classname, expchange);
    }

    @Override
    public void showLevelChange(IActiveCharacter character, ExtendedNClass clazz, int level) {
        Player player = character.getPlayer();
        player.sendMessage(Text.of("Level up: " + clazz.getConfigClass().getName() + " - " + level));
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
        content.add(Text.builder().append(Text.of("Attribute points: ", TextColors.GREEN))
                .append(Text.of(character.getCharacterBase().getAttributePoints(), TextColors.AQUA))
                .append(Text.of(String.format("(%s)", character.getCharacterBase().getUsedAttributePoints(), TextColors.GRAY))).toText());
        Player player = character.getPlayer();

        builder.contents(content);
        builder.sendTo(character.getPlayer());
    }

    @Override
    public void showAvalaibleClasses(IActiveCharacter character) {
        final Collection<ConfigClass> classes = groupService.getClasses();
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        final PaginationList.Builder builder = paginationService.builder();
        Sponge.getScheduler().createTaskBuilder().async().execute(() -> {

            builder.padding(Text.of("=", TextColors.DARK_GRAY));
            builder.header(Text.of("=", TextColors.DARK_GRAY));
            builder.linesPerPage(10);
            List<Text> texts = new ArrayList<>();

            String next = IoC.get().build(InfoCommand.class).getAliases().iterator().next();

            classes.stream().filter(PlayerGroup::isShowsInMenu).forEach(cc -> {
                String name = cc.getName();
                Text t = Text.builder().append(
                        Text.builder(" [").color(TextColors.DARK_GRAY)
                                .append(Text.builder("DETAILS").color(TextColors.GREEN).onClick(TextActions.runCommand("/" + next + " class " + name)).build())
                                .append(Text.builder("] - ").color(TextColors.DARK_GRAY).build())
                                .append(Text.builder(name).color(TextColors.DARK_RED)
                                        .onHover(TextActions.showText(Text.of(cc.getDescription(), TextColors.GRAY, TextStyles.UNDERLINE))).build()
                                ).build()).build();

                texts.add(t);
            });
            builder.contents(texts);
            builder.sendTo(character.getPlayer());

        }).submit(plugin);
    }

    @Override
    public void showClassInfo(IActiveCharacter character, ConfigClass cc) {

        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        final PaginationList.Builder builder = paginationService.builder();
        Sponge.getScheduler().createTaskBuilder().async().execute(() -> {
            List<Text> content = new ArrayList<Text>();
            builder.linesPerPage(16);
            builder.padding(Text.builder("=").color(TextColors.DARK_GRAY).build());
            builder.title(Text.of("Class Details"));
            int attributepointsperlevel = cc.getAttributepointsperlevel();
            String name = cc.getName();
            double[] levels = cc.getLevels();
            int maxLevel = cc.getMaxLevel();
            double totalExp = cc.getTotalExp();
            Set<ItemType> allowedArmor = cc.getAllowedArmor();
            Map<ItemType, Double> weapons = cc.getWeapons();

            builder.header(Text.of(name));

            content.add(Text.builder().color(TextColors.GREEN).append(Text.of(Localization.ATTRIBUTE_POINTS_PER_LEVEL + ": ", TextColors.GREEN))
                    .append(Text.of(attributepointsperlevel,TextColors.YELLOW)).build());

            content.add(Text.builder().color(TextColors.GREEN).append(Text.of(Localization.MAX_LEVEL + ": "))
                    .append(Text.of(maxLevel,TextColors.YELLOW)).build());

            content.add(Text.builder().color(TextColors.GREEN).append(Text.of(Localization.TOTAL_EXP + ": ", TextColors.GREEN))
                    .append(Text.builder(totalExp+"").color(TextColors.YELLOW).build()).build());

            content.add(Text.builder().color(TextColors.GREEN).append(Text.of(Localization.ALLOWED_ARMOR + ":", TextColors.GREEN)).build());

            SkillTree tree = cc.getSkillTree();
            if (tree != null) {
                String next = IoC.get().build(InfoCommand.class).getAliases().iterator().next();
                content.add(Text.builder(" [").color(TextColors.DARK_GRAY)
                    .append(Text.builder("DETAIL").color(TextColors.GREEN).onClick(TextActions.runCommand("/" + next + " skilltree " + cc.getSkillTree().getId())).build())
                    .append(Text.builder("] - ").color(TextColors.DARK_GRAY).build())
                    .append(Text.of(tree.getDescription())).build());
            }

            Text.Builder helmets = Text.builder().color(TextColors.YELLOW);
            Text.Builder chestplates = Text.builder().color(TextColors.YELLOW);
            Text.Builder leggings = Text.builder().color(TextColors.YELLOW);
            Text.Builder boots = Text.builder().color(TextColors.YELLOW);

            for (ItemType type : allowedArmor) {
                if (ItemStackUtils.isHelmet(type)) {
                    helmets.append(Text.of(" " + type.getName()));
                } else if (ItemStackUtils.isChestplate(type)) {
                    chestplates.append(Text.of(" " + type.getName()));
                } else if (ItemStackUtils.isLeggings(type)) {
                    leggings.append(Text.of(" " + type.getName()));
                } else if (ItemStackUtils.isBoots(type)) {
                    boots.append(Text.of(" " + type.getName()));
                }
            }


            content.add(Text.builder().color(TextColors.GREEN).append(Text.of(Localization.HELMETS + ":", TextColors.GREEN))
                    .append(helmets.build()).build());
            content.add(Text.builder().color(TextColors.GREEN).append(Text.of(Localization.CHESTPLATES + ":", TextColors.GREEN))
                    .append(chestplates.build()).build());
            content.add(Text.builder().color(TextColors.GREEN).append(Text.of(Localization.LEGGINGS + ":", TextColors.GREEN))
                    .append(leggings.build()).build());
            content.add(Text.builder().color(TextColors.GREEN).append(Text.of(Localization.BOOTS + ":", TextColors.GREEN))
                    .append(boots.build()).build());

            //todo weapons + levels

            builder.contents(content);
            builder.sendTo(character.getPlayer());
        }).submit(plugin);
    }

    @Override
    public void sendListOfCharacters(final IActiveCharacter player, CharacterBase currentlyCreated) {
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        PaginationList.Builder builder = paginationService.builder();
        Sponge.getScheduler().createTaskBuilder().async().execute(() -> {
            DirectAccessDao build = IoC.get().build(DirectAccessDao.class);
            String query = "select new cz.neumimto.rpg.utils.model.CharacterListModel(" +
                    "c.name,d.name,d.experiences) " +
                    "from CharacterBase c left join c.characterClasses d " +
                    "where c.uuid = :id order by c.updated desc";
            Map map = new HashMap<>();
            map.put("id", player.getPlayer().getUniqueId());
            List<CharacterListModel> list = build.findList(CharacterListModel.class, query, map);
            List<Text> content = new ArrayList<Text>();
            builder.linesPerPage(10);
            builder.padding(Text.builder("=").color(TextColors.DARK_GRAY).build());
            GroupService s = IoC.get().build(GroupService.class);
            String current = player.getName();
            CommandChoose build1 = IoC.get().build(CommandChoose.class);
            String s1 = build1.getAliases().get(0);
            list.forEach(a -> {
                Text.Builder b = Text.builder(" -")
                        .color(TextColors.GRAY);
                if (!a.getCharacterName().equalsIgnoreCase(current)) {
                    b.append(Text.builder(" [").color(TextColors.DARK_GRAY).build())
                            .append(Text.builder("SELECT").color(TextColors.GREEN).onClick(TextActions.runCommand("/" + s1 + " character " + a.getCharacterName())).build())
                            .append(Text.builder("] - ").color(TextColors.DARK_GRAY).build());
                } else {
                    b.append(Text.builder(" [").color(TextColors.DARK_GRAY).build())
                            .append(Text.builder("*").color(TextColors.RED).build())
                            .append(Text.builder("] - ").color(TextColors.DARK_GRAY).build());
                }
                b.append(Text.builder(a.getCharacterName()).color(TextColors.GRAY).append(Text.of(" ")).build());
                b.append(Text.builder(a.getPrimaryClassName()).color(TextColors.AQUA).append(Text.of(" ")).build());
                ConfigClass cc = s.getNClass(a.getPrimaryClassName());
                int level = 0;
                int m = 0;
                if (cc != ConfigClass.Default) {
                    level = s.getLevel(cc, a.getPrimaryClassExp());
                    m = cc.getMaxLevel();
                }
                b.append(Text.builder("Level: ").color(TextColors.DARK_GRAY).append(
                        Text.builder(level + "").color(level == m ? TextColors.RED : TextColors.DARK_PURPLE).build()).build());
                content.add(b.build());
            });
            builder.title(Text.of("Characters", TextColors.WHITE))
                    .contents(content);
            builder.sendTo(player.getEntity());

        }).submit(plugin);
    }

    @Override
    public void sendListOfRunes(IActiveCharacter character) {
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        PaginationList.Builder builder = paginationService.builder();

        List<Text> content = new ArrayList<>();
        List<Rune> r = new ArrayList<>(rwService.getRunes().values());
        Collections.sort(r,(o1, o2) -> (int)(o1.getSpawnchance() - o2.getSpawnchance()));
        for (Rune rune : r) {
            content.add(Text.builder(rune.getName()).color(TextColors.GREEN).append(Text.builder(" - ").color(TextColors.WHITE).build()).append(rune.getLore()).color(TextColors.DARK_PURPLE).build());
        }
        builder.contents(content);
        builder.linesPerPage(10);
        builder.padding(Text.builder("=").color(TextColors.DARK_GRAY).build());
        builder.sendTo(character.getPlayer());


    }

    @Override
    public void sendListOfRaces(IActiveCharacter target) {
        String s1 = IoC.get().build(InfoCommand.class).getAliases().iterator().next();
        Inventory i = Inventory.builder().of(InventoryArchetypes.CHEST).build(plugin);
        int x = 0;
        for (Race race : groupService.getRaces()) {
            if (race == Race.Default) {
                continue;
            }
            if (!race.isShowsInMenu() && !target.getPlayer().hasPermission("ntrpg.showsEverything")) {
                continue;
            }
            ItemStack s = ItemStack.of(race.getItemType(),1);
            s.offer(Keys.DISPLAY_NAME, Text.of(race.getName(), TextColors.DARK_PURPLE));
            s.offer(Keys.ITEM_LORE, getItemLore(race.getDescription()));
            s.offer(new InventoryItemMenuData(s1 + " race " + race.getName()));
            i.offer(s);
        }
        target.getPlayer().openInventory(i, Cause.of(NamedCause.source("ntrpg-itemmenu")));
    }

    @Override
    public void displayGroupArmor(PlayerGroup g, Player target) {
        Inventory i = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).build(plugin);
        List<List<ItemType>> rows = new ArrayList<>(5);
        for (int ki = 0;ki <=5; ki++) {
            rows.add(new ArrayList<>());
        }
        for (ItemType type : g.getAllowedArmor()) {
            if (ItemStackUtils.isHelmet(type)) {
                rows.get(0).add(type);
            } else if (ItemStackUtils.isChestplate(type)) {
                rows.get(1).add(type);
            } else if (ItemStackUtils.isLeggings(type)) {
                rows.get(2).add(type);
            } else if (ItemStackUtils.isBoots(type)) {
                rows.get(3).add(type);
            } else {
                rows.get(4).add(type);
            }
        }
        ItemStack of = ItemStack.of(ItemTypes.PAPER, 1);
        String l = "class";
        if (g instanceof Race) {
            l = "race";
        }
        of.offer(Keys.DISPLAY_NAME, Text.of(Localization.BACK, TextColors.WHITE));
        of.offer(new InventoryItemMenuData("show "+ l + " " + g.getName()));
        i.query(new SlotPos(0,0)).offer(of);

        int x = 2;
        int y = 0;
        for (List<ItemType> row : rows) {
            y = 0;
            for (ItemType type : row) {
                i.query(new SlotPos(x,y)).offer(ItemStack.of(type, 1));
                y++;
            }
            x++;
        }
    }

    @Override
    public void displayGroupWeapon(PlayerGroup g, Player target) {
        Inventory i = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).build(plugin);
        List<List<ItemType>> rows = new ArrayList<>(5);
        for (int ki = 0;ki <=5; ki++) {
            rows.add(new ArrayList<>());
        }

        ItemStack of = ItemStack.of(ItemTypes.PAPER, 1);
        String l = "class";
        if (g instanceof Race) {
            l = "race";
        }
        of.offer(Keys.DISPLAY_NAME, Text.of(Localization.BACK, TextColors.WHITE));
        of.offer(new InventoryItemMenuData("show "+ l + " " + g.getName()));
        i.query(new SlotPos(0,0)).offer(of);

        g.getWeapons().entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new)).forEach((type, aDouble) -> {
            ItemStack q = ItemStack.of(type, 1);
            q.offer(Keys.ITEM_LORE, Collections.singletonList(Text.of(TextColors.DARK_RED, TextStyles.BOLD, aDouble.toString())));
            i.offer(q);
        });
    }

    @Override
    public void sendRaceInfo(IActiveCharacter target, Race race) {
        Inventory i = createPlayerGroupView(race);
        target.getPlayer().openInventory(i, Cause.of(NamedCause.of("ntrpg", plugin)));
    }

    @Listener
    public void onOptionSelect(ClickInventoryEvent event, @First(typeFilter = Player.class) Player player ) {
        //todo fix
        if (event.getTargetInventory().getClass().getSimpleName().toLowerCase().contains("custom")) {
            List<SlotTransaction> transactions = event.getTransactions();
            player.closeInventory(Cause.of(NamedCause.of("player", player)));
            if (transactions.size() == 1) {
                SlotTransaction t = transactions.get(0);
                Optional<String> s = t.getOriginal().get(NKeys.ANY_STRING);
                if (s.isPresent()) {
                    Sponge.getCommandManager().process(player, s.get());
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    public Inventory createPlayerGroupView(PlayerGroup group) {
        Inventory.Builder builder = Inventory.builder();
        Inventory i = builder.of(InventoryArchetypes.DOUBLE_CHEST).build(plugin);
        i.query(new SlotPos(3,3)).offer(createWeaponCommand(group));
        i.query(new SlotPos(3,4)).offer(createArmorCommand(group));
        i.query(new SlotPos(4,3)).offer(createAttributesCommand(group));
        i.query(new SlotPos(0,0)).offer(createDescriptionItem(group.getDescription()));
        return i;
    }

    private ItemStack createAttributesCommand(PlayerGroup group) {
        ItemStack i = ItemStack.of(ItemTypes.BOOK, 1);
        i.offer(Keys.DISPLAY_NAME, Text.of(Localization.ATTRIBUTES, TextColors.DARK_RED));
        i.offer(new InventoryItemMenuData("show attributes " + group.getName()));
        return i;
    }

    private ItemStack createDescriptionItem(String description) {
        ItemStack i = ItemStack.of(ItemTypes.PAPER, 1);
        i.offer(Keys.DISPLAY_NAME, Text.of(""));
        i.offer(Keys.ITEM_LORE, Arrays.asList(Text.of(description,TextColors.GRAY)));
        return i;
    }

    private ItemStack createArmorCommand(PlayerGroup group) {
        ItemStack i = ItemStack.of(ItemTypes.DIAMOND_SWORD, 1);

        i.offer(Keys.DISPLAY_NAME, Text.of(Localization.WEAPONS, TextColors.DARK_RED));
        i.offer(Keys.ITEM_LORE, Arrays.asList(Text.of(Localization.WEAPONS_MENU_HELP,TextColors.GRAY)));
        i.offer(new InventoryItemMenuData("show armor " + group.getName()));
        return i;
    }

    private ItemStack createWeaponCommand(PlayerGroup group) {
        ItemStack i = ItemStack.of(ItemTypes.DIAMOND_CHESTPLATE, 1);

        i.offer(Keys.DISPLAY_NAME, Text.of(Localization.ARMOR, TextColors.DARK_RED));
        i.offer(Keys.ITEM_LORE, Arrays.asList(Text.of(Localization.ARMOR_MENU_HELP,TextColors.GRAY)));
        i.offer(new InventoryItemMenuData("show weapons " + group.getName()));
        return i;
    }

    public List<Text> getItemLore(String s) {
        String[] a = s.split("\\n");
        List<Text> t = new ArrayList<>();
        for (String s1 : a) {
            t.add(Text.builder(s1).color(TextColors.GOLD).style(TextStyles.ITALIC).build());
        }
        return t;
    }
}
