package cz.neumimto.rpg.players;

import cz.neumimto.rpg.Pair;
import cz.neumimto.rpg.configuration.Localization;

/**
 * Created by NeumimTo on 22.10.2017.
 */
public class SkillTreeViewModel {

    private InteractiveMode interactiveMode;
    private Pair<Integer, Integer> location;

    public SkillTreeViewModel() {
        interactiveMode = InteractiveMode.DETAILED;
        location = new Pair<>(0,0);
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

    public enum InteractiveMode {
        DETAILED(Localization.INTERACTIVE_SKILLTREE_MOD_DETAILS),
        FAST(Localization.INTERACTIVE_SKILLTREE_MOD_FAST);
        private String transltion;

        InteractiveMode(String interactiveSkilltreeModFast) {
            this.transltion = interactiveSkilltreeModFast;
        }

        public String getTransltion() {
            return transltion;
        }
    }
}
