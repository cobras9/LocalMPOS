package com.mobilis.android.nfc.model;

import android.app.Activity;

import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.interfaces.HideServerResponse;
import com.mobilis.android.nfc.tasks.TopUpCreditCodeTask;

public class TopUpCreditDestinationCodes extends AbstractModel implements HideServerResponse {

	public TopUpCreditDestinationCodes(Activity activity){
		super(activity, activity);
	}
	
	@Override
	public String getRequestParameters() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getFullParamString(getResString(R.string.REQ_MESSAGE_TYPE), getResString(R.string.REQ_MESSAGE_TYPE_TRANSFERCODESGET), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_TERMINAL_ID), getAndroidId(getActivity()), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_TRANSACTION_TYPE), getResString(R.string.REQ_C2CT), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_TRANSACTION_ID), getTransactionId(), true));
		return new String(buffer);
	}

	@Override
	public void verifyPostTaskResults() {
		super.verifyTransferTXL();
		TopUpCreditCodeTask task = new TopUpCreditCodeTask(getServerResponse());
		task.execute();
/*		LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(INTENT.CODE_CREDIT_TOP_UP.toString()).
                putExtra(INTENT.EXTRA_SERVER_RESPONSE.toString(), getServerResponse()));*/
	}
	
	public String getTransactionId() {
		setTxl(generateTXLId(getResString(R.string.DB_TXL_PAYMENT)));
		return getTxl().getTxlId();
	}


}
