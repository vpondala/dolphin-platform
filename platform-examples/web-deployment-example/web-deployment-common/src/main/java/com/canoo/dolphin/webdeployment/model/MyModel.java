/*
 * Copyright 2015-2017 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.webdeployment.model;

import com.canoo.platform.remoting.DolphinBean;
import com.canoo.platform.remoting.Property;

/**
 * Defines the model of the MVC that is defined in this example. When using Dolphin Platform a view-controller-pair will
 * share one model that is automatially synchronized between the view on the client and teh controller on the server.
 * The model will automatically instanziated and managed by the platform and its lifecylce is bound to lifecylce of the view.
 *
 * This basic model only contains one value. When defining models with Dolphin Platform all attributes must be
 * defined as observable properties. To do so Dolphin Platform provides the {@link Property} interface. A property
 * can contain a primitive datatype like String or int. In addition a property can contain another Dolphin Platform
 * model. By doing so a hierarchy can be defined. In addition Dolphin Platform contains extensions for the Java
 * collections API like an observable list. This types can be used in model classes, too.
 */
@DolphinBean
public class MyModel {

    /**
     * The value that is part of this model
     */
    private Property<String> value;

    /**
     * This method returns the property. By using this method you can easily add an observer to the property
     * @return thze property
     */
    public Property<String> valueProperty() {
        return value;
    }

    /**
     * This is a convenience method that defines a getter based on the property.
     * @return the internal value of the property
     */
    public String getValue() {
        return valueProperty().get();
    }

    /**
     * This is a convenience method that defines a setter based on the property.
     * @param value the new internal value of the property
     */
    public void setValue(String value) {
        valueProperty().set(value);
    }
}
