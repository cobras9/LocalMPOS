package com.mobilis.android.nfc.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.activities.ApplicationActivity;
import com.mobilis.android.nfc.util.SecurePreferences;

@SuppressLint("UseValueOf")
public class InternalTopup extends AbstractModel {

	
	public InternalTopup(Context context, Activity activity){
		super(context, activity);
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
		buffer.append(getFullParamString(getResString(R.string.REQ_MESSAGE_TYPE), getResString(R.string.REQ_MESSAGE_TYPE_MERCHANTTRANSACTION), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_TERMINAL_ID), getAndroidId(getActivity()), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_CLIENT_ID), ApplicationActivity.loginClientId, false));
		buffer.append(getFullParamString(getResString(R.string.REQ_CLIENT_PIN), ApplicationActivity.loginClientPin, false));
		buffer.append(getFullParamString(getResString(R.string.REQ_CUST_SEARCH_DATA), getCustomerSearchData(), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_WORKING_AMOUNT), getWorkingAmount(), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_WORKING_CURRENCY), getWorkingCurrency(), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_PAYMENT_TYPE), getResString(R.string.PAYMENT_TYPE_SELLTOPUP), false));	
		buffer.append(getFullParamString(getResString(R.string.REQ_TRANSACTION_ID), getTransactionId(), true));
		return new String(buffer);
	}

	@Override
	public void verifyPostTaskResults() {
		SecurePreferences preferences = new SecurePreferences(getContext());
		String quickBalance = preferences.getString(SecurePreferences.KEY_WIDGET_QUICKBALANCE, null);
		super.verifyTransferTXL();
	}
	
	
	public String getTransactionId() {
		setTxl(generateTXLId(getResString(R.string.DB_TXL_PAYMENT)));
		return getTxl().getTxlId();
	}

}
