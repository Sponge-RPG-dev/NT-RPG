package cz.neumimto.rpg.common.entity.players;

/**
 * Created by ja on 8.10.2016.
 */
public class PlayerNotInGameException extends RuntimeException {

    private final PreloadCharacter preloadCharacter;

    public PlayerNotInGameException(String format, PreloadCharacter preloadCharacter) {
        super(format);
        this.preloadCharacter = preloadCharacter;
    }

    public PreloadCharacter getPreloadCharacter() {
        return preloadCharacter;
    }
}
