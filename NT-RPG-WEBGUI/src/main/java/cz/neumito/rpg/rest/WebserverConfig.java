package cz.neumito.rpg.rest;

import com.google.common.collect.Sets;
import cz.neumimto.configuration.ConfigValue;
import cz.neumimto.configuration.ConfigurationContainer;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ConfigurationContainer(path = "{WorkingDir}", filename = "WebServer.conf")
public class WebserverConfig {

    @ConfigValue
    public static int WEBSERVER_PORT = 8080;

    @ConfigValue
    public static int WEBSERVER_THREADPOOL = 5;

 }
