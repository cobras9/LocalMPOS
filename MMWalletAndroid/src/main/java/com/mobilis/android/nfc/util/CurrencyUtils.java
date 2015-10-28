package com.mobilis.android.nfc.util;

import android.util.Log;

import com.mobilis.android.nfc.jsonModel.Currency;
import com.mobilis.android.nfc.model.TransferCode;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by lewischao on 18/03/15.
 */
public class CurrencyUtils {
    private static String TAG = "CurrencyUtils";
    private static String formatPattern;
    private static DecimalFormat formatter;
    private static CurrencyUtils amountUtils;
    private static int index = 1;
    private static String currencyCode;
    public static int decimalPlaces = 0;

    private static CurrencyUtils currencyUtils;

    public static CurrencyUtils getInstance() {
        if (currencyUtils == null) {
            currencyUtils = new CurrencyUtils();
        }
        return currencyUtils;
    }

    private CurrencyUtils() {
        setCurrency();
    }
    public String getQuickAmount(String amount){
        try{
            double acutalAmount = Double.parseDouble(amount)*index;
            formatter.setParseBigDecimal(false);
            return formatter.format(acutalAmount/ index);
        }catch(Exception e){
            return amount;
        }
    }
    public String formatAmount(String amount) {
        try {
            amount = amount.replaceAll("[//;$()*#\\+\\-,.]", "");
        }catch(Exception e){
            Log.e(this.getClass().getSimpleName() , "formatAmount error  "+e.toString());
        }
        String testAmount = amount.replaceAll("0", "");
        if (testAmount.equals("")) {
            return "0";
        } else if (amount.equals("")) {
            return "0";
        } else {
            if (amount.length() > 9) {
                BigDecimal bigAmount = new BigDecimal(amount);
                bigAmount = bigAmount.movePointLeft(decimalPlaces);
                formatter.setParseBigDecimal(true);
                return formatter.format(bigAmount);
            } else {
                formatter.setParseBigDecimal(false);
                return formatter.format(Double.parseDouble(amount) / index);
            }
        }
    }

    public String getCleanAmount(String amount) {
        return amount.toString().replaceAll("[$,]", "");
    }

    public String getCurrencyFormat(Currency currency) {
        String format = "#" + currency.getGroupSeparator();
        for (int i = 0; i < 2; i++) {
            for (int k = 0; k < currency.getGroupSizeInt(); k++) {
                format += "#";

            }
            if (i != 1) {
                format += currency.getGroupSeparator();
            }
        }
        if (currency.getCurrencyDecimalInt() > 0) {
            format += currency.getDecimalSeparator();
            for (int i = 0; i < currency.getCurrencyDecimalInt(); i++) {
                format += "0";
                index = index * 10;
            }
        }else{

        }
        return format;
    }
    public String getCurrencyLabel(){

        currencyCode = Constants.currency;
        if (TransferCode.currencyMap.containsKey(currencyCode)) {
            Currency currency = TransferCode.currencyMap.get(currencyCode);
            Log.d(TAG, "setCurrency found getGroupSeparator " + currency.getSymbol());
            return currency.getSymbol();
        }else{
            return "";
        }
    }
    public void setCurrency() {
        currencyCode = Constants.currency;
        Log.d(TAG, "setCurrency currencyCode " + currencyCode);
        if (TransferCode.currencyMap.containsKey(currencyCode)) {
            Currency currency = TransferCode.currencyMap.get(currencyCode);
            Log.d(TAG, "setCurrency found getDecimalSeparator " + currency.getDecimalSeparator());
            Log.d(TAG, "setCurrency found getGroupSeparator " + currency.getGroupSeparator());
            try {
                decimalPlaces = currency.getCurrencyDecimalInt();
                Log.d(TAG, "setCurrency found decimalPlaces " + decimalPlaces);
            } catch (Exception e) {
                Log.e(TAG, "setCurrency decimalPlaces " + decimalPlaces);
            }
            index = 1;
            formatPattern = getCurrencyFormat(currency);
            Log.d(TAG, "setCurrency formatPattern " + formatPattern);
/*            if(decimalPlaces>0) {

                formatPattern = "#,###,###,##0.";
                for (int i = 0; i < decimalPlaces; i++) {
                    formatPattern += "0";
                    index=index*10;
                }
            }else{
                formatPattern = "#,###,###,##0";
            }*/

        }
        Log.d(TAG, "setCurrency decimalPlaces " + decimalPlaces);
        DecimalFormatSymbols dfs = new  DecimalFormatSymbols();
        dfs.setGroupingSeparator(',');
        if(decimalPlaces>0){
            formatter = new DecimalFormat(formatPattern);
        }else{
            formatter = new DecimalFormat(formatPattern,dfs);
        }
        Log.d(TAG, "setCurrency formatPattern " + formatPattern);
        Log.d(TAG, "setCurrency formatPattern index " + index);
        Log.d(TAG, "setCurrency formatPattern decimalPlaces " + decimalPlaces);
    }
}
