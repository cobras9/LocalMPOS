package com.mobilis.android.nfc.jsonModel;

/**
 * Created by lewischao on 17/03/15.
 */
public class Currency {
    private String currency;
    private String currencyCode;
    private String currencyNumber;
    private String currencyDecimal;
    private String groupSeparator;
    private String groupSize;
    private String decimalSeparator;
    private String symbol;
    private String countryName;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyNumber() {
        return currencyNumber;
    }

    public void setCurrencyNumber(String currencyNumber) {
        this.currencyNumber = currencyNumber;
    }

    public String getCurrencyDecimal() {
        return currencyDecimal;
    }
    public int getCurrencyDecimalInt() {
        return Integer.parseInt(currencyDecimal);
    }
    public void setCurrencyDecimal(String currencyDecimal) {
        this.currencyDecimal = currencyDecimal;
    }

    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(String decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public String getGroupSeparator() {
        return groupSeparator;
    }

    public void setGroupSeparator(String groupSeparator) {
        this.groupSeparator = groupSeparator;
    }

    public String getGroupSize() {
        return groupSize;
    }
    public int getGroupSizeInt() {
        return Integer.parseInt(groupSize);
    }

    public void setGroupSize(String groupSize) {
        this.groupSize = groupSize;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
