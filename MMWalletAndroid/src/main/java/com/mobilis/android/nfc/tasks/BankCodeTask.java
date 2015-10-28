package com.mobilis.android.nfc.tasks;

import android.os.AsyncTask;

import com.mobilis.android.nfc.model.TransferCode;

import java.util.ArrayList;

/**
 * Created by lewischao on 15/10/15.
 */
public class BankCodeTask extends AsyncTask<Void, Void, ArrayList<TransferCode>> {
    String serverResponse;
    public BankCodeTask(String serverResponse){
        this.serverResponse = serverResponse;
    }
    @Override
    protected ArrayList<TransferCode> doInBackground(Void... params) {
        ArrayList<TransferCode> bankCodes = new ArrayList<TransferCode>();
        String[] firstHalf = serverResponse.split("\\(");
        if(firstHalf.length <= 1)
        {// error occurred - no DSP DATA
            return bankCodes;
        }
        String[] secondHalf = firstHalf[1].split("\\)");
        String[] displayData = secondHalf[0].split(",");
        for (String string : displayData) {
            String[] items = string.split("\\|");
            TransferCode bpc = new TransferCode();
            bpc.setCode(items[0]);
            bpc.setAmount(items[1]);
            bpc.setDescription(items[2]);
            bankCodes.add(bpc);
        }
        return bankCodes;
    }
    @Override
    protected void onPostExecute(ArrayList<TransferCode> transferCodes) {
        super.onPostExecute(transferCodes);
        TransferCode.bankCodes = transferCodes;
    }
}
