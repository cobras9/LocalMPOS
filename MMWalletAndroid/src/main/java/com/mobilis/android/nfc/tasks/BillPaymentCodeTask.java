package com.mobilis.android.nfc.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.mobilis.android.nfc.jsonModel.Payee;
import com.mobilis.android.nfc.model.TransferCode;
import com.mobilis.android.nfc.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewischao on 14/10/15.
 */
public class BillPaymentCodeTask extends AsyncTask<Void, Void, List<TransferCode>> {
    String serverResponse;
    public BillPaymentCodeTask(String serverResponse){
        this.serverResponse = serverResponse;
        Log.d("Lewis", "Lewis BillPaymentCodeTask serverResponse " + serverResponse);
    }
    @Override
    protected List<TransferCode> doInBackground(Void... params) {
        List<TransferCode> billPaymentCodes = new ArrayList<TransferCode>();
        String[] firstHalf = serverResponse.split("\\(");
        Log.d("Lewis", "Lewis BillPaymentCodeTask "+firstHalf);
        if(firstHalf.length <= 1)
        {// error occurred - no DSP DATA
            return billPaymentCodes;
        }
        String[] secondHalf = firstHalf[1].split("\\)");
        String[] displayData = secondHalf[0].split(",");
        ArrayList allPayees =new ArrayList();
        for (String string : displayData) {
            Log.d("Lewis", "Lewis BillPaymentCodeTask string " + string);
            String[] items = string.split("\\|");
            TransferCode bpc = new TransferCode();
            Log.d("Lewis", "Lewis BillPaymentCodeTask items[0] "+items[0]);
            Log.d("Lewis", "Lewis BillPaymentCodeTask items[1] "+items[1]);
            Log.d("Lewis", "Lewis BillPaymentCodeTask items[2] " + items[2]);
            bpc.setCode(items[0]);
            bpc.setAmount(items[1]);
            bpc.setDescription(items[2]);

            billPaymentCodes.add(bpc);

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
        Log.d("Lewis", "Lewis BillPaymentCodeTask  Constants.currency " +  Constants.currency);
        Log.d("Lewis", "Lewis BillPaymentCodeTask  currencyCode " +  currencyCode);
        Log.d("Lewis", "Lewis BillPaymentCodeTask  allPayees.size() " +  allPayees.size());
        TransferCode.addBillPayee(currencyCode, allPayees);
        return billPaymentCodes;
    }

    @Override
    protected void onPostExecute(List<TransferCode> transferCodes) {
        super.onPostExecute(transferCodes);
/*            if(TransferCode.countryNameToCodeMap.containsKey(data[0])){
                String oneCurrencyCode = TransferCode.countryNameToCodeMap.get(data[0]);
                Log.d("LOADING","data oneCurrencyCode "+oneCurrencyCode);
                ArrayList allPayees =null;
                if(TransferCode.currencyBillPayees.containsKey(oneCurrencyCode)){
                    allPayees = TransferCode.currencyBillPayees.get(oneCurrencyCode);
                }else{
                    allPayees = new ArrayList();
                }
                allPayees.add(parsePayees(data));
                Log.d("LOADING", "data oneCurrencyCode oneCurrencyCode " + oneCurrencyCode);
                Log.d("LOADING", "data oneCurrencyCode " + allPayees.size());
                TransferCode.addBillPayee(oneCurrencyCode, allPayees);
                Log.d("LOADING", "data oneCurrencyCode " + TransferCode.currencyBillPayees.size());
            }
            asdf*/
        //TransferCode.billPaymentCodes = transferCodes;
    }
}