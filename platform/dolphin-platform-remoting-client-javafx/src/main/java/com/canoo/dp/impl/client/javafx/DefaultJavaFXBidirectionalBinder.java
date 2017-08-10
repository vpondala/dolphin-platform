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
package com.canoo.dp.impl.client.javafx;

import com.canoo.platform.remoting.client.javafx.binding.BidirectionalConverter;
import com.canoo.platform.remoting.Binding;
import com.canoo.platform.remoting.client.javafx.binding.JavaFXBidirectionalBinder;
import com.canoo.platform.remoting.Property;
import javafx.beans.value.ChangeListener;

public class DefaultJavaFXBidirectionalBinder<S> extends DefaultJavaFXBinder<S> implements JavaFXBidirectionalBinder<S> {

    private final javafx.beans.property.Property<S> javaFxProperty;

    public DefaultJavaFXBidirectionalBinder(final javafx.beans.property.Property<S> javaFxProperty) {
        super(javaFxProperty);
        this.javaFxProperty = javaFxProperty;
    }

    @Override
    public <T> Binding bidirectionalTo(final Property<T> property, BidirectionalConverter<T, S> converter) {
        final Binding unidirectionalBinding = to(property, converter);

        final ChangeListener<S> listener = (obs, oldVal, newVal) -> property.set(converter.convertBack(newVal));
        javaFxProperty.addListener(listener);
        return () -> {
            javaFxProperty.removeListener(listener);
            unidirectionalBinding.unbind();
        };
    }

}

