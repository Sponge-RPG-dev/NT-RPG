package cz.neumimto.rpg.api.logging;


import cz.neumimto.rpg.common.utils.DebugLevel;
import org.slf4j.Logger;

import static cz.neumimto.rpg.sponge.NtRpgPlugin.pluginConfig;

/**
 * Created by NeumimTo on 19.8.2018.
 */
public class Log {

    protected static Logger logger;

    public static void info(String message) {
        logger.info(message);
    }

    public static void info(String message, DebugLevel debugLevel) {
        if (debugLevel.getLevel() <= pluginConfig.DEBUG.getLevel()) {
            logger.info(message);
        }
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void error(String message, Throwable t) {
        logger.error(message, t);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void setLogger(Logger logger) {
        Log.logger = logger;
    }
}
