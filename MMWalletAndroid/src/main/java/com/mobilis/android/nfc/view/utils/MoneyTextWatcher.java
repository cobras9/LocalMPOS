package com.mobilis.android.nfc.view.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.util.CurrencyUtils;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.regex.Pattern;

/**
 * Created by lewischao on 24/03/15.
 */
public class MoneyTextWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;

    public MoneyTextWatcher(EditText editText) {
        editTextWeakReference = new WeakReference<EditText>(editText);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        EditText editText = editTextWeakReference.get();
        if (s.length() <= 0) {
            editText.setError(editText.getResources().getString(R.string.mandatory_field));
            return;
        } else {
            editText.setError(null);
        }

        editText.removeTextChangedListener(this);
        Log.d(this.getClass().getSimpleName(), "changed onTextChanged "+String.valueOf(s));
        String finalAmount = CurrencyUtils.getInstance().formatAmount(String.valueOf(s));
        Log.d(this.getClass().getSimpleName(), "changed onTextChanged "+finalAmount);
        editText.setText(finalAmount);
        editText.setSelection(finalAmount.length());
        editText.addTextChangedListener(this);
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
