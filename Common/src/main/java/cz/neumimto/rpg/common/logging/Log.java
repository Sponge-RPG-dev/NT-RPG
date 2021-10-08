package cz.neumimto.rpg.common.logging;


import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.utils.DebugLevel;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by NeumimTo on 19.8.2018.
 */
public class Log {

    protected static Logger logger;

    private Log() {
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void info(String message, DebugLevel debugLevel) {
        if (debugLevel.getLevel() <= Rpg.get().getPluginConfig().DEBUG.getLevel()) {
            logger.info(message);
        }
    }

    public static void warn(String message) {
        logger.log(Level.WARNING, message);
    }

    public static void error(String message, Throwable t) {
        logger.log(Level.SEVERE, message, t);
    }

    public static void error(String message) {
        logger.log(Level.SEVERE, message);
    }

    public static void setLogger(Logger logger) {
        Log.logger = logger;
    }

    public Logger logger() {
        return logger;
    }
}
