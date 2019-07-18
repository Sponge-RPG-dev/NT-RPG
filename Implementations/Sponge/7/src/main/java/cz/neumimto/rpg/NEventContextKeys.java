package cz.neumimto.rpg;

import org.spongepowered.api.event.cause.EventContextKey;

import java.util.UUID;

/**
 * Created by ja on 16.9.2017.
 */
public class NEventContextKeys {

    public static final EventContextKey<UUID> GAME_PROFILE = EventContextKey
            .builder(UUID.class)
            .name("gameprofile")
            .id("ntrpg.gameprofile")
            .build();
}
