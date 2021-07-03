package cz.neumimto.rpg.spigot.gui.inventoryviews;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.hocon.HoconParser;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.spigot.gui.elements.GuiCommand;
import cz.neumimto.rpg.spigot.gui.elements.Icon;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

public abstract class ConfigurableInventoryGui extends GuiHelper {

    @Inject
    private AssetService assetService;

    private final String fileName;

    public ConfigurableInventoryGui(String fileName) {
        this.fileName = fileName;
    }

    public ChestGui loadGui() {
        return loadGui(null, null);
    }

    public ChestGui loadGui(Player commandSender) {
        return loadGui(commandSender, null);
    }

    public ChestGui loadGui(Player commandSender, String param) {
        Path path = getPath();

        if (!Files.exists(path)) {
            String assetAsString = assetService.getAssetAsString("gui/" + fileName);

            HoconParser hoconParser = new HoconParser();
            try (StringReader stringReader = new StringReader(assetAsString)) {
                CommentedConfig parsed = hoconParser.parse(stringReader);
                GuiConfig guiConfig = new ObjectConverter().toObject(parsed, GuiConfig::new);
                return createPane(guiConfig, commandSender, getPaneData(commandSender, param), param);
            }
        } else {
            try (FileConfig fileConfig = FileConfig.of(path)) {
                fileConfig.load();
                GuiConfig guiConfig = new ObjectConverter().toObject(fileConfig, GuiConfig::new);
                return createPane(guiConfig, commandSender, getPaneData(commandSender, param), param);
            }
        }
    }

    protected String getTitle(CommandSender commandSender, GuiConfig guiConfig, String param) {
        if (guiConfig.translationkey != null) {
            return t(guiConfig.translationkey);
        } else {
            return guiConfig.name;
        }
    }

    protected ChestGui createPane(GuiConfig guiConfig, CommandSender commandSender, Map<String, List<GuiCommand>> data, String param) {
        String title = getTitle(commandSender, guiConfig, param);
        ChestGui chestGui = new ChestGui(6, title);



        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        int alphabetidx = 0;
        List<String> actualContent = new ArrayList<>();

        Map<Character, GuiCommand> mask = new HashMap<>();

        Map<String, Iterator<GuiCommand>> dataIt = toIterator(data);
        for (String row : guiConfig.inventory) {
            StringBuilder stringBuilder = new StringBuilder();
            for (char slot : row.toCharArray()) {

                a: for (Map.Entry<String, Iterator<GuiCommand>> content : dataIt.entrySet()) {
                    String replaceKey = content.getKey(); //classType
                    Iterator<GuiCommand> value = content.getValue();
                    for (GuiConfig.MaskConfig maskConfig : guiConfig.mask) {
                        if (maskConfig.C.toCharArray()[0] == slot && replaceKey.equalsIgnoreCase(maskConfig.supplier) && value.hasNext()) {
                            GuiCommand next = value.next();
                            char c = alphabet[alphabetidx];
                            mask.put(c, next);
                            alphabetidx++;
                            slot = c;
                            break a;
                        }
                    }
                }
                stringBuilder.append(slot);
            }

            actualContent.add(stringBuilder.toString());
        }


        PatternPane pane = new PatternPane(9, 6, new Pattern(
                actualContent.toArray(new String[0])
        ));

        for (Map.Entry<Character, GuiCommand> e : mask.entrySet()) {
            pane.bindItem(e.getKey(), e.getValue());
        }

        for (GuiConfig.MaskConfig maskConfig : guiConfig.mask) {
            GuiConfig.OnClick onClick = maskConfig.onClick;
            char maskKez = maskConfig.C.toCharArray()[0];
            if (onClick != null && onClick.command != null) {
                if (commandSender == null) {
                    pane.bindItem(maskKez, new GuiCommand(i(maskConfig), onClick.command.replaceAll("%title%", ChatColor.stripColor(title))));
                } else {
                    pane.bindItem(maskKez, new GuiCommand(i(maskConfig), onClick.command.replaceAll("%title%", ChatColor.stripColor(title)), commandSender));
                }
            } else {
                pane.bindItem(maskKez, new Icon(i(maskConfig)));
            }
        }
        chestGui.addPane(pane);
        return chestGui;
    }

    public void initialize() {
    }

    public Map<String, List<GuiCommand>> getPaneData(CommandSender commandSender, String param) {
        return getPaneData(commandSender);
    }

    public Map<String, List<GuiCommand>> getPaneData(CommandSender commandSender) {
        return Collections.emptyMap();
    }

    private Map<String, Iterator<GuiCommand>> toIterator(Map<String, List<GuiCommand>> b) {
        Map<String, Iterator<GuiCommand>> a = new HashMap<>();
        for (Map.Entry<String, List<GuiCommand>> c : b.entrySet()) {
            a.put(c.getKey(), c.getValue().iterator());
        }
        return a;
    }

    public void install() {
        Path path = getPath();
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assetService.copyToFile("gui/" + fileName, getPath());
    }

    @NotNull
    private Path getPath() {
        return Paths.get(Rpg.get().getWorkingDirectory(), "gui/" + fileName);
    }

    public void clearCache() {

    }

    public void clearCache(UUID uuid) {

    }

    public String getFileName() {
        return fileName;
    }
}
