package com.mobilis.android.nfc.util;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.mobilis.android.nfc.R;

import java.text.DecimalFormat;

/**
 * Created by ahmed on 8/05/14.
 */
public class UtilitiesOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    Activity mActivity;

    public UtilitiesOnItemSelectedListener(Activity activity){
        mActivity = activity;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        final EditText amount = (EditText) mActivity.findViewById(R.id.Fragment_Utilities_EditText_Amount);
        Double amountAsDouble =0.0;// Double.parseDouble(TransferCode.utilityCodes.get(pos).getAmount()) / 100;
        final DecimalFormat df = new DecimalFormat("#.00");
        if(amountAsDouble <= 0)
            amount.setEnabled(true);
        else
        {
            amount.setEnabled(false);
            amount.setText(df.format(amountAsDouble));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

}
