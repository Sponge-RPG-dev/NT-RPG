package cz.neumimto.rpg.api.messaging;

import cz.neumimto.rpg.players.IActiveCharacter;

@FunctionalInterface
public interface MessageProcessor {

    void sendMessage(IActiveCharacter character, String message);
}
