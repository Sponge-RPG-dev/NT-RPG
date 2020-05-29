package cz.neumimto.rpg.api.configuration.adapters;

import com.electronwill.nightconfig.core.Config;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.DependencyGraph;

import java.util.*;
import java.util.stream.Collectors;

public class ClassDependencyGraphAdapter {

    public static DependencyGraph load(Config value, ClassDefinition classDef, Set<ClassDefinition> set) {
        List<String> soft = readOrEmpty(value,"Soft");
        List<String> hard = readOrEmpty(value,"Hard");
        List<String> conflicts = readOrEmpty(value,"Conflicts");

        DependencyGraph graph = classDef.getClassDependencyGraph();

        graph.getSoftDepends().addAll(toClass(soft, set));
        graph.getHardDepends().addAll(toClass(hard, set));
        graph.getConflicts().addAll(toClass(conflicts, set));
        return graph;
    }

    private static List<String> readOrEmpty(Config config, String node) {
        return config.contains(node) ? config.get(node) : Collections.emptyList();
    }

    private static Collection<? extends ClassDefinition> toClass(List<String> list, Set<ClassDefinition> all) {
        return all.stream().filter(c -> list.contains(c.getName())).collect(Collectors.toSet());
    }

    public static void serialize(ClassDefinition classDef, Config value) {
        Map<String, Set<String>> map = new HashMap<>();
        value.set("Soft", classDef.getClassDependencyGraph().getSoftDepends().stream().map(ClassDefinition::getName).collect(Collectors.toSet()));
        value.set("Conflicts", classDef.getClassDependencyGraph().getConflicts().stream().map(ClassDefinition::getName).collect(Collectors.toSet()));
        value.set("Hard", classDef.getClassDependencyGraph().getHardDepends().stream().map(ClassDefinition::getName).collect(Collectors.toSet()));
    }

}
