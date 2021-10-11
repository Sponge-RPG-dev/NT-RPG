package cz.neumimto.rpg.spigot.gui.inventoryviews;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.DependencyGraph;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.spigot.gui.elements.GuiCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
@AutoService(ConfigurableInventoryGui.class)
public class ClassDepedenciesGuiView extends ConfigurableInventoryGui {

    private static final Map<String, ChestGui> cache = new HashMap<>();

    private static ClassDepedenciesGuiView instance;

    @Inject
    private ClassService classService;

    @Inject
    private LocalizationService localizationService;

    public ClassDepedenciesGuiView() {
        super("ClassDependencies.conf");
        instance = this;
    }

    public static ChestGui get(String className) {
        if (cache.containsKey(className)) {
            return cache.get(className);
        }
        ChestGui gui = instance.loadGui(null, className);
        cache.put(className, gui);
        return gui;
    }

    @Override
    public void clearCache() {
        super.clearCache();
        cache.clear();
    }

    @Override
    protected String getTitle(CommandSender commandSender, GuiConfig guiConfig, String param) {
        ClassDefinition classDefinitionByName = classService.getClassDefinitionByName(param);
        return ChatColor.valueOf(classDefinitionByName.getPreferedColor()) + classDefinitionByName.getName() + " " + localizationService.translate(LocalizationKeys.CLASS_DEPENDENCIES);
    }

    @Override
    public Map<String, List<GuiCommand>> getPaneData(CommandSender commandSender, String className) {
        Map<String, List<GuiCommand>> map = new HashMap<>();
        List<GuiCommand> list = new ArrayList<>();

        ClassDefinition classDefinitionByName = classService.getClassDefinitionByName(className);
        DependencyGraph classDependencyGraph = classDefinitionByName.getClassDependencyGraph();
        if (classDependencyGraph != null) {
            map.put("HardDep", toList(classDependencyGraph.getHardDepends()));
            map.put("SoftDep", toList(classDependencyGraph.getSoftDepends()));
            map.put("ConflictingDep", toList(classDependencyGraph.getConflicts()));
        }

        map.put("Dependency", list);
        return map;
    }

    private List<GuiCommand> toList(Set<ClassDefinition> a) {
        return a.stream()
                .sorted((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()))
                .map(CharacterGuiView::classDefinitionToItemStack)
                .map(GuiCommand::new)
                .collect(Collectors.toList());
    }
}

