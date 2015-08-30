package cz.neumimto.events;

import cz.neumimto.players.IActiveCharacter;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.util.event.callback.CallbackList;

import java.util.Set;

/**
 * Created by NeumimTo on 25.7.2015.
 */
public class EventCharacterArmorPostUpdate implements Event {
    IActiveCharacter character;
    Set<ItemType> armor;

    public EventCharacterArmorPostUpdate(IActiveCharacter character, Set<ItemType> allowedArmor) {
        this.character = character;
        this.armor = allowedArmor;
    }

    public IActiveCharacter getCharacter() {
        return character;
    }

    public Set<ItemType> getArmor() {
        return armor;
    }

    @Override
    public CallbackList getCallbacks() {
        return new CallbackList();
    }
}
