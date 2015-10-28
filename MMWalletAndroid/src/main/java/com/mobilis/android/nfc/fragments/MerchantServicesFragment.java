package com.mobilis.android.nfc.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.activities.ApplicationActivity;
import com.mobilis.android.nfc.domain.INTENT;
import com.mobilis.android.nfc.model.MerchantServiceModel;
import com.mobilis.android.nfc.util.CurrencyUtils;
import com.mobilis.android.nfc.view.utils.MoneyTextWatcher;

public class MerchantServicesFragment extends NFCFragment implements View.OnClickListener {//}, TextWatcher{

    private final String TAG = MerchantServicesFragment.class.getSimpleName();
    TextView resultTV;
    EditText amountET;
    Button submitBTN;
    ProgressBar progressBar;
    BroadcastReceiver broadcastReceiver;
    MerchantServiceModel model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_merchant_services_view, container, false);
        submitBTN = (Button) rootView.findViewById(R.id.MS_Submit_BTN);
        amountET = (EditText) rootView.findViewById(R.id.MS_Amount_ET);
        resultTV = (TextView) rootView.findViewById(R.id.MS_Result_TV);
        progressBar = (ProgressBar) rootView.findViewById(R.id.MS_ProgressBar);
        submitBTN.setOnClickListener(this);
        amountET.addTextChangedListener(new MoneyTextWatcher(amountET));
        amountET.requestFocus();
        amountET.postDelayed(new Runnable() {
            @Override
            public void run() {
                ApplicationActivity.showKeyboard(getActivity(), amountET);
            }
        }, 30);
        Bundle requestBundle = getArguments();
        model = new MerchantServiceModel(getActivity());
        model.setMessageType(requestBundle.getString(getString(R.string.REQ_MESSAGE_TYPE)));
        return rootView;
    }

    @Override

    public void onAttach(Activity activity) {
        super.onAttach(activity);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(MerchantServicesFragment.class.getSimpleName(), "onReceive is called with action: " + intent.getAction());
                Log.d(MerchantServicesFragment.class.getSimpleName(), "!isAdded()? " + (!isAdded()));
                if (!isAdded())
                    return;
                String action = intent.getAction();
                if (action.equalsIgnoreCase(INTENT.MERCHANT_SERVICE_MERCHANT_VOUCHER.toString()) || action.equalsIgnoreCase(INTENT.MERCHANT_SERVICE_COMMISSION_TRANSFER.toString())) {
                    if (model == null)
                        return;
                    String response = model.getMessageFromServerResponse(intent.getStringExtra(INTENT.EXTRA_RESPONSE.toString()));
                    int status = model.getResponseStatus();
                    //Not sure how server is going to return a successful message
                    if (status == 0) {
                        if (response.equalsIgnoreCase("OK"))
                            hideProgressBar(getString(R.string.SUCCESS_MSG));
                    } else {
                        hideProgressBar(response);
                    }

                }
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.MERCHANT_SERVICE_COMMISSION_TRANSFER.toString()));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.MERCHANT_SERVICE_MERCHANT_VOUCHER.toString()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        broadcastReceiver = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.MS_Submit_BTN:
                ApplicationActivity.hideKeyboard(getActivity());
                if (amountET.getText().toString().isEmpty()) {
                    Toast.makeText(v.getContext(), getString(R.string.AMOUNT_IS_MANDATORY), Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgressBar();
                model.setWorkingAmount(CurrencyUtils.getInstance().getCleanAmount(amountET.getText().toString()));
                model.getConnTaskManager().startBackgroundTask();
                break;
        }
    }

    private void showProgressBar() {
        amountET.setEnabled(false);
        submitBTN.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        resultTV.setVisibility(View.GONE);
    }

    private void hideProgressBar(String response) {
        amountET.setEnabled(true);
        submitBTN.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        resultTV.setText(response);
        resultTV.setVisibility(View.VISIBLE);
    }


    @Override
    public void finishedA2ACommunication(String scannedId) {

    }

}
