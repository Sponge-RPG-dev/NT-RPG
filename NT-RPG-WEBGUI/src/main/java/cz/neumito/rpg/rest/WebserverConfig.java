package cz.neumito.rpg.rest;

import cz.neumimto.configuration.ConfigValue;
import cz.neumimto.configuration.ConfigurationContainer;

@ConfigurationContainer(path = "{WorkingDir}", filename = "WebServer.conf")
public class WebserverConfig {

	@ConfigValue
	public static int WEBSERVER_PORT = 8080;

	@ConfigValue
	public static int WEBSERVER_THREADPOOL = 5;

}
