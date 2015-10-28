package com.mobilis.android.nfc.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.domain.INTENT;
import com.mobilis.android.nfc.model.TerminalRegistration;
import com.mobilis.android.nfc.view.utils.ClearErrorTextWatcher;

import java.lang.ref.WeakReference;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.add_terminals)
public class AddTerminalsActivity extends RoboActivity implements OnClickListener {
    @InjectView(R.id.Add_Terminals_MerchantId)
    EditText merchantId;
    @InjectView(R.id.Add_Merchant_Pin)
    EditText merchantPin;
    @InjectView(R.id.Add_Terminals_Submit_BTN)
    Button addNewTerminalBtn;
    @InjectView(R.id.Add_Terminals_ProgressBar_layout)
    RelativeLayout progressLayout;
    @Inject
    Activity mActivity;
    TerminalRegistration terminalRegistration;
    private RegistrationHandler mHandler = new RegistrationHandler(this);
    private BroadcastReceiver broadcastReceiver;
    final static int SHOW_SERVER_RESPONSE = 1;
    final static int SHOW_TOAST = 2;
    final static int PERFORM_REGISTRATION = 3;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }
    private void setUpPage(){
        addNewTerminalBtn.setOnClickListener(this);
        merchantId.addTextChangedListener(new ClearErrorTextWatcher(merchantId));
        merchantPin.addTextChangedListener(new ClearErrorTextWatcher(merchantPin));
        merchantId.requestFocus();
        merchantId.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(merchantId, InputMethodManager.SHOW_FORCED);
            }
        }, 30);
    }
    @Override
    public void onResume() {
        super.onResume();
        terminalRegistration = new TerminalRegistration(this);
        setUpReceiver();
        setUpPage();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        super.onPause();
        cleanUp();
    }
    private void cleanUp() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        broadcastReceiver = null;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
    private boolean validateData(){
        boolean validData = true;
        if(merchantId.getText()==null || merchantId.getText().toString().isEmpty()){
            validData = false;
            merchantId.setError(getString(R.string.mandatory_field));
        }
        if(merchantPin.getText() ==null || merchantPin.getText().toString().isEmpty()){
            validData = false;
            merchantPin.setError(getString(R.string.mandatory_field));
        }
        return validData;
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.Add_Terminals_Submit_BTN){
            if(validateData()) {
                progressStatus(View.VISIBLE);
                terminalRegistration.setMerchantPin(merchantPin.getText().toString());
                terminalRegistration.setUserMerchantId(merchantId.getText().toString());
                createMessage(PERFORM_REGISTRATION);
            }
        }
    }
    private void progressStatus(int status) {
        progressLayout.setVisibility(status);
    }
    private void setUpReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                progressStatus(View.GONE);
                if (intent.getAction().equalsIgnoreCase(INTENT.SERVER_COMM_TIME_OUT.toString())) {
                    showServerResponseDialog(intent.getStringExtra(INTENT.EXTRA_ERROR.toString()));
                } else if (intent.getAction().equalsIgnoreCase(INTENT.TERMINAL_REGISTRATION.toString())) {
                    String serverResponse = intent.getStringExtra(INTENT.EXTRA_RESPONSE.toString());
                    Log.d(this.getClass().getSimpleName(), "registration serverResponse " + serverResponse);
                    int status = terminalRegistration.getStatusFromServerResponse(serverResponse);
                    String message = terminalRegistration.getMessageFromServerResponse(serverResponse);
                    Log.d(this.getClass().getSimpleName(), "registration message " + message);
                    Log.d(this.getClass().getSimpleName(), "registration status " + status);
                    if (status == 0) {
                        terminalRegistration.saveMerchantId(merchantId.getText().toString());
                        createMessage(SHOW_TOAST, getString(R.string.terminal_registered));
                        mActivity.finish();
                    } else {
                        createMessage(SHOW_SERVER_RESPONSE, message);
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.TERMINAL_REGISTRATION.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.SERVER_COMM_TIME_OUT.toString()));
    }
    private void createMessage(int messageType) {
        createMessage(messageType, null);
    }
    private void createMessage(int messageType, String message) {
        Message msg = new Message();
        msg.what = messageType;
        if (message != null) {
            Bundle details = new Bundle();
            details.putString("MESSAGE", message);
            msg.setData(details);
        }
        mHandler.sendMessage(msg);
    }
    private void showServerResponseDialog(String message) {
        final Dialog messageDialog = new Dialog(this);
        messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        messageDialog.setContentView(R.layout.dialog_general_message);
        messageDialog.setCanceledOnTouchOutside(false);
        messageDialog.setCancelable(false);
        final Button button = (Button) messageDialog.findViewById(R.id.Dialog_General_Message_OK_BTN);
        final TextView textView = (TextView) messageDialog.findViewById(R.id.Dialog_General_message);
        textView.setText(message);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                messageDialog.dismiss();
            }
        });
        messageDialog.show();
    }
    static class RegistrationHandler extends Handler {
        WeakReference<AddTerminalsActivity> registrationActivity;

        RegistrationHandler(AddTerminalsActivity rActivity) {
            registrationActivity = new WeakReference<AddTerminalsActivity>(rActivity);
        }

        @Override
        public void handleMessage(Message message) {
            AddTerminalsActivity registrationHandler = registrationActivity.get();
            if (registrationHandler != null) {
                super.handleMessage(message);
                Bundle details = message.getData();
                switch (message.what) {
                    case SHOW_SERVER_RESPONSE:
                        registrationHandler.showServerResponseDialog(details.getString("MESSAGE"));
                        break;
                    case SHOW_TOAST:
                        registrationHandler.showToast(details.getString("MESSAGE"), Boolean.FALSE);
                        break;
                    case PERFORM_REGISTRATION:
                        registrationHandler.terminalRegistration.getConnTaskManager().startBackgroundTask();
                        break;
                }
            }
        }
    }
    private void showToast(String message, boolean close) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        if (close) {
            this.finish();
        }

    }
}
