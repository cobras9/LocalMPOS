package com.mobilis.android.nfc.model;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.domain.INTENT;
import com.mobilis.android.nfc.domain.TxlDomain;
import com.mobilis.android.nfc.util.SpecialTxl;

import java.text.DateFormat;
import java.util.Date;

public class MerchantRegistration extends AbstractModel implements SpecialTxl {

    private String dob;
    private String email;
    private String givenName;
    private String surName;
    private String phone;
    private String mobMonPin;
    private String country;
    private String businessName;
    private String city;
    private String state;
    private String geoLocation;
    private String IMEI;
    private String addressLine;
    //private String address2;
    private boolean idVerified =false;
    public MerchantRegistration(Activity activity) {
        super(activity, activity, true);

    }

    private String getCustomerSearchData() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getResString(R.string.OPEN_BRACKET)).append(getResString(R.string.REQ_MOBMONPIN)).append(getResString(R.string.EQUAL));
        buffer.append(getMobMonPin());

        buffer.append(getResString(R.string.COMMA)).append(getResString(R.string.REQ_CONTACT_PHONE)).append(getResString(R.string.EQUAL));
        buffer.append(getPhone());

        buffer.append(getResString(R.string.COMMA)).append(getResString(R.string.REQ_GIVENNAME)).append(getResString(R.string.EQUAL));
        buffer.append(getGivenName());

        buffer.append(getResString(R.string.COMMA)).append(getResString(R.string.REQ_SURNAME)).append(getResString(R.string.EQUAL));
        buffer.append(getSurName());
        if (getBusinessName() != null && !getBusinessName().isEmpty()) {
            buffer.append(getResString(R.string.COMMA)).append(getResString(R.string.REQ_BUSINESS_NAME)).append(getResString(R.string.EQUAL));
            buffer.append(getBusinessName());
        }
        buffer.append(getResString(R.string.COMMA)).append(getResString(R.string.REQ_EMAIL_ADDRESS)).append(getResString(R.string.EQUAL));
        buffer.append(getEmail());
        if (getDob() != null && !"".equals(getDob())) {
            buffer.append(getResString(R.string.COMMA)).append(getResString(R.string.REQ_DOB)).append(getResString(R.string.EQUAL));
            buffer.append(getDob());
        }
        buffer.append(getResString(R.string.COMMA)).append(getResString(R.string.REQ_CUSTOMER_TYPE)).append(getResString(R.string.EQUAL));
        buffer.append(getResString(R.string.LABEL_MERCHANT));

        buffer.append(getResString(R.string.COMMA)).append(getResString(R.string.REQ_ASSIGNEDTID)).append(getResString(R.string.EQUAL));
        buffer.append(getAndroidId(activity));//getPhone());

        buffer.append(getResString(R.string.COMMA)).append(getResString(R.string.REQ_GEOLOCATION)).append(getResString(R.string.EQUAL));
        buffer.append(getGeoLocation());

        buffer.append(getResString(R.string.CLOSE_BRACKET));
        return buffer.toString();
    }

    @Override
    public String getRequestParameters() {

        StringBuilder buffer = new StringBuilder();
        // buffer.append(getFullParamString(getResString(R.string.REQ_MESSAGE_TYPE), getResString(R.string.REQ_CUSTOMER_CREATE), false))
        buffer.append(getFullParamString(getResString(R.string.REQ_MESSAGE_TYPE), getActivity().getResources().getString(R.string.ATOMIC_CUSTOMER_CREATE), false))
                .append(getFullParamString(getResString(R.string.REQ_APP), getAppVersionCode(getActivity()), false))
                .append(getFullParamString(getResString(R.string.REQ_OS), getActivity().getResources().getString(R.string.REQ_OS_ANDROID), false))
                .append(getFullParamString(getResString(R.string.REQ_TRANSACTION_ID), getTransactionId(), false))
                .append(getFullParamString(getResString(R.string.REQ_CLIENT_ID), getPhone(), false))
                .append(getFullParamString(getResString(R.string.REQ_TERMINAL_ID), getAndroidId(activity), false))
                .append(getFullParamString(getResString(R.string.REQ_SUBSCRIBER_ADDRESS), getCustomerAddress(), false));
        if (idVerified) {
            buffer.append(getFullParamString(getResString(R.string.REQ_ID_VERIFIED), "true", false));
            buffer.append(getFullParamString("IdData", "(idType=1,idName=Joe,Bloggs,idNumber=12345678DC,idCountry=NZ,idExpiry=20251212)", false));
        } else {
            buffer.append(getFullParamString(getResString(R.string.REQ_ID_VERIFIED), "false", false));
        }
        buffer.append(getFullParamString(getResString(R.string.REQ_CUST_DATA), getCustomerSearchData(), true));
        return buffer.toString();
    }

    private String getCustomerAddress(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(getResString(R.string.OPEN_BRACKET));
        if(getAddressLine() != null && !getAddressLine().isEmpty())
            buffer.append(getFullParamString(getResString(R.string.REQ_ADDRESS), getAddressLine(), false));
        if(getCity() != null && !getCity().isEmpty())
            buffer.append(getFullParamString(getResString(R.string.REQ_CITY), getCity(), false));
        if(getState() != null && !getState().isEmpty())
            buffer.append(getFullParamString(getResString(R.string.REQ_STATE), getState(), false));
        if(getCountry() != null && !getCountry().isEmpty())
            buffer.append(getFullParamString(getResString(R.string.REQ_COUNTRY), getCountry(), true));
        buffer.append(getResString(R.string.CLOSE_BRACKET));
        return new String(buffer);
    }
    @Override
    public void verifyPostTaskResults() {
        Log.d(MerchantRegistration.class.getSimpleName(), "Verifying result for merchant registration task ");
        LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(INTENT.MERCHANT_REGISTRATION.toString()).putExtra(INTENT.EXTRA_RESPONSE.toString(), getServerResponse()));
    }

    public String getTransactionId() {
        setTxl(generateTXLId(getResString(R.string.DB_TXL_PAYMENT)));
        return getTxl().getTxlId();
    }

    public String getFullParamString(String key, String value, boolean lastParam) {
        StringBuilder buffer = new StringBuilder();
        if (lastParam) {
            buffer.append(key).append(getResString(R.string.EQUAL)).append(value);
        } else {
            buffer.append(key).append(getResString(R.string.EQUAL)).append(value).append(getResString(R.string.COMMA));
        }
        return buffer.toString();

    }

    public TxlDomain generateTXLId(String txlType) {
        TxlDomain lastTxl = getDBService().getLastLogin();
        String txlId;
        String dateCreated = DateFormat.getDateTimeInstance().format(new Date());

        if (lastTxl.getTxlId() != null) {
            txlId = constructNewTxlId(lastTxl.getTxlId());
        } else {
            txlId = getResString(R.string.TRANSACTIONID_BASE);
        }
        TxlDomain newTxl = new TxlDomain();
        newTxl.setTxlId(txlId);
        newTxl.setDateCreated(dateCreated);
        newTxl.setTxlType(txlType);
        return newTxl;
    }

    private String constructNewTxlId(String lastTransaction) {
        int counter = Integer.parseInt(lastTransaction);
        counter++;
        StringBuilder buffer = new StringBuilder(String.valueOf(counter));
        for (int i = buffer.length(); i < 10; i++) {
            buffer.insert(0, getResString(R.string.ZERO_STRING));
        }
        return new String(buffer);
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobMonPin() {
        return mobMonPin;
    }

    public void setMobMonPin(String mobMonPin) {
        this.mobMonPin = mobMonPin;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }
}
