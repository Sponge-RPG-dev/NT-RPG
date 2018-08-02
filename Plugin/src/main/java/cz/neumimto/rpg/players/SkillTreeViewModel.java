package cz.neumimto.rpg.players;

import cz.neumimto.rpg.Pair;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.skills.tree.SkillTree;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;

/**
 * Created by NeumimTo on 22.10.2017.
 */
public class SkillTreeViewModel {

    private InteractiveMode interactiveMode;
    private Pair<Integer, Integer> location;
    private boolean current = true;
    private SkillTree skillTree;
    
    public SkillTreeViewModel() {
        interactiveMode = InteractiveMode.DETAILED;
        location = new Pair<>(0,0);
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

    public enum InteractiveMode {
        DETAILED(Localizations.INTERACTIVE_SKILLTREE_MOD_DETAILS.toText(), ItemTypes.BOOK),
        FAST(Localizations.INTERACTIVE_SKILLTREE_MOD_FAST.toText(), ItemTypes.GOLD_NUGGET);
        private Text transltion;
        private ItemType type;

        InteractiveMode(Text interactiveSkilltreeModFast, ItemType type) {
            this.transltion = interactiveSkilltreeModFast;
            this.type = type;
        }

        public Text getTransltion() {
            return transltion;
        }

        public InteractiveMode opposite() {
            return this == DETAILED ? FAST : DETAILED;
        }

        public ItemType getItemType() {
            return type;
        }
    }
}
