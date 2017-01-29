package cz.neumimto.rpg.inventory.runewords;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.GroupService;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.Pair;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.events.RebuildRunewordEvent;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.players.groups.PlayerGroup;
import cz.neumimto.rpg.utils.ItemStackUtils;
import cz.neumimto.rpg.utils.Utils;
import cz.neumimto.rpg.utils.XORShiftRnd;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by NeumimTo on 29.10.2015.
 */
@Singleton
public class RWService {

    private final Pattern socket = Pattern.compile("\\{@\\}");
    private final Path file = Paths.get(NtRpgPlugin.workingDir, "Runes.conf");

    @Inject
    private RWDao dao;

    @Inject
    private EffectService effectService;

    @Inject
    private Logger logger;

    @Inject
    private Game game;

    @Inject
    private GroupService groupService;

    private Map<String, RuneWord> runewords = new HashMap();
    private Map<String, Rune> runes = new HashMap<>();
    private List<ItemType> allowedRuneItemTypes = new ArrayList<>();


    @PostProcess(priority = 10000)
    public void load() {
        File p = file.toFile();
        if (!p.exists()) {
            try {
                p.createNewFile();
                Files.write(p.toPath(), "Runes:{},\nRuneWords:{}".getBytes());
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (Rune rune : dao.getAllRunes(p)) {
            runes.put(rune.getName().toLowerCase(), rune);
        }
        for (RuneWordTemplate runeWord : dao.getAllRws(p)) {
            runewords.put(runeWord.getName().toLowerCase(), getRuneword(runeWord));
        }
        for (String s : PluginConfig.ALLOWED_RUNES_ITEMTYPES) {
            Optional<ItemType> type = Sponge.getGame().getRegistry().getType(ItemType.class, s);
            if (type.isPresent()) {
                allowedRuneItemTypes.add(type.get());
            }
        }
    }

    protected RuneWord getRuneword(RuneWordTemplate template) {
        template.getRunes().stream().filter(Utils.not(runes::containsKey)).forEach(e -> logger.warn("Runeword " + template + " is not possible to create, due to missing Rune:" + e));
        template.getEffects().keySet().stream().filter(Utils.not(effectService::isGlobalEffect)).forEach(e -> logger.warn("Runeword " + template + " defined non existing global effect:" + e));
        RuneWord rw = new RuneWord();
        rw.setName(template.getName());
        rw.setRunes(template.getRunes().stream()./*filter(this::existsRune).*/map(this::getRune).collect(Collectors.toList()));
        rw.setMinLevel(template.getMinLevel());
        Set<ItemType> types = new HashSet<>();
        rw.setAllowedItems(types);
        rw.setEffects(template.getEffects().entrySet().stream()
                .filter(l -> effectService.isGlobalEffect(l.getKey()))
                .map(a -> new Pair<>(effectService.getGlobalEffect(a.getKey()), a.getValue()))
                .collect(HashMap::new, (map, a) -> map.put(a.key, a.value), HashMap::putAll)); //wtf i just did?
        rw.setRestrictedClasses(template.getRestrictedClasses().stream()
                .filter(groupService::existsClass)
                .map(groupService::getNClass).collect(Collectors.toSet()));
        rw.setAllowedClasses(template.getAllowedClasses().stream()
                .filter(groupService::existsClass)
                .map(groupService::getNClass).collect(Collectors.toSet()));
        return rw;
    }

    public RuneWord getRuneword(List<Text> lore) {
        if (lore.size() <= 1) {
            return null;
        }
        Text t = lore.get(0);
        String s = t.toPlain();
        if (!s.startsWith(PluginConfig.RW_LORE_COLOR) || s.length() < 3)
            return null;
        return runewords.get(s.substring(2));
    }

    public RuneWord getRuneword(String name) {
        return runewords.get(name);
    }

    public List<Text> addRune(List<Text> lore, Rune rune) {
        if (!hasEmptySocket(lore))
            return lore;
        String s = lore.get(1).toPlain();
        s = s.replaceFirst(socket.pattern(), rune.getName() + " ");
        lore.set(1, Text.of(s));
        return lore;
    }


    public int getSocketCount(List<Text> lore) {
        if (lore.size() < 2)
            return 0;
        String s = lore.get(1).toPlain();
        int c = 0;
        Matcher matcher = socket.matcher(s);
        while (matcher.find())
            c++;
        return c;
    }

    public boolean hasEmptySocket(List<Text> lore) {
        if (lore.size() < 2)
            return false;
        String s = lore.get(1).toPlain();
        Matcher matcher = socket.matcher(s);
        return matcher.find();
    }

    public boolean existsRune(String rune) {
        return runes.containsKey(rune.toLowerCase());
    }

    public boolean existsRuneword(String rw) {
        return runewords.containsKey(rw.toLowerCase());
    }

    public Rune getRune(String rune) {
        return runes.get(rune.toLowerCase());
    }

    public Map<String, Rune> getRunes() {
        return runes;
    }

    public ItemStack toItemStack(Rune r) {
        XORShiftRnd rnd = new XORShiftRnd();
        int i = rnd.nextInt(allowedRuneItemTypes.size());
        ItemType type = allowedRuneItemTypes.get(i);
        ItemStack.Builder builder = ItemStack.builder();
        ItemStack stack = builder.quantity(1).itemType(type).build();
        stack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, r.getName()));
        stack.offer(Keys.ITEM_LORE, Arrays.asList(Text.of(InventoryService.LORE_FIRSTLINE, Localization.RUNE), Text.of(TextColors.DARK_GRAY, Localization.RUNE_FOOTER)));
        return stack;
    }


    public ItemStack createSockets(ItemStack itemStack, int i) {
        List<Text> arr = new ArrayList<>();
        arr.clear();
        arr.add(0, Text.of(InventoryService.LORE_FIRSTLINE, Localization.SOCKET));
        String s = "";
        while (i > 0) {
            s += "{@}";
            i--;
        }
        arr.add(1, Text.of(InventoryService.SOCKET_COLOR, s));
        itemStack.offer(Keys.ITEM_LORE, arr);
        return itemStack;
    }

    public ItemStack insertRune(ItemStack itemStack, String currentRune) {
        Optional<List<Text>> texts = itemStack.get(Keys.ITEM_LORE);
        List<Text> t = texts.get();
        Text text = t.get(1);
        String s = text.toPlain();
        String s1 = s.replaceFirst("\\{@\\}", currentRune);
        t.set(1, Text.of(TextColors.RED, s1));
        itemStack.offer(Keys.ITEM_LORE, t);
        return itemStack;
    }

    public ItemStack findRuneword(ItemStack i) {
        Optional<List<Text>> texts = i.get(Keys.ITEM_LORE);
        if (texts.isPresent()) {
            List<Text> t = texts.get();
            Text text = t.get(1);
            String s = text.toPlain();
            for (RuneWord rw : runewords.values()) {
                String collect = rw.getRunes().stream().map(Rune::getName).collect(Collectors.joining());
                if (s.equalsIgnoreCase(collect)) {
                    if (rw.getAllowedItems().contains(i.getItem())) {
                        if (rw.getAllowedItems().isEmpty()) {
                            return reBuildRuneword(i, rw);
                        } else {
                            if (rw.getAllowedItems().contains(i.getItem())) {
                                return reBuildRuneword(i, rw);
                            }
                        }
                    }
                }
            }
        }
        return i;
    }

    public ItemStack reBuildRuneword(ItemStack i, RuneWord rw) {
        if (rw == null) {
            if (PluginConfig.AUTOREMOVE_NONEXISTING_RUNEWORDS) {
                i.offer(Keys.DISPLAY_NAME, Text.of(i.getItem().getName()));
                i.offer(Keys.ITEM_LORE, Collections.<Text>emptyList());
            }
            return i;
        }

        i.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, rw.getName()));
        List<Text> l = new ArrayList<>();
        l.add(Text.of(TextColors.BLUE, Localization.RUNEWORD));
        l.add(Text.of(TextColors.RED, i.get(Keys.ITEM_LORE).get().get(1).toPlain()));

        Map<IGlobalEffect, Float> effects = rw.getEffects();
        if (!rw.getRestrictedClasses().isEmpty()) {
            l.add(Text.of(TextColors.DARK_RED, Localization.RESTRICTED_CLASSES));
            l.add(Text.of(TextColors.GRAY, "- " + rw.getRestrictedClasses().stream().map(PlayerGroup::getName).collect(Collectors.joining(", "))));
        }
        if (!rw.getRestrictedClasses().isEmpty()) {
            l.add(Text.of(TextColors.RED, Localization.ALLOWED_CLASSES));
            l.add(Text.of(TextColors.GRAY, "- " + rw.getAllowedClasses().stream().map(PlayerGroup::getName).collect(Collectors.joining(", "))));
        }
        if (rw.getMinLevel() > 1) {
            l.add(Text.of(TextColors.GRAY, Localization.MIN_LEVEL + ": " + rw.getMinLevel()));
        }

        for (Map.Entry<IGlobalEffect, Float> entry : effects.entrySet()) {
            IGlobalEffect key = entry.getKey();
            Float value = entry.getValue();
            l.add(Text.of(TextColors.AQUA, key.getName() + ": " + value));
        }
        RebuildRunewordEvent event = new RebuildRunewordEvent(rw,l,i);
        Sponge.getEventManager().post(event);
        i = event.getItemStack();
        i.offer(Keys.ITEM_LORE, event.getLore());
        return i;
    }


    public boolean canBeInserted(RuneWordTemplate template, ItemStack itemStack) {
        List<String> allowedItems = template.getAllowedItems();
        ItemType i = itemStack.getItem();
        if (allowedItems.contains(itemStack.toString())) {
            return true;
        }
        for (String allowedItem : allowedItems) {
            if (allowedItem.startsWith("any")) {
                String[] split = allowedItem.split(" ");
                if (split.length == 1) {
                    return true;
                }
                if (split.length == 2) {
                    return ItemStackUtils.checkType(i,split[1]);
                }

            }
        }

        return false;
    }

    public boolean canUse(RuneWord rw, IActiveCharacter character) {
        if (character.isStub())
            return false;
        if (rw.getMinLevel() > character.getPrimaryClass().getLevel()) {
            return false;
        }
        if (!rw.getRestrictedClasses().isEmpty()) {
            for (ConfigClass configClass : rw.getRestrictedClasses()) {
                for (ExtendedNClass nClass : character.getClasses()) {
                    if (nClass.getConfigClass() == configClass) {
                        return false;
                    }
                }
            }
        }
        if (!rw.getAllowedClasses().isEmpty()) {
            boolean k = false;
            for (ConfigClass configClass : rw.getAllowedClasses()) {
                for (ExtendedNClass nClass : character.getClasses()) {
                    if (nClass.getConfigClass() == configClass) {
                        k = true;
                        break;
                    }
                }
            }
            if (!k) {
                return false;
            }
        }
        return true;
    }
}
