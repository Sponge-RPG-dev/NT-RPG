package cz.neumimto.rpg.spigot.events.character;

import cz.neumimto.rpg.common.IRpgElement;
import cz.neumimto.rpg.common.events.character.CharacterResourceChangeValueEvent;
import org.bukkit.event.HandlerList;

/**
 * Created by NeumimTo on 9.8.2015.
 */
public class SpigotCharacterResourceChangeValueEvent extends AbstractCharacterEvent implements CharacterResourceChangeValueEvent {

    private String type;
    private double amount;
    private IRpgElement source;
    private boolean cancelled;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

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
