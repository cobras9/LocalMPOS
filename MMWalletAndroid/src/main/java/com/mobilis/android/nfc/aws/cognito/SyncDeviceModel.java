/*
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.mobilis.android.nfc.aws.cognito;


public class SyncDeviceModel {
    private String terminalId = null;
    private String globalMerchantId = null;
    private String imei = null;
    private String appVersion = null;
    private String geo = null;

    /**
     * Gets terminalId
     *
     * @return terminalId
     **/
    public String getTerminalId() {
        return terminalId;
    }

    /**
     * Sets the value of terminalId.
     *
     * @param terminalId the new value
     */
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getGlobalMerchantId() {
        return globalMerchantId;
    }

    public void setGlobalMerchantId(String globalMerchantId) {
        this.globalMerchantId = globalMerchantId;
    }

    /**
     * Gets imei
     *
     * @return imei
     **/
    public String getImei() {
        return imei;
    }

    /**
     * Sets the value of imei.
     *
     * @param imei the new value
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets appVersion
     *
     * @return appVersion
     **/
    public String getAppVersion() {
        return appVersion;
    }

    /**
     * Sets the value of appVersion.
     *
     * @param appVersion the new value
     */
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    /**
     * Gets geo
     *
     * @return geo
     **/
    public String getGeo() {
        return geo;
    }

    /**
     * Sets the value of geo.
     *
     * @param geo the new value
     */
    public void setGeo(String geo) {
        this.geo = geo;
    }

}
