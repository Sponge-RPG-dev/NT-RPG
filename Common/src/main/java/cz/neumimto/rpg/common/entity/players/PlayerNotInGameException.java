package cz.neumimto.rpg.common.entity.players;

/**
 * Created by ja on 8.10.2016.
 */
public class PlayerNotInGameException extends RuntimeException {

    public PlayerNotInGameException(String format) {
        super(format);
    }
}
