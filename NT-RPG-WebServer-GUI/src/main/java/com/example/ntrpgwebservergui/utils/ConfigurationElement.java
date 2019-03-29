package com.example.ntrpgwebservergui.utils;

import com.vaadin.flow.component.Component;

public class ConfigurationElement {

    public final Component element;
    public final String notes;
    public final String label;

    public ConfigurationElement(Component element, String notes, String label) {

        this.element = element;
        this.notes = notes;
        this.label = label;
    }

}
