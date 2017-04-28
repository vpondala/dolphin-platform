package org.opendolphin.core.server;

import org.junit.Assert;
import org.junit.Test;

public class ServerAttributeTest {

    @Test
    public void testSetIdOnce() {
        ServerAttribute attribute = new ServerAttribute("a", 0);
        Assert.assertTrue(attribute.getId().endsWith("S"));
    }

}
