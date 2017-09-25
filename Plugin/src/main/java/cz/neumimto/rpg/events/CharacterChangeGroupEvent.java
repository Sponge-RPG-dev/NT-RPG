package cz.neumimto.rpg.events;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.PlayerGroup;

/**
 * Created by NeumimTo on 29.4.2017.
 */
public class CharacterChangeGroupEvent extends CharacterEvent {
	private final PlayerGroup _new;
	private final PlayerGroup old;

	public CharacterChangeGroupEvent(PlayerGroup playerGroup, IActiveCharacter character, PlayerGroup old) {
		super(character);
		this._new = playerGroup;
		this.old = old;
	}

	public PlayerGroup getNew() {
		return _new;
	}

	public PlayerGroup getOld() {
		return old;
	}
}
