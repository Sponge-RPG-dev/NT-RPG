package cz.neumimto.rpg.players;

import java.util.HashMap;
import java.util.Map;

public class AttributeViewSession {
    private IActiveCharacter character;

    private Map<String, Integer> change;

    public AttributeViewSession(IActiveCharacter character) {
        this.character = character;
        this.change = new HashMap<>();
    }

    public void addOne(String attribute) {
        if (change.containsKey(attribute)) {
            change.put(attribute, change.get(attribute) + 1);
        } else {
            change.put(attribute, 1);
        }
    }

    public void removeOne(String attribute) {
        if (change.containsKey(attribute)) {
            Integer integer = change.get(attribute);
            change.put(attribute, integer == 0 ? 0 : integer - 1);
        }
    }

    public void commit() {

        reset();
    }

    public void reset() {
        change.clear();
    }
}
