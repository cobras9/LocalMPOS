package com.mobilis.android.nfc.tasks;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.activities.LoginActivity;
import com.mobilis.android.nfc.jsonModel.Currency;
import com.mobilis.android.nfc.model.TransferCode;
import com.mobilis.android.nfc.util.Constants;
import com.mobilis.android.nfc.util.SecurePreferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by lewischao on 10/03/15.
 */
public class LoadingCurrencyOnly extends AsyncTask<Object, Void, Void> {
    public static boolean processd = false;
    private final String TAG = this.getClass().getSimpleName();
    //TOPUPS
    private final String COUNTRY_NAME = "country_name";
    private final String COUNTRY_CODE = "country_code";
    private final String OPERATORS = "operators";

    private final String DESCRIPTION = "description";
    private final String PRESETS = "presets";
    private final String AMOUNT = "amount";
    private final String CODE = "code";
    private Context mContext;

    //BANKS
    private final String BANKS = "banks";
    private boolean useJsonCurrencyFile = false;

    @Override
    protected Void doInBackground(Object... params) {
        this.mContext = (Context) params[0];
        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(mContext, "Error occurred while getting version code", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        String appVersionCode =  mContext.getResources().getString(R.string.app_name)+pInfo.versionName;
        String currencyCode = LoginActivity.mainSecurePreferences.getString(appVersionCode+ SecurePreferences.KEY_DEFAULT_CURRENCY, null);
        if(currencyCode == null) {
            currencyCode = mContext.getString(R.string.DEFAULT_CURRENCY);
        }
        Constants.currency = currencyCode;

        ObjectMapper om = new ObjectMapper();
        om.setPropertyNamingStrategy(
                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<TransferCode> topUpAirtimeCodes = new ArrayList<TransferCode>();
        List<TransferCode> bankCodes = new ArrayList<TransferCode>();
        List<TransferCode> utilities = new ArrayList<TransferCode>();
        if(useJsonCurrencyFile) {
            try {
                List<Currency> allCurrencyList = om.readValue(mContext.getAssets().open("currency.json"), new TypeReference<List<Currency>>() {
                });
                ArrayList<String> currencyLabels = new ArrayList<>();
                Currency currency;
                TreeMap<String, Currency> map = new TreeMap<>();
                HashMap<String, String> currencyCodesMap = new HashMap<>();
                for (int i = 0; i < allCurrencyList.size(); i++) {
                    currency = allCurrencyList.get(i);
                    Log.d(TAG, "currencyLabels getCurrencyCode " + currency.getCurrencyCode());
                    Log.d(TAG, "currencyLabels getCurrencyNumber " + currency.getCurrencyNumber());
                    currencyLabels.add(currency.getCurrencyCode());
                    map.put(currency.getCurrencyCode(), currency);
                    currencyCodesMap.put(currency.getCurrencyCode(), currency.getCurrencyNumber());
                }
                TransferCode.currencyMap = map;
                TransferCode.currencyCodesMap = currencyCodesMap;
            } catch (Exception e) {
                Log.e(TAG, "error parsing currencies" + e.toString());
            }
        }else{//Loading from plain txt file
            ArrayList<String> data = returnDataLines("currencyLocale.txt");
            List<Currency> allCurrencyList =  ParseCurrencyTxtFile.parseCurrency(data);

            ArrayList<String> currencyLabels = new ArrayList<>();
            Currency currency;
            TreeMap<String, Currency> map = new TreeMap<>();
            HashMap<String, String> currencyCodesMap = new HashMap<>();
            HashMap<String, String> countryNameToCodeMap = new HashMap<>();
            for (int i = 0; i < allCurrencyList.size(); i++) {
                currency = allCurrencyList.get(i);
                Log.d(TAG, "Plainfile currencyLabels getcountryname  " + currency.getCountryName());
                Log.d(TAG, "Plainfile currencyLabels getCurrencyCode  " + currency.getCurrencyCode());
                Log.d(TAG, "Plainfile currencyLabels getCurrencyNumber " + currency.getCurrencyNumber());
                currencyLabels.add(currency.getCurrencyCode());
                map.put(currency.getCurrencyCode(), currency);
                currencyCodesMap.put(currency.getCurrencyCode(), currency.getCurrencyNumber());
                countryNameToCodeMap.put(currency.getCountryName(),currency.getCurrencyNumber());
            }
            TransferCode.currencyMap = map;
            TransferCode.currencyCodesMap = currencyCodesMap;
            TransferCode.countryNameToCodeMap = countryNameToCodeMap;
        }
        processd = true;
         return null;
    }

    private ArrayList<String> returnDataLines(String file) {
        BufferedReader reader = null;
        ArrayList<String> data = new ArrayList<String>();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(mContext.getAssets().open(file), "UTF-8"));

            String mLine = reader.readLine();
            while (mLine != null) {

                mLine = reader.readLine();
                data.add(mLine);
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
        return data;
    }
}
