package com.mobilis.android.nfc.aws.cognito;

import com.amazonaws.mobileconnectors.apigateway.annotation.Operation;
import com.amazonaws.mobileconnectors.apigateway.annotation.Service;


@Service(endpoint = "https://2gl73012k5.execute-api.us-west-2.amazonaws.com/prod")
public interface LambdaMicroserviceClient {

    /**
     * @param body
     * @return SyncDeviceResponseModel
     */
    @Operation(path = "/syncDevice", method = "POST")
    SyncDeviceResponseModel syncDevicePost(
            SyncDeviceModel body);

}