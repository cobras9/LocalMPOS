package com.mobilis.android.nfc.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.mobilis.android.nfc.jsonModel.Payee;
import com.mobilis.android.nfc.model.TransferCode;
import com.mobilis.android.nfc.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewischao on 15/10/15.
 */
public class UtilityCodeTask extends AsyncTask<Void, Void, List<TransferCode>> {
    String serverResponse;
    public UtilityCodeTask(String serverResponse){
        this.serverResponse = serverResponse;
    }
    @Override
    protected List<TransferCode> doInBackground(Void... params) {
        List<TransferCode> utilityCodes = new ArrayList<TransferCode>();
        String[] firstHalf = serverResponse.split("\\(");
        if(firstHalf.length <= 1)
        {// error occurred - no DSP DATA
            return utilityCodes;
        }
        String[] secondHalf = firstHalf[1].split("\\)");
        String[] displayData = secondHalf[0].split(",");
        ArrayList allPayees =new ArrayList();
        for (String string : displayData) {
            String[] items = string.split("\\|");
            TransferCode bpc = new TransferCode();

            bpc.setCode(items[0]);
            bpc.setAmount(items[1]);
            bpc.setDescription(items[2]);
            utilityCodes.add(bpc);
            Payee payee = new Payee();
            payee.setCode(items[0]);
            payee.setAmount(items[1]);
            payee.setName(items[2]);
            payee.setPayeeId(items[0]);
            allPayees.add(payee);
        }

        String currencyCode = Constants.currency;
        if (TransferCode.currencyCodesMap.containsKey(Constants.currency)) {
            currencyCode = (String)TransferCode.currencyCodesMap.get(Constants.currency);
        }
        Log.d("Lewis", "Lewis UtilityCodeTask  Constants.currency " + Constants.currency);
        Log.d("Lewis", "Lewis UtilityCodeTask  currencyCode " +  currencyCode);
        Log.d("Lewis", "Lewis UtilityCodeTask  allPayees.size() " +  allPayees.size());
        TransferCode.addBillPayee(currencyCode, allPayees);
        return utilityCodes;
    }
    @Override
    protected void onPostExecute(List<TransferCode> transferCodes) {
        super.onPostExecute(transferCodes);
        //TransferCode.utilityCodes = transferCodes;
    }
}
