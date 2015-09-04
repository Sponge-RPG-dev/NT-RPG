package cz.neumimto.players;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by NeumimTo on 17.3.2015.
 */
public class CharacterAttributes {
    private Map<Class<? extends CharacterAttribute>, CharacterAttribute> attributes = new HashMap<>();
    public static Set<CharacterAttributeBuilder> builders = new HashSet<>();
    private IActiveCharacter IActiveCharacter;

    public CharacterAttributes(IActiveCharacter IActiveCharacter) {
        for (CharacterAttributeBuilder b : builders) {

        }
    }


    public void changeValue(Class<? extends CharacterAttribute> c, int amount) {
        CharacterAttribute a = attributes.get(c);

    }
}
