package com.mobilis.android.nfc.util;

import android.content.Context;
import android.util.Log;

import com.mobilis.android.nfc.model.TransferCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewischao on 9/03/15.
 */
public class ResourceParser {
    private static final String COUNTRY_NAME = "country_name";
    private static final String COUNTRY_CODE = "country_code";
    private static final String OPERATORS = "operators";
    private static final String DESCRIPTION = "description";

    private static final String PRESETS = "presets";
    private static final String AMOUNT = "amount";
    private static final String CODE = "code";
    private final String TAG =this.getClass().getSimpleName();

    public void parseTopups(Context mContext) {
        try {
            String result = openFile(mContext);
            JSONObject topLevel = new JSONObject(result);
            JSONArray countries = topLevel.getJSONArray("records");
            TransferCode bpc ;
            String currentOperator;
            List<TransferCode> topUpAirtimeCodes = new ArrayList<TransferCode>();
            for (int i = 0; i < countries.length(); i++) {
                try {
                    JSONObject oneCountry = countries.getJSONObject(i);
                    String countryName = oneCountry.getString(COUNTRY_NAME);
                    String countryCode = oneCountry.getString(COUNTRY_CODE);
                    Log.d(TAG, "countryName : " + countryName + "..." + "countryCode : " + countryCode);
                    JSONArray operators = oneCountry.getJSONArray(OPERATORS);
                    Log.d(TAG,"operators "+operators.length());
                    for(int j =0;j<operators.length();j++){
                        JSONObject oneOperator  = operators.getJSONObject(j);
                        currentOperator = oneOperator.getString(DESCRIPTION);
                        Log.d(TAG,"operators description "+currentOperator);
                        JSONArray presets = oneOperator.getJSONArray(PRESETS);
                        for(int k=0; k<presets.length();k++){
                            JSONObject preset =  presets.getJSONObject(k);
                            String amount =preset.getString(AMOUNT);
                            String code = preset.getString(CODE);
                            bpc =new TransferCode();
                            bpc.setCode(code);
                            bpc.setAmount(amount);
                            bpc.setDescription(currentOperator+":"+code);
                            topUpAirtimeCodes.add(bpc);
                        }
                    }
                } catch (JSONException e) {
                    // Oops
                }
            }
            TransferCode.topupAirtimeCodes = topUpAirtimeCodes;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String openFile(Context mContext) {
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder("{");
        try {
            reader = new BufferedReader(
                    new InputStreamReader(mContext.getAssets().open("topups.json"), "UTF-8"));

            String mLine = reader.readLine();
            while (mLine != null) {

                mLine = reader.readLine();
                content.append(mLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        content.append("}");
        return content.toString();
    }
}
