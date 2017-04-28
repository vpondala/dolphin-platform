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
package com.canoo.dolphin.server.config;

import org.testng.annotations.Test;

import java.util.logging.Level;

import static org.testng.Assert.*;

public class ConfigurationFileLoaderTest {

    @Test
    public void testConfigLoad() {
        try {
            //given:
            DolphinPlatformConfiguration configuration = ConfigurationFileLoader.loadConfiguration();

            //then:
            assertEquals(configuration.isUseCrossSiteOriginFilter(), false);
            assertEquals(configuration.getDolphinPlatformServletMapping(), "/test");
            assertEquals(configuration.getOpenDolphinLogLevel(), Level.FINER);
        } catch (Exception e) {
            fail("Error in test", e);
        }
    }

    @Test
    public void testConfigurationProvider() {
        try {
            //given:
            DolphinPlatformConfiguration configuration = ConfigurationFileLoader.loadConfiguration();

            //then:
            assertEquals(configuration.getProperty(TestConfigurationProvider.PROPERTY_1_NAME), TestConfigurationProvider.PROPERTY_1_VALUE);
            assertEquals(configuration.getProperty(TestConfigurationProvider.PROPERTY_2_NAME), TestConfigurationProvider.PROPERTY_2_VALUE);
            assertEquals(configuration.getProperty(TestConfigurationProvider.PROPERTY_3_NAME), TestConfigurationProvider.PROPERTY_3_VALUE);
            assertEquals(configuration.getProperty(AdditionalTestConfigurationProvider.PROPERTY_NAME), AdditionalTestConfigurationProvider.PROPERTY_VALUE);
        } catch (Exception e) {
            fail("Error in test", e);
        }
    }

    @Test
    public void testConfigurationProviderDoNotOverwrite() {
        try {
            //given:
            DolphinPlatformConfiguration configuration = ConfigurationFileLoader.loadConfiguration();

            //then:
            assertEquals(configuration.isUseCrossSiteOriginFilter(), false);
            assertEquals(configuration.getProperty(DolphinPlatformConfiguration.USE_CROSS_SITE_ORIGIN_FILTER), "false");
        } catch (Exception e) {
            fail("Error in test", e);
        }
    }

    @Test
    public void testNullPropertyWillNotBeAdded() {
        try {
            //given:
            DolphinPlatformConfiguration configuration = ConfigurationFileLoader.loadConfiguration();

            //when:
            configuration.setProperty("test-key", null);

            //then:
            assertEquals(configuration.getProperty("test-key"), null);
            assertEquals(configuration.getProperty("test-key", "a"), "a");
            assertEquals(configuration.getProperty(TestConfigurationProvider.PROPERTY_3_NAME), null);
            assertFalse(configuration.getPropertyKeys().contains("test-key"));
            assertFalse(configuration.getPropertyKeys().contains(TestConfigurationProvider.PROPERTY_3_NAME));
        } catch (Exception e) {
            fail("Error in test", e);
        }
    }

    @Test
    public void testNullPropertyWillNotRemoveOldValue() {
        try {
            //given:
            DolphinPlatformConfiguration configuration = ConfigurationFileLoader.loadConfiguration();

            //when:
            configuration.setProperty("test-key", "a");
            configuration.setProperty("test-key", null);

            //then:
            assertEquals(configuration.getProperty("test-key"), "a");
            assertEquals(configuration.getProperty("test-key", "b"), "a");
            assertTrue(configuration.getPropertyKeys().contains("test-key"));
        } catch (Exception e) {
            fail("Error in test", e);
        }
    }
}
