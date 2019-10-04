package cz.neumimto.rpg.common.entity;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;

import java.util.UUID;

public class TestCharacter extends ActiveCharacter<UUID, TestParty> implements IActiveCharacter<UUID, TestParty> {


    public TestCharacter(UUID uuid, CharacterBase base, int propertyCount) {
        super(uuid, base, propertyCount);
    }

    @Override
    public UUID getUUID() {
        return pl;
    }

    @Override
    public void sendMessage(String message) {
        Log.info("<<< message " + message);
    }

    @Override
    public void sendMessage(int channel, String message) {

    }

    @Override
    public void sendNotification(String message) {
        Log.info("<<< notification " + message);
    }

    @Override
    public String getPlayerAccountName() {
        return "null";
    }

    @Override
    public UUID getEntity() {
        return pl;
    }

    @Override
    public boolean isDetached() {
        return pl != null;
    }
}
