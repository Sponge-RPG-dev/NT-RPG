import cz.neumimto.players.ActiveCharacter;
import cz.neumimto.players.CharacterBase;
import cz.neumimto.players.IActiveCharacter;
import org.mockito.Mockito;
import org.spongepowered.api.entity.living.player.Player;

import javax.persistence.EntityManager;

import static org.mockito.Mockito.*;

/**
 * Created by fs on 7.10.15.
 */
public class TestUtils {

    public IActiveCharacter buildCharacter(Player player, CharacterBase characterBase) {
        ActiveCharacter character = new ActiveCharacter(player,characterBase);
        return character;
    }

}
