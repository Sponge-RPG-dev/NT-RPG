package cz.neumimto.rpg.spigot.events.character;

import cz.neumimto.rpg.api.IRpgElement;
import cz.neumimto.rpg.api.events.character.CharacterManaRegainEvent;
import org.bukkit.event.HandlerList;

/**
 * Created by NeumimTo on 9.8.2015.
 */
public class SpigotCharacterManaRegainEvent extends AbstractCharacterEvent implements CharacterManaRegainEvent {

    private double amount;
    private IRpgElement source;
    private boolean cancelled;

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public IRpgElement getSource() {
        return source;
    }

    @Override
    public void setSource(IRpgElement source) {
        this.source = source;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean state) {
        cancelled = state;
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
