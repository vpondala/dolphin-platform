package com.canoo.dolphin.integration;

import com.canoo.dolphin.client.ClientContext;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConnectionTest extends AbstractIntegrationTest {

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Tests if the client API can create a connection to the server")
    public void testConnection(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            Assert.assertNotNull(context);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create connection for " + containerType, e);
        }
    }
}
