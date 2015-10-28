package com.mobilis.android.nfc.model;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.activities.ApplicationActivity;
import com.mobilis.android.nfc.domain.INTENT;
import com.mobilis.android.nfc.util.Financial;

public class Tap extends AbstractModel implements Financial {

    private String pin;

	public Tap(Activity activity){
		super(activity, activity);
	}
	
	private String getCustomerSearchData(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(getResString(R.string.OPEN_BRACKET) + getResString(R.string.REQ_NFCTAGID) + getResString(R.string.EQUAL));
		buffer.append(getNFCId());
		buffer.append(getResString(R.string.CLOSE_BRACKET));
		return new String(buffer);
	}
	
	@Override
	public String getRequestParameters() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getFullParamString(getResString(R.string.REQ_MESSAGE_TYPE), getResString(R.string.REQ_MESSAGE_TYPE_TAP), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_TERMINAL_ID), getAndroidId(getActivity()), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_CLIENT_ID), getMerchantId(), false));
        if(ApplicationActivity.loginClientPin != null && !ApplicationActivity.loginClientPin.equals("")) {
            buffer.append(getFullParamString(getResString(R.string.REQ_CLIENT_PIN), ApplicationActivity.loginClientPin, false));
        }
		buffer.append(getFullParamString(getResString(R.string.REQ_CUST_SEARCH_DATA), getCustomerSearchData(), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_TRANSACTION_ID), getTransactionId(), true));
		return new String(buffer);
	}

	public String getTransactionId() {
		setTxl(generateTXLId(getResString(R.string.DB_TXL_PAYMENT)));
		return getTxl().getTxlId();
	}

	@Override
	public void verifyPostTaskResults() {
        Intent intent = new Intent(INTENT.TAP.toString());
        String serverResponse = getServerResponse();
        intent.putExtra(INTENT.EXTRA_SERVER_RESPONSE.toString(), serverResponse);
        intent.putExtra(getResString(R.string.REQ_MESSAGE_TYPE_TAP_STATUS), String.valueOf(getStatusFromServerResponse(serverResponse)));
        intent.putExtra(getResString(R.string.REQ_MESSAGE_TYPE_TAP_MESSAGE), getMessageFromServerResponse(serverResponse));
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
		super.verifyTransferTXL();
	}

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
