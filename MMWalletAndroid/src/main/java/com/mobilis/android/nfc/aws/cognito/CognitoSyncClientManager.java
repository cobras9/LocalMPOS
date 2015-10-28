package com.mobilis.android.nfc.aws.cognito;

/**
 * Created by lewischao on 22/07/15.
 */
/**
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.AWSAbstractCognitoIdentityProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.mobilis.android.nfc.R;

import java.util.HashMap;
import java.util.Map;

public class CognitoSyncClientManager {

    private static final String TAG = "ClientManager";

    /**
     * Enter here the Identity Pool associated with your app and the AWS
     * region where it belongs. Get this information from the AWS console.
     */

    private static String IDENTITY_POOL_ID = "";//"us-east-1:424ea457-2d99-4efd-927f-bcadad924e0c";
    private static final Regions REGION = Regions.US_EAST_1;

    private static CognitoSyncManager syncClient;
    protected static CognitoCachingCredentialsProvider credentialsProvider = null;
    protected static AWSAbstractCognitoIdentityProvider developerIdentityProvider;

    /**
     * Set this flag to true for using developer authenticated identities
     */
    private static boolean useDeveloperAuthenticatedIdentities = true;


    /**
     * Initializes the Cognito Identity and Sync clients. This must be called before getInstance().
     *
     * @param context a context of the app
     */
    public static void init(Context context) {
        if (syncClient != null) return;
        Log.d("TAG","My pool id "+context.getString(R.string.MPOS_PROFILE_IDENTITY_POOL_ID));
        IDENTITY_POOL_ID = context.getString(R.string.MPOS_PROFILE_IDENTITY_POOL_ID);
        /*
         * For using developer authenticated identities make sure you set the
         * flag to true and configure all the constants in the
         * DeveloperAuthenticationProvider class.
         */
        try {
            useDeveloperAuthenticatedIdentities = useDeveloperAuthenticatedIdentities
                    && DeveloperAuthenticationProvider.isDeveloperAuthenticatedAppConfigured();

            if (useDeveloperAuthenticatedIdentities) {
                developerIdentityProvider = new DeveloperAuthenticationProvider(
                        null, IDENTITY_POOL_ID, context, Regions.US_EAST_1);
                credentialsProvider = new CognitoCachingCredentialsProvider(context, developerIdentityProvider,
                        REGION);
                Log.i(TAG, "Using developer authenticated identities");
            } else {
                credentialsProvider = new CognitoCachingCredentialsProvider(context, IDENTITY_POOL_ID,
                        REGION);
                Log.i(TAG, "Developer authenticated identities is not configured");
            }
            new RefreshMyCredentials().execute();
            syncClient = new CognitoSyncManager(context, REGION, credentialsProvider);
        }catch(Exception e){
            Log.i(TAG, "CognitoSyncClientManager init error "+e.toString());
        }
        //Log.d(TAG, "after initial dataset size " + syncClient.listDatasets().size());
    }
    public static class RefreshMyCredentials extends
            AsyncTask<Void, Void, Void> {
        boolean authError;
        String TAG ="RefreshMyCredentials";
        @Override
        protected Void doInBackground(Void... params) {
            try {
                credentialsProvider.refresh();
            } catch (DataStorageException dse) {
                Log.e(TAG, "failed to refresh credentialsProvider", dse);
            } catch (NotAuthorizedException e) {
                Log.e(TAG, "failed to refresh credentialsProvider", e);
                authError = true;
            } catch(Exception e){
                Log.e(TAG, "failed to refresh credentialsProvider", e);
            }
            return null;
        }
    }
    /**
     * Sets the login so that you can use authorized identity. This requires a
     * network request, so you should call it in a background thread.
     *
     * @param providerName the name of the external identity provider
     * @param token openId token
     */
    public static void addLogins(String providerName, String token) {
        if (syncClient == null) {
            throw new IllegalStateException("CognitoSyncClientManager not initialized yet");
        }

        Map<String, String> logins = credentialsProvider.getLogins();
        if (logins == null) {
            logins = new HashMap<String, String>();
        }
        logins.put(providerName, token);
        credentialsProvider.setLogins(logins);
    }

    /**
     * Gets the singleton instance of the CognitoClient. init() must be called
     * prior to this.
     *
     * @return an instance of CognitoClient
     */
    public static CognitoSyncManager getInstance() {
        if (syncClient == null) {
            throw new IllegalStateException("CognitoSyncClientManager not initialized yet");
        }
        return syncClient;
    }

    /**
     * Returns a credentials provider object
     *
     * @return
     */
    public CognitoCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }
}
