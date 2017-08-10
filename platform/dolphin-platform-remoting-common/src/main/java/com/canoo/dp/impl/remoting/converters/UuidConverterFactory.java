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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.canoo.dp.impl.remoting.converters;

import com.canoo.platform.remoting.spi.converter.Converter;
import com.canoo.platform.remoting.spi.converter.ValueConverterException;
import java.util.UUID;

/**
 *
 * @author onn
 */
public class UuidConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_UUID = 14;

    private final static Converter CONVERTER = new AbstractStringConverter<UUID>() {
        @Override
        public UUID convertFromDolphin(String value) throws ValueConverterException {
            try {
                return value == null ? null : UUID.fromString(value);
            } catch (Exception ex) {
                throw new ValueConverterException("Unable to parse UUID: " + value, ex);
            }
        }

        @Override
        public String convertToDolphin(UUID value) throws ValueConverterException {
            if (value == null)
                return null;
            
            return value.toString();
        }
    };

    @Override
    public boolean supportsType(Class<?> cls) {
        return UUID.class.equals(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_UUID;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }

}
