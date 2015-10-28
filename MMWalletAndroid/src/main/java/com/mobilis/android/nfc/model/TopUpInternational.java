package com.mobilis.android.nfc.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.activities.ApplicationActivity;
import com.mobilis.android.nfc.domain.INTENT;
import com.mobilis.android.nfc.util.Constants;
import com.mobilis.android.nfc.util.Financial;

@SuppressLint("UseValueOf")
public class TopUpInternational extends AbstractModel  implements Financial {

	private String phoneNumber;
	private String destinationCode;
	private String creditCardNumber;
    private String creditCardExpiry;
	private String pin;
	public TopUpInternational(Activity activity){
		super(activity, activity);
	}

	private String getCustomerSearchData(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(getResString(R.string.OPEN_BRACKET));

		if(getPhoneNumber() != null && !getPhoneNumber().isEmpty()) {
			buffer.append(getResString(R.string.REQ_MSISDN) + getResString(R.string.EQUAL));
			buffer.append(getPhoneNumber());
		}else if(getNFCId() !=null){
			buffer.append(getResString(R.string.REQ_NFCTAGID) + getResString(R.string.EQUAL));
			buffer.append(getNFCId());
			//if(getActivity().getResources().getBoolean(R.bool.PAY_PIN_REQUIRED)) {
			if(Constants.USSD_PAY_PIN_REQUIRED) {
				buffer.append(getResString(R.string.COMMA) + getResString(R.string.REQ_MOBMONPIN) + getResString(R.string.EQUAL));
				buffer.append(getPin());
			}
		}
		buffer.append(getResString(R.string.CLOSE_BRACKET));
		return new String(buffer);
	}

	@Override
	public String getRequestParameters() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getFullParamString(getResString(R.string.REQ_MESSAGE_TYPE), getResString(R.string.REQ_MESSAGE_TYPE_TRANSFER), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_CHANNEL), getResString(R.string.MPOS), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_TERMINAL_ID), getAndroidId(getActivity()), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_CLIENT_ID), getMerchantId(), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_CLIENT_PIN), ApplicationActivity.loginClientPin, false));
        /** This is removed because no NFC Tag could be scanned **/
		if(!getActivity().getResources().getBoolean(R.bool.USING_YOUTAP_WALLET)) {
			buffer.append(getFullParamString(getResString(R.string.REQ_CUST_SEARCH_DATA), getCustomerSearchData(), false));
		}
		buffer.append(getFullParamString(getResString(R.string.REQ_WORKING_AMOUNT), getWorkingAmount(), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_WORKING_CURRENCY), getWorkingCurrency(), false));
		buffer.append(getFullParamString(getResString(R.string.REQ_PAYMENTTYPE), "OUTTXF", false));
		if(getPhoneNumber() == null || getPhoneNumber().isEmpty()) {
			// buffer.append(getFullParamString(getResString(R.string.REQ_DESTINATION), getMerchantId(), false));
			buffer.append(getFullParamString(getResString(R.string.REQ_DESTINATION), getNFCId(), false));
		}else {
			buffer.append(getFullParamString(getResString(R.string.REQ_DESTINATION), getPhoneNumber(), false));
		}
        buffer.append(getFullParamString(getResString(R.string.REQ_ORIGINCODE), "VFMM", false));
		buffer.append(getFullParamString(getResString(R.string.REQ_DESTINATION_CODE), getDestinationCode(), false));
		if(getCreditCardNumber() != null && !getCreditCardNumber().isEmpty()) {
			buffer.append(getFullParamString(getResString(R.string.REQ_CREDIT_CARD_NUMBER), getCreditCardNumber(), false));
		}
		if(getCreditCardExpiry() != null && !getCreditCardExpiry().isEmpty()) {
			buffer.append(getFullParamString(getResString(R.string.REQ_CREDIT_CARD_EXPIRY), getCreditCardExpiry(), false));
		}

        buffer.append(getFullParamString(getResString(R.string.REQ_TRANSACTION_ID), getTransactionId(), true));
		return new String(buffer);
	}

	@Override
	public void verifyPostTaskResults() {

        Intent intent = new Intent(INTENT.TOP_UP.toString());
        intent.putExtra(INTENT.EXTRA_RESPONSE.toString(), getServerResponse());
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
	}
	
	public String getTransactionId() {
		setTxl(generateTXLId(getResString(R.string.DB_TXL_PAYMENT)));
		return getTxl().getTxlId();
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDestinationCode() {
		return destinationCode;
	}

	public void setDestinationCode(String destinationCode) {
		this.destinationCode = destinationCode;
	}


    public String getCreditCardExpiry() {
        return creditCardExpiry;
    }

    public void setCreditCardExpiry(String creditCardExpiry) {
        this.creditCardExpiry = creditCardExpiry;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
}
