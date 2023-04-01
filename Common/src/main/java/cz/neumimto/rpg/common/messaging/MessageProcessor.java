package cz.neumimto.rpg.common.messaging;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;

@FunctionalInterface
public interface MessageProcessor {

    void sendMessage(ActiveCharacter character, String message);
}
