package com.canoo.dolphin.client.javafx.impl.numeric;

import javafx.beans.property.FloatProperty;

/**
 * Created by hendrikebbers on 29.09.15.
 */
public class FloatJavaFXBidirectionalBinder extends AbstractNumericJavaFXBidirectionalBinder<Float> {

    public FloatJavaFXBidirectionalBinder(final FloatProperty javaFxProperty) {
        super(javaFxProperty);
    }

    @Override
    public Float convertNumber(Number value) {
        if (value == null) {
            return null;
        }
        return value.floatValue();
    }
}