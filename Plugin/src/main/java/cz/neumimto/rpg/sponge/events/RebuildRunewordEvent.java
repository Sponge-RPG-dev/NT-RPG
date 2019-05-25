package cz.neumimto.rpg.sponge.events;

import cz.neumimto.rpg.inventory.runewords.RuneWord;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by NeumimTo on 8.1.2017.
 */
public class RebuildRunewordEvent extends AbstractEvent {

    private final RuneWord runeWord;
    private ItemStack itemStack;

    public RebuildRunewordEvent(RuneWord rw, ItemStack i) {
        this.runeWord = rw;
        this.itemStack = i;
    }

    public RuneWord getRuneWord() {
        return runeWord;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public Cause getCause() {
        return Cause.of(EventContext.empty(), runeWord);
    }
}
