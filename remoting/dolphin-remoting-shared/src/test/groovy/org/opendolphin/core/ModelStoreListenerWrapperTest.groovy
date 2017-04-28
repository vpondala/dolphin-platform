package org.opendolphin.core;

import org.junit.Assert;
import org.junit.Test;

public class ModelStoreListenerWrapperTest {

    @Test
    public void testEquals() {

        //given:
        ModelStoreListener listener = new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent event) {

            }

        };
        ModelStoreListenerWrapper wrapper = new ModelStoreListenerWrapper("no-type", listener);


        //then:
        Assert.assertEquals(new ModelStoreListenerWrapper("no-type", listener), wrapper);
        Assert.assertNotEquals(new ModelStoreListenerWrapper("other-type", listener), wrapper);
    }

}
