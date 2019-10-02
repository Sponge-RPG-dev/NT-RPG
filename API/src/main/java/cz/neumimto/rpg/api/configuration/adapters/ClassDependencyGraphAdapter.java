package cz.neumimto.rpg.api.configuration.adapters;

import com.electronwill.nightconfig.core.Config;
import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.DependencyGraph;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ClassDependencyGraphAdapter {
    public DependencyGraph deserialize(Config value, ClassDefinition classDef)  {
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

//    @Override
    public void serialize(TypeToken<?> type, DependencyGraph obj, ConfigurationNode value) throws ObjectMappingException {
    /*    Map<String, Set<String>> map = new HashMap<>();
        map.put("Soft", classDef.getSoftDepends().stream().map(ClassDefinition::getName).collect(Collectors.toSet()));
        map.put("Conflicts", classDef.getConflicts().stream().map(ClassDefinition::getName).collect(Collectors.toSet()));
        map.put("Hard", classDef.getSoftDepends().stream().map(ClassDefinition::getName).collect(Collectors.toSet()));
        value.setValue(map);
        */
    }
}
