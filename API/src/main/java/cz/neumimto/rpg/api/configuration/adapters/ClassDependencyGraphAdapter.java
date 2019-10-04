package cz.neumimto.rpg.api.configuration.adapters;

import com.electronwill.nightconfig.core.Config;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.DependencyGraph;

import java.util.*;
import java.util.stream.Collectors;

public class ClassDependencyGraphAdapter {

    public DependencyGraph deserialize(Config value, ClassDefinition classDef) {
        List<String> soft = value.get("Soft");
        List<String> hard = value.get("Hard");
        List<String> conflicts = value.get("Conflicts");

        DependencyGraph graph = classDef.getClassDependencyGraph();

        graph.getSoftDepends().addAll(toClass(soft));
        graph.getHardDepends().addAll(toClass(hard));
        graph.getConflicts().addAll(toClass(conflicts));
        return graph;
    }

    private Collection<? extends ClassDefinition> toClass(List<String> list) {
        ClassService classService = Rpg.get().getClassService();
        return list.stream().map(classService::getClassDefinitionByName).collect(Collectors.toSet());
    }

    public void serialize(ClassDefinition classDef, Config value) {
        Map<String, Set<String>> map = new HashMap<>();
        value.set("Soft", classDef.getClassDependencyGraph().getSoftDepends().stream().map(ClassDefinition::getName).collect(Collectors.toSet()));
        value.set("Conflicts", classDef.getClassDependencyGraph().getConflicts().stream().map(ClassDefinition::getName).collect(Collectors.toSet()));
        value.set("Hard", classDef.getClassDependencyGraph().getHardDepends().stream().map(ClassDefinition::getName).collect(Collectors.toSet()));
    }
}
