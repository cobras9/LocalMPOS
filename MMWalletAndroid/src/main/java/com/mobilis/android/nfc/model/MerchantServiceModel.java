package com.mobilis.android.nfc.model;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.activities.ApplicationActivity;
import com.mobilis.android.nfc.domain.INTENT;
import com.mobilis.android.nfc.util.Financial;

public class MerchantServiceModel extends AbstractModel implements Financial {
	private String messageType;

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public MerchantServiceModel(Activity activity){
		super(activity, activity);
	}


	@Override
	public String getRequestParameters() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getFullParamString(getResString(R.string.REQ_MESSAGE_TYPE), getMessageType(), false));
		buffer.append(super.addDateTime());
		buffer.append(getFullParamString(getResString(R.string.REQ_CLIENT_PIN), ApplicationActivity.loginClientPin, false));
		buffer.append(getFullParamString(getResString(R.string.REQ_WORKING_CURRENCY), getWorkingCurrency(), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_WORKING_AMOUNT), getWorkingAmount(), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_CLIENT_ID), getMerchantId(), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_TERMINAL_ID), getAndroidId(getActivity()), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_TRANSACTION_ID), getTransactionId(), true));
		return new String(buffer);
	}

	public String getTransactionId() {
		setTxl(generateTXLId(getResString(R.string.DB_TXL_PAYMENT)));
		return getTxl().getTxlId();
	}

	@Override
	public void verifyPostTaskResults() {
        Intent intent =null;
		if(getMessageType().equals(getActivity().getString(R.string.REQ_MESSAGE_TYPE_MERCHANT_COMMISSION_TRANSFER))){
			intent = new Intent(INTENT.MERCHANT_SERVICE_COMMISSION_TRANSFER.toString());
		}else if(getMessageType().equals(getActivity().getString(R.string.REQ_MESSAGE_TYPE_MERCHANT_VOUCHER))){
			intent = new Intent(INTENT.MERCHANT_SERVICE_MERCHANT_VOUCHER.toString());
		}
        intent.putExtra(INTENT.EXTRA_RESPONSE.toString(), getServerResponse());
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
		super.verifyTransferTXL();
	}

}
