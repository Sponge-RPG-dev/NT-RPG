package cz.neumimto.rpg.common.messaging;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;

@FunctionalInterface
public interface MessageProcessor {

    void sendMessage(IActiveCharacter character, String message);
}
