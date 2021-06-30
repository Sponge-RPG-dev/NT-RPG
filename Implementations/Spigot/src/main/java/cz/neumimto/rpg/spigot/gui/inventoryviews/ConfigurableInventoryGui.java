package cz.neumimto.rpg.spigot.gui.inventoryviews;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.hocon.HoconParser;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.spigot.gui.elements.GuiCommand;
import cz.neumimto.rpg.spigot.gui.elements.Icon;
import cz.neumimto.rpg.spigot.gui.elements.MaskPane;
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

public abstract class ConfigurableInventoryGui extends GuiHelper {

    @Inject
    private AssetService assetService;

    private final String fileName;

    public ConfigurableInventoryGui(String fileName) {
        this.fileName = fileName;
    }

    public ChestGui loadGui() {
        return loadGui(null);
    }

    public ChestGui loadGui(Player commandSender) {
        Path path = getPath();

        if (!Files.exists(path)) {
            String assetAsString = assetService.getAssetAsString("gui/" + fileName);

            HoconParser hoconParser = new HoconParser();
            try (StringReader stringReader = new StringReader(assetAsString)){
                CommentedConfig parsed = hoconParser.parse(stringReader);
                GuiConfig guiConfig = new ObjectConverter().toObject(parsed, GuiConfig::new);
                return createPane(guiConfig, commandSender);
            }
        } else {
            try (FileConfig fileConfig = FileConfig.of(path)) {
                fileConfig.load();
                GuiConfig guiConfig = new ObjectConverter().toObject(fileConfig, GuiConfig::new);
                return createPane(guiConfig, commandSender);
            }
        }
    }

    protected ChestGui createPane(GuiConfig guiConfig, CommandSender commandSender) {
        ChestGui chestGui;
        if (guiConfig.translationkey != null) {
            chestGui = new ChestGui(6, t(guiConfig.translationkey));
        } else {
            chestGui = new ChestGui(6, guiConfig.name);
        }
        Map<String, List<GuiCommand>> data = getPaneData(commandSender);

        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        int alphabetidx = 0;
        List<String> actualContent = new ArrayList<>();
        Map<Character, GuiCommand> mask = new HashMap<>();

        for (Map.Entry<String, List<GuiCommand>> content : data.entrySet()) {
            String replaceKey = content.getKey(); //classType
            Iterator<GuiCommand> value = content.getValue().iterator();


            for (String row : guiConfig.inventory) {
                StringBuilder stringBuilder = new StringBuilder();
                for (char slot : row.toCharArray()) {

                    for (GuiConfig.MaskConfig maskConfig : guiConfig.mask) {
                        if (maskConfig.C.toCharArray()[0] == slot && replaceKey.equalsIgnoreCase(maskConfig.supplier) && value.hasNext()) {
                            GuiCommand next = value.next();
                            char c = alphabet[alphabetidx];
                            mask.put(c, next);
                            alphabetidx++;
                            slot = c;
                            break;
                        }
                    }
                    stringBuilder.append(slot);
                }
                actualContent.add(stringBuilder.toString());
            }
        }

        MaskPane maskPane = new MaskPane(0,0,
                new MaskPane.ItemMask(
                       actualContent.toArray(new String[0])
                ));

        for (Map.Entry<Character, GuiCommand> e : mask.entrySet()) {
            maskPane.bindItem(e.getKey(), e.getValue());
        }

        for (GuiConfig.MaskConfig maskConfig : guiConfig.mask) {
            GuiConfig.OnClick onClick = maskConfig.onClick;
            char maskKez = maskConfig.C.toCharArray()[0];
            if (onClick != null && onClick.command != null) {
                maskPane.bindItem(maskKez, new GuiCommand(i(maskConfig), onClick.command, commandSender));
            } else {
                maskPane.bindItem(maskKez, new Icon(i(maskConfig)));
            }
        }

        return chestGui;
    }

    public void initialize() {}

    public abstract Map<String, List<GuiCommand>> getPaneData(CommandSender commandSender);

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

}
