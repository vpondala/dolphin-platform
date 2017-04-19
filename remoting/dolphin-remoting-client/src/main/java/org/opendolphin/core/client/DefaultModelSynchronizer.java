package org.opendolphin.core.client;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.client.comm.AbstractClientConnector;
import org.opendolphin.core.comm.*;
import org.opendolphin.util.Provider;

import java.beans.PropertyChangeEvent;

public class DefaultModelSynchronizer implements ModelSynchronizer {

    private final Provider<AbstractClientConnector> connectionProvider;

    public DefaultModelSynchronizer(Provider<AbstractClientConnector> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void onAdded(final ClientPresentationModel model) {
        final Command command = CreatePresentationModelCommand.makeFrom(model);
        send(command);
    }

    @Override
    public void onDeleted(final ClientPresentationModel model) {
        final Command command = new DeletedPresentationModelNotification(model.getId());
        send(command);
    }

    @Override
    public void onPropertyChanged(final PropertyChangeEvent evt) {
        final Command command = new ValueChangedCommand(((Attribute) evt.getSource()).getId(), evt.getOldValue(), evt.getNewValue());
        send(command);
    }

    @Override
    public void onMetadataChanged(final PropertyChangeEvent evt) {
        final Command command = new ChangeAttributeMetadataCommand(((Attribute) evt.getSource()).getId(), evt.getPropertyName(), evt.getNewValue());
        send(command);
    }

    private void send(Command command) {
        AbstractClientConnector clientConnector = connectionProvider.get();
        if(clientConnector == null) {
            throw new IllegalStateException("No connection defined!");
        }
        clientConnector.send(command);
    }
}
