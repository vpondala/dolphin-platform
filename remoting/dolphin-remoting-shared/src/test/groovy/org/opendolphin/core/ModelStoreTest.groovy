package org.opendolphin.core

import org.junit.Assert
import org.junit.Test

public class ModelStoreTest {

    @Test
    public void testSimpleAccessAndStoreEventListening() {

        //given:

        BasePresentationModel parent = new BasePresentationModel("0", new ArrayList());
        parent.setPresentationModelType("parent");
        BasePresentationModel child1 = new BasePresentationModel("1", new ArrayList());
        TestStoreListener storeListener = new TestStoreListener();
        TestStoreListener parentStoreListener = new TestStoreListener();
        ModelStore modelStore = new ModelStore();
        modelStore.addModelStoreListener(storeListener);
        modelStore.addModelStoreListener("parent", parentStoreListener);
        modelStore.add(parent);

        //then:

        Assert.assertNotNull(storeListener.getEvent());
        Assert.assertNotNull(storeListener.getEvent().toString());
        Assert.assertNotNull(storeListener.getEvent().hashCode());
        Assert.assertEquals(parent, storeListener.getEvent().getPresentationModel());
        Assert.assertEquals(ModelStoreEvent.Type.ADDED, storeListener.getEvent().getType());
        Assert.assertNotNull(parentStoreListener.getEvent());
        Assert.assertEquals(parent, parentStoreListener.getEvent().getPresentationModel());
        Assert.assertEquals(ModelStoreEvent.Type.ADDED, parentStoreListener.getEvent().getType());


        //when:

        storeListener.setEvent(null);
        parentStoreListener.setEvent(null);
        modelStore.add(child1);

        //then:

        Assert.assertNotNull(storeListener.getEvent());
        Assert.assertEquals(child1, storeListener.getEvent().getPresentationModel());
        Assert.assertEquals(ModelStoreEvent.Type.ADDED, storeListener.getEvent().getType());
        Assert.assertNull(parentStoreListener.getEvent());
    }

}

