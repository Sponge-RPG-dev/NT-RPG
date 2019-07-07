package cz.neumimto.rpg.api.messaging;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

@FunctionalInterface
public interface MessageProcessor {

    void sendMessage(IActiveCharacter character, String message);
}
