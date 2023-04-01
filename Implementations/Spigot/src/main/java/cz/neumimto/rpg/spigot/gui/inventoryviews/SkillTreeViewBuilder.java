package cz.neumimto.rpg.spigot.gui.inventoryviews;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.common.skills.tree.SkillTree;
import cz.neumimto.rpg.spigot.Resourcepack;
import cz.neumimto.rpg.spigot.bridges.DatapackManager;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import cz.neumimto.rpg.spigot.gui.SpigotGuiHelper;
import cz.neumimto.rpg.spigot.skills.SpigotSkillTreeInterfaceModel;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Singleton
@AutoService(ConfigurableInventoryGui.class)
public class SkillTreeViewBuilder extends ConfigurableInventoryGui {

    public static SkillTreeViewBuilder instance;

    private static Resourcepack.RPItem UP;
    private static Resourcepack.RPItem DOWN;
    private static Resourcepack.RPItem LEFT;
    private static Resourcepack.RPItem RIGHT;

    private static Map<Character, SpigotSkillTreeInterfaceModel> guiModelByCharacter;

    private static Map<Short, SpigotSkillTreeInterfaceModel> guiModelById;

    private static Set<Runnable> set = new HashSet<>();

    public SkillTreeViewBuilder() {
        super("Skilltree.conf");
        instance = this;
    }

    public static void loadLater(Runnable r) {
        set.add(r);
    }

    @Override
    public void clearCache() {
        loadGui();
    }

    @Override
    public ChestGui loadGui() {
        guiModelByCharacter = new HashMap<>();
        guiModelById = new HashMap<>();

        Path path = getPath();

        reloadGuiConfig(path);

        int i = 0;

        for (GuiConfig.MaskConfig mask : guiConfig.mask) {
            if (mask.C.equalsIgnoreCase("u")) {
                ItemStack byId = DatapackManager.instance.findById(mask.id, mask.model == null ? 0 : mask.model);
                ItemMeta itemMeta = byId.getItemMeta();
                UP = new Resourcepack.RPItem(byId.getType(), itemMeta.hasCustomModelData() ? itemMeta.getCustomModelData() : 0);
            } else if (mask.C.equalsIgnoreCase("d")) {
                ItemStack byId = DatapackManager.instance.findById(mask.id, mask.model == null ? 0 : mask.model);
                ItemMeta itemMeta = byId.getItemMeta();
                DOWN = new Resourcepack.RPItem(byId.getType(), itemMeta.hasCustomModelData() ? itemMeta.getCustomModelData() : 0);
            } else if (mask.C.equalsIgnoreCase("r")) {
                ItemStack byId = DatapackManager.instance.findById(mask.id, mask.model == null ? 0 : mask.model);
                ItemMeta itemMeta = byId.getItemMeta();
                RIGHT = new Resourcepack.RPItem(byId.getType(), itemMeta.hasCustomModelData() ? itemMeta.getCustomModelData() : 0);
            } else if (mask.C.equalsIgnoreCase("l")) {
                ItemStack byId = DatapackManager.instance.findById(mask.id, mask.model == null ? 0 : mask.model);
                ItemMeta itemMeta = byId.getItemMeta();
                LEFT = new Resourcepack.RPItem(byId.getType(), itemMeta.hasCustomModelData() ? itemMeta.getCustomModelData() : 0);
            } else {
                short k = (short) (Short.MAX_VALUE - i);

                ItemStack byId = DatapackManager.instance.findById(mask.id, mask.model == null ? 0 : mask.model);
                ItemMeta itemMeta = byId.getItemMeta();
                int cmodel = itemMeta.hasCustomModelData() ? itemMeta.getCustomModelData() : 0;
                SpigotSkillTreeInterfaceModel model = new SpigotSkillTreeInterfaceModel(cmodel, byId.getType(), k);

                guiModelById.put(k, model);
                guiModelByCharacter.put(mask.C.charAt(0), model);
                i++;
            }
        }

        for (Runnable runnable : set) {
            runnable.run();
        }
        set.clear();
        return null;
    }

    @Override
    protected Component getTitle(CommandSender commandSender, GuiConfig guiConfig, String param) {
        return getPrefix(guiConfig).append(Component.text(param));
    }

    public static Inventory createSkillTreeView(SpigotCharacter character, SkillTree skillTree) {
        Player player = character.getPlayer();
        Inventory inventory = Bukkit.createInventory(player, 54, instance.getTitle(player, instance.guiConfig, skillTree.getId()));
        fillSkillTreeViewInterface(inventory);
        return inventory;
    }

    private static void fillSkillTreeViewInterface(Inventory i) {
        i.setItem(26, SpigotGuiHelper.button(UP, "Up", "skilltree north"));
        i.setItem(35, SpigotGuiHelper.button(DOWN, "Down", "skilltree south"));
        i.setItem(44, SpigotGuiHelper.button(RIGHT, "Right", "skilltree west"));
        i.setItem(53, SpigotGuiHelper.button(LEFT, "Left", "skilltree east"));
    }

    public static SpigotSkillTreeInterfaceModel getGuiModelById(Short k) {
        return guiModelById.get(k);
    }

    public static ISkillTreeInterfaceModel getGuiModelByCharacter(char c) {
        return guiModelByCharacter.get(c);
    }
}
