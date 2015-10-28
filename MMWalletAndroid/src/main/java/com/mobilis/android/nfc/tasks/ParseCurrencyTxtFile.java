package com.mobilis.android.nfc.tasks;

import com.mobilis.android.nfc.jsonModel.Currency;

import java.util.ArrayList;

/**
 * Created by lewischao on 27/03/15.
 */
public class ParseCurrencyTxtFile {


    /**
     * Data follows this structure for each line
     * New Zealand|NZ|New Zealand dollar|NZD|554|64|2|3|,|.|NZ$
     * <p/>
     * 1(Country Name)|
     * 2(Country Code)|
     * 3(Currency description)|
     * 4(Currency alphabetic code)|
     * 5(Currency numeric code)|
     * 6(Dial Prefix)|
     * 7(Fraction digits)|
     * 8(Group size)|
     * 9(Group separator)|
     * 10(Decimal  separator)|
     * 11(Symbol)|
     *
     * @param allData lines containing all currency details
     * @return a list of Currency model for mapping
     */

    private static int COUNTRY_NAME = 0;
    private static int COUNTRY_CODE = 1;
    private static int CURRENCY_DESCRIPTION = 2;
    private static int CURRENCY_CODE = 3;
    private static int CURRENCY_NUMBER = 4;
    private static int DIAL_FIX = 5;
    private static int FRACTION_DIGITS = 6;
    private static int GROUP_SIZE = 7;
    private static int GROUP_SEPARATOR = 8;
    private static int DECIMAL_SEPARATOR = 9;
    private static int SYMBOL = 10;

    public static ArrayList<Currency> parseCurrency(ArrayList<String> allData) {
        ArrayList<Currency> allCurrencies = new ArrayList<>();
        String oneLineData;
        String currencyData[];
        Currency currency;
        for (int i = 0; i < allData.size(); i++) {
            oneLineData = allData.get(i);
            if (oneLineData != null && !oneLineData.equals("")) {
                if (oneLineData.contains("|")) {
                    currencyData = oneLineData.split("\\|");
                    currency = new Currency();
/*                    Log.d("Lewis", " oneLineData " + oneLineData);
                    Log.d("Lewis", " currencyData length " + currencyData.length);
                    Log.d("Lewis", " currencyData COUNTRY_NAME " + currencyData[COUNTRY_NAME]);
                    Log.d("Lewis", " currencyData COUNTRY_CODE " + currencyData[COUNTRY_CODE]);
                    Log.d("Lewis", " currencyData CURRENCY_DESCRIPTION " + currencyData[CURRENCY_DESCRIPTION]);
                    Log.d("Lewis", " currencyData CURRENCY_CODE " + currencyData[CURRENCY_CODE]);
                    Log.d("Lewis", " currencyData CURRENCY_NUMBER " + currencyData[CURRENCY_NUMBER]);
                    Log.d("Lewis", " currencyData DIAL_FIX " + currencyData[DIAL_FIX]);
                    Log.d("Lewis", " currencyData FRACTION_DIGITS " + currencyData[FRACTION_DIGITS]);
                    Log.d("Lewis", " currencyData GROUP_SIZE " + currencyData[GROUP_SIZE]);
                    Log.d("Lewis", " currencyData GROUP_SEPARATOR " + currencyData[GROUP_SEPARATOR]);
                    Log.d("Lewis", " currencyData DECIMAL_SEPARATOR " + currencyData[DECIMAL_SEPARATOR]);
                    Log.d("Lewis", " currencyData SYMBOL " + currencyData[SYMBOL]);*/
                    currency.setCountryName(currencyData[COUNTRY_NAME]);
                    currency.setCurrency(currencyData[CURRENCY_DESCRIPTION]);
                    currency.setCurrencyCode(currencyData[CURRENCY_CODE]);
                    currency.setCurrencyDecimal(currencyData[FRACTION_DIGITS]);
                    currency.setCurrencyNumber(currencyData[CURRENCY_NUMBER]);
                    currency.setGroupSeparator(currencyData[GROUP_SEPARATOR]);
                    currency.setGroupSize(currencyData[GROUP_SIZE]);
                    currency.setDecimalSeparator(currencyData[DECIMAL_SEPARATOR]);
                    currency.setSymbol(currencyData[SYMBOL]);
                    //TODO adding rest of the details
                    allCurrencies.add(currency);
                }
            }
        }
        return allCurrencies;
    }

}
