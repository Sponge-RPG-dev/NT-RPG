package cz.neumito.rpg.rest;

import com.google.common.collect.Sets;
import cz.neumimto.configuration.ConfigValue;
import cz.neumimto.configuration.ConfigurationContainer;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ConfigurationContainer
public class WebserverConfig {

    @ConfigValue
    public static int WEBSERVER_PORT;

    @ConfigValue
    public static int WEBSERVER_THREADPOOL;

    @ConfigValue
    public Set<String> BLOCKED_IPS =  Sets.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
}
