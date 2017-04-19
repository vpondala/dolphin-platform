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
package com.canoo.dolphin.client;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.client.util.*;
import com.canoo.dolphin.impl.BeanDefinitionException;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.EventDispatcher;
import mockit.Mocked;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.AbstractClientConnector;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestModelDeletion extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel(@Mocked AbstractClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository repository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, repository, dispatcher);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        repository.delete(model);

        List<ClientPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType("simple_test_model");
        assertThat(dolphinModels, empty());

        Collection<ClientPresentationModel> allDolphinModels = dolphin.getModelStore().listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(repository.isManaged(model), is(false));
    }

    @Test
    public void testWithSimpleModel(@Mocked AbstractClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository repository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, repository, dispatcher);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        repository.delete(model);

        List<ClientPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType(SimpleTestModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ClientPresentationModel> allDolphinModels = dolphin.getModelStore().listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(repository.isManaged(model), is(false));
    }

    @Test(expectedExceptions = BeanDefinitionException.class)
    public void testWithWrongModelType(@Mocked AbstractClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository repository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, repository, dispatcher);

        repository.delete("I'm a String");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testWithNull(@Mocked AbstractClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository repository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, repository, dispatcher);

        repository.delete(null);
    }

    @Test
    public void testWithSingleReferenceModel(@Mocked AbstractClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository repository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, repository, dispatcher);

        SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        repository.delete(model);

        List<ClientPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType(SingleReferenceModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ClientPresentationModel> allDolphinModels = dolphin.getModelStore().listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(repository.isManaged(model), is(false));
    }

    @Test
    public void testWithListReferenceModel(@Mocked AbstractClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository repository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, repository, dispatcher);

        ListReferenceModel model = manager.create(ListReferenceModel.class);

        repository.delete(model);

        List<ClientPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType(ListReferenceModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ClientPresentationModel> allDolphinModels = dolphin.getModelStore().listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));    //Dolphin Class Repository wurde bereits angelegt

        assertThat(repository.isManaged(model), is(false));
    }

    @Test
    public void testWithInheritedModel(@Mocked AbstractClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository repository = createBeanRepository(dolphin, dispatcher);
        final BeanManager manager = createBeanManager(dolphin, repository, dispatcher);

        ChildModel model = manager.create(ChildModel.class);

        repository.delete(model);

        List<ClientPresentationModel> dolphinModels = dolphin.getModelStore().findAllPresentationModelsByType(ChildModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ClientPresentationModel> allDolphinModels = dolphin.getModelStore().listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(repository.isManaged(model), is(false));
    }


}

