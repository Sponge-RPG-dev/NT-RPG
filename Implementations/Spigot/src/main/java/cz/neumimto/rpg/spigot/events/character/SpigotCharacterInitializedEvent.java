package cz.neumimto.rpg.spigot.events.character;

import cz.neumimto.rpg.common.events.character.CharacterInitializedEvent;
import org.bukkit.event.HandlerList;

/**
 * Created by NeumimTo on 24.6.2017.
 */
public class SpigotCharacterInitializedEvent extends AbstractCharacterEvent implements CharacterInitializedEvent {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
