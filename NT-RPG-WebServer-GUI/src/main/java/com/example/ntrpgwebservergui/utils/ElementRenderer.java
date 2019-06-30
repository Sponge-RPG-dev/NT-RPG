package com.example.ntrpgwebservergui.utils;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.TextField;
import ninja.leaping.configurate.objectmapping.Setting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ElementRenderer {

    static final private Map<Class<?>, Supplier<Component>> builders = new HashMap<>();

    static {
        initBuilders();
    }

    Object object;

    public ElementRenderer(Object object) {
        this.object = object;
    }

    public List<ConfigurationElement> render() {
        List<ConfigurationElement> list = new ArrayList<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            Class<?> type = field.getType();
            Supplier<Component> componentSupplier = builders.get(type);
            if (componentSupplier != null) {
                Component editablePart = componentSupplier.get();
                Setting annotation = field.getAnnotation(Setting.class);
                if (annotation != null) {
                    String value = annotation.value();
                    if (value.isEmpty()) {
                        value = field.getName();
                    }
                    String comment = annotation.comment();
                    list.add(new ConfigurationElement(editablePart, comment, value));
                    //field.get(object);
                }
            }
        }
        return list;
    }

    private void populate() {
        for (Field field : object.getClass().getDeclaredFields()) {

        }
    }

    private static void initBuilders() {
        builders.put(boolean.class, Checkbox::new);
        builders.put(Boolean.class, Checkbox::new);
        builders.put(String.class, TextField::new);
    }
}
