package cz.neumimto.rpg;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.mctester.api.junit.MinecraftRunner;
import org.spongepowered.mctester.internal.BaseTest;
import org.spongepowered.mctester.internal.event.StandaloneEventListener;
import org.spongepowered.mctester.junit.TestUtils;

@RunWith(MinecraftRunner.class)
public class McRunnerTests extends BaseTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    public McRunnerTests(TestUtils testUtils) {
        super(testUtils);
    }

    @Test
    public void testOneShotEventListenerException() throws Throwable {
        expectedEx.expect(AssertionError.class);
        expectedEx.expectMessage("Got message: One shot");

        this.testUtils.listenOneShot(() -> {
            this.testUtils.getClient().sendMessage("One shot");
        }, new StandaloneEventListener<>(MessageChannelEvent.Chat.class, (MessageChannelEvent.Chat event) ->
                Assert.fail("Got message: " + event.getRawMessage().toPlain())));
    }
}
