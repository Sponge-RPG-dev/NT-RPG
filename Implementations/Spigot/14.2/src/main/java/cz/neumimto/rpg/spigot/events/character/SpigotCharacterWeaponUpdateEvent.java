package cz.neumimto.rpg.spigot.events.character;

import cz.neumimto.rpg.api.events.character.CharacterWeaponUpdateEvent;
import cz.neumimto.rpg.api.items.RpgItemType;
import org.bukkit.event.HandlerList;

import java.util.Map;

/**
 * Created by NeumimTo on 10.10.2015.
 */
public class SpigotCharacterWeaponUpdateEvent extends AbstractCharacterEvent implements CharacterWeaponUpdateEvent {

    private Map<RpgItemType, Double> weapons;

    @Override
    public Map<RpgItemType, Double> getAllowedWeapons() {
        return weapons;
    }

    @Override
    public void setWeapons(Map<RpgItemType, Double> weapons) {
        this.weapons = weapons;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
