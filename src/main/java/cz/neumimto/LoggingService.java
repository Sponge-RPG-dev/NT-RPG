package cz.neumimto;

import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.Singleton;
import org.slf4j.Logger;

/**
 * Created by NeumimTo on 20.7.2015.
 */
@Singleton
public class LoggingService {


    @Inject
    private Logger logger;

    public void debug(String msg) {
        if (PluginConfig.DEBUG)
            logger.info(msg);
    }
}
