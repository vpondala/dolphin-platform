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
package com.canoo.platform.remoting.server;

import com.canoo.platform.remoting.DolphinBean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * Can be used in a dolphin controller (see {@link DolphinController}) to
 * define and inject the model of the controller (MVC group). In each dolphin controller only one field can
 * be annoted by {@link DolphinModel} and the field type must match the model
 * definition of the Dolphin Platform (see {@link DolphinBean} for a detailed definition).
 *</p>
 * <p>
 * As defined by the Dolphin Platform all models will be automatically synchronized between client and server. See
 * {@link DolphinBean} for more details.
 * <br>
 * <center><img src="doc-files/model-sync.png" alt="model is synchronized between client and server"></center>
 *</p>
 * <p>
 * A model that is injected by using {@link DolphinModel} will automatically be create when the
 * server controller is created and will be destroyed with the controller. By doing so the complete MVC group (shared
 * model, controller on server side and the view on client side) will have the same lifecycle and the model can easily
 * be accessed from client and server.
 *</p>
 * <p>
 * Example:
 *<blockquote>
 * <pre>
 *     {@literal @}DolphinController("my-controller")
 *     public class MyController {
 *
 *          {@literal @}DolphinModel
 *          private MyModel model;
 *
 *         {@literal @}PostContruct()
 *         private void init() {
 *             model.setViewTitle("My View");
 *         };
 *     }
 * </pre>
 * </blockquote>
 *</p>
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.FIELD)
public @interface DolphinModel {
}
