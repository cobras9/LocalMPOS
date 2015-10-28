package com.mobilis.android.nfc.activities;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.inject.Inject;
import com.mobilis.android.nfc.R;
import com.tobishiba.snappingseekbar.library.views.SnappingSeekBar;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by lewischao on 23/10/15.
 */
@ContentView(R.layout.social_id_registration)
public class SocialIdRegistration extends RoboActivity implements View.OnClickListener {
    //ActionBar actionBar;
    @InjectView(R.id.SIR_SeekBar)
    SnappingSeekBar SIR_SeekBar;
    @InjectView(R.id.SIR_HEADER_LABEL)
    TextView SIR_HEADER_LABEL;
    @InjectView(R.id.SIR_FOOTER_CANCEL_BUTTON)
    Button SIR_FOOTER_CANCEL_BUTTON;
    @InjectView(R.id.SIR_FOOTER_NEXT_BUTTON)
    Button SIR_FOOTER_NEXT_BUTTON;
    @InjectView(R.id.SIR_CONTENT_LAYOUT)
    RelativeLayout SIR_CONTENT_LAYOUT;
    @InjectView(R.id.SIR_PERSONAL_LAYOUT)
    LinearLayout SIR_PERSONAL_LAYOUT;
    @InjectView(R.id.SIR_ID_CARD_NO)
    EditText SIR_ID_CARD_NO;

    //Personal data
    @InjectView(R.id.SIR_PERSONAL_FIRST_NAME)
    EditText SIR_PERSONAL_FIRST_NAME;
    @InjectView(R.id.SIR_PERSONAL_LAST_NAME)
    EditText SIR_PERSONAL_LAST_NAME;
    @InjectView(R.id.SIR_PERSONAL_PHONE_NUMBER)
    EditText SIR_PERSONAL_PHONE_NUMBER;
    @InjectView(R.id.SIR_PERSONAL_ID_NUMBER)
    EditText SIR_PERSONAL_ID_NUMBER;
    @InjectView(R.id.SIR_PERSONAL_REFERENCE_NUMBER)
    EditText SIR_PERSONAL_REFERENCE_NUMBER;


    //Birth data
    @InjectView(R.id.SIR_BIRTH_MAIDEN_NAME)
    EditText SIR_BIRTH_MAIDEN_NAME;
    @InjectView(R.id.SIR_BIRTH_PLACE)
    EditText SIR_BIRTH_PLACE;
    @InjectView(R.id.SIR_BIRTH_DATE)
    EditText SIR_BIRTH_DATE;


    //Address
    @InjectView(R.id.SIR_ADDRESS_FULL_ADDRESS)
    EditText SIR_ADDRESS_FULL_ADDRESS;
    @InjectView(R.id.SIR_ADDRESS_POSTCODE)
    EditText SIR_ADDRESS_POSTCODE;
    @InjectView(R.id.SIR_ADDRESS_RT)
    EditText SIR_ADDRESS_RT;
    @InjectView(R.id.SIR_ADDRESS_RW)
    EditText SIR_ADDRESS_RW;
    @InjectView(R.id.SIR_ADDRESS_URBAN_VILLAGE)
    EditText SIR_ADDRESS_URBAN_VILLAGE;
    @InjectView(R.id.SIR_ADDRESS_DISTRICT)
    EditText SIR_ADDRESS_DISTRICT;
    @InjectView(R.id.SIR_ADDRESS_DISTRICT_1)
    EditText SIR_ADDRESS_DISTRICT_1;
    @InjectView(R.id.SIR_ADDRESS_PROVINCE)
    EditText SIR_ADDRESS_PROVINCE;

    //Images documents
    private String TAG = this.getClass().getSimpleName();
    @Inject
    Context mContext;
    private int currentPage = 0;
    private String headerTitles[];
    private boolean socialIdVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpBasic();
        //setScocialIdVerified();
    }

    private void setScocialIdVerified() {


        if (socialIdVerified) {
            SIR_SeekBar.setVisibility(View.VISIBLE);
            SIR_ID_CARD_NO.setVisibility(View.GONE);
            SIR_PERSONAL_LAYOUT.setVisibility(View.VISIBLE);
            setCurrentPageDetails(currentPage);
        } else {
            SIR_ID_CARD_NO.setVisibility(View.VISIBLE);
            SIR_PERSONAL_LAYOUT.setVisibility(View.GONE);
            SIR_SeekBar.setVisibility(View.INVISIBLE);
        }
    }

    private void setUpBasic() {
        SIR_SeekBar.setOnItemSelectionListener(new SnappingSeekBar.OnItemSelectionListener() {
            @Override
            public void onItemSelected(int itemIndex, String itemString) {
                setCurrentPageDetails(itemIndex);
            }
        });
        headerTitles = getResources().getStringArray(R.array.header_array);
        SIR_FOOTER_CANCEL_BUTTON.setOnClickListener(this);
        SIR_FOOTER_NEXT_BUTTON.setOnClickListener(this);
        setScocialIdVerified();
    }

    private void setCurrentPageDetails(int itemIndex) {
        SIR_HEADER_LABEL.setText(headerTitles[itemIndex]);
        SIR_SeekBar.setProgressToIndex(itemIndex);
        currentPage = itemIndex;
    }

    private void setPageContent() {

/*        <string name="header_social_id_data">Social ID Data</string>
        <string name="header_personal_data">Personal Data</string>
        <string name="header_birth_data">Birth Data</string>
        <string name="header_address">Address</string>
        <string name="header_upload_documents">Upload Documents</string>
        <string name="header_confirm">Confirm your information</string>
        <string name="header_completion">Completion</string>*/
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SIR_FOOTER_CANCEL_BUTTON:
                if (socialIdVerified) {
                    --currentPage;
                    if (currentPage <= 0) {
                        currentPage = 0;
                        setCurrentPageDetails(currentPage);
                    } else {
                        setCurrentPageDetails(currentPage);
                    }
                } else {
                    //TODO waiting for backend server for verification service api to be called
                    this.finish();
                }
                //SIR_SeekBar.setProgressToIndex(currentPage);
                break;
            case R.id.SIR_FOOTER_NEXT_BUTTON:
                if (socialIdVerified) {
                    ++currentPage;
                    Log.d(TAG, "currentPage " + currentPage);
                    Log.d(TAG, "headerTitles.length " + headerTitles.length);
                    if (currentPage >= headerTitles.length) {
                        currentPage = 5;
                        setCurrentPageDetails(currentPage);
                    } else {
                        setCurrentPageDetails(currentPage);
                    }
                } else {
                    //TODO waiting for backend server for verification service api to be called

                    socialIdVerified = true;
                    setScocialIdVerified();
                }

                break;
        }

    }
}
