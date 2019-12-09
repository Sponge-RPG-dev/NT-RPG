package cz.neumimto.rpg.api.gui;

import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.utils.Pair;

import java.util.function.Supplier;

public abstract class SkillTreeViewModel {

    private InteractiveMode interactiveMode;
    private Pair<Integer, Integer> location;
    private boolean current = true;
    private SkillTree skillTree;
    private ClassDefinition viewedClass;

    public SkillTreeViewModel() {
        interactiveMode = InteractiveMode.DETAILED;
        location = new Pair<>(0, 0);
    }


    public SkillTree getSkillTree() {
        return skillTree;
    }

    public void setSkillTree(SkillTree skillTree) {
        this.skillTree = skillTree;
    }

    public InteractiveMode getInteractiveMode() {
        return interactiveMode;
    }

    public void setInteractiveMode(InteractiveMode interactiveMode) {
        this.interactiveMode = interactiveMode;
    }

    public Pair<Integer, Integer> getLocation() {
        return location;
    }

    public void setLocation(Pair<Integer, Integer> location) {
        this.location = location;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public void setViewedClass(ClassDefinition viewedClass) {
        this.viewedClass = viewedClass;
    }

    public ClassDefinition getViewedClass() {
        return viewedClass;
    }

    public abstract void reset();

    protected static Supplier<SkillTreeViewModel> factory;

    public static SkillTreeViewModel get() {
        return factory.get();
    }

    public enum InteractiveMode {
        DETAILED,
        FAST;

        public InteractiveMode opposite() {
            return this == DETAILED ? FAST : DETAILED;
        }
    }
}
