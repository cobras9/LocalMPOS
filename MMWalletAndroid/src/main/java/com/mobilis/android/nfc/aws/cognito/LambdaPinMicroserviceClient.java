package com.mobilis.android.nfc.aws.cognito;

import com.amazonaws.mobileconnectors.apigateway.annotation.Service;
import com.mobilis.android.nfc.tasks.PINModel;


@Service(endpoint = "https://jscc512gya.execute-api.us-west-2.amazonaws.com/prod")
public interface LambdaPinMicroserviceClient {

    /**
     * @param body
     * @return SyncDeviceResponseModel
     */
    //@Operation(path = "/merchantPinRetrive/{id}", method = "GET")
    PINModel getPin(
            PINModel body);

}