package com.mobilis.android.nfc.util;

import android.content.Context;
import android.util.Log;

import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.slidemenu.utils.CustomerType;
import com.mobilis.android.nfc.slidemenu.utils.IDType;
import com.mobilis.android.nfc.slidemenu.utils.LoginResponseConstants;
import com.mobilis.android.nfc.slidemenu.utils.WalletMenuOptions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Locale;

import static com.mobilis.android.nfc.util.Constants.getApplicationContext;

public class BitsMapper {
    public static final int GENERALFLAGS_TAGOWNERNAME = 8;
    private static final int MENU_G_TOPUP_POSITION = 0;
    private static final int MENU_G_TOPUP_DIRECT_TOPUP_POSITION = 1;
    private static final int MENU_G_TOPUP_INTERNATIONAL_TOP_UP_POSITION = 2;
    private static final int MENU_G_ELECTRONIC_VOUCHER_POSITION = 3;
    private static final int MENU_G_EVD_POSITION = 5;
    private static final int MENU_G_BUY_EVD_POSITION = 6;
    private static final int MENU_G_BUY_BULK_EVD_POSITION = 7;

    private static final int MENU_F_REGISTRATION_SERVICE = 0;
    private static final int MENU_F_CUSTOMER = 1;
    private static final int MENU_F_CUSTOMER_LOOKUP = 2;
    private static final int MENU_F_CUSTOMER_SERVICES_EXISTING = 3;
    private static final int MENU_F_REGISTERING_TAG = 4;
    private static final int MENU_F_REGISTERING_TAG_PRIMARY = 5;
    private static final int MENU_F_REGISTERING_TAG_SECONDARY = 6;
    private static final int MENU_F_REGISTERING_TAG_REPLACE = 7;
    private static final int MENU_F_ADDITIONAL_DATA = 8;
    private static final int MENU_F_UPDATE_CUSTOMER_DETAILS = 9;

    private static final int MENU_B_DEPOSIT_CASH = 0;
    private static final int MENU_B_WITHDRAW_CASH = 1;
    private static final int MENU_B_RECEIVE_PAYMENT_PURCHASE = 2;
    private static final int MENU_B_SEND_MONEY = 3;
    private static final int MENU_B_BILL_PAYMENTS = 4;
    private static final int MENU_B_CASH_OUNT_VOUCHER_REDEEM_VOUCHER = 5;
    private static final int MENU_B_CREDIT_CASH_IN = 7;
    private static final int MENU_C_BALANCE = 2;
    private static final int MENU_C_CHANGE_PIN = 3;
    private static final int MENU_C_GENERATE_TOKEN = 11;
    private static final int MENU_C_SELECT_CURRENCY = 14;
    private static final int MENU_C_CONFIG_SERVER = 15;
    private static final int MENU_C_COMMISION_TRANSFER = 12;
    private static final int MENU_C_MERCHANT_VOUCHER = 13;


    private static Context context = getApplicationContext();
    private static final String LOG_TAG = BitsMapper.class.getSimpleName();
    private static final int MENU_D_SHOW_BALANCE = 1;
    private static final int MENU_D_CUSTOMER_CHANGE_PIN = 3;

    public static boolean isShowBalanceAvailable(String menuC, String menuD) {
        //  String menuD3 = getHexRepresentationFromByte(menuD, 3);
        //if(menuD3.charAt(2) == '1')
        boolean menuCBool = parseBitSet(menuC, MENU_C_BALANCE);
        boolean menuDBool = parseBitSet(menuD, MENU_D_SHOW_BALANCE);
/*        if () {
            return true;
        } else {
            return false;
        }
        if (parseBitSet(menuD, MENU_D_SHOW_BALANCE)) {
            return true;
        } else {
            return false;
        }*/
        return (menuCBool || menuDBool);
    }

    //<editor-fold desc="Menu B">
    public static ArrayList<String> getMenuBOptions(String menuB, String menuF) {
        //menuF=0020;
//        menuB = "FFFF";
//        menuF = "FFFF";

        if (LoginResponseConstants.walletOptions == null)
            LoginResponseConstants.walletOptions = new WalletMenuOptions();
        Log.d(LOG_TAG, "getMenuBOptions will go get byte position 3 in menuB");


        //String hexB3 = getHexRepresentationFromByte(menuB, 3);
        //	String hexB2 = getHexRepresentationFromByte(menuB, 2);
//        String hexF2 = getHexRepresentationFromByte(menuF, 2);

        //Log.d(LOG_TAG, "hexB3: "+hexB3);
        //Log.d(LOG_TAG, "hexB2: "+hexB2);

        ArrayList<String> walletList = new ArrayList<String>();

        //if(hexB3.charAt(0) == '1') {
        if (parseBitSet(menuB, MENU_B_SEND_MONEY)) {
            Log.d(LOG_TAG, "Make Payment ON");
            walletList.add(getResString(R.string.MAKE_PAYMENT));
            LoginResponseConstants.walletOptions.setMakePayment(true);
        } else {
            Log.d(LOG_TAG, "Make Payment OFF");
            LoginResponseConstants.walletOptions.setMakePayment(false);
        }

        //if(hexB3.charAt(1) == '1') {
        if (parseBitSet(menuB, MENU_B_RECEIVE_PAYMENT_PURCHASE)) {
            Log.d(LOG_TAG, "Receive Payment (Purchases or Merchant Payment) ON");
            walletList.add(getResString(R.string.RECEIVE_PAYMENT));
            LoginResponseConstants.walletOptions.setReceivePaymentAvailable(true);
        } else {
            Log.d(LOG_TAG, "Receive Payment (Purchases or Merchant Payment)  OFF");
            LoginResponseConstants.walletOptions.setReceivePaymentAvailable(false);
        }

        //if(hexB3.charAt(2) == '1') {
        if (parseBitSet(menuB, MENU_B_WITHDRAW_CASH)) {
            Log.d(LOG_TAG, "Give Cash Out (Withdraw) ON");
            walletList.add(getResString(R.string.GIVE_CASH_OUT));
            LoginResponseConstants.walletOptions.setGiveCashOutAvailable(true);
        } else {
            Log.d(LOG_TAG, "Give Cash Out (Withdraw) OFF");
            LoginResponseConstants.walletOptions.setGiveCashOutAvailable(false);
        }

        //if(hexB3.charAt(3) == '1') {
        if (parseBitSet(menuB, MENU_B_DEPOSIT_CASH)) {
            Log.d(LOG_TAG, "Receive Cash In (Deposit) ON");
            walletList.add(getResString(R.string.RECEIVE_CASH_IN));
            LoginResponseConstants.walletOptions.setReceiveCashInAvailable(true);
        } else {
            Log.d(LOG_TAG, "Receive Cash In (Deposit) OFF");
            LoginResponseConstants.walletOptions.setReceiveCashInAvailable(false);
        }

        //if(hexB2.charAt(0) == '1') {
        if (parseBitSet(menuB, MENU_B_CREDIT_CASH_IN)) {
            Log.d(LOG_TAG, "Cash In via Credit Card");
            walletList.add(getResString(R.string.RECEIVE_CASH_IN_CC));
            LoginResponseConstants.walletOptions.setReceiveCashInCCAvailable(true);
        }
//
        //if(hexB2.charAt(1) == '1') {
        if (parseBitSet(menuB, MENU_B_CASH_OUNT_VOUCHER_REDEEM_VOUCHER)) {
            Log.d(LOG_TAG, "REDEEM VOUCHER ON");
            walletList.add(getResString(R.string.REDEEM_VOUCHER));
            LoginResponseConstants.walletOptions.setRedeemVoucherAvailable(true);
        } else {
            LoginResponseConstants.walletOptions.setRedeemVoucherAvailable(false);
            Log.d(LOG_TAG, "REDEEM VOUCHER OFF");
        }

        //if(hexB2.charAt(2) == '1') {
        if (parseBitSet(menuB, MENU_B_CASH_OUNT_VOUCHER_REDEEM_VOUCHER)) {
            Log.d(LOG_TAG, "Cash out voucher ON");
            walletList.add(getResString(R.string.CASH_OUT_VOUCHER));
            LoginResponseConstants.walletOptions.setCashoutVouchers(true);
        } else {
            Log.d(LOG_TAG, "Cash out voucher OFF");
            LoginResponseConstants.walletOptions.setCashoutVouchers(false);
        }

        //if(hexB2.charAt(3) == '1') {
        if (parseBitSet(menuB, MENU_B_BILL_PAYMENTS)) {
            Log.d(LOG_TAG, "Bill Payments ON");
            walletList.add(getResString(R.string.Drawer_BillPayments));

            LoginResponseConstants.walletOptions.setBillPayments(true);
        } else {
            Log.d(LOG_TAG, "Bill Payments OFF");
            LoginResponseConstants.walletOptions.setBillPayments(false);
        }
        ArrayList<String> menuFList = getMenuFOptions(menuF);
        for (String option : menuFList) {
            walletList.add(option);
        }

//        if(hexF2.charAt(2) == '1') {
//            Log.d(LOG_TAG,"Tag registration (Primary) ON");
//            SMConstants.walletOptions.setRegistrationAvailable(true);
//        } else {
//            Log.d(LOG_TAG,"Tag registration (Primary) OFF");
//            SMConstants.walletOptions.setRegistrationAvailable(false);
//        }

        return walletList;
    }
    //</editor-fold>

    //<editor-fold desc="Menu C">
    public static ArrayList<String> getMenuCOptions(String menuC) {
//        menuC="FFFF";
        if (LoginResponseConstants.walletOptions == null)
            LoginResponseConstants.walletOptions = new WalletMenuOptions();

        //String hexC3 = getHexRepresentationFromByte(menuC, 3);
        //String hexC1 = getHexRepresentationFromByte(menuC, 1);
        // String hexC0 = getHexRepresentationFromByte(menuC, 0);
        //Log.d(LOG_TAG, "hexC0: "+hexC0);
        //Log.d(LOG_TAG, "hexC1: " + hexC1);
        //Log.d(LOG_TAG, "hexC3: " + hexC3);

        ArrayList<String> arrayList = new ArrayList<String>();


        /**  MenuC, First Byte from right(first four bits), bit at position 3 represents the value for Change PIN Feature**/
        //if (hexC3.charAt(0) == '1') {
        if (parseBitSet(menuC, MENU_C_CHANGE_PIN)) {
            arrayList.add(getResString(R.string.CHANGE_PIN));
            LoginResponseConstants.walletOptions.setChangePin(true);
        } else
            LoginResponseConstants.walletOptions.setChangePin(false);

        /**  MenuC, Third Byte(first four bits), bit at position 2 represents the value for Search for Transactions Feature**/


        //if (hexC1.charAt(3) == '1') {
        if (parseBitSet(menuC, MENU_C_GENERATE_TOKEN)) {
            arrayList.add(getResString(R.string.GENERATE_TOKEN));
            LoginResponseConstants.walletOptions.setGenerateTokenAvailable(true);
        } else {
            if (arrayList.contains(getResString(R.string.GENERATE_TOKEN))) {
                arrayList.remove(getResString(R.string.GENERATE_TOKEN));
            }
            LoginResponseConstants.walletOptions.setGenerateTokenAvailable(false);
        }

        //if (hexC0.charAt(3) == '1') {
        if (parseBitSet(menuC, MENU_C_CONFIG_SERVER)) {
            arrayList.add(getResString(R.string.CONFIG_SERVER));
            LoginResponseConstants.walletOptions.setServerConfigurable(true);
            Log.d(LOG_TAG, "Turning CONFIG_SERVER on");
        } else {
            if (arrayList.contains(getResString(R.string.CONFIG_SERVER))) {
                arrayList.remove(getResString(R.string.CONFIG_SERVER));
            }
            LoginResponseConstants.walletOptions.setServerConfigurable(false);
            Log.d(LOG_TAG, "Turning CONFIG_SERVER off");
        }

        // if (hexC0.charAt(2) == '1') {
        if (parseBitSet(menuC, MENU_C_SELECT_CURRENCY)) {
            arrayList.add(getResString(R.string.SELECT_CURRENCY));
            LoginResponseConstants.walletOptions.setSelectCurrencyAvailable(true);
            Log.d(LOG_TAG, "Turning SELECT_CURRENCY on");
        } else {
            if (arrayList.contains(getResString(R.string.SELECT_CURRENCY))) {
                arrayList.remove(getResString(R.string.SELECT_CURRENCY));
            }
            LoginResponseConstants.walletOptions.setSelectCurrencyAvailable(false);
            Log.d(LOG_TAG, "Turning SELECT_CURRENCY off");
        }


        if (parseBitSet(menuC, MENU_C_COMMISION_TRANSFER)) {
            arrayList.add(getResString(R.string.COMMISSION_TRANSFER));
            LoginResponseConstants.walletOptions.setCommissionTransferAvailable(true);
        } else {
            if (arrayList.contains(getResString(R.string.COMMISSION_TRANSFER))) {
                arrayList.remove(getResString(R.string.COMMISSION_TRANSFER));
            }
            LoginResponseConstants.walletOptions.setCommissionTransferAvailable(false);
        }

        if (parseBitSet(menuC, MENU_C_MERCHANT_VOUCHER)) {
            arrayList.add(getResString(R.string.MERCHANT_VOUCHER));
            LoginResponseConstants.walletOptions.setMerchantVoucher(true);
        } else {
            if (arrayList.contains(getResString(R.string.MERCHANT_VOUCHER))) {
                arrayList.remove(getResString(R.string.MERCHANT_VOUCHER));
            }
            LoginResponseConstants.walletOptions.setMerchantVoucher(false);
        }
        return arrayList;
    }
    //</editor-fold>

    //<editor-fold desc="Menu G">
    public static ArrayList<String> getMenuGOptions(String menuG) {

        Log.d(LOG_TAG, "getMenuGOptions is called");
        Log.d(LOG_TAG, "param passed to above method is: " + menuG);
        if (LoginResponseConstants.walletOptions == null)
            LoginResponseConstants.walletOptions = new WalletMenuOptions();

        // String hexG2 = getHexRepresentationFromByte(menuG, 2);
        //String hexG3 = getHexRepresentationFromByte(menuG, 3);
//        String hexG4 = getHexRepresentationFromByte(menuG, 4);

        //Log.d(LOG_TAG, "hexG2: " + hexG2);
        //Log.d(LOG_TAG, "hexG3: " + hexG3);

        ArrayList<String> arrayList = new ArrayList<String>();

        //   Log.d(LOG_TAG, "hexG2.charAt(3): " + hexG2.charAt(3));
        // Log.d(LOG_TAG, "hexG3.charAt(0): " + hexG3.charAt(0));

        /**  MenuG, Fourth Byte..third bit from the left**/
        // if (hexG2.charAt(3) == '1') {
        if (parseBitSet(menuG, MENU_G_TOPUP_POSITION) || parseBitSet(menuG, MENU_G_TOPUP_DIRECT_TOPUP_POSITION) || parseBitSet(menuG, MENU_G_TOPUP_INTERNATIONAL_TOP_UP_POSITION)) {
            LoginResponseConstants.walletOptions.setSendTopup(true);
            Log.d(LOG_TAG, "Send Top up is turned on");
        } else {
            Log.d(LOG_TAG, "Send Top up is turned off");
            LoginResponseConstants.walletOptions.setSendTopup(false);
        }

        if (parseBitSet(menuG, MENU_G_ELECTRONIC_VOUCHER_POSITION)) {
            Log.d(LOG_TAG, "Electronic Vouchers are turned on");
            LoginResponseConstants.walletOptions.setElectronicVouchersAvailable(true);
        } else {
            Log.d(LOG_TAG, "Electronic Vouchers are turned off");
            LoginResponseConstants.walletOptions.setElectronicVouchersAvailable(false);
        }
        if (parseBitSet(menuG, MENU_G_EVD_POSITION)) {
            LoginResponseConstants.walletOptions.setTxfEVDAvailable(true);
        } else {
            LoginResponseConstants.walletOptions.setTxfEVDAvailable(false);
        }

        if (parseBitSet(menuG, MENU_G_BUY_EVD_POSITION)) {
            LoginResponseConstants.walletOptions.setBuyVoucherAvailable(true);
        } else {
            LoginResponseConstants.walletOptions.setBuyVoucherAvailable(false);
        }

        if (parseBitSet(menuG, MENU_G_BUY_BULK_EVD_POSITION)) {
            LoginResponseConstants.walletOptions.setBuyBulkVoucherAvailable(true);
        } else {
            LoginResponseConstants.walletOptions.setBuyBulkVoucherAvailable(false);
        }
        Log.d(LOG_TAG, "Conclusion from above..do we have top up set to true? " + LoginResponseConstants.walletOptions.isSendTopup());

        return arrayList;
    }
    //</editor-fold>

    //<editor-fold desc="Menu F">
    public static ArrayList<String> getMenuFOptions(String menuF) {

        if (LoginResponseConstants.walletOptions == null)
            LoginResponseConstants.walletOptions = new WalletMenuOptions();
//        menuF="1111";
//        menuF="0040";
        Log.d(LOG_TAG, "Menu F Options menuF is: " + menuF);
        //String hexF3 = getHexRepresentationFromByte(menuF, 3);
        //String hexF2 = getHexRepresentationFromByte(menuF, 2);
        //String hexF1 = getHexRepresentationFromByte(menuF, 1);
        //String hexF0 = getHexRepresentationFromByte(menuF, 0);
//        hexF3 = "0010";

        //Log.d(LOG_TAG, "hexF3: " + hexF3);
        //Log.d(LOG_TAG, "hexF2: " + hexF2);
        //Log.d(LOG_TAG, "hexF1: " + hexF1);

        ArrayList<String> arrRegList = new ArrayList<String>();

        // if(hexF3.charAt(0) == '1') {
        if (parseBitSet(menuF, MENU_F_REGISTRATION_SERVICE)) {
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES) + " ON");
            arrRegList.add(getResString(R.string.REG_SERVICES));
            LoginResponseConstants.walletOptions.setRegistrationServices(true);
        } else {
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES) + " OFF");
        }

        //if(hexF3.charAt(1) == '1') {
        if (parseBitSet(menuF, MENU_F_CUSTOMER)) {
            Log.d(LOG_TAG, "Customer Registration" + " ON");
            arrRegList.add(getResString(R.string.REG_SERVICES_CUSTOMER));
            LoginResponseConstants.walletOptions.setCustomerRegistrationAvailable(true);
        } else {
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES_CUSTOMER) + " OFF");
        }

        //if(hexF3.charAt(2) == '1') {
        if (parseBitSet(menuF, MENU_F_CUSTOMER_LOOKUP)) {
            Log.d(LOG_TAG, "Customer Lookup" + " ON");
            arrRegList.add(getResString(R.string.REG_SERVICES_NEW));
            LoginResponseConstants.walletOptions.setCustomerLookupAvailable(true);
        } else {
            Log.d(LOG_TAG, "CustomerLookup" + " OFF");
        }

        //if(hexF3.charAt(3) == '1') {
        if (parseBitSet(menuF, MENU_F_CUSTOMER_SERVICES_EXISTING)) {
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES_EXISTING) + " ON");
            arrRegList.add(getResString(R.string.REG_SERVICES_EXISTING));
            LoginResponseConstants.walletOptions.setRegServicesCustomerExisting(true);
        } else {
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES_EXISTING) + " OFF");
        }

        /** TAG Registration **/
        //if(hexF2.charAt(3) == '1') {
        if (parseBitSet(menuF, MENU_F_REGISTERING_TAG)) {
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES_TAGS) + " ON");
            arrRegList.add(getResString(R.string.REG_SERVICES_TAGS));
            LoginResponseConstants.walletOptions.setRegServicesTag(true);
        } else {
            LoginResponseConstants.walletOptions.setRegServicesTag(false);
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES_TAGS) + " OFF");
        }

        //if(hexF2.charAt(2) == '1') {
        if (parseBitSet(menuF, MENU_F_REGISTERING_TAG_PRIMARY)) {
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES_TAGS_PRIMARY) + " ON");
            arrRegList.add(getResString(R.string.REG_SERVICES_TAGS_PRIMARY));
            LoginResponseConstants.walletOptions.setRegServicesTagPrimary(true);
        } else {
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES_TAGS_PRIMARY) + " OFF");
            LoginResponseConstants.walletOptions.setRegServicesTagPrimary(false);
        }

        //if(hexF2.charAt(1) == '1') {
        if (parseBitSet(menuF, MENU_F_REGISTERING_TAG_SECONDARY)) {
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES_TAGS_SECONDARY) + " ON");
            arrRegList.add(getResString(R.string.REG_SERVICES_TAGS_SECONDARY));
            LoginResponseConstants.walletOptions.setRegServicesTagSecondary(true);
        } else {
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES_TAGS_SECONDARY) + " OFF");
            LoginResponseConstants.walletOptions.setRegServicesTagSecondary(false);
        }

        // if(hexF2.charAt(0) == '1') {
        if (parseBitSet(menuF, MENU_F_REGISTERING_TAG_REPLACE)) {
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES_TAGS_REPLACE) + " ON");
            arrRegList.add(getResString(R.string.REG_SERVICES_TAGS_REPLACE));
            LoginResponseConstants.walletOptions.setRegServicesTagReplace(true);
        } else {
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES_TAGS_REPLACE) + " OFF");
            LoginResponseConstants.walletOptions.setRegServicesTagReplace(false);
        }
        /** END Of TAG Registration **/

        //if(hexF1.charAt(0) == '1') {
        if (parseBitSet(menuF, MENU_F_ADDITIONAL_DATA)) {
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES_ADDITIONAL) + " ON");
            arrRegList.add(getResString(R.string.REG_SERVICES_ADDITIONAL));
            LoginResponseConstants.walletOptions.setRegServicesAdditional(true);
        } else {
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES_ADDITIONAL) + " OFF");
        }

        //if(hexF1.charAt(1) == '1') {
        if (parseBitSet(menuF, MENU_F_UPDATE_CUSTOMER_DETAILS)) {
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES_CUSTOMER_DETAILS) + " ON");
            arrRegList.add(getResString(R.string.REG_SERVICES_CUSTOMER_DETAILS));
            LoginResponseConstants.walletOptions.setRegServicesCustomerDetails(true);
        } else {
            Log.d(LOG_TAG, getResString(R.string.REG_SERVICES_CUSTOMER_DETAILS) + " OFF");
        }

        return arrRegList;
    }
    //</editor-fold>

    /**
     * Convert menu to long for checking bits
     */
    public static boolean parseBitSet(String menu, int bytePosition) {
/*        Log.d(LOG_TAG, "parseBitSet() " + menu);
        boolean menuSet = false;
        try {
            BigInteger menuHex = new BigInteger(menu, 16);
            Log.d(LOG_TAG, "parseBitSet() " + menuHex + " testBit " + bytePosition + "   " + menuHex.testBit(bytePosition));
            if (menuHex.testBit(bytePosition)) {
                menuSet = true;
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "parseBitSet() error  " + menu + "\n" + e.toString());
        }*/
        return parseBitSet(menu, bytePosition, 16);
    }

    public static boolean parseBitSet(String menu, int bytePosition, int radix) {
        Log.d(LOG_TAG, "parseBitSet() " + menu);
        boolean menuSet = false;
        try {
            BigInteger menuHex = new BigInteger(menu, radix);
            Log.d(LOG_TAG, "parseBitSet() " + menuHex + " testBit " + bytePosition + "   " + menuHex.testBit(bytePosition));
            if (menuHex.testBit(bytePosition)) {
                menuSet = true;
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "parseBitSet() error  " + menu + "\n" + e.toString());
        }
        return menuSet;
    }
/*	*/

    /**
     * converts the position in the menu to four bits
     */
/*    public static String getHexRepresentationFromByte(String menu, int bytePosition) {

		Log.d(LOG_TAG, "getHexRepresentationFromByte() menu is: "+menu);
		StringBuffer buffer  = new StringBuffer();
		buffer.append(menu.charAt(bytePosition));
		Log.d(LOG_TAG, "getHexRepresentationFromByte buffer is: "+buffer.toString());
		int decimalValue = Integer.parseInt(new String(buffer),16);
		Log.d(LOG_TAG, "---------------------");
		if(Integer.toBinaryString(decimalValue).length() < 4)
			return addBits(Integer.toBinaryString(decimalValue));
		else
			return Integer.toBinaryString(decimalValue);
		
	}*/
    public static ArrayList<IDType> resolveIdTypesFromBytes(String Hex) {

        Hex = Hex.toUpperCase(Locale.US);
        ArrayList<IDType> idTypes = new ArrayList<IDType>();
        try {
            int i = Integer.parseInt(Hex, 16);
            String Bin = Integer.toBinaryString(i);
            Log.d("AhmedBIN", "resolveIdTypesFromBytes Hex is: " + Hex + " Bin is: " + Bin);
            if (Bin.length() <= 1)
                return null;
            if (Bin.charAt(Bin.length() - 1) == '1')
                idTypes.add(new IDType(IDType.TYPE.PASSPORT));
            if (Bin.charAt(Bin.length() - 2) == '1')
                idTypes.add(new IDType(IDType.TYPE.DRIVER_LICENCE));
            if (Bin.charAt(Bin.length() - 3) == '1')
                idTypes.add(new IDType(IDType.TYPE.TAZKIRA));
            if (Bin.charAt(Bin.length() - 4) == '1')
                idTypes.add(new IDType(IDType.TYPE.NATIONAL_ID));
            if (Bin.charAt(Bin.length() - 5) == '1')
                idTypes.add(new IDType(IDType.TYPE.TAXATION));
            if (Bin.charAt(Bin.length() - 6) == '1')
                idTypes.add(new IDType(IDType.TYPE.PHOTO_ID));
        } catch (Exception e) {
        }
        return idTypes;

    }

    public static ArrayList<CustomerType> resolveCustomerTypeFromBytes(String Hex) {

        if (Hex.endsWith(")"))
            Hex = Hex.replace(")", "");
        ArrayList<CustomerType> customers = new ArrayList<CustomerType>();
        Hex = Hex.toUpperCase(Locale.US);
        int i = Integer.parseInt(Hex, 16);
        String Bin = Integer.toBinaryString(i);
        Log.d("AhmedBIN", "resolveCustomerTypeFromBytes Hex is: " + Hex + " Bin is: " + Bin);
        if (Bin.charAt(Bin.length() - 1) == '1')
            customers.add(new CustomerType(CustomerType.TYPE.SUBSCRIBER));
        if (Bin.length() > 1 && Bin.charAt(Bin.length() - 2) == '1')
            customers.add(new CustomerType(CustomerType.TYPE.MERCHANT));
        if (Bin.length() > 2 && Bin.charAt(Bin.length() - 3) == '1')
            customers.add(new CustomerType(CustomerType.TYPE.AGENT));
        if (Bin.length() > 3 && Bin.charAt(Bin.length() - 4) == '1')
            customers.add(new CustomerType(CustomerType.TYPE.SUPER_AGENT));
        if (Bin.length() > 4 && Bin.charAt(Bin.length() - 5) == '1')
            customers.add(new CustomerType(CustomerType.TYPE.MM_STAFF));

        return customers;

    }

    /**
     * helper method to getHexRepresentationFromByte method
     */
    private static String addBits(String bits) {
        StringBuffer buffer = new StringBuffer();

        switch (bits.length()) {
            case 1:
                buffer.append("000");
                break;
            case 2:
                buffer.append("00");
                break;
            case 3:
                buffer.append("0");
                break;
        }
        Log.d(LOG_TAG, "add bits to string bits: " + bits);
        buffer.append(bits);
        Log.d(LOG_TAG, "now returning bits as: " + buffer.toString());
        return buffer.toString();
    }

    public static String getMenuContents(String serverResponse, String menuType) {
        String strProfile = null;
        String[] array = serverResponse.split(getResString(R.string.DELIMITER));

        if (menuType.equalsIgnoreCase(getResString(R.string.MENU_B))) {
            strProfile = BitsMapper.populateMenu(getResString(R.string.MENU_B), array);
        } else if (menuType.equalsIgnoreCase(getResString(R.string.MENU_C))) {
            strProfile = BitsMapper.populateMenu(getResString(R.string.MENU_C), array);
        } else if (menuType.equalsIgnoreCase(getResString(R.string.MENU_F))) {
            strProfile = BitsMapper.populateMenu(getResString(R.string.MENU_F), array);
        } else if (menuType.equalsIgnoreCase(getResString(R.string.MENU_G))) {
            strProfile = BitsMapper.populateMenu(getResString(R.string.MENU_G), array);
        } else if (menuType.equalsIgnoreCase(getResString(R.string.MENU_D))) {
            strProfile = BitsMapper.populateMenu(getResString(R.string.MENU_D), array);
        } else {
            strProfile = "MenuNotFound";
        }

        return strProfile;
    }


    private static String populateMenu(String strMenu, String[] arrMenu) {

        String strProfile = null;
        for (String item : arrMenu) {
            Log.d(LOG_TAG, strMenu + "  populateMenu " + item);
            if (item.length() > 6) {
                if (item.substring(0, 6).equalsIgnoreCase(strMenu)) {
                    strProfile = item.substring(6, 10);
                    Log.d(LOG_TAG, "Found Profile: " + strMenu + " IN MenusBuilder.java = " + strProfile);
                    break;
                }
            }
        }
        return strProfile;
    }

    private static String getResString(int res) {
        return getContext().getResources().getString(res);
    }

    //<editor-fold desc="Setters and Getters">
    public static Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        BitsMapper.context = context;
    }
    //</editor-fold>

}
