package cz.neumimto.rpg;

import cz.neumimto.core.ioc.IoC;
import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventManager;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
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

    public static UUID uuid1 = UUID.randomUUID();
    public static UUID uuid2 = UUID.randomUUID();
    private static EntityManager em;
    private static Game game;

    static {
        game = buildGameImpl();
    }

    public static IActiveCharacter buildCharacter(UUID player, CharacterBase characterBase) {
        ActiveCharacter character = new ActiveCharacter(player, characterBase);
        return character;
    }

    public static EntityManager buildEntityManager() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        NtRpgPlugin pl = new NtRpgPlugin();
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


    public static CharacterBase buildCharacterBase(UUID uuid) {
        CharacterBase characterBase = new CharacterBase();
        characterBase.setUuid(uuid);
        characterBase.setAttributePoints(10);
        characterBase.setCanResetskills(true);
        //characterBase.setGuild("attributes");
        characterBase.setLastReset(new Date(System.currentTimeMillis()));
        characterBase.setName("testChar");
        return characterBase;
    }

    public static Player buildPlayerImpl(UUID uuid) {
        Player mock = mock(Player.class);
        when(mock.getUniqueId()).thenReturn(uuid);
        return mock;
    }

    public static void setField(Object o, String f, Object v) {
        try {
            Field declaredField = o.getClass().getDeclaredField(f);
            declaredField.setAccessible(true);

            declaredField.set(o, v);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
