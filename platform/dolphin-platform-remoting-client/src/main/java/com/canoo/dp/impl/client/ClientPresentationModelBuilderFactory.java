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
package com.canoo.dp.impl.client;

import com.canoo.dp.impl.remoting.PresentationModelBuilderFactory;
import com.canoo.dp.impl.remoting.PresentationModelBuilder;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.ClientPresentationModel;

public class ClientPresentationModelBuilderFactory implements PresentationModelBuilderFactory<ClientPresentationModel> {

    private final ClientModelStore clientModelStore;

    public ClientPresentationModelBuilderFactory(final ClientModelStore clientModelStore) {
        this.clientModelStore = clientModelStore;
    }

    @Override
    public PresentationModelBuilder createBuilder() {
        return new ClientPresentationModelBuilder(clientModelStore);
    }
}
