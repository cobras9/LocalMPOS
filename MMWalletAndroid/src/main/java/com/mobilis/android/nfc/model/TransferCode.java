package com.mobilis.android.nfc.model;

import com.mobilis.android.nfc.jsonModel.Country;
import com.mobilis.android.nfc.jsonModel.Currency;
import com.mobilis.android.nfc.jsonModel.Operator;
import com.mobilis.android.nfc.jsonModel.Payee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class TransferCode {

	private String code;
	private String description;
	private String amount;
	public static List<TransferCode> billPaymentCodes = new ArrayList<TransferCode>();
	public static List<TransferCode> utilityCodes = new ArrayList<TransferCode>();

	public static List<TransferCode> bankCodes = new ArrayList<TransferCode>();
	public static List<TransferCode> topupAirtimeCodes = new ArrayList<TransferCode>();
    public static List<TransferCode> topupcreditCodes = new ArrayList<TransferCode>();

    public static HashMap<String,String> currencyCodesMap  = new HashMap<>();
    public static TreeMap<String,Currency> currencyMap;
    public static HashMap<String,Country> countryMap  = new HashMap<>();
	public static HashMap<String,String> countryNameToCodeMap  = new HashMap<>();
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public static void addCountry(String key, Country country){
        countryMap.put(key,country);
    }

	public static HashMap<String,TreeMap<String,Operator>> currencyTopUps = new HashMap<>();

	public static void addTopUpData(String currencyCode, TreeMap<String ,Operator> operators){
		currencyTopUps.put(currencyCode,operators);
	}
	public static HashMap<String,ArrayList<Payee>> currencyBillPayees = new HashMap<>();
	public static void addBillPayee(String currencyCode, ArrayList<Payee> payees){
		currencyBillPayees.put(currencyCode,payees);
	}

}
