package cz.neumimto.rpg.spigot;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.help.HelpMapMock;
import cz.neumimto.rpg.api.Rpg;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class Test {

    private ServerMock server;
    private SpigotRpgPlugin plugin;

    @BeforeEach
    public void setUp()
    {
        server = MockBukkit.mock();
        plugin = (SpigotRpgPlugin) MockBukkit.load(SpigotRpgPlugin.class);
    }

    @org.junit.jupiter.api.Test
    public void test() {

    }

    @AfterEach
    public void tearDown()
    {
        MockBukkit.unmock();
    }
}
