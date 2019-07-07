package cz.neumimto.rpg.api.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.config.blackjack.and.hookers.annotations.EnableSetterInjection;
import cz.neumimto.config.blackjack.and.hookers.annotations.Setter;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.DependencyGraph;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@EnableSetterInjection
public class ClassDependencyGraphAdapter implements TypeSerializer<DependencyGraph> {

    private ClassDefinition classDef;

    @Setter
    public void setClassDefinition(ClassDefinition def) {
        this.classDef = def;
    }

    @Override
    public DependencyGraph deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        ConfigurationNode soft = value.getNode("Soft");
        ConfigurationNode hard = value.getNode("Hard");
        ConfigurationNode conflicts = value.getNode("Conflicts");

        DependencyGraph graph = classDef.getClassDependencyGraph();

        graph.getSoftDepends().addAll(toClass(soft.getList(TypeToken.of(String.class))));
        graph.getHardDepends().addAll(toClass(hard.getList(TypeToken.of(String.class))));
        graph.getConflicts().addAll(toClass(conflicts.getList(TypeToken.of(String.class))));
        return graph;
    }

    private Collection<? extends ClassDefinition> toClass(List<String> list) {
        ClassService classService = Rpg.get().getClassService();
        return list.stream().map(classService::getClassDefinitionByName).collect(Collectors.toSet());
    }

    @Override
    public void serialize(TypeToken<?> type, DependencyGraph obj, ConfigurationNode value) throws ObjectMappingException {
    /*    Map<String, Set<String>> map = new HashMap<>();
        map.put("Soft", classDef.getSoftDepends().stream().map(ClassDefinition::getName).collect(Collectors.toSet()));
        map.put("Conflicts", classDef.getConflicts().stream().map(ClassDefinition::getName).collect(Collectors.toSet()));
        map.put("Hard", classDef.getSoftDepends().stream().map(ClassDefinition::getName).collect(Collectors.toSet()));
        value.setValue(map);
        */
    }
}
