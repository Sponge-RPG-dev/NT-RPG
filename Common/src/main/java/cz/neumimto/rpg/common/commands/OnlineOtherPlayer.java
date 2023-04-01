package cz.neumimto.rpg.common.commands;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;

public class OnlineOtherPlayer {
    public final ActiveCharacter character;

    public OnlineOtherPlayer(ActiveCharacter character) {
        this.character = character;
    }
}
