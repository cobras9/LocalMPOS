package com.mobilis.android.nfc.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.activities.ApplicationActivity;
import com.mobilis.android.nfc.domain.INTENT;
import com.mobilis.android.nfc.jsonModel.Payee;
import com.mobilis.android.nfc.model.AbstractModel;
import com.mobilis.android.nfc.model.BillPayment;
import com.mobilis.android.nfc.model.Login;
import com.mobilis.android.nfc.model.TransferCode;
import com.mobilis.android.nfc.util.Constants;
import com.mobilis.android.nfc.view.utils.MoneyTextWatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ahmed on 10/06/14.
 */
public class CableTVFragment extends NFCFragment implements View.OnClickListener , DialogInterface.OnDismissListener, DialogInterface.OnShowListener {

    private final String TAG = CableTVFragment.class.getSimpleName();
    //TextView amountTV;
    TextView payeeRefTV;
    TextView resultTV;
    EditText amountET;
    EditText payeeRefET;
    Button payButton;
    ProgressBar progressBar;
    BroadcastReceiver broadcastReceiver;
    Dialog NFCDialog;
    Spinner destinationCodeSpinner;
    EditText msisdnET;
    BillPayment model;
    ArrayList<Payee> payees = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cable_tv_view, container, false);
        amountET = (EditText) rootView.findViewById(R.id.Fragment_CableTV_EditText_Amount);
        //amountTV = (TextView) rootView.findViewById(R.id.Fragment_CableTV_TextView_Amount);
        payeeRefET = (EditText) rootView.findViewById(R.id.Fragment_CableTV_EditText_PayeeRef);
        payeeRefTV = (TextView) rootView.findViewById(R.id.Fragment_CableTV_TextView_PayeeRef);
        resultTV = (TextView) rootView.findViewById(R.id.Fragment_CableTV_TextView_Result);
        payButton = (Button) rootView.findViewById(R.id.Fragment_CableTV_Button_Pay);
        progressBar = (ProgressBar) rootView.findViewById(R.id.Fragment_CableTV_Progressbar);
        destinationCodeSpinner = (Spinner) rootView.findViewById(R.id.Fragment_CableTV_Spinner);
        msisdnET = (EditText) rootView.findViewById(R.id.Fragment_CableTV_EditText_MSISDN);
        List<String> spinnerList = new ArrayList<String>();
        if(getResources().getBoolean(R.bool.LOAD_TRANSFERCODES_FROM_SERVER)) {
            final AbstractModel modela = new Login(getActivity());
            String savedCurr = modela.getWorkingCurrency();

            String currencyCode = "";
            if (TransferCode.currencyCodesMap.containsKey(savedCurr)) {
                currencyCode = (String) TransferCode.currencyCodesMap.get(savedCurr);
            }
// List<String> spinnerList = new ArrayList<String>();
            Log.d("Lewis", "Lewis Fragment  savedCurr " + savedCurr);
            Log.d("Lewis", "Lewis Fragment  currencyCode " + currencyCode);
            Log.d("Lewis", "Lewis Fragment  currencyCode " + currencyCode);
            payees = TransferCode.currencyBillPayees.get(currencyCode);
            Log.d("Lewis", "Lewis Fragment  payees size " + payees.size());
            if (payees != null) {
                for (Payee payee : payees) {
                    spinnerList.add(payee.getName());
                }
            }
        }else{
            Payee onePayee = null;
            for (TransferCode bpc : TransferCode.billPaymentCodes) {
                onePayee = new Payee();
                onePayee.setCode(bpc.getCode());
                onePayee.setName(bpc.getDescription());
                onePayee.setAmount(bpc.getAmount());
                payees.add(onePayee);
                spinnerList.add(bpc.getDescription());
            }
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, R.id.Spinner_TextView, spinnerList);

        destinationCodeSpinner.setAdapter(spinnerAdapter);
        //destinationCodeSpinner.setOnItemSelectedListener(new CableTVOnItemSelectedListener(getActivity()));
        destinationCodeSpinner.setSelection(0);

        payButton.setOnClickListener(this);

        amountET.addTextChangedListener(new MoneyTextWatcher(amountET));
        amountET.requestFocus();
        amountET.postDelayed(new Runnable() {
            @Override
            public void run() {
                ApplicationActivity.showKeyboard(getActivity(), amountET);
            }
        },30);
        payeeRefET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (payeeRefET.getText().toString().isEmpty())
                    payeeRefTV.setVisibility(View.VISIBLE);
                else
                    payeeRefTV.setVisibility(View.GONE);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equalsIgnoreCase(INTENT.CABLE_TV.toString()))
                {
                    if( intent.getStringExtra(INTENT.EXTRA_RESPONSE.toString()) != null) {
                        String serverResponse = intent.getStringExtra(INTENT.EXTRA_RESPONSE.toString());
                        String response = model.getMessageFromServerResponse(serverResponse);
                        if(response.equalsIgnoreCase("OK")) {
                            //hideProgressBar("SUCCESSFUL");
                            hideProgressBar(getString(R.string.SUCCESS_MSG));
                        } else if (model.getStatusFromServerResponse(serverResponse) == getResources().getInteger(R.integer.REFERENCE_NUMBER_FORMAT_ERROR) || model.getStatusFromServerResponse(serverResponse) ==4) {
                            hideProgressBar(getResources().getString(R.string.REFERENCE_NUMBER_FORMAT_ERROR_MESSAGE));
                        } else {
                            hideProgressBar(response);
                        }
                    }else{
                        hideProgressBar("Operation timeout");
                    }
                }else if(action.equalsIgnoreCase(INTENT.NFC_SCANNED.toString()))
                {
                    Log.d("TAG , ", " action NFCDialog "+NFCDialog);
                    //if(amountET.getText().toString().isEmpty())
                    //   Toast.makeText(getActivity(), "Insert amount", Toast.LENGTH_SHORT).show();

                    if(NFCDialog != null && NFCDialog.isShowing())
                    {
                        NFCDialog.dismiss();

                        showProgressBar();
                        model = new BillPayment(getActivity());
                        model.setNfcScanned(true);
                        model.setNFCId(intent.getStringExtra(INTENT.EXTRA_NFC_ID.toString()).toUpperCase(Locale.US));
                        model.setWorkingAmount(amountET.getText().toString());
                        model.setPayeeRef(payeeRefET.getText().toString());
                        model.setPayeeId(payees.get(destinationCodeSpinner.getSelectedItemPosition()).getCode());
                        model.getConnTaskManager().startBackgroundTask();
                        //if(getActivity().getResources().getBoolean(R.bool.PAY_PIN_REQUIRED)) {
                        if(Constants.USSD_PAY_PIN_REQUIRED) {
                            showPinDialog();
                        }else{
                            model.getConnTaskManager().startBackgroundTask();
                        }

                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.CABLE_TV.toString()));
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
        switch (v.getId())
        {
            case R.id.Fragment_CableTV_Button_Pay:
                if(amountET.getText().toString().isEmpty())
                {
                   Toast.makeText(v.getContext(), getString(R.string.AMOUNT_IS_MANDATORY), Toast.LENGTH_SHORT).show();
                    return;
                }

                ApplicationActivity.hideKeyboard(getActivity());
                if(payeeRefET.getText().toString().isEmpty())
                {
                    Toast.makeText(v.getContext(), "Payee reference is mandatory", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(msisdnET.getText() != null && !msisdnET.getText().toString().isEmpty()) {
                    showProgressBar();
                    model = new BillPayment(getActivity());
                    model.setWorkingAmount(amountET.getText().toString());
                    model.setPayeeRef(payeeRefET.getText().toString());
                    model.setPayeeId(payees.get(destinationCodeSpinner.getSelectedItemPosition()).getCode());
                    model.setMsisdn(msisdnET.getText().toString());
                    // model.setPayeeId(TransferCode.billPaymentCodes.get(destinationCodeSpinner.getSelectedItemPosition()).getCode());
                    //                } else  if(strType.equalsIgnoreCase(PaymentType.UTILITIES.toString())) {
                    //                    ((BillPayment) model).setPayeeId(TransferCode.utilityCodes.get(payeeIdSpinner.getSelectedItemPosition()).getCode());
                    //                }
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
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        broadcastReceiver = null;
    }

    private void showProgressBar(){
        amountET.setEnabled(false);
        payeeRefET.setEnabled(false);
        payButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        resultTV.setVisibility(View.GONE);
    }

    private void hideProgressBar(String response){
        amountET.setEnabled(true);
        payeeRefET.setEnabled(true);
        payButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        resultTV.setText(response);
        resultTV.setVisibility(View.VISIBLE);
    }
    @Override
    public void finishedA2ACommunication(String scannedId) {
        Intent intent = new Intent(INTENT.NFC_SCANNED.toString());
        Log.d("TAG , ", " finishedA2ACommunication  " + scannedId);
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
