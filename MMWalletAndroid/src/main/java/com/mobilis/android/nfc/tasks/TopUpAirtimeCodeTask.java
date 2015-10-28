package com.mobilis.android.nfc.tasks;

import android.os.AsyncTask;
import android.util.Log;

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
public class TopUpAirtimeCodeTask extends AsyncTask<Void, Void, List<TransferCode>> {
    String serverResponse;
    public TopUpAirtimeCodeTask(String serverResponse){
        this.serverResponse = serverResponse;
    }
    @Override
    protected List<TransferCode> doInBackground(Void... params) {
        List<TransferCode> topUpAirtimeCodes = new ArrayList<TransferCode>();
        String[] firstHalf = serverResponse.split("DISPLAYDATA=\\(");
        if(firstHalf.length <= 1)
        {// error occurred - no DSP DATA
            return topUpAirtimeCodes;
        }
        String[] secondHalf = firstHalf[1].split("=");
        String[] details = secondHalf[0].split(",");
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
        Log.d("TAG", " TopupAirtime currencyCode " + currencyCode);
        for (int i = 0; i < details.length; i++) {
            String[] codes = details[i].split("\\|");
            Log.d("TAG"," TopupAirtime  codes length "+codes.length);
            if(codes.length >1)
            {
                TransferCode bpc = new TransferCode();
                bpc.setCode(codes[0]);
                bpc.setAmount(codes[1]);
                String desc = codes[2];
                if(desc.contains("("))
                    desc = desc.replace("(", "");
                if(desc.contains(")"))
                    desc = desc.replace(")", "");

                Log.d("TAG", " TopupAirtime  desc " + desc);
                bpc.setDescription(desc);
                topUpAirtimeCodes.add(bpc);

                preset = new Preset();
                preset.setAmount(codes[1]);
                preset.setCode(codes[0]);
                Log.d("TAG", "TopupAirtime  preset  getAmount " + preset.getAmount());
                Log.d("TAG", "TopupAirtime  preset  getCode " + preset.getCode());
                if(operatorMap.containsKey(desc)){
                    operator = operatorMap.get(desc);
                }else{
                    operator = new Operator();
                    operator.setDescription(desc);
                }
                operator.addPreset(preset);
                operatorMap.put(desc, operator);

            }
        }

        TransferCode.addTopUpData(currencyCode, operatorMap);
        Log.d("TAG", "TopupAirtime  currencyTopUps  size " +TransferCode.currencyTopUps.size());
        String lastItem = topUpAirtimeCodes.get(topUpAirtimeCodes.size()-1).getDescription();
        if(lastItem.endsWith(")"))
            topUpAirtimeCodes.get(topUpAirtimeCodes.size()-1).setDescription(lastItem.replace(")",""));
        return topUpAirtimeCodes;
    }
    @Override
    protected void onPostExecute(List<TransferCode> transferCodes) {
        super.onPostExecute(transferCodes);
        TransferCode.topupAirtimeCodes = transferCodes;
    }
}
