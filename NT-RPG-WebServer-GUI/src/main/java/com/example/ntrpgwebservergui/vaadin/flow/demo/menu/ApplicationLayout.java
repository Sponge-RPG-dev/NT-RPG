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
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.router.*;

import java.util.Arrays;
import java.util.Optional;

/**
 * Main view layout for the application containing the dynamically built menu.
 */
@StyleSheet("frontend://styles/styles.css")
@BodySize(height = "100vh", width = "100%")
public class ApplicationLayout extends Div
        implements RouterLayout, BeforeLeaveObserver {

    private final MenuBar menuBar = new MenuBar();

    /**
     * Constructor.
     */
    public ApplicationLayout() {
        init();
    }

    private void init() {
        getElement().getStyle().set("height", "100%");
        add(menuBar);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (menuBar.isExternal(event.getNavigationTarget())) {
            Component routeTarget = getRouteTarget(
                    (Class<? extends Component>) event.getNavigationTarget());

            UI.getCurrent().getInternals().showRouteTarget(event.getLocation(),
                    routeTarget.getClass().getAnnotation(Route.class).value(),
                    routeTarget, Arrays.asList(this));
            ((HasUrlParameter<?>) routeTarget).setParameter(event, null);
            event.postpone();
        }
    }

    protected <T extends HasElement> T getRouteTarget(
            Class<T> routeTargetType) {
        UI ui = UI.getCurrent();
        Optional<HasElement> currentInstance = ui.getInternals()
                .getActiveRouterTargetsChain().stream()
                .filter(component -> component.getClass()
                        .equals(routeTargetType)).findAny();
        return (T) currentInstance.orElseGet(() -> Instantiator.get(ui)
                .createRouteTarget(routeTargetType, null));
    }
}
