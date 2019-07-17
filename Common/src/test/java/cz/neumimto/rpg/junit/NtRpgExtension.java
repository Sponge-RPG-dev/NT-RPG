package cz.neumimto.rpg.junit;

import cz.neumimto.rpg.api.logging.Log;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.impl.SimpleLoggerFactory;


public class NtRpgExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        new TestDictionary().reset();
        org.slf4j.Logger logger = new SimpleLoggerFactory().getLogger("Testing");
        Log.setLogger(logger);
    }

}
