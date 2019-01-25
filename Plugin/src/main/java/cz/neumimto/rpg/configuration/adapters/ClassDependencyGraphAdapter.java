package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.players.groups.ClassDependencyGraph;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ClassDependencyGraphAdapter implements TypeSerializer<ClassDependencyGraph> {
    @Override
    public ClassDependencyGraph deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        ConfigurationNode soft = value.getNode("Soft");
        ConfigurationNode hard = value.getNode("Hard");
        ConfigurationNode conflicts = value.getNode("Conflicts");
        ClassDependencyGraph cdg = new ClassDependencyGraph();
        cdg.getSoftDepends().addAll(toClass(soft.getList(TypeToken.of(String.class))));
        cdg.getHardDepends().addAll(toClass(hard.getList(TypeToken.of(String.class))));
        cdg.getConflicts().addAll(toClass(conflicts.getList(TypeToken.of(String.class))));
        return cdg;
    }

    private Collection<? extends ClassDefinition> toClass(List<String> list) {
        return list.stream().map(NtRpgPlugin.GlobalScope.groupService::getClassDefinitionByName).collect(Collectors.toSet());
    }

    @Override
    public void serialize(TypeToken<?> type, ClassDependencyGraph obj, ConfigurationNode value) throws ObjectMappingException {

    }
}
