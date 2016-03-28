
import cz.neumimto.NtRpgPlugin;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.players.ActiveCharacter;
import cz.neumimto.players.CharacterBase;
import cz.neumimto.players.ExtendedNClass;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.groups.NClass;
import cz.neumimto.players.properties.DefaultProperties;
import cz.neumimto.players.properties.PlayerPropertyService;
import org.slf4j.Logger;
import org.slf4j.helpers.SubstituteLogger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventManager;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Created by fs on 7.10.15.
 */
public class TestUtils {

    private static EntityManager em;

    public static UUID uuid1 = UUID.randomUUID();
    public static UUID uuid2 = UUID.randomUUID();

    private static Game game;
    private static Logger logger;

    static {
        game = buildGameImpl();
    }

    public static IActiveCharacter buildCharacter(Player player, CharacterBase characterBase) {
        ActiveCharacter character = new ActiveCharacter(player,characterBase);
        return character;
    }

    public static EntityManager buildEntityManager() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        NtRpgPlugin pl = new NtRpgPlugin();
        pl.logger = new SubstituteLogger("test");
        Method method = pl.getClass().getDeclaredMethod("setupEntityManager", Path.class);
        method.setAccessible(true);
        EntityManager em = (EntityManager) method.invoke(pl, Paths.get("./src/main/resources/database.properties"));
        return em;
    }

    public static EntityManager getEntityManager() {
        if (em == null) {
            try {
                em = buildEntityManager();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return em;
    }

    public static Game buildGameImpl() {
        Game g = mock(Game.class);
        EventManager manager = mock(EventManager.class);
        when(manager.post(any())).thenReturn(true);
        when(g.getEventManager()).thenReturn(manager);
        return g;
    }

    public static IoC setupIocEnviromentTest() {
        IoC c = IoC.get();
        c.registerInterfaceImplementation(Game.class, game);
        c.registerInterfaceImplementation(EntityManager.class, getEntityManager());

        return c;
    }

    public static ActiveCharacter buildActiveCharacter(UUID uuid) {
        IoC ioC = setupIocEnviromentTest();
        PlayerPropertyService p = ioC.build(PlayerPropertyService.class);
        p.process(DefaultProperties.class);
        ActiveCharacter character = mock(ActiveCharacter.class);
        Player player = buildPlayerImpl(uuid);
        when(character.getPlayer()).thenReturn(player);
        CharacterBase characterBase = buildCharacterBase(uuid);
        when(character.getCharacterBase()).thenReturn(characterBase);
        ExtendedNClass k = new ExtendedNClass();
        k.setnClass(new NClass("test"));
        when(character.getPrimaryClass()).thenReturn(k);
        return character;
    }

    public static CharacterBase buildCharacterBase(UUID uuid) {
        CharacterBase characterBase = new CharacterBase();
        characterBase.setUuid(uuid);
        characterBase.setAttributePoints((short) 10);
        characterBase.setSkillPoints((short) 10);
        characterBase.setCanResetskills(true);
        characterBase.getClasses().put("test",5000D);
        characterBase.setRace("test");
        //characterBase.setGuild("attributes");
        characterBase.setLastReset(new Date(System.currentTimeMillis()));
        characterBase.getSkills().put("test",9);
        characterBase.setName("testChar");
        return characterBase;
    }

    public static Player buildPlayerImpl(UUID uuid) {
        Player mock = mock(Player.class);
        when(mock.getUniqueId()).thenReturn(uuid);
        return mock;
    }
}
