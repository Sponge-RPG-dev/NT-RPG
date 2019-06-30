package com.example.ntrpgwebservergui;

import com.vaadin.flow.server.startup.ServletContextListeners;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.*;

import java.net.URI;
import java.net.URL;

//@Plugin(id = "nt-rpg-webgui", version = "0.0.1")
public class NTRpgWebGui {

    public static final String JAR_PATTERN = "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern";

/*
    @Inject
    org.slf4j.Logger logger;

    @Listener
    public void onGameStart(GameStartedServerEvent event) {
        startServlet();
    }
    */

    public static void main(String[]a) throws Exception {
        new NTRpgWebGui().startServlet(5, 8080);
    }

    public void startServlet(int threads, int port) throws Exception {

        URL webRootLocation = NTRpgWebGui.class.getResource("/webapp/");
        URI webRootUri = webRootLocation.toURI();

        WebAppContext context = new WebAppContext();
        context.setBaseResource(Resource.newResource(webRootUri));
        context.setContextPath("/");
        context.setAttribute(JAR_PATTERN , ".*");
        context.setConfigurationDiscovered(true);
        context.setConfigurations(new Configuration[]{
                new AnnotationConfiguration(),
                new WebInfConfiguration(),
                new WebXmlConfiguration(),
                new MetaInfConfiguration()
        });
        context.getServletContext().setExtendedListenerTypes(true);
        context.addEventListener(new ServletContextListeners());

        ThreadPool threadPool = new ExecutorThreadPool();
        Server server = new Server(threadPool);

        HttpConfiguration httpConfiguration = new HttpConfiguration();
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfiguration));
        http.setPort(port);

        server.addConnector(http);
        server.setHandler(context);
        server.start();
        server.join();
    }
}
