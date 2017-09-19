package com.canoo.dolphin.integration;

import com.canoo.platform.remoting.client.ClientContext;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by hendrikebbers on 25.08.17.
 */
public class ClientIdTest extends AbstractIntegrationTest {

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testThatClientIdIsNotNull(String containerType, String endpoint) {
        ClientContext context = connect(endpoint);
        Assert.assertNotNull(context.getClientId());
    }

}
