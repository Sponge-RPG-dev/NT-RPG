package cz.neumimto.rpg.sponge.gui;

import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.utils.Pair;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NeumimTo on 22.10.2017.
 */
public class SkillTreeViewModel {

    private InteractiveMode interactiveMode;
    private Pair<Integer, Integer> location;
    private boolean current = true;
    private SkillTree skillTree;
    private ClassDefinition viewedClass;
    private Map<String, Pair<List<Text>, TextColor>> buttonCache;


    public SkillTreeViewModel() {
        interactiveMode = InteractiveMode.DETAILED;
        location = new Pair<>(0, 0);
        buttonCache = new HashMap<>();
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

    public void reset() {
        buttonCache.clear();
    }

    public Pair<List<Text>, TextColor> getFromCache(ISkill iSkill) {
        return buttonCache.get(iSkill.getId());
    }

    public void addToCache(ISkill iSkill, List<Text> lore, TextColor textColor) {
        buttonCache.put(iSkill.getId(), new Pair<>(lore, textColor));
    }

    public enum InteractiveMode {
        DETAILED,
        FAST;

        public InteractiveMode opposite() {
            return this == DETAILED ? FAST : DETAILED;
        }
    }
}
