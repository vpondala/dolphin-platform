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
package com.canoo.impl.server;

import com.canoo.platform.remoting.BeanManager;
import com.canoo.dp.impl.remoting.BeanRepository;
import com.canoo.dp.impl.remoting.EventDispatcher;
import com.canoo.impl.server.util.AbstractDolphinBasedTest;
import com.canoo.impl.server.util.SimpleAnnotatedTestModel;
import com.canoo.impl.server.util.SimpleTestModel;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class TestDeleteAll extends AbstractDolphinBasedTest {

    @Test
    public void testWithSimpleModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository beanRepository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, beanRepository, dispatcher);

        SimpleTestModel model1 = manager.create(SimpleTestModel.class);
        SimpleTestModel model2 = manager.create(SimpleTestModel.class);
        SimpleTestModel model3 = manager.create(SimpleTestModel.class);

        SimpleAnnotatedTestModel wrongModel = manager.create(SimpleAnnotatedTestModel.class);

        for (final Object bean : manager.findAll(SimpleTestModel.class)) {
            beanRepository.delete(bean);
        }

        assertThat(beanRepository.isManaged(model1), is(false));
        assertThat(beanRepository.isManaged(model2), is(false));
        assertThat(beanRepository.isManaged(model3), is(false));
        assertThat(beanRepository.isManaged(wrongModel), is(true));

        List<ServerPresentationModel> testModels = dolphin.getModelStore().findAllPresentationModelsByType("com.canoo.dolphin.server.util.SimpleTestModel");
        assertThat(testModels, hasSize(0));

    }
}

