package cz.neumimto.rpg.events;

import cz.neumimto.rpg.inventory.runewords.RuneWord;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.List;

/**
 * Created by NeumimTo on 8.1.2017.
 */
public class RebuildRunewordEvent extends AbstractEvent {

	private final RuneWord runeWord;
	private List<Text> lore;
	private ItemStack itemStack;

	public RebuildRunewordEvent(RuneWord rw, List<Text> lore, ItemStack i) {

		this.runeWord = rw;
		this.lore = lore;
		this.itemStack = i;
	}

	public RuneWord getRuneWord() {
		return runeWord;
	}

	public List<Text> getLore() {
		return lore;
	}

	public void setLore(List<Text> lore) {
		this.lore = lore;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	@Override
	public Cause getCause() {
		return Cause.of(NamedCause.source(runeWord));
	}
}
