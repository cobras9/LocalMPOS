package com.mobilis.android.nfc.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.jsonModel.Currency;
import com.mobilis.android.nfc.jsonModel.Preset;
import com.mobilis.android.nfc.util.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewischao on 18/03/15.
 */
public class TopUpDetailsAdapters extends BaseAdapter {
    ArrayList<Preset> presets;
    Context mContext;
    int layoutResourceId;

    public TopUpDetailsAdapters(Context context, int resource, ArrayList<Preset> presets) {
        this.presets = presets;
        this.mContext = context;
        this.layoutResourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PresetHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new PresetHolder();
            holder.dispayAmount = (TextView)row.findViewById(R.id.Spinner_TextView);
            row.setTag(holder);
        } else {
            holder = (PresetHolder) row.getTag();
        }
        Preset preset = presets.get(position);
        holder.code = preset.getCode();
        holder.amount = preset.getAmount();
        holder.dispayAmount.setText(CurrencyUtils.getInstance().formatAmount(preset.getAmount()));
        return row;
    }

    @Override
    public int getCount() {
        return presets.size();
    }

    @Override
    public String getItem(int position) {
        if(null != presets){
            try {
                return CurrencyUtils.getInstance().formatAmount(presets.get(position).getAmount());
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;//Long.parseLong(presets.get(position).getCode());
    }

    public String getItemCode(int position) {
        if(null != presets){
            try {
                return presets.get(position).getCode();
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }
        return null;
    }

    static class PresetHolder {
        TextView dispayAmount;
        String code;
        String amount;
    }
}
