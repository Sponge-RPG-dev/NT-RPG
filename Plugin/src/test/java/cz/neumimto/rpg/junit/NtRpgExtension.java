package cz.neumimto.rpg.junit;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.TestHelper;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.configuration.PluginConfig;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.LoggerFactory;


public class NtRpgExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        TestHelper.initLocalizations();
        Log.setLogger(LoggerFactory.getLogger("TestLogger"));
        NtRpgPlugin.pluginConfig = new PluginConfig();
        NtRpgPlugin.workingDir = ".";
    }

}
