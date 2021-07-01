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

package com.ibm.cloud.appconfiguration.sdk.configurations.models;

import com.ibm.cloud.appconfiguration.sdk.configurations.internal.ConfigConstants;
import com.ibm.cloud.appconfiguration.sdk.core.AppConfigException;
import com.ibm.cloud.appconfiguration.sdk.core.BaseLogger;
import com.ibm.cloud.appconfiguration.sdk.configurations.ConfigurationHandler;
import com.ibm.cloud.appconfiguration.sdk.configurations.internal.Validators;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Feature class.
 */
public class Feature {

    private Boolean enabled;
    private String name;
    private String featureId;
    private JSONArray segmentRules;
    private ConfigurationType type;
    private Object disabledValue;
    private Object enabledValue;

    /**
     * @param featureData features JSON object that contains all the features
     */
    public Feature(JSONObject featureData) {

        try {
            this.enabled = featureData.getBoolean(ConfigConstants.ENABLED);
            this.name = featureData.getString(ConfigConstants.NAME);
            this.featureId = featureData.getString(ConfigConstants.FEATURE_ID);
            this.segmentRules = featureData.getJSONArray(ConfigConstants.SEGMENT_RULES);
            this.type = ConfigurationType.valueOf(featureData.getString(ConfigConstants.TYPE));
            this.disabledValue = featureData.get(ConfigConstants.DISABLED_VALUE);
            this.enabledValue = featureData.get(ConfigConstants.ENABLED_VALUE);

        } catch (Exception e) {
            AppConfigException.logException("Feature", "Constructor", e, new Object[] { "Invalid action in Feature class."});
        }
    }

    /**
     * Return the enabled status of the feature.
     *
     * @return {@code true} or {@code false}
     */
    public Boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Get the Feature name.
     *
     * @return the feature name
     */
    public String getFeatureName() {
        return this.name;
    }

    /**
     * Get the Feature Id.
     *
     * @return the feature id
     */
    public String getFeatureId() {
        return this.featureId;
    }

    /**
     * Get the enabled value of the feature.
     *
     * @return enabled value
     */
    public Object getEnabledValue() {
        return enabledValue;
    }

    /**
     * Get the disabled value of the feature.
     *
     * @return disabled value
     */
    public Object getDisabledValue() {
        return disabledValue;
    }

    /**
     * Get the feature data type.
     *
     * @return string named BOOLEAN/STRING/NUMERIC
     */
    public ConfigurationType getFeatureDataType() {
        return type;
    }

    /**
     * Get the rules of the Segment targeted.
     *
     * @return segment rules
     */
    public JSONArray getSegmentRules() {
        return segmentRules;
    }

    /**
     * Get the evaluated value of the feature.
     *
     * @param entityId id of the entity
     * @param entityAttributes entity attributes JSON object
     * @return evaluated value
     */
    public Object getCurrentValue(String entityId, JSONObject entityAttributes) {

        if (!Validators.validateString(entityId)) {
            BaseLogger.error("A valid entity id should be passed for this method.");
            return null;
        }
        ConfigurationHandler configurationHandler = ConfigurationHandler.getInstance();
        return configurationHandler.featureEvaluation(this, entityId, entityAttributes);

    }
}
