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
import com.mobilis.android.nfc.jsonModel.Operator;
import com.mobilis.android.nfc.jsonModel.Payee;
import com.mobilis.android.nfc.jsonModel.Preset;
import com.mobilis.android.nfc.model.TransferCode;
import com.mobilis.android.nfc.util.Constants;
import com.mobilis.android.nfc.util.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class LoadingCodesTask extends AsyncTask<Object, Void, Void> {
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


    private String files[] = {"topups.json", "banks.json", "billpayees.json"};
    private Object codeLists[] = {TransferCode.topupAirtimeCodes};
    private String codeKey[] = {OPERATORS, BANKS};
    private boolean useJsonCurrencyFile = false;
    private Preset parseValues (String oneData){
        Preset preset = null;
        String values [];
        oneData =oneData.replaceAll("[()]","");
        if(oneData.contains("|")){
            values = oneData.split("\\|");
            preset = new Preset();
            preset.setAmount(values[1]);
            preset.setCode(values[0]);
        }
        return preset;
    }
    private void loadTopUps(){
        ArrayList<String> allContent = openFileAsList("topups.txt");
        String oneLine ;
        String currentCountry;
        String TOPUP_COUNTRY_PREFIX = "!";
        String TOPUP_SPLIT_DELI =",";
        String data [] = null;
        Preset preset;
        TreeMap<String,Operator> operatorMap = null;
        for(int i =0;i<allContent.size();i++){
            oneLine =allContent.get(i);
            if(!oneLine.startsWith(TOPUP_COUNTRY_PREFIX)){
                Operator operator;
                data = oneLine.split(TOPUP_SPLIT_DELI);
                if(TransferCode.currencyTopUps.containsKey(data[0])){
                    operatorMap = TransferCode.currencyTopUps.get(data[0]);
                }else{
                    operatorMap = new TreeMap<>();
                }
                if(operatorMap.containsKey(data[1])){
                    operator = operatorMap.get(data[1]);
                }else{
                    operator = new Operator();
                    operator.setDescription(data[1]);
                }
                Log.d("LOADING","list of presets"+ data.length);
                for(int j = 2;j<data.length;j++) {
                    preset = parseValues(data[j]);
                    Log.d("LOADING",preset.getCode()+"..."+preset.getAmount());
                    operator.addPreset(preset);
                }
                operatorMap.put(data[1], operator);
                TransferCode.addTopUpData(data[0], operatorMap);
            }
        }
    }
    private Payee parsePayees(String [] data){
        Payee payee = new Payee();
        payee.setCode(data[2]);
        payee.setAmount(data[5]);
        payee.setName(data[1]);
        payee.setPayeeId(data[3]);
        return payee;
    }
    private void loadBillPayees(){
        ArrayList<String> allContent = openFileAsList("billpayee.txt");

        String oneLine ;
        for(int i =0;i<allContent.size();i++) {
            oneLine = allContent.get(i);
            Log.d("LOADING","data "+oneLine);
            String data [] = oneLine.split(",");
            Log.d("LOADING","data data[0] "+data[0]);
            Log.d("LOADING","data data[0] "+TransferCode.countryNameToCodeMap.containsKey(data[0]));
            if(TransferCode.countryNameToCodeMap.containsKey(data[0])){
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
        }
/*        if (country.getPayees() != null) {
            Log.d(TAG, " country.getPayees() " + country.getPayees().size());
            for (int k = 0; k < country.getPayees().size(); k++) {
                Payee payee = country.getPayees().get(k);
                Log.d(TAG, "  payee.getCode() " + payee.getCode());
                Log.d(TAG, "  payee.getName() " + payee.getName());
                bpc = new TransferCode();
                bpc.setCode(payee.getCode());
                bpc.setAmount(payee.getAmount());
                bpc.setDescription(payee.getName());
                utilities.add(bpc);
            }
            TransferCode.billPaymentCodes = utilities;
            TransferCode.utilityCodes = utilities;
        }*/
    }
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
        loadTopUps();
        loadBillPayees();
        processd = true;
        //TODO  use the mapped object instead of using TransferCode instead
        //TODO reading single file instead of 3
/*        TransferCode bpc;
        for (int i = 0; i < files.length; i++) {
            try {
                // List<<Country> operators = om.readValue(mContext.getAssets().open(files[i]), new TypeReference< List<Records<Country>>>(){});
                List<Country> countries = om.readValue(mContext.getAssets().open(files[i]), new TypeReference<List<Country>>() {
                });
                //Topups
                for (int j = 0; j < countries.size(); j++) {
                    Country country = countries.get(j);

                    Log.d(TAG, " country.getCountryName() " + country.getCountryName());
                    Log.d(TAG, " country.getCountryCode() " + country.getCountryCode());
                    if(!TransferCode.countryMap.containsKey((country.getCountryCode()))){
                        TransferCode.addCountry(country.getCountryCode(),country);
                    }
                    if (country.getOperators() != null) {
                        Log.d(TAG, " country.getOperators() " + country.getOperators().size());
                        for (int k = 0; k < country.getOperators().size(); k++) {
                            Operator operator = country.getOperators().get(k);
                            Log.d(TAG, " operator.getDescription() " + operator.getDescription());
                            for (int l = 0; l < operator.getPresets().size(); l++) {
                                Preset preset = operator.getPresets().get(l);
                                Log.d(TAG, "  preset.getCode() " + preset.getCode());
                                Log.d(TAG, "  preset.getAmount() " + preset.getAmount());
                                bpc = new TransferCode();
                                bpc.setCode(preset.getCode());
                                bpc.setAmount(preset.getAmount());
                                bpc.setDescription(operator.getDescription() + " " + CurrencyUtils.getInstance().formatAmount(preset.getAmount()));
                                topUpAirtimeCodes.add(bpc);
                            }
                        }
                        TransferCode.topupAirtimeCodes = topUpAirtimeCodes;
                        TransferCode.topupcreditCodes = topUpAirtimeCodes;
                        //TODO single list?
                    }
                    //Banks
                    if (country.getBanks() != null) {
                        Log.d(TAG, " country.getBanks() " + country.getBanks().size());
                        for (int k = 0; k < country.getBanks().size(); k++) {
                            Bank bank = country.getBanks().get(k);
                            Log.d(TAG, "  bank.getCode() " + bank.getCode());
                            Log.d(TAG, "  bank.getName() " + bank.getName());
                            bpc = new TransferCode();
                            bpc.setCode(bank.getCode());
                            bpc.setAmount("0");
                            bpc.setDescription(bank.getName());
                            bankCodes.add(bpc);
                        }
                        TransferCode.bankCodes = bankCodes;
                    }
                    //TODO Same list for now
                    //Utilities, cable...
                    if (country.getPayees() != null) {
                        Log.d(TAG, " country.getPayees() " + country.getPayees().size());
                        for (int k = 0; k < country.getPayees().size(); k++) {
                            Payee payee = country.getPayees().get(k);
                            Log.d(TAG, "  payee.getCode() " + payee.getCode());
                            Log.d(TAG, "  payee.getName() " + payee.getName());
                            bpc = new TransferCode();
                            bpc.setCode(payee.getCode());
                            bpc.setAmount(payee.getAmount());
                            bpc.setDescription(payee.getName());
                            utilities.add(bpc);
                        }
                        TransferCode.billPaymentCodes = utilities;
                        TransferCode.utilityCodes = utilities;
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
           *//* result = openFile(files[i]);
            switch (i) {
                case 0:
                    codeLists[i] = parseTopups(result);
                    break;
                case 1:
                    codeLists[i] = parseBanks(result);
                    break;
            }*//*
        }*/
        return null;
    }

    public List<TransferCode> parseResults(String result, String key) {
        List<TransferCode> codes = new ArrayList<TransferCode>();
        try {
            JSONObject topLevel = new JSONObject(result);
            JSONArray countries = topLevel.getJSONArray("records");
            TransferCode bpc;
            String currentOperator;
            for (int i = 0; i < countries.length(); i++) {
                try {
                    JSONObject oneCountry = countries.getJSONObject(i);
                    String countryName = oneCountry.getString(COUNTRY_NAME);
                    String countryCode = oneCountry.getString(COUNTRY_CODE);
                    Log.d(TAG, "countryName : " + countryName + "..." + "countryCode : " + countryCode);
                    JSONArray lists = oneCountry.getJSONArray(key);
                    Log.d(TAG, "key lists size " + lists.length());

                } catch (JSONException e) {
                    // Oops
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, "error parsing json" + e.toString() + result);
        }
        return codes;
    }

    public List<TransferCode> parseBanks(String result) {
        List<TransferCode> bankCodes = new ArrayList<TransferCode>();
        try {
            JSONObject topLevel = new JSONObject(result);
            JSONArray countries = topLevel.getJSONArray("records");
            TransferCode bpc;
            String currentOperator;
            for (int i = 0; i < countries.length(); i++) {
                try {
                    JSONObject oneCountry = countries.getJSONObject(i);
                    String countryName = oneCountry.getString(COUNTRY_NAME);
                    String countryCode = oneCountry.getString(COUNTRY_CODE);
                    Log.d(TAG, "countryName : " + countryName + "..." + "countryCode : " + countryCode);
                    JSONArray operators = oneCountry.getJSONArray(OPERATORS);
                    Log.d(TAG, "operators " + operators.length());
                    for (int j = 0; j < operators.length(); j++) {
                        JSONObject oneOperator = operators.getJSONObject(j);
                        currentOperator = oneOperator.getString(DESCRIPTION);
                        Log.d(TAG, "operators description " + currentOperator);
                        JSONArray presets = oneOperator.getJSONArray(PRESETS);
                        for (int k = 0; k < presets.length(); k++) {
                            JSONObject preset = presets.getJSONObject(k);
                            String amount = preset.getString(AMOUNT);
                            String code = preset.getString(CODE);
                            bpc = new TransferCode();
                            bpc.setCode(code);
                            bpc.setAmount(amount);
                            bpc.setDescription(currentOperator + ":" + code);
                            bankCodes.add(bpc);
                        }
                    }
                } catch (JSONException e) {
                    // Oops
                }
            }
            // TransferCode.topupAirtimeCodes = topUpAirtimeCodes;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bankCodes;
    }


    public List<TransferCode> parseTopups(String result) {
        List<TransferCode> topUpAirtimeCodes = new ArrayList<TransferCode>();
        try {
            JSONObject topLevel = new JSONObject(result);
            JSONArray countries = topLevel.getJSONArray("records");
            TransferCode bpc;
            String currentOperator;

            for (int i = 0; i < countries.length(); i++) {
                try {
                    JSONObject oneCountry = countries.getJSONObject(i);
                    String countryName = oneCountry.getString(COUNTRY_NAME);
                    String countryCode = oneCountry.getString(COUNTRY_CODE);
                    Log.d(TAG, "countryName : " + countryName + "..." + "countryCode : " + countryCode);
                    JSONArray operators = oneCountry.getJSONArray(OPERATORS);
                    Log.d(TAG, "operators " + operators.length());
                    for (int j = 0; j < operators.length(); j++) {
                        JSONObject oneOperator = operators.getJSONObject(j);
                        currentOperator = oneOperator.getString(DESCRIPTION);
                        Log.d(TAG, "operators description " + currentOperator);
                        JSONArray presets = oneOperator.getJSONArray(PRESETS);
                        for (int k = 0; k < presets.length(); k++) {
                            JSONObject preset = presets.getJSONObject(k);
                            String amount = preset.getString(AMOUNT);
                            String code = preset.getString(CODE);
                            bpc = new TransferCode();
                            bpc.setCode(code);
                            bpc.setAmount(amount);
                            bpc.setDescription(currentOperator + ":" + code);
                            topUpAirtimeCodes.add(bpc);
                        }
                    }
                } catch (JSONException e) {
                    // Oops
                }
            }
            // TransferCode.topupAirtimeCodes = topUpAirtimeCodes;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return topUpAirtimeCodes;
    }
    private ArrayList<String> openFileAsList(String file) {
        BufferedReader reader = null;
        ArrayList myLines = new ArrayList();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(mContext.getAssets().open(file), "UTF-8"));

            String mLine = reader.readLine();
            while (mLine != null && !"".equals(mLine)) {
                myLines.add(mLine);
                mLine = reader.readLine();
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
        return myLines;
    }
    private String openFile(String file) {
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(mContext.getAssets().open(file), "UTF-8"));

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
        return content.toString();
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
