package org.opendolphin.core;

public class TestStoreListener implements ModelStoreListener {

    private ModelStoreEvent event;

    @Override
    public void modelStoreChanged(ModelStoreEvent event) {
        this.event = event;
    }

    public ModelStoreEvent getEvent() {
        return event;
    }

    public void setEvent(ModelStoreEvent event) {
        this.event = event;
    }
}
