/**
 * Copyright 2021 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.cloud.appconfiguration.sdk.test;

import com.ibm.cloud.appconfiguration.sdk.AppConfiguration;
import com.ibm.cloud.appconfiguration.sdk.configurations.ConfigurationUpdateListener;
import com.ibm.cloud.appconfiguration.sdk.configurations.models.Feature;
import com.ibm.cloud.appconfiguration.sdk.configurations.models.Property;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class AppConfigurationTest {

    @Test
    public void testConfigurationHandler() throws InterruptedException {

        AppConfiguration appConfiguration = AppConfiguration.getInstance();
        AppConfiguration.overrideServerHost = "overrideServerHost";
        assertTrue(AppConfiguration.overrideServerHost == "overrideServerHost");

        appConfiguration.setContext("collectionId", "environmentId");
        appConfiguration.setContext("collectionId", "environmentId","",true);


        appConfiguration.init("","guid","apikey");
        appConfiguration.init("region","","apikey");

        appConfiguration.init("region","guid","");
        appConfiguration.init("region","guid","apikey");

        appConfiguration.setContext("", "");
        appConfiguration.setContext("collectionId", "environmentId");

        appConfiguration.fetchConfigurations();

        final Boolean[] onData = {false};
        appConfiguration.registerConfigurationUpdateListener(new ConfigurationUpdateListener() {
            @Override
            public void onConfigurationUpdate() {
                onData[0] = true;
            }
        });


        Path resourceDirectory = Paths.get("src","test","resources");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();

        appConfiguration.enableDebug(true);
        assertTrue(appConfiguration.getFeatures().size() >= 0);
        assertTrue(appConfiguration.getProperties().size() >= 0);

        assertNotNull(appConfiguration.getProperty("numericproperty"));
        assertNotNull(appConfiguration.getFeature("defaultfeature"));

        assertNull(appConfiguration.getProperty("numericproperty1"));
        assertNull(appConfiguration.getFeature("defaultfeature2"));

        appConfiguration.setContext("collectionId", "environmentId", absolutePath + "/user.json",false);
        appConfiguration.fetchConfigurations();


        TimeUnit.SECONDS.sleep(3);
        assertTrue(onData[0]);

        assertTrue(appConfiguration.getFeatures().size() == 3);
        Feature feature = appConfiguration.getFeature("defaultfeature");
        String idVal = feature.getFeatureId();

        assertTrue(idVal.equals("defaultfeature"));
        assertTrue(appConfiguration.getProperties().size() == 1);
        Property property = appConfiguration.getProperty("numericproperty");
        assertTrue(property.getPropertyId().equals("numericproperty"));

        JSONObject attributes = new JSONObject();
        attributes.put("email", "dev@tester.com");

        assertEquals("Welcome",feature.getCurrentValue("pqvr", attributes));
        assertEquals(81,property.getCurrentValue("pqvr", attributes));
    }
}
