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
package org.opendolphin.core.comm

import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.comm.OnFinishedHandler
import org.opendolphin.core.server.DefaultServerDolphin
import org.opendolphin.core.server.ServerDolphin
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.CommandHandler

import java.util.concurrent.TimeUnit

class DeletePresentationModelTests extends GroovyTestCase {

    volatile TestInMemoryConfig context
    DefaultServerDolphin serverDolphin
    ClientDolphin clientDolphin

    private final class TriggerDeleteCommand extends Command {}


    @Override
    protected void setUp() {
        context = new TestInMemoryConfig()
        serverDolphin = context.serverDolphin
        clientDolphin = context.clientDolphin
    }

    @Override
    protected void tearDown() {
        assert context.done.await(2, TimeUnit.SECONDS)
    }

    public <T extends Command> void registerAction(ServerDolphin serverDolphin, Class<T> commandClass, CommandHandler<T> handler) {
        serverDolphin.getServerConnector().register(new DolphinServerAction() {

            @Override
            void registerIn(ActionRegistry registry) {
                registry.register(commandClass, handler);
            }
        });
    }
    
    void testCreateAndDeletePresentationModel() {
        // create the pm
        String modelId = 'modelId'
        def model = clientDolphin.getModelStore().createModel(modelId, null, new ClientAttribute("someAttribute", "someValue"));
        // sanity check: we have a least the client model store listening to changes of someAttribute
        assert model.getAttribute("someAttribute").propertyChangeListeners
        // the model is in the client model store
        def found = clientDolphin.getModelStore().findPresentationModelById(modelId)
        assert model == found
        // ... and in the server model store after roundtrip
        clientDolphin.sync {
            assert serverDolphin.getModelStore().findPresentationModelById(modelId)
        }
        // when we now delete the pm
        clientDolphin.getModelStore().delete(model)
        // ... it is no longer in the client model store
        assert !clientDolphin.getModelStore().findPresentationModelById(modelId)
        // ... all listeners have been detached from model and all its attributes
        assert ! model.getPropertyChangeListeners()
        // what is allowed to remain is the "detached" model still listening to its own attribute changes
        model.attributes*.propertyChangeListeners.flatten()*.listener.each {
            assert (it.toString() =~ "PresentationModel")
            // todo dk: the below should also work but there is some weird boxing going on
            // assert it.is(model)
        }
        // the model is also gone from the server model store
        clientDolphin.sync {
            assert !serverDolphin.getModelStore().findPresentationModelById(modelId)
            context.assertionsDone()
        }
    }

    void testCreateAndDeletePresentationModelFromServer() {
        // create the pm
        String modelId = 'modelId'
        def model = clientDolphin.getModelStore().createModel(modelId, null, new ClientAttribute("someAttribute", "someValue"))
        // the model is in the client model store
        def found = clientDolphin.getModelStore().findPresentationModelById(modelId)
        assert model == found
        // ... and in the server model store after roundtrip
        clientDolphin.sync {
            assert serverDolphin.getModelStore().findPresentationModelById(modelId)
        }

        registerAction(serverDolphin, TriggerDeleteCommand.class, new CommandHandler<TriggerDeleteCommand>() {

            @Override
            void handleCommand(TriggerDeleteCommand command) {
                serverDolphin.getModelStore().currentResponse.add(new DeletePresentationModelCommand(modelId));
            }
        });
        // when we now delete the pm
        clientDolphin.getClientConnector().send(new TriggerDeleteCommand(), new OnFinishedHandler() {
            @Override
            void onFinished() {
                clientDolphin.sync {
                    // ... it is no longer in the client model store
                    assert !clientDolphin.getModelStore().findPresentationModelById(modelId)
                }
                clientDolphin.sync {
                    // the model is also gone from the server model store
                    assert !serverDolphin.getModelStore().findPresentationModelById(modelId)
                    // we are done
                    context.assertionsDone()
                }
            }
        });
    }

}