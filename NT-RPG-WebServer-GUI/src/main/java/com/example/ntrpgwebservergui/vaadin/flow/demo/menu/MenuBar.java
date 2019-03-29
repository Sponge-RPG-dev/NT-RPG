/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.example.ntrpgwebservergui.vaadin.flow.demo.menu;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteData;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Menu that is dynamically populated from registered routes.
 */
public class MenuBar extends UnorderedList {

    private List<Class<?>> external = new ArrayList<>();

    /**
     * Constructor that collects the routes and builds the menu.
     */
    public MenuBar() {
        init();
    }

    private void init() {
        setId("menu");
        Map<Class<? extends RouterLayout>, List<RouteData>> routes = UI
                .getCurrent().getRouter().getRoutesByParent();
        List<Class<? extends RouterLayout>> parentLayouts = routes.keySet()
                .stream().filter(Objects::nonNull)
                .collect(Collectors.toList());

        Collections.sort(parentLayouts, (o1, o2) -> o1.getSimpleName()
                .compareToIgnoreCase(o2.getSimpleName()));
        for (Class<? extends RouterLayout> key : parentLayouts) {
            populateMenuItem(routes.get(key), key);
        }

        // Handle external routes that do not contain our ApplicationLayout
        if (routes.containsKey(null)) {
            populateExternalRoutes(routes.get(null));
        }
    }

    private void populateMenuItem(List<RouteData> routes,
            Class<? extends RouterLayout> key) {
        UnorderedList subList = new UnorderedList();

        for (RouteData route : routes) {
            RouterLink routerLink = new RouterLink(getRouteName(route),
                    route.getNavigationTarget());

            // ApplicationLayout children should be as root targets.
            if (key.equals(ApplicationLayout.class)) {
                ListItem menuItem = new ListItem(routerLink);
                add(menuItem);
            } else {
                subList.add(new ListItem(routerLink));
            }
        }

        if (subList.getChildren().count() != 0) {
            String title = key.getSimpleName();
            if (key.isAnnotationPresent(PageTitle.class)) {
                title = key.getAnnotation(PageTitle.class).value();
            }
            Anchor anchor = new Anchor("#", title);

            // Create menu item
            addMenuItem(subList, anchor);
        }
    }

    private void addMenuItem(UnorderedList subList, Anchor anchor) {
        ListItem listItem = new ListItem();
        listItem.add(anchor);
        listItem.add(subList);
        add(listItem);
    }

    private void populateExternalRoutes(List<RouteData> routes) {
        UnorderedList subList = new UnorderedList();
        for (RouteData route : routes) {
            // Book keeping for external routes.
            external.add(route.getNavigationTarget());

            String routeName = getRouteName(route);
            if (routeName.contains("View")) {
                RouterLink routerLink = new RouterLink(
                        routeName.substring(0, routeName.indexOf("View")),
                        route.getNavigationTarget());
                subList.add(new ListItem(routerLink));
            }
        }

        Anchor anchor = new Anchor("#", "External Demos");
        addMenuItem(subList, anchor);
    }

    private String getRouteName(RouteData route) {
        Class<? extends Component> navigationTarget = route
                .getNavigationTarget();
        if (navigationTarget.isAnnotationPresent(PageTitle.class)) {
            return navigationTarget.getAnnotation(PageTitle.class).value();
        }
        return navigationTarget.getSimpleName();
    }

    protected boolean isExternal(Class<?> target) {
        return external.contains(target);
    }
}
