package com.example.ntrpgwebservergui.vaadin.flow.demo.menu.routes;

import com.example.ntrpgwebservergui.utils.ConfigurationElement;
import com.example.ntrpgwebservergui.utils.ElementRenderer;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import cz.neumimto.rpg.configuration.PluginConfig;

import java.util.List;

@Route(value = "settings", layout = SubView.class)
public class SettingsEditorView extends Div {

    public SettingsEditorView() {
        PluginConfig pluginConfig = new PluginConfig();
        ElementRenderer elementRenderer = new ElementRenderer(pluginConfig);
        List<ConfigurationElement> render = elementRenderer.render();

        for (ConfigurationElement configurationElement : render) {
            HorizontalLayout inner = new HorizontalLayout();
            inner.setSpacing(false);
            inner.add(new Label(configurationElement.label));
            inner.add(configurationElement.element);
            inner.add(new Label(configurationElement.notes));
            add(inner);
        }
    }
}
