package cz.neumimto.rpg.players.groups;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DependencyGraph {

    private ClassDefinition classDefinition;

    public DependencyGraph(ClassDefinition classDefinition) {
        this.classDefinition = classDefinition;
    }

    private Set<ClassDefinition> softDepends = new HashSet<>();

    private Set<ClassDefinition> hardDepends = new HashSet<>();

    private Set<ClassDefinition> conflicts = new HashSet<>();

    public Set<ClassDefinition> getSoftDepends() {
        return softDepends;
    }

    public Set<ClassDefinition> getHardDepends() {
        return hardDepends;
    }

    public Set<ClassDefinition> getConflicts() {
        return conflicts;
    }

    public boolean isValidFor(Collection<ClassDefinition> context) {
        boolean found = softDepends.isEmpty();
        for (ClassDefinition softDepend : softDepends) {
            if (context.contains(softDepend)) {
                found = true;
                break;
            }
        }
        if (!found) {
            return false;
        }
        found = context.containsAll(hardDepends);
        if (!found) {
            return false;
        }

        for (ClassDefinition conflict : conflicts) {
            if (context.contains(conflict)) {
                return false;
            }
        }

        return true;
    }

    public ClassDefinition getOwner() {
        return classDefinition;
    }
}
