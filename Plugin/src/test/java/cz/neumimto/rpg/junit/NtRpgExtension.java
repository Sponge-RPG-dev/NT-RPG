package cz.neumimto.rpg.junit;

import cz.neumimto.rpg.GlobalScope;
import cz.neumimto.rpg.RpgTest;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.impl.SimpleLoggerFactory;


public class NtRpgExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        new RpgTest();
        new TestDictionary().reset();
        org.slf4j.Logger logger = new SimpleLoggerFactory().getLogger("Testing");
        Log.setLogger(logger);
        NtRpgPlugin.pluginConfig = new PluginConfig();
        NtRpgPlugin.workingDir = ".";
        NtRpgPlugin.GlobalScope = new GlobalScope();
    }

}
