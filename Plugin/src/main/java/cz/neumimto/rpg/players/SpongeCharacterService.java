package cz.neumimto.rpg.players;

import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.events.PlayerDataPreloadComplete;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class SpongeCharacterService extends CharacterService {

    @Inject
    private Game game;

    @Inject
    private NtRpgPlugin plugin;

    @Override
    protected void addCharacterToGame(UUID id, IActiveCharacter character, List<CharacterBase> playerChars) {
        game.getScheduler().createTaskBuilder().name("Callback-PlayerDataLoad" + id).execute(() -> {
            PlayerDataPreloadComplete event = new PlayerDataPreloadComplete(id, playerChars);
            game.getEventManager().post(event);

            Optional<Player> popt = game.getServer().getPlayer(event.getPlayer());
            if (popt.isPresent()) {
                finalizePlayerDataPreloadStage(id, character, event, popt.get());
            } else {
                playerDataPreloadStagePlayerNotReady(id, character);
            }
        }).submit(plugin);
    }


}
