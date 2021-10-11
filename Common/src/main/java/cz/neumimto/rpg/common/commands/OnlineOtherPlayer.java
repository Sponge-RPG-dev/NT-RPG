package cz.neumimto.rpg.common.commands;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;

public class OnlineOtherPlayer {
    public final IActiveCharacter character;

    public OnlineOtherPlayer(IActiveCharacter character) {
        this.character = character;
    }
}
