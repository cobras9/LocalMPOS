package com.mobilis.android.nfc.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.inject.Inject;
import com.mobilis.android.nfc.R;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.multi_registration)
public class MultiRegistrationAcitivity extends RoboActivity implements OnClickListener {
    @InjectView(R.id.Multi_Registration_Add_Terminal)
    Button addTerminalBtn;
    @InjectView(R.id.Multi_Registration_Add_New_Merchant)
    Button addNewMerchantBtn;
    @Inject
    Activity mActivity;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    private void setUpPage() {
        addTerminalBtn.setOnClickListener(this);
        addNewMerchantBtn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpPage();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.Multi_Registration_Add_Terminal) {
            startActivity(new Intent(MultiRegistrationAcitivity.this, AddTerminalsActivity.class));
            this.finish();
        } else if (v.getId() == R.id.Multi_Registration_Add_New_Merchant) {
            startActivity(new Intent(MultiRegistrationAcitivity.this, MerchantRegistrationActivity.class));
            this.finish();
        }
    }
}
