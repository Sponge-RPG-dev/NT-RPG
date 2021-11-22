package cz.neumimto.rpg;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.RpgApi;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.scripting.EffectScriptGenerator;
import cz.neumimto.rpg.common.skills.scripting.ListenerScriptGenerator;
import cz.neumimto.rpg.common.skills.scripting.ScriptListenerModel;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.Arrays;

@ExtendWith({GuiceExtension.class})
@IncludeModule(TestGuiceModule.class)
public class TestListenerScripts {

    @Inject
    private RpgApi rpgApi;

    @BeforeEach
    public void before() {
        new RpgTest(rpgApi);
    }

    public static class CancellableEvent {
        private boolean cancelled;

        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        public boolean isCancelled() {
            return cancelled;
        }
    }

    @Test
    public void generate_listener() throws Exception {
        Rpg.get().getEventFactory().registerProvider(CancellableEvent.class, CancellableEvent::new);
        ScriptListenerModel model = new ScriptListenerModel();
        model.id = "TestListener";
        model.event = "CancellableEvent";
        model.script = """
            @event.cancelled = T
            RETURN
            """;
        Class from = ListenerScriptGenerator.from(model, this.getClass().getClassLoader());
        CancellableEvent cancellableEvent = new CancellableEvent();
        Arrays.stream(from.getDeclaredMethods()).findFirst().get().invoke(from.newInstance(), cancellableEvent);
        Assertions.assertTrue(cancellableEvent.cancelled);
    }

}
