package cz.neumimto.rpg.events;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.ConfigClass;

/**
 * Created by ja on 29.4.2017.
 */
public class CharacterChangeClassEvent extends CharacterChangeGroupEvent {

	private final int slot;

	public CharacterChangeClassEvent(IActiveCharacter character, ConfigClass next, int slot, ConfigClass old) {
		super(next, character, old);
		this.slot = slot;
	}

	public int getSlot() {
		return slot;
	}

	@Override
	public String toString() {
		return "CharacterChangeClassEvent{" +
				"slot=" + slot +
				"New=" + getNew() +
				"Old=" + getOld() +
				'}';
	}
}
