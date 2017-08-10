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
import com.canoo.dp.impl.remoting.BeanDefinitionException;
import com.canoo.dp.impl.remoting.BeanRepository;
import com.canoo.dp.impl.remoting.EventDispatcher;
import com.canoo.impl.server.util.AbstractDolphinBasedTest;
import com.canoo.impl.server.util.ChildModel;
import com.canoo.impl.server.util.ListReferenceModel;
import com.canoo.impl.server.util.SimpleAnnotatedTestModel;
import com.canoo.impl.server.util.SimpleTestModel;
import com.canoo.impl.server.util.SingleReferenceModel;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class TestModelDeletion extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository beanRepository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, beanRepository, dispatcher);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        beanRepository.delete(model);

        List<ServerPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType(SimpleAnnotatedTestModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.getModelStore().listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(beanRepository.isManaged(model), is(false));
    }

    @Test
    public void testWithSimpleModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository beanRepository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, beanRepository, dispatcher);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        beanRepository.delete(model);

        List<ServerPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.getModelStore().listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(beanRepository.isManaged(model), is(false));
    }

    @Test
    public void testWithSingleReferenceModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository beanRepository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, beanRepository, dispatcher);

        SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        beanRepository.delete(model);

        List<ServerPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType(SingleReferenceModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.getModelStore().listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(beanRepository.isManaged(model), is(false));
    }

    @Test(expectedExceptions = BeanDefinitionException.class)
    public void testWithWrongModelType() {
        final ServerDolphin dolphin = createServerDolphin();
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository beanRepository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, beanRepository, dispatcher);

        beanRepository.delete("I'm a String");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testWithNull() {
        final ServerDolphin dolphin = createServerDolphin();
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository beanRepository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, beanRepository, dispatcher);

        beanRepository.delete(null);
    }

    @Test
    public void testWithListReferenceModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository beanRepository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, beanRepository, dispatcher);

        ListReferenceModel model = manager.create(ListReferenceModel.class);

        beanRepository.delete(model);

        List<ServerPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.getModelStore().listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));    //Dolphin Class Repository wurde bereits angelegt

        assertThat(beanRepository.isManaged(model), is(false));
    }

    @Test
    public void testWithInheritedModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository beanRepository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, beanRepository, dispatcher);

        ChildModel model = manager.create(ChildModel.class);

        beanRepository.delete(model);

        List<ServerPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType(ChildModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.getModelStore().listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(beanRepository.isManaged(model), is(false));
    }


}

