package com.mobilis.android.nfc.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.model.TerminalConfigurationKeyValueModel;

import java.util.HashMap;
import java.util.List;


public class ConfigurationsAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<TerminalConfigurationKeyValueModel>> _listDataChild;

    public ConfigurationsAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<TerminalConfigurationKeyValueModel>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public TerminalConfigurationKeyValueModel getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        TerminalConfigurationKeyValueModel configurationKeyValueModel = getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.configuration_list_item, null);
        }
        TextView keyTV = (TextView) convertView
                .findViewById(R.id.CONF_KEY);
        TextView valueTV = (TextView) convertView
                .findViewById(R.id.CONF_VALUE);
        keyTV.setText(configurationKeyValueModel.getKey());
        valueTV.setText(String.valueOf(configurationKeyValueModel.getValue()));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Log.d("TAG", "getChildView getChildrenCount : " + this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size());
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.configuration_list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.CONF_LABEL_TV);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}