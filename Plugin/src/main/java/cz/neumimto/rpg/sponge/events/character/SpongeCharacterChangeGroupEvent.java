package cz.neumimto.rpg.sponge.events.character;

import cz.neumimto.rpg.api.events.character.CharacterChangeGroupEvent;
import cz.neumimto.rpg.players.groups.ClassDefinition;

/**
 * Created by NeumimTo on 29.4.2017.
 */
public class SpongeCharacterChangeGroupEvent extends AbstractCharacterEvent implements CharacterChangeGroupEvent {

    private ClassDefinition newClass;
    private ClassDefinition oldClass;

    @Override
    public ClassDefinition getNewClass() {
        return newClass;
    }

    @Override
    public void setNewClass(ClassDefinition newClass) {
        this.newClass = newClass;
    }

    @Override
    public ClassDefinition getOldClass() {
        return oldClass;
    }

    @Override
    public void setOldClass(ClassDefinition oldClass) {
        this.oldClass = oldClass;
    }
}
