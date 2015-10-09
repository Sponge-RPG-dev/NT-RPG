import com.sun.media.jfxmedia.events.PlayerEvent;
import cz.neumimto.ioc.IoC;
import cz.neumimto.players.IActiveCharacter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import javax.sql.ConnectionEvent;
import java.util.Optional;

@PrepareForTest(DamageEntityEvent.class)
public class TestEvent {

    @Before
    public void init(){

    }

    @Test
    public void dmTest() {
        DamageEntityEvent event = PowerMockito.mock(DamageEntityEvent.class);
        Player target = PowerMockito.mock(Player.class);
        Player source = PowerMockito.mock(Player.class);

        EntityDamageSource source1 = PowerMockito.mock(EntityDamageSource.class);
        when(source1.getSource()).thenReturn(source);

        when(event.getCause().first(EntityDamageSource.class)).thenReturn(Optional.of(source1));
        when(event.getTargetEntity()).thenReturn(target);
    }
}
