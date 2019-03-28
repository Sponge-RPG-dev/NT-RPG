package com.example.ntrpgwebservergui;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@WebServlet(
    asyncSupported=false,
    urlPatterns={"/*","/VAADIN/*"},
    initParams={
        @WebInitParam(name="ui", value="com.example.ntrpgwebservergui.NTRPGWebServerGUI")
    })
public class NTRPGWebServerGUIServlet extends com.vaadin.server.VaadinServlet { }
