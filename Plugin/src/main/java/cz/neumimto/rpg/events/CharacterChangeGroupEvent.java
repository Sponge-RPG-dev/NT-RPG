package cz.neumimto.rpg.events;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.PlayerGroup;

/**
 * Created by NeumimTo on 29.4.2017.
 */
public class CharacterChangeGroupEvent extends CharacterEvent {
	private final PlayerGroup playerGroup;

	public CharacterChangeGroupEvent(PlayerGroup playerGroup, IActiveCharacter character) {
		super(character);
		this.playerGroup = playerGroup;
	}

	public PlayerGroup getPlayerGroup() {
		return playerGroup;
	}
}
