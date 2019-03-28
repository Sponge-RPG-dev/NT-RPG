package com.example.ntrpgwebservergui;

import com.vaadin.server.VaadinServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.spongepowered.api.plugin.Plugin;

import java.util.EventListener;

@Plugin(id = "nt-rpg-webgui", version = "0.0.1")
public class NTRpgWebGui {
/*
    @Inject
    org.slf4j.Logger logger;

    @Listener
    public void onGameStart(GameStartedServerEvent event) {
        startServlet();
    }
    */

    public static void main(String[]a) {
        new NTRpgWebGui().startServlet();
    }

    public void startServlet() {
        Server server = new Server(8080);

        ServletContextHandler contextHandler
                = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");

        ServletHolder sh = new ServletHolder(new VaadinServlet());
        contextHandler.addServlet(sh, "/*");
        contextHandler.setInitParameter("ui", NTRPGWebServerGUI.class.getCanonicalName());

        // Register cdn.virit.in if present
        try {
            Class cls = Class.forName("in.virit.WidgetSet");
            if (cls != null) {
                contextHandler.getSessionHandler().addEventListener((EventListener) cls.newInstance());
            }
        } catch (Exception ex) {
          //  logger.error("Could not run Vaadin: ", ex);
        }

        server.setHandler(contextHandler);

        try {
            server.start();
            server.join();

        } catch (Exception ex) {
        //    logger.error("Could not run Vaadin: ", ex);
        }
    }
}
