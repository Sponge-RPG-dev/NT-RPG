package cz.neumimto.rpg.spigot.events.character;

import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.events.character.CharacterChangeGroupEvent;
import org.bukkit.event.HandlerList;

/**
 * Created by NeumimTo on 29.4.2017.
 */
public class SpigotCharacterChangeGroupEvent extends AbstractCharacterEvent implements CharacterChangeGroupEvent {

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

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
