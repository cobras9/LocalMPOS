package com.mobilis.android.nfc.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mobilis.android.nfc.util.CustomerLookupUtil;
import com.mobilis.android.nfc.util.CustomerLookupUtil.Callable;

/**
 * Created by lewischao on 2/02/15.
 */
public class LookupCustomerTask extends AsyncTask<Object, Void, Void> {

    private Context activity;
    private Callable callable;

    @Override
    protected Void doInBackground(Object... params) {
        String msisdn;
        CustomerLookupUtil customerUtil;
        activity = (Context) params[0];
        callable = (Callable) params[1];
        customerUtil = (CustomerLookupUtil) params[2];
        msisdn = (String) params[3];

        customerUtil.lookupCustomers(msisdn);

        return null;
    }
}
