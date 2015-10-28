package com.mobilis.android.nfc.model;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.domain.INTENT;
import com.mobilis.android.nfc.util.SpecialTxl;

public class TerminalRegistration extends AbstractModel implements SpecialTxl {
    private String merchantPin;
    private String userMerchantId;

    public String getUserMerchantId() {
        return userMerchantId;
    }

    public void setUserMerchantId(String userMerchantId) {
        this.userMerchantId = userMerchantId;
    }

    public String getMerchantPin() {
        return merchantPin;
    }

    public void setMerchantPin(String merchantPin) {
        this.merchantPin = merchantPin;
    }
    public TerminalRegistration(Activity activity) {
        super(activity, activity, true);

    }
    @Override
    public String getRequestParameters() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.addDateTime());
        buffer.append(getFullParamString(getResString(R.string.REQ_MESSAGE_TYPE), getResString(R.string.REQ_MESSAGE_TYPE_TERMINAL_REGISTRATION), false));
        buffer.append(getFullParamString(getResString(R.string.REQ_TRANSACTION_ID), getTransactionId(), false));
        buffer.append(getFullParamString(getResString(R.string.REQ_TERMINAL_ID), getAndroidId(context), false));
        buffer.append(getFullParamString(getResString(R.string.REQ_CLIENT_ID), getUserMerchantId(), false));
        buffer.append(getFullParamString(getResString(R.string.REQ_CLIENT_PIN), getMerchantPin(), true));
        return buffer.toString();
    }

    @Override
    public void verifyPostTaskResults() {
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(INTENT.TERMINAL_REGISTRATION.toString()).putExtra(INTENT.EXTRA_RESPONSE.toString(), getServerResponse()));
    }

    public String getTransactionId() {
        setTxl(generateTXLId(getResString(R.string.DB_TXL_PAYMENT)));
        return getTxl().getTxlId();
    }
}
