package com.mobilis.android.nfc.view.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.mobilis.android.nfc.R;

/**
 * Created by lewischao on 24/03/15.
 */
public class ClearErrorTextWatcher implements TextWatcher {
    EditText currentET;

    public ClearErrorTextWatcher(EditText editText) {
        currentET = editText;
    }

    public void onTextChanged(CharSequence s, int start, int before,
                              int count) {

    }

    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    public void afterTextChanged(Editable s) {
        if (!s.toString().equals("")) {
            currentET.setError(null);
        } else {
            currentET.setError(currentET.getResources().getString(R.string.mandatory_field));
        }
    }
}
