package cz.neumimto.rpg.spigot.gui.inventoryviews;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.google.auto.service.AutoService;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.gui.SpigotGuiHelper;
import cz.neumimto.rpg.spigot.gui.elements.GuiCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;


@Singleton
@AutoService(ConfigurableInventoryGui.class)
public class CharacterGuiView extends ConfigurableInventoryGui {

    private static final Map<UUID, ChestGui> cached = new HashMap<>();
    private static CharacterGuiView instance;

    @Inject
    private SpigotCharacterService characterService;

    public CharacterGuiView() {
        super("Character.conf");
        instance = this;
    }

    @Override
    public void clearCache() {
        super.clearCache();
        cached.clear();
    }

    @Override
    public void clearCache(UUID uuid) {
        cached.remove(uuid);
    }

    public static ChestGui get(Player player) {
        if (cached.containsKey(player.getUniqueId())) {
            return cached.get(player.getUniqueId());
        } else {
            ChestGui chestGui = instance.loadGui(player);
            cached.put(player.getUniqueId(), chestGui);
            return chestGui;
        }
    }

    @Override
    protected String getTitle(CommandSender commandSender, GuiConfig guiConfig, String param) {
        return characterService.getCharacter((Player) commandSender).getName();
    }

    @Override
    public Map<String, List<GuiCommand>> getPaneData(CommandSender commandSender) {
        Map<String, List<GuiCommand>> map = new HashMap<>();
        List<GuiCommand> list = new ArrayList<>();

        ISpigotCharacter character = characterService.getCharacter(((Player) commandSender).getUniqueId());

        for (PlayerClassData classData : character.getClasses().values()) {
            ClassDefinition classDefinition = classData.getClassDefinition();

            GuiCommand guiCommand = new GuiCommand(classDefinitionToItemStack(classDefinition),
                    "ninfo class " + classDefinition.getName(),
                    commandSender);
            list.add(guiCommand);
        }

        map.put("Class", list);
        return map;
    }

    public static ItemStack classDefinitionToItemStack(ClassDefinition a) {
        String sItemType = a.getItemType();
        Material material = Material.matchMaterial(sItemType);
        ItemStack itemStack = i(material, a.getItemModel());

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(SpigotGuiHelper.itemLoreFactory.toLore(a));

        if (a.getItemModel() != null) {
            itemMeta.setCustomModelData(a.getItemModel());
        }
        itemMeta.addItemFlags(ItemFlag.values());
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
