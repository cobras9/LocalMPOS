package com.mobilis.android.nfc.domain;

import com.mobilis.android.nfc.util.CurrencyUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ahmed on 9/06/14.
 */
public class AccountBalance implements Serializable{

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;

    public ArrayList<AccountBalance> getAllBalances() {
        return allBalances;
    }
    public void addAccountBalance(AccountBalance oneBalance){
        if(oneBalance.getValue() != null){
            try {
                oneBalance.setValue(CurrencyUtils.getInstance().getCurrencyLabel()+" "+CurrencyUtils.getInstance().formatAmount(oneBalance.getValue()));
            }catch(Exception e){}
        }
        this.allBalances.add(oneBalance);
    }
    public void setAllBalances(ArrayList<AccountBalance> allBalances) {
        this.allBalances = allBalances;
    }

    public ArrayList<AccountBalance> allBalances = new ArrayList<AccountBalance>();

}
