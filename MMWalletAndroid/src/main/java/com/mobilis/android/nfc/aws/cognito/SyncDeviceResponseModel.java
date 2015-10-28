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


public class SyncDeviceResponseModel {
    private Integer sync = null;
    private Boolean inconsistent = null;

    /**
     * Gets sync
     *
     * @return sync
     **/
    public Integer getSync() {
        return sync;
    }

    /**
     * Sets the value of sync.
     *
     * @param sync the new value
     */
    public void setSync(Integer sync) {
        this.sync = sync;
    }

    /**
     * Gets inconsistent
     *
     * @return inconsistent
     **/
    public Boolean getInconsistent() {
        return inconsistent;
    }

    /**
     * Sets the value of inconsistent.
     *
     * @param inconsistent the new value
     */
    public void setInconsistent(Boolean inconsistent) {
        this.inconsistent = inconsistent;
    }

}
