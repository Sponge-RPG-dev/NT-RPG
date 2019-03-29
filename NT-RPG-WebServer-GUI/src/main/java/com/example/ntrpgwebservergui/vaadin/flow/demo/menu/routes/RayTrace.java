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
package com.example.ntrpgwebservergui.vaadin.flow.demo.menu.routes;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.ui.LoadMode;

/**
 * Simple canvas ray trace component.
 * Script from http://ncase.me/sight-and-light/
 */
@Route(value = "ray-trace", layout = SubView.class)
public class RayTrace extends Div {

    /**
     * Constructor.
     */
    public RayTrace() {
        init();
    }

    private void init() {
        Element baseCanvas = new Element("canvas");
        baseCanvas.setAttribute("id", "canvas");
        baseCanvas.setAttribute("width", "640");
        baseCanvas.setAttribute("height", "360");

        getElement().appendChild(baseCanvas);

        UI.getCurrent().getPage()
                .addJavaScript("frontend://script/rayTrace.js", LoadMode.LAZY);
    }

}
