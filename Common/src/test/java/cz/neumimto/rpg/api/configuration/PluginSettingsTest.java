package cz.neumimto.rpg.api.configuration;

import cz.neumimto.rpg.common.AbstractRpg;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

public class PluginSettingsTest {

    @Test
    public void testPluginReload() {
        new TestRpg().reloadMainPluginConfig();
    }

    @AfterAll
    public static void deleteFile() {
        new File("./Settings.conf").delete();
    }

    private static class TestRpg extends AbstractRpg {

        public TestRpg() {
            super(".");
        }

        @Override
        public void broadcastMessage(String message) {

        }

        @Override
        public String getTextAssetContent(String templateName) {
            return null;
        }

        @Override
        public void executeCommandBatch(Map<String, String> args, List<String> enterCommands) {

        }

        @Override
        public void executeCommandAs(UUID sender, Map<String, String> args, List<String> enterCommands) {

        }

        @Override
        public boolean postEvent(Object event) {
            return false;
        }

        @Override
        public void unregisterListeners(Object listener) {

        }

        @Override
        public void registerListeners(Object listener) {

        }

        @Override
        public Executor getAsyncExecutor() {
            return null;
        }

        @Override
        public void scheduleSyncLater(Runnable runnable) {

        }

        @Override
        public Set<UUID> getOnlinePlayers() {
            return null;
        }

        @Override
        public void doImplSpecificreload() {

        }

        @Override
        public String getPlatform() {
            return null;
        }
    }
}
