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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * A method of a Dolphin controller (see {@link DolphinController}) that is annotated by this
 * annotation defines a Dolphin action that can be called on the client by using the controller proxy.
 * <br>
 * <center><img src="doc-files/invoke-action.png" alt="server controller action is invoked by a client view"></center>
 *</p>
 * <p>
 * Example:
 *<blockquote>
 * <pre>
 *     {@literal @}DolphinControllerInfo("my-controller")
 *     public class MyController {
 *
 *         {@literal @}DolphinAction("my-action")
 *         private void doSomeAction() { . . . };
 *     }
 * </pre>
 * </blockquote>
 *</p>
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface DolphinAction {

    /**
     * Defines the name of the action
     * @return name of the action
     */
    String value() default "";
}
