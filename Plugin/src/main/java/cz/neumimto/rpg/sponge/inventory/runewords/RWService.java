package cz.neumimto.rpg.sponge.inventory.runewords;

import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.effects.EffectParams;
import cz.neumimto.rpg.api.effects.IEffectService;
import cz.neumimto.rpg.common.inventory.runewords.RuneWord;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.utils.Pair;
import cz.neumimto.rpg.api.utils.rng.XORShiftRnd;
import cz.neumimto.rpg.common.inventory.crafting.runewords.*;
import cz.neumimto.rpg.common.inventory.sockets.SocketType;
import cz.neumimto.rpg.common.inventory.sockets.SocketTypes;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.events.RebuildRunewordEvent;
import cz.neumimto.rpg.sponge.inventory.ItemUpgradeTransactionResult;
import cz.neumimto.rpg.sponge.inventory.SpongeInventoryService;
import cz.neumimto.rpg.sponge.inventory.SpongeItemService;
import cz.neumimto.rpg.sponge.inventory.data.DataConstants;
import cz.neumimto.rpg.sponge.inventory.data.NKeys;
import cz.neumimto.rpg.sponge.inventory.data.manipulators.ItemSocketsData;
import cz.neumimto.rpg.sponge.inventory.data.manipulators.ItemStackUpgradeData;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.api.logging.Log.warn;
import static cz.neumimto.rpg.sponge.NtRpgPlugin.pluginConfig;

/**
 * Created by NeumimTo on 29.10.2015.
 */
@Singleton
public class RWService {

    private final Path file = Paths.get(NtRpgPlugin.workingDir, "Runes.conf");

    @Inject
    private RWDao dao;

    @Inject
    private IEffectService effectService;

    @Inject
    private SpongeItemService itemService;


    @Inject
    private Game game;

    @Inject
    private SpongeInventoryService spongeInventoryService;

    @Inject
    private ClassService classService;

    private Map<String, RuneWord> runewords = new HashMap<>();
    private Map<String, RuneWord> combinations = new HashMap<>();
    private Map<String, Rune> runes = new HashMap<>();
    private List<ItemType> allowedRuneItemTypes = new ArrayList<>();

    public void load() {
        File p = file.toFile();
        if (!p.exists()) {
            try {
                p.createNewFile();
                Files.write(p.toPath(), "Runes:[],\nRuneWords:[]".getBytes());
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (Rune rune : dao.getAllRunes(p)) {
            runes.put(rune.getName(), rune);
        }
        Set<RuneWordTemplate> allRws = dao.getAllRws(p);
        for (RuneWordTemplate runeWord : allRws) {
            registerRuneword(getRuneword(runeWord));
        }
        for (String s : pluginConfig.ALLOWED_RUNES_ITEMTYPES) {
            Optional<ItemType> type = Sponge.getGame().getRegistry().getType(ItemType.class, s);
            type.ifPresent(itemType -> allowedRuneItemTypes.add(itemType));
        }
    }

    protected RuneWord getRuneword(RuneWordTemplate template) {
        template.getEffects().keySet().stream().filter(Utils.not(effectService::isGlobalEffect))
                .forEach(e -> warn("Runeword " + template + " defined non existing global effect:" + e));
        RuneWord rw = new RuneWord();
        rw.setName(template.getName());
        rw.setRunes(template.getRunes()
                .stream()/*filter(this::existsRune).*/
                .map(this::getRune)
                .collect(Collectors.toList()));
        rw.setMinLevel(template.getMinLevel());

        rw.setAllowedItems(new HashSet<>(template.getAllowedItems()));

        rw.setEffects(template.getEffects().entrySet().stream()
                .filter(l -> effectService.isGlobalEffect(l.getKey()))
                .map(a -> new Pair<>(effectService.getGlobalEffect(a.getKey()), a.getValue()))
                .collect(HashMap::new, (map, a) -> map.put(a.key, a.value), HashMap::putAll));

        return rw;
    }

    public RuneWord getRuneword(String name) {
        return runewords.get(name.toLowerCase());
    }

    public int getSocketCount(ItemStack itemStack) {
        return itemStack.get(NKeys.ITEM_SOCKET_CONTAINER).orElse(Collections.emptyList()).size();
    }

    public boolean hasEmptySocket(ItemStack itemStack) {
        List<Text> texts = itemStack.get(NKeys.ITEM_SOCKET_CONTAINER_CONTENT).orElse(Collections.emptyList());
        for (Text text : texts) {
            if (DataConstants.EMPTY_SOCKET.equals(text)) {
                return true;
            }
        }
        return false;
    }

    public boolean existsRune(String rune) {
        return runes.containsKey(rune.toLowerCase());
    }

    public boolean existsRuneword(String rw) {
        return runewords.containsKey(rw.toLowerCase());
    }

    public ItemUpgrade getRune(String rune) {
        return runes.get(rune);
    }

    public Map<String, Rune> getRunes() {
        return runes;
    }

    public ItemStack createRune(SocketType r, String name) {
        ItemUpgrade rune = getRune(name);
        if (rune == null) {
            return ItemStack.empty();
        }
        XORShiftRnd rnd = new XORShiftRnd();
        int i = rnd.nextInt(allowedRuneItemTypes.size());
        ItemType itemType = allowedRuneItemTypes.get(i);
        ItemStack of = ItemStack.of(itemType, 1);
        of.offer(Keys.HIDE_ATTRIBUTES, true);
        of.offer(Keys.HIDE_MISCELLANEOUS, true);
        of.offer(new ItemStackUpgradeData(r));
        of.offer(Keys.DISPLAY_NAME, TextHelper.parse(rune.getName()));
        return of;
    }


    public ItemStack createSocket(ItemStack itemStack, SocketType type) {
        Optional<ItemSocketsData> opt = itemStack.getOrCreate(ItemSocketsData.class);
        ItemSocketsData socketsData = opt.get();

        socketsData.getSockets().add(type);
        socketsData.getContent().add(DataConstants.EMPTY_SOCKET);

        itemStack.offer(socketsData);

        spongeInventoryService.updateLore(itemStack);
        return itemStack;
    }

    public ItemUpgradeTransactionResult insertToNextEmptySocket(ItemStack itemStack, ItemStack rune) {
        Optional<ItemSocketsData> itemSocketsData = itemStack.get(ItemSocketsData.class);
        if (!itemSocketsData.isPresent()) {
            return ItemUpgradeTransactionResult.NO_EMPTY_SOCKET;
        }
        Optional<SocketType> runeSocketType = rune.get(NKeys.ITEMSTACK_UPGRADE);
        if (!runeSocketType.isPresent()) {
            return ItemUpgradeTransactionResult.NOT_VALID_UPGRADE;
        }
        ItemSocketsData socketsData = itemSocketsData.get();
        SocketType type = runeSocketType.get();

        List<SocketType> socketTypes = socketsData.getSockets();
        List<Text> content = socketsData.getContent();
        int iter = 0;

        for (SocketType socketType : socketTypes) {
            if ((type == SocketTypes.ANY || socketType == type || socketType == SocketTypes.ANY)
                    && content.get(iter).equals(DataConstants.EMPTY_SOCKET)) {
                //
                content.set(iter, rune.get(Keys.DISPLAY_NAME).get());
                itemStack.offer(NKeys.ITEM_SOCKET_CONTAINER_CONTENT, content);
                //
                Map<IGlobalEffect, EffectParams> itemEffects = itemService.getItemEffects(rune);
                for (Map.Entry<IGlobalEffect, EffectParams> entry : itemEffects.entrySet()) {
                    entry.getValue().put(DataConstants.ITEM_EFFECT_SOCKET_ID_REF, String.valueOf(iter));
                    spongeInventoryService.addEffectsToItemStack(itemStack, entry.getKey().getName(), entry.getValue());
                }
                spongeInventoryService.updateLore(itemStack);
                return ItemUpgradeTransactionResult.OK;
            }
            iter++;
        }
        return ItemUpgradeTransactionResult.NO_EMPTY_SOCKET;
    }

    /**
     * @param itemstack having {@link cz.neumimto.rpg.sponge.inventory.data.manipulators.ItemSocketsData}
     * @return rune combination or null if there is at least one jewel or gem in the socket container
     */
    public String getCurrentRuneCombination(ItemStack itemstack) {
        Optional<List<SocketType>> socketTypes = itemstack.get(NKeys.ITEM_SOCKET_CONTAINER);
        if (!socketTypes.isPresent()) {
            return null;
        }
        List<SocketType> sockets = socketTypes.get();

        List<Text> content = itemstack.get(NKeys.ITEM_SOCKET_CONTAINER_CONTENT).get();

        int id = 0;
        StringBuilder builder = new StringBuilder();

        for (SocketType socket : sockets) {
            if (socket.equals(SocketTypes.RUNE)) {
                builder.append(content.get(id).toPlain());
            } else if (socket.equals(SocketTypes.ANY)) {
                String s = content.get(id).toPlain();
                if (runes.containsKey(s)) {
                    builder.append(s);
                } else {
                    return null;
                }
            } else {
                return null;
            }
            id++;
        }

        return builder.toString();
    }

    public ItemStack findRuneword(ItemStack i) {
        String combination = getCurrentRuneCombination(i);
        if (combination == null) {
            return i;
        }
        RuneWord rw = combinations.get(combination);
        if (rw != null) {
            if (rw.getAllowedItems().contains(i.getType())) {
                if (rw.getAllowedItems().isEmpty()) {
                    return reBuildRuneword(i, rw);
                } else {
                    if (rw.getAllowedItems().contains(i.getType())) {
                        return reBuildRuneword(i, rw);
                    }
                }
            }
        }
        return i;
    }

    public ItemStack reBuildRuneword(ItemStack i, RuneWord rw) {
        if (rw == null) {
            if (pluginConfig.AUTOREMOVE_NONEXISTING_RUNEWORDS) {
                i.offer(Keys.DISPLAY_NAME, Text.of(i.getType().getName()));
                i.offer(Keys.ITEM_LORE, Collections.emptyList());
            }
            return i;
        }
        i.offer(Keys.HIDE_ATTRIBUTES, true);
        i.offer(Keys.HIDE_MISCELLANEOUS, true);
        spongeInventoryService.updateLore(i);
        RebuildRunewordEvent event = new RebuildRunewordEvent(rw, i);
        Sponge.getEventManager().post(event);
        i = event.getItemStack();
        return i;
    }


    public List<ItemType> getAllowedRuneItemTypes() {
        return allowedRuneItemTypes;
    }

    public RuneWord findByCombination(String runes) {
        return combinations.get(runes);
    }

    public void registerRuneword(RuneWord runeWord) {
        runewords.put(runeWord.getName(), runeWord);
        if (runeWord.getRunes() != null && !runeWord.getRunes().isEmpty()) {
            combinations.put(runeWord.getRunes().stream().map(ItemUpgrade::getName).collect(Collectors.joining("")), runeWord);
        }
    }

    public Map<String, RuneWord> getRunewords() {
        return runewords;
    }

    public boolean isRune(ItemStack rune) {
        Optional<ItemStackUpgradeData> upgradeData = rune.get(ItemStackUpgradeData.class);
        if (upgradeData.isPresent()) {
            //todo to data key
            Optional<Text> text = rune.get(Keys.DISPLAY_NAME);
            if (text.isPresent()) {
                return runes.containsKey(text.get().toPlain());
            }

        }
        return false;
    }

}
