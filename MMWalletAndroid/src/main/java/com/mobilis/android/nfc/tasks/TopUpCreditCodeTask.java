package com.mobilis.android.nfc.tasks;

import android.os.AsyncTask;

import com.mobilis.android.nfc.jsonModel.Operator;
import com.mobilis.android.nfc.jsonModel.Preset;
import com.mobilis.android.nfc.model.TransferCode;
import com.mobilis.android.nfc.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by lewischao on 15/10/15.
 */
public class TopUpCreditCodeTask extends AsyncTask<Void, Void, List<TransferCode>> {
    String serverResponse;
    public TopUpCreditCodeTask(String serverResponse){
        this.serverResponse = serverResponse;
    }
    @Override
    protected List<TransferCode> doInBackground(Void... params) {

        List<TransferCode> topUpCreditCodes = new ArrayList<TransferCode>();
        String[] firstHalf = serverResponse.split("\\(");
        if(firstHalf.length <= 1)
        {// error occurred - no DSP DATA
            return topUpCreditCodes;
        }
        String[] secondHalf = firstHalf[1].split("\\)");
        String[] displayData = secondHalf[0].split(",");
        String currencyCode = Constants.currency;
        if (TransferCode.currencyCodesMap.containsKey(Constants.currency)) {
            currencyCode = (String)TransferCode.currencyCodesMap.get(Constants.currency);
        }
        Preset preset;
        TreeMap<String,Operator> operatorMap = null;
        Operator operator;
        if(TransferCode.currencyTopUps.containsKey(currencyCode)){
            operatorMap = TransferCode.currencyTopUps.get(currencyCode);
        }else{
            operatorMap = new TreeMap<>();
        }
        for (String string : displayData) {
            String[] items = string.split("\\|");
            TransferCode bpc = new TransferCode();
            String desc = items[2];
            bpc.setCode(items[0]);
            bpc.setAmount(items[1]);
            bpc.setDescription(desc);
            topUpCreditCodes.add(bpc);
            preset = new Preset();
            preset.setAmount(items[1]);
            preset.setCode(items[0]);
            if(operatorMap.containsKey(desc)){
                operator = operatorMap.get(desc);
            }else{
                operator = new Operator();
                operator.setDescription(desc);
            }
            operatorMap.put(desc, operator);
        }

        //TransferCode.addTopUpData(currencyCode, operatorMap);
        return topUpCreditCodes;
    }
    @Override
    protected void onPostExecute(List<TransferCode> transferCodes) {
        super.onPostExecute(transferCodes);
        TransferCode.topupcreditCodes = transferCodes;
    }
}
