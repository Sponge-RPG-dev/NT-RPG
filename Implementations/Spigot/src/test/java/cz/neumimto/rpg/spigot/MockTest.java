package cz.neumimto.rpg.spigot;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

public class MockTest {

    @Test
    public void test() {
        MockBukkit.mock();
        MockPlugin test = MockBukkit.createMockPlugin("test");

        JavaPlugin.getProvidingPlugin(MockTest.class);
    }
}
