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
package org.opendolphin.core.client.comm;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.comm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ClientResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ClientResponseHandler.class);

    private final ClientModelStore clientModelStore;

    private boolean strictMode = true;

    public ClientResponseHandler(final ClientModelStore clientModelStore) {
        this.clientModelStore = Objects.requireNonNull(clientModelStore);
    }

    public void dispatchHandle(Command command) {
        if (command instanceof DeletePresentationModelCommand) {
            handleDeletePresentationModelCommand((DeletePresentationModelCommand) command);
        } else if (command instanceof CreatePresentationModelCommand) {
            handleCreatePresentationModelCommand((CreatePresentationModelCommand) command);
        } else if (command instanceof ValueChangedCommand) {
            handleValueChangedCommand((ValueChangedCommand) command);
        } else if (command instanceof AttributeMetadataChangedCommand) {
            handleAttributeMetadataChangedCommand((AttributeMetadataChangedCommand) command);
        } else {
            LOG.error("C: cannot handle unknown command '{}'", command );
        }

    }

    private void handleDeletePresentationModelCommand(DeletePresentationModelCommand serverCommand) {
        ClientPresentationModel model = clientModelStore.findPresentationModelById(serverCommand.getPmId());
        if (model == null) {
            return;
        }
        clientModelStore.delete(model);
    }

    private void handleCreatePresentationModelCommand(CreatePresentationModelCommand serverCommand) {
        if (clientModelStore.containsPresentationModel(serverCommand.getPmId())) {
            throw new IllegalStateException("There already is a presentation model with id '" + serverCommand.getPmId() + "' known to the client.");
        }

        List<ClientAttribute> attributes = new ArrayList<ClientAttribute>();
        for (Map<String, Object> attr : serverCommand.getAttributes()) {

            Object propertyName = attr.get("propertyName");
            Object value = attr.get("value");
            Object qualifier = attr.get("qualifier");
            Object id = attr.get("id");

            ClientAttribute attribute = new ClientAttribute(propertyName != null ? propertyName.toString() : null, value, qualifier != null ? qualifier.toString() : null);
            if (id != null && id.toString().endsWith("S")) {
                attribute.setId(id.toString());
            }

            attributes.add(attribute);
        }

        ClientPresentationModel model = new ClientPresentationModel(serverCommand.getPmId(), attributes);
        model.setPresentationModelType(serverCommand.getPmType());
        if (serverCommand.isClientSideOnly()) {
            model.setClientSideOnly(true);
        }

        clientModelStore.add(model);
        clientModelStore.updateQualifiers(model);
    }

    private void handleValueChangedCommand(ValueChangedCommand serverCommand) {
        Attribute attribute = clientModelStore.findAttributeById(serverCommand.getAttributeId());
        if (attribute == null) {
            LOG.warn("C: attribute with id '{}' not found, cannot update old value '{}' to new value '{}'", serverCommand.getAttributeId() ,serverCommand.getOldValue(), serverCommand.getNewValue() );
            return;
        }

        if (attribute.getValue() == null && serverCommand.getNewValue() == null || (attribute.getValue() != null && serverCommand.getNewValue() != null && attribute.getValue().equals(serverCommand.getNewValue()))) {
            return;
        }


        if (strictMode && ((attribute.getValue() == null && serverCommand.getOldValue() != null) || (attribute.getValue() != null && serverCommand.getOldValue() == null) || (attribute.getValue() != null && !attribute.getValue().equals(serverCommand.getOldValue())))) {
            // todo dk: think about sending a RejectCommand here to tell the server about a possible lost update
            LOG.warn("C: attribute with id '{}' and value '{}' cannot be set to new value '{}' because the change was based on an outdated old value of '{}'.", serverCommand.getAttributeId(), attribute.getValue(), serverCommand.getNewValue(), serverCommand.getOldValue());
            return;
        }

        LOG.info("C: updating '{}' id '{}' from '{}' to '{}' " + attribute.getPropertyName(), serverCommand.getAttributeId(), attribute.getValue(), serverCommand.getNewValue());
        attribute.setValue(serverCommand.getNewValue());
        return;
    }

    private void handleAttributeMetadataChangedCommand(AttributeMetadataChangedCommand serverCommand) {
        ClientAttribute attribute = clientModelStore.findAttributeById(serverCommand.getAttributeId());
        if (attribute == null) {
            return;
        }

        if (serverCommand.getMetadataName() != null && serverCommand.getMetadataName().equals(Attribute.VALUE_NAME)) {
            attribute.setValue(serverCommand.getValue());
        }

        if (serverCommand.getMetadataName() != null && serverCommand.getMetadataName().equals(Attribute.QUALIFIER_NAME)) {
            if (serverCommand.getValue() == null) {
                attribute.setQualifier(null);
            } else {
                attribute.setQualifier(serverCommand.getValue().toString());
            }
        }
    }

    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

}
