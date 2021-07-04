package cz.neumimto.rpg.spigot.events.character;

import cz.neumimto.rpg.api.events.character.EventCharacterArmorPostUpdate;
import cz.neumimto.rpg.api.items.RpgItemType;
import org.bukkit.event.HandlerList;

import java.util.Set;

/**
 * Created by NeumimTo on 25.7.2015.
 */
public class SpigotEventCharacterArmorPostUpdate extends AbstractCharacterEvent implements EventCharacterArmorPostUpdate {

    private Set<RpgItemType> armor;

    @Override
    public Set<RpgItemType> getArmor() {
        return armor;
    }

    @Override
    public void setArmor(Set<RpgItemType> armor) {
        this.armor = armor;
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
