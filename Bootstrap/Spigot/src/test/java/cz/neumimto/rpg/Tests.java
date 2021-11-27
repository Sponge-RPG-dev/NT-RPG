package cz.neumimto.rpg;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class Tests {

    private ServerMock server;
    private SpigotRpgBootstrap plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(SpigotRpgBootstrap.class);
    }

    //  @Test
    public void load() {

    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }
}
