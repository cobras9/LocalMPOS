package com.mobilis.android.nfc.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.activities.ApplicationActivity;
import com.mobilis.android.nfc.adapters.TopUpDetailsAdapters;
import com.mobilis.android.nfc.domain.INTENT;
import com.mobilis.android.nfc.jsonModel.Operator;
import com.mobilis.android.nfc.jsonModel.Preset;
import com.mobilis.android.nfc.listeners.TopUpDetailsOnItemSelectedListener;
import com.mobilis.android.nfc.model.AbstractModel;
import com.mobilis.android.nfc.model.Login;
import com.mobilis.android.nfc.model.TopUpInternational;
import com.mobilis.android.nfc.model.TransferCode;
import com.mobilis.android.nfc.util.Constants;
import com.mobilis.android.nfc.util.CurrencyUtils;
import com.mobilis.android.nfc.view.utils.MoneyTextWatcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

/**
 * Created by ahmed on 10/06/14.
 */
public class TopUpFragment extends  NFCFragment implements View.OnClickListener, DialogInterface.OnDismissListener, DialogInterface.OnShowListener{

    private final String TAG = TopUpFragment.class.getSimpleName();
    //TextView amountTV;
    TextView accountIdTV;
    TextView resultTV;
    EditText amountET;
    EditText accountIdET;
    Button payButton;
    ProgressBar progressBar;
    BroadcastReceiver broadcastReceiver;
    Dialog NFCDialog;
    Spinner destinationCodeSpinner;
    Spinner detailedDestinationCodeSpinner;
    TopUpInternational model;

    TextView ccNumberTV;
    TextView ccExpiryTV;
    EditText ccNumberET;
    EditText ccExpiryET;
    RelativeLayout spinnerLayout;
    boolean isCreditCardSupported = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_up_view, container, false);
        amountET = (EditText) rootView.findViewById(R.id.Fragment_TopUp_EditText_Amount);
        //amountTV = (TextView) rootView.findViewById(R.id.Fragment_TopUp_TextView_Amount);
        accountIdET = (EditText) rootView.findViewById(R.id.Fragment_TopUp_EditText_AccountId);
        accountIdTV = (TextView) rootView.findViewById(R.id.Fragment_TopUp_TextView_AccountId);
        resultTV = (TextView) rootView.findViewById(R.id.Fragment_TopUp_TextView_Result);
        payButton = (Button) rootView.findViewById(R.id.Fragment_TopUp_Button_Pay);
        progressBar = (ProgressBar) rootView.findViewById(R.id.Fragment_TopUp_Progressbar);
        spinnerLayout = (RelativeLayout) rootView.findViewById(R.id.Fragment_TopUp_OperatorLayout);
        destinationCodeSpinner = (Spinner) rootView.findViewById(R.id.Fragment_TopUp_Spinner);
        detailedDestinationCodeSpinner = (Spinner) rootView.findViewById(R.id.Fragment_TopUp_Details_Spinner);
        ccNumberTV = (TextView) rootView.findViewById(R.id.Fragment_TopUp_TextView_CreditCardNumber);
        ccExpiryTV = (TextView) rootView.findViewById(R.id.Fragment_TopUp_TextView_CreditCardExpiry);
        ccNumberET = (EditText) rootView.findViewById(R.id.Fragment_TopUp_EditText_CreditCardNumber);
        ccExpiryET = (EditText) rootView.findViewById(R.id.Fragment_TopUp_EditText_CreditCardExpiry);

        if (isCreditCardSupported) {
            ccNumberTV.setVisibility(View.VISIBLE);
            ccNumberET.setVisibility(View.VISIBLE);
            ccExpiryTV.setVisibility(View.VISIBLE);
            ccExpiryET.setVisibility(View.VISIBLE);

            ccNumberET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (ccNumberET.getText().toString().isEmpty())
                        ccNumberTV.setVisibility(View.VISIBLE);
                    else
                        ccNumberTV.setVisibility(View.INVISIBLE);
                }
            });

            ccExpiryET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (ccExpiryET.getText().toString().isEmpty())
                        ccExpiryTV.setVisibility(View.VISIBLE);
                    else
                        ccExpiryTV.setVisibility(View.INVISIBLE);
                }
            });

        } else {
            ccNumberTV.setVisibility(View.INVISIBLE);
            ccNumberET.setVisibility(View.INVISIBLE);
            ccExpiryTV.setVisibility(View.INVISIBLE);
            ccExpiryET.setVisibility(View.INVISIBLE);
        }

        payButton.setOnClickListener(this);
        amountET.addTextChangedListener(new MoneyTextWatcher(amountET));
        amountET.requestFocus();
        amountET.postDelayed(new Runnable() {
            @Override
            public void run() {
                ApplicationActivity.showKeyboard(getActivity(), amountET);
            }
        },30);
/*        amountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (amountET.getText().toString().isEmpty())
                    amountTV.setVisibility(View.VISIBLE);
                else
                    amountTV.setVisibility(View.INVISIBLE);
            }
        });*/
        accountIdET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (accountIdET.getText().toString().isEmpty())
                    accountIdTV.setVisibility(View.VISIBLE);
                else
                    accountIdTV.setVisibility(View.GONE);
            }
        });

        //Setting topup list
        final AbstractModel model = new Login(getActivity());
        String savedCurr = model.getWorkingCurrency();
        List<String> spinnerList = new ArrayList<String>();
        String currencyCode = "";
        Log.d(TAG," Country Constants.currency"+ savedCurr);
        if (TransferCode.currencyCodesMap.containsKey(savedCurr)) {
            currencyCode = (String)TransferCode.currencyCodesMap.get(savedCurr);
        }
        hideOperatorsCreditCard();
        String operatorName="";
        Operator operator;
        ArrayList<Preset> oneList=null;
        String operatorKey;
        Log.d(TAG,"  currencyCode"+ currencyCode);
        if(TransferCode.currencyTopUps.containsKey(currencyCode)){
            TreeMap operators = TransferCode.currencyTopUps.get(currencyCode);
            Log.d(TAG,"  operators.size() "+ operators.size());
            if(operators.size()>0){
                showOperatorsCreditCard();
            }
            for(Iterator it = operators.keySet().iterator();it.hasNext();){
                operatorKey = (String)it.next();
                Log.d(TAG,"  operatorKey "+ operatorKey);
                operator = (Operator)operators.get(operatorKey);
                operatorName = operator.getDescription();
                Log.d(TAG,"  operatorName "+ operatorName);
                spinnerList.add(operatorName);
                ArrayList<Preset> presets = (ArrayList)operator.getPresets();
                if(presets != null) {
                    Log.d(TAG, "  presets " + presets.size());
                }
                if(presets != null && operator.getPresets().size()>0){
                    showDetails();
                }else{
                    hideDetails();
                }
                for (int i=0;i<presets.size();i++){
                    Preset preset = operator.getPresets().get(i);
                    Log.d(TAG, "  preset.getCode() " + preset.getCode());
                    Log.d(TAG, "  preset.getAmount() " + preset.getAmount());
                    Log.d(TAG, "  operatorName" + operatorName);
                    if(detailsMap.containsKey(operatorName)){
                        oneList = detailsMap.get(operatorName);
                    }else{
                        oneList = new ArrayList();
                    }
                    oneList.add(preset);
                    detailsMap.put(operatorName,oneList);
                }
            }
        }
/*        if (TransferCode.countryMap.containsKey(currencyCode)) {
            Country country = TransferCode.countryMap.get(currencyCode);
            Log.d(TAG," Country country"+ country.getCountryName());
            Log.d(TAG," Country country"+ country.getOperators());
            if (country.getOperators() != null && country.getOperators().size() > 0) {
                //list operators
                showOperatorsCreditCard();

                for (int k = 0; k < country.getOperators().size(); k++) {
                    Operator operator = country.getOperators().get(k);
                    operatorName = operator.getDescription();
                    spinnerList.add(operatorName);
                    if(operator.getPresets().size()>1){
                        showDetails();
                    }else{
                        hideDetails();
                    }
                    for (int l = 0; l < operator.getPresets().size(); l++) {
                        Preset preset = operator.getPresets().get(l);
                        Log.d(TAG, "  preset.getCode() " + preset.getCode());
                        Log.d(TAG, "  preset.getAmount() " + preset.getAmount());
                        if(detailsMap.containsKey(operatorName)){
                            oneList = detailsMap.get(operatorName);
                        }else{
                            oneList = new ArrayList();
                        }
                        oneList.add(preset);
                        detailsMap.put(operatorName,oneList);
                    }

                }
            }
        }*/
/*
        for (TransferCode bpc : TransferCode.topupAirtimeCodes) {
            spinnerList.add(bpc.getDescription());
        }*/
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, R.id.Spinner_TextView, spinnerList);
        destinationCodeSpinner.setAdapter(spinnerAdapter);
        destinationCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setUpDetailSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        destinationCodeSpinner.setSelection(0);
        setUpDetailSpinner();
        return rootView;
    }
    private LinkedHashMap<String ,ArrayList<Preset>> detailsMap= new LinkedHashMap<>();
    private void setUpDetailSpinner(){
        Log.d(TAG, "setUpDetailSpinner " + (String) destinationCodeSpinner.getSelectedItem());
        ArrayList<String> spinnerList = new ArrayList<>();
        boolean hideDetails = false;
        ArrayList<Preset> presets =new ArrayList<>();
        if(detailsMap.containsKey((String)destinationCodeSpinner.getSelectedItem())){
            presets =detailsMap.get(destinationCodeSpinner.getSelectedItem());
/*           for (int l = 0; l < presets.size(); l++) {
                Preset preset = presets.get(l);
                spinnerList.add(preset.getAmount());
            }*/
            Log.d(TAG, "setUpDetailSpinner " + (String) destinationCodeSpinner.getSelectedItem());
            if(presets != null && presets.size()>1){
                hideDetails  =false;
            }else{
                hideDetails = true;
            }
            TopUpDetailsAdapters detailsAdapters = new TopUpDetailsAdapters(getActivity(), R.layout.spinner_layout,presets);
            //ArrayAdapter<String> detailAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, R.id.Spinner_TextView, spinnerList);
            detailedDestinationCodeSpinner.setAdapter(detailsAdapters);
            detailedDestinationCodeSpinner.setOnItemSelectedListener(new TopUpDetailsOnItemSelectedListener(getActivity(),presets));
        }

        detailedDestinationCodeSpinner.setSelection(0);

        if(hideDetails && presets !=null){
            Log.d(TAG, "setUpDetailSpinner presets " + presets);
            Log.d(TAG, "setUpDetailSpinner presets.size() " + presets.size());
            Log.d(TAG, "setUpDetailSpinner hideDetails " + hideDetails);
            hideDetails();
            cleanAmountField(presets,0);
        }else{
            showDetails();
        }

    }
    private void cleanAmountField(ArrayList<Preset> presets, int pos){
        String parsedAmount = CurrencyUtils.getInstance().formatAmount(presets.get(pos).getAmount());
        parsedAmount = parsedAmount.replaceAll(",","");
        if(!parsedAmount.equals("0")) {
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(INTENT.DISABLE_AMOUNT_EDIT_TEXT.toString()));
            amountET.setText(parsedAmount);
            amountET.setEnabled(false);
            amountET.setBackgroundColor(getActivity().getResources().getColor(R.color.APP_MAIN_COLOR));
            amountET.setTextColor(getActivity().getResources().getColor(R.color.APP_MAIN_TEXT_COLOR));
            amountET.setTextSize(26);
            amountET.setTypeface(null, Typeface.BOLD);
            amountET.setGravity(Gravity.CENTER);
        }else{
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(INTENT.ENABLE_AMOUNT_EDIT_EXT.toString()));
            //TextView amountTV = (TextView) getActivity().findViewById(R.id.Fragment_TopUp_EditText_Amount);
            //amountTV.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
            amountET.setText("");
            amountET.setEnabled(true);
            amountET.setBackgroundColor(Color.TRANSPARENT);
            amountET.setTextColor(Color.BLACK);
            amountET.setTextSize(16);
            amountET.setTypeface(null, Typeface.NORMAL);
            amountET.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        }
    }
    private void showDetails() {
        detailedDestinationCodeSpinner.setVisibility(View.VISIBLE);
    }
    private void hideDetails() {
        detailedDestinationCodeSpinner.setVisibility(View.GONE);
    }
    private void hideOperatorsCreditCard() {
        destinationCodeSpinner.setVisibility(View.GONE);
        ccNumberTV.setVisibility(View.GONE);
        ccExpiryTV.setVisibility(View.GONE);
        ccNumberET.setVisibility(View.GONE);
        ccExpiryET.setVisibility(View.GONE);
        spinnerLayout.setVisibility(View.GONE);
    }

    private void showOperatorsCreditCard() {
        destinationCodeSpinner.setVisibility(View.VISIBLE);
        if (isCreditCardSupported) {
            ccNumberTV.setVisibility(View.VISIBLE);
            ccExpiryTV.setVisibility(View.VISIBLE);
            ccNumberET.setVisibility(View.VISIBLE);
            ccExpiryET.setVisibility(View.VISIBLE);
        }
        spinnerLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (model == null)
                    return;
                String action = intent.getAction();
                if (action.equalsIgnoreCase(INTENT.TOP_UP.toString())) {
                    String response = model.getMessageFromServerResponse(intent.getStringExtra(INTENT.EXTRA_RESPONSE.toString()));
                    if(response.equalsIgnoreCase("OK")) {
                        //hideProgressBar("SUCCESSFUL");
                        hideProgressBar(getString(R.string.SUCCESS_MSG));
                    }else {
                        hideProgressBar(response);
                    }
                }else if(action.equalsIgnoreCase(INTENT.NFC_SCANNED.toString())){
                    if(NFCDialog != null && NFCDialog.isShowing())
                    {
                        NFCDialog.dismiss();
                        showProgressBar();
                        model.setNfcScanned(true);
                        model.setNFCId(intent.getStringExtra(INTENT.EXTRA_NFC_ID.toString()).toUpperCase(Locale.US));
                        if(getActivity().getResources().getBoolean(R.bool.USING_YOUTAP_WALLET)){
                            model.getConnTaskManager().startBackgroundTask();
                        } else {
                            //if(getActivity().getResources().getBoolean(R.bool.PAY_PIN_REQUIRED)) {
                            if(Constants.USSD_PAY_PIN_REQUIRED) {
                                showPinDialog();
                            }else{
                                model.getConnTaskManager().startBackgroundTask();
                            }
                        }
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.TOP_UP.toString()));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.NFC_SCANNED.toString()));
    }
    boolean pinDialogIsOn = false;
    private void showPinDialog(){
        if(pinDialogIsOn)
            return;
        pinDialogIsOn = true;
        final Dialog pinDialog = new Dialog(getActivity());
        pinDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pinDialog.setContentView(R.layout.pin_dialog);
        pinDialog.setCanceledOnTouchOutside(false);
        //final TextView pinTextView = (TextView)pinDialog.findViewById(R.id.Dialog_PIN_TextView_PIN);
        final EditText pinEditText = (EditText)pinDialog.findViewById(R.id.Dialog_PIN_EditText_PIN);
        final Button Dialog_PIN_SUBMIT = (Button)pinDialog.findViewById(R.id.Dialog_PIN_SUBMIT);
        Dialog_PIN_SUBMIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pinEditText.getWindowToken(), 0);
                pinDialogIsOn = false;
                pinDialog.dismiss();
                showProgressBar();
                model.setPin(pinEditText.getText().toString());
                model.getConnTaskManager().startBackgroundTask();
            }
        });
        pinEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.showSoftInput(pinEditText, InputMethodManager.SHOW_FORCED);
            }
        }, 30);
        pinDialog.show();
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Fragment_TopUp_Button_Pay:
                if (amountET.getText().toString().isEmpty()) {
                    amountET.setError(getString(R.string.AMOUNT_IS_MANDATORY));
                    return;
                }
                ApplicationActivity.hideKeyboard(getActivity());
                showProgressBar();
                model = new TopUpInternational(getActivity());
                if (AbstractModel.isNumeric(CurrencyUtils.getInstance().getCleanAmount(amountET.getText().toString())))
                    model.setWorkingAmount(CurrencyUtils.getInstance().getCleanAmount(amountET.getText().toString()));
                else
                    model.setWorkingAmount(CurrencyUtils.getInstance().getCleanAmount(amountET.getText().toString()));
                Log.d("PROB", "model.getWorkingAmount(): " + model.getWorkingAmount());
                model.setPhoneNumber(accountIdET.getText().toString());
                //model.setDestinationCode(TransferCode.topupAirtimeCodes.get(destinationCodeSpinner.getSelectedItemPosition()).getCode());
                if(detailedDestinationCodeSpinner != null && detailedDestinationCodeSpinner.getAdapter() !=null && ((TopUpDetailsAdapters) detailedDestinationCodeSpinner.getAdapter()).getItemCode(detailedDestinationCodeSpinner.getSelectedItemPosition()) !=null){
                    model.setDestinationCode(((TopUpDetailsAdapters) detailedDestinationCodeSpinner.getAdapter()).getItemCode(detailedDestinationCodeSpinner.getSelectedItemPosition()));
                }
                //model.setDestinationCode(((Preset)detailedDestinationCodeSpinner.getAdapter().getitem.getItem(detailedDestinationCodeSpinner.getSelectedItemPosition())).getCode());//.getSelectedItem().get.getSelectedItemCode());
                if (ccNumberET.getText() != null && ccNumberET.length() > 0) {
                    model.setCreditCardNumber(ccNumberET.getText().toString());
                }

                if (ccExpiryET.getText() != null && ccExpiryET.length() > 0) {
                    model.setCreditCardExpiry(ccExpiryET.getText().toString());
                }
                if (getResources().getBoolean(R.bool.USING_YOUTAP_WALLET)) {
                    showProgressBar();
                    model.getConnTaskManager().startBackgroundTask();
                }else{
                    if(accountIdET.getText() != null && !accountIdET.getText().toString().isEmpty()){
                        showProgressBar();
                        model.getConnTaskManager().startBackgroundTask();
                    }else{
                            NFCDialog = new Dialog(v.getContext());
                            NFCDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            NFCDialog.setContentView(R.layout.nfc_dialog);
                            NFCDialog.setCanceledOnTouchOutside(false);
                            NFCDialog.setOnShowListener(this);
                            NFCDialog.setOnDismissListener(this);
                            NFCDialog.show();
                    }
                }

                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        broadcastReceiver = null;
    }

    private void showProgressBar() {
        amountET.setEnabled(false);
        accountIdET.setEnabled(false);
        payButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        resultTV.setVisibility(View.GONE);
    }

    private void hideProgressBar(String response) {
        amountET.setEnabled(true);
        accountIdET.setEnabled(true);
        payButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        resultTV.setText(response);
        resultTV.setVisibility(View.VISIBLE);
    }


    @Override
    public void finishedA2ACommunication(String scannedId) {
        Intent intent = new Intent(INTENT.NFC_SCANNED.toString());
        intent.putExtra(INTENT.EXTRA_NFC_ID.toString(), scannedId);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        disableNFCScan();
    }

    @Override
    public void onShow(DialogInterface dialog) {
        enableNFCScan();
    }
}
