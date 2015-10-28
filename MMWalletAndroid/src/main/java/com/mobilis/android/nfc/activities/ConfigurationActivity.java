package com.mobilis.android.nfc.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DatasetMetadata;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.amazonaws.mobileconnectors.cognito.SyncConflict;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.adapters.ConfigurationsAdapter;
import com.mobilis.android.nfc.aws.cognito.CognitoSyncClientManager;
import com.mobilis.android.nfc.model.TerminalConfigurationKeyValueModel;
import com.mobilis.android.nfc.model.TerminalConfigurationVariablesModel;
import com.mobilis.android.nfc.util.Constants;
import com.mobilis.android.nfc.util.ObjectMapperUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by lewischao on 21/08/15.
 */

@ContentView(R.layout.configuration)
public class ConfigurationActivity extends RoboActivity implements View.OnClickListener {
    private boolean debug = false;
    @InjectView(R.id.CONF_USSD_LABEL)
    TextView label;
    @InjectView(R.id.CONF_USSD_VALUES)
    Spinner ussdSpinner;
    @InjectView(R.id.CONF_SAVE)
    Button saveBtn;
    @Inject
    Context mContext;
    @InjectView(R.id.CONF_TERMINAL_PROFILE)
    ExpandableListView terminalConfList;
    @InjectView(R.id.CONF_PROGRESS)
    RelativeLayout progress;
    @InjectView(R.id.CONF_SERVER_PROFILES)
    Spinner profileSpinner;
    @InjectView(R.id.CONF_GENERAL_FLAG_ET)
    EditText generalFlageET;
    TerminalConfigurationVariablesModel serverConfs;
    ConfigurationsAdapter listAdapter;
    private final String TAG = "CONF";
    private CognitoSyncManager client;
    List<String> listDataHeader;
    HashMap<String, List<TerminalConfigurationKeyValueModel>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saveBtn.setOnClickListener(this);

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getBooleanValues(Constants.USSD_PAY_PIN_REQUIRED));
        countryAdapter.setDropDownViewResource(R.layout.spinner_layout);
        ussdSpinner.setAdapter(countryAdapter);
        ussdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(mContext.getResources().getColor(R.color.APP_MAIN_TEXT_COLOR));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        setUpAWSConfigurations();
    }

    public List<TerminalConfigurationKeyValueModel> getFields(TerminalConfigurationVariablesModel model) {
        ArrayList<TerminalConfigurationKeyValueModel> allData = new ArrayList<>();
        TerminalConfigurationKeyValueModel keyValueModel;
        try {
            for (Method method : model.getClass().getMethods()) {
                if (method.getName().startsWith("get") && !method.getName().equalsIgnoreCase("getClass") && !method.getName().equalsIgnoreCase("getServerSourceJSON") && !method.getName().equalsIgnoreCase("getInstance")) {
                    if (method.invoke(model) != null) {
                        keyValueModel = new TerminalConfigurationKeyValueModel();
                        if (debug) Log.d("TAG", "getName : " + method.getName());
                        if (debug) Log.d("TAG", "invoke : " + String.valueOf(method.invoke(model)));
                        keyValueModel.setKey(method.getName().replace("get", "").toUpperCase());
                        keyValueModel.setValue(String.valueOf(method.invoke(model)));
                        allData.add(keyValueModel);
                    }
                }
            }
        } catch (Exception e) {
            Log.d("TAG", "Exception: " + e.toString());
        }
        if (debug) Log.d("TAG", "all lists allData : " + allData.size());
        return allData;
    }

    private void toggleProgressBar(int status) {
        //progress.setVisibility(status);
/*       if(status != View.VISIBLE){
           terminalConfList.setVisibility(View.VISIBLE);
       }*/
    }

    //private Dataset dataset;
    // private Dataset.SyncCallback syncCallback =
    private void setUpAWSConfigurations() {
        try {
            client = CognitoSyncClientManager.getInstance();
            serverConfs = null;
            toggleProgressBar(View.VISIBLE);
            refreshDatasetMetadata();

        } catch (Exception e) {
            Log.d("TAG", "setUpAWSConfigurations error : " + e.toString());
            toggleProgressBar(View.GONE);
        }
    }

    private void onSyncFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toggleProgressBar(View.GONE);
            }
        });
    }

    private ArrayList<Record> serverProfileList = new ArrayList<>();

    private void setUpProfileSpinner(final ArrayList<String> profileList, final int selected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (debug) Log.d("TAG", "setUpProfileSpinner " + profileList.size());
                ArrayAdapter<String> profileAdapter = new ArrayAdapter<String>(mContext,
                        android.R.layout.simple_spinner_item, profileList);
                profileAdapter.setDropDownViewResource(R.layout.spinner_layout);
                profileSpinner.setAdapter(profileAdapter);
                profileSpinner.setSelection(selected);
                profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ((TextView) parent.getChildAt(0)).setTextColor(mContext.getResources().getColor(R.color.APP_MAIN_TEXT_COLOR));
                        ObjectMapper objectMapper = ObjectMapperUtil.getObjectMapper();
                        listDataHeader = new ArrayList<>();
                        listDataChild = new HashMap<>();
                        listDataHeader.add(serverProfileList.get(position).getKey());
                        try {
                            serverConfs = objectMapper.readValue(serverProfileList.get(position).getValue(), TerminalConfigurationVariablesModel.class);

                            List<TerminalConfigurationKeyValueModel> data = getFields(serverConfs);
                            if (debug) Log.d(TAG, "onItemSelected data size " + data.size());
                            listDataChild.put(serverProfileList.get(position).getKey(), data); //

                            listAdapter = new ConfigurationsAdapter(mContext, listDataHeader, listDataChild);
                            // setting list adapter
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    terminalConfList.setAdapter(listAdapter);
                                    terminalConfList.setVisibility(View.VISIBLE);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });

    }

    //private void displayData() {
    private void displayData(final List<Record> updatedRecords) {
        try {
            if (debug) Log.d("TAG", "displayData " + updatedRecords.size());
            ArrayList<String> profileList = new ArrayList<>();
            Record record = null;
            int defaultSelectedProfileIndex = 0;
            Record serverRecord;//: updatedRecords
            for (int i = 0; i < updatedRecords.size(); i++) {
                serverRecord = updatedRecords.get(i);
                if (debug) Log.d("TAG", "displayData " + serverRecord.getKey());
                if (debug) Log.d("TAG", "displayData " + serverRecord.getValue());
                if (serverRecord.getKey().equalsIgnoreCase(getString(R.string.AWS_TERMINAL_CONFS_PROFILE))) {
                    record = serverRecord;
                    defaultSelectedProfileIndex = i;
                }
                serverProfileList.add(serverRecord);
                profileList.add(serverRecord.getKey());
            }
            setUpProfileSpinner(profileList, defaultSelectedProfileIndex);
            if (record != null) {
                if (debug) Log.d("TAG", "displayData " + record.getKey());
                if (debug) Log.d("TAG", "displayData " + record.getValue());
                // for (Record record : dataset.getAllRecords()) {
                ObjectMapper objectMapper = ObjectMapperUtil.getObjectMapper();

                try {
                    // if(debug) Log.d(TAG, "serverConfigs.generalFlags before "+serverConfs);
                    serverConfs = objectMapper.readValue(record.getValue(), TerminalConfigurationVariablesModel.class);
                    if (debug)
                        Log.d(TAG, "serverConfigs.generalFlags after " + serverConfs.getGENERALFLAGS());
                    serverConfs.setServerSourceJSON(record.getValue());
                    listDataHeader = new ArrayList<>();
                    listDataChild = new HashMap<>();
                    listDataHeader.add(record.getKey());
                    List<TerminalConfigurationKeyValueModel> data = getFields(serverConfs);
                    if (debug) Log.d(TAG, "serverConfigs.generalFlags after " + data.size());
                    listDataChild.put(record.getKey(), data); //

                    listAdapter = new ConfigurationsAdapter(mContext, listDataHeader, listDataChild);
                    if (debug) Log.d(TAG, "serverConfigs.generalFlags after " + listAdapter);
                    // setting list adapter
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (debug)
                                Log.d(TAG, "serverConfigs.generalFlags setting " + listAdapter);
                            terminalConfList.setAdapter(listAdapter);
                            terminalConfList.setVisibility(View.VISIBLE);
                        }
                    });

                } catch (Exception e) {
                    Log.d("TAG", "error parsing " + e.toString());
                    toggleProgressBar(View.GONE);
                } finally {
                    toggleProgressBar(View.GONE);
                }

            }
        }catch(Exception e){}
    }

    private void refreshDatasetMetadata() {
        new RefreshDatasetMetadataTask().execute();
    }

    private class RefreshDatasetMetadataTask extends
            AsyncTask<Void, Void, Void> {
        boolean authError;
        String TAG = "RefreshDatasetMetadataTask";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                client.refreshDatasetMetadata();
            } catch (DataStorageException dse) {
                Log.e(TAG, "failed to refresh dataset metadata", dse);
            } catch (NotAuthorizedException e) {
                Log.e(TAG, "failed to refresh dataset metadata", e);
                authError = true;
            } catch (Exception e) {
                Log.e(TAG, "failed to refresh dataset metadata" + e.toString());
                authError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            try {
                ArrayList<DatasetMetadata> allDataSet = (ArrayList) client.listDatasets();//CognitoSyncClientManager.getInstance().listDatasets();
                if (allDataSet != null && allDataSet.size() > 0) {
                    Dataset dataset = null;
                    for (DatasetMetadata oneMeta : allDataSet) {
                        String dataName = oneMeta.getDatasetName();
                        if (debug) Log.d("TAG", "oneMeta dataName : " + dataName);

                        if (dataName.equals(getString(R.string.AWS_TERMINAL_CONFS))) {
                            dataset = client.openOrCreateDataset(dataName);
                            dataset.synchronize(new Dataset.SyncCallback() {
                                @Override
                                public void onSuccess(Dataset dataset, final List<Record> updatedRecords) {
                                    if (debug)
                                        Log.d("TAG", "setUpAWSConfigurations onSuccess : " + updatedRecords);
                                    if (debug)
                                        Log.d("TAG", "setUpAWSConfigurations onSuccess : " + updatedRecords.size());
                                    // displayData(updatedRecords);
                                    displayData(dataset.getAllRecords());
                                    //displayData();
                                }

                                @Override
                                public boolean onConflict(Dataset dataset, List<SyncConflict> conflicts) {
                                    if (debug) Log.d("TAG", "synchronize onConflict  : ");
                                    onSyncFailed();
                                    return false;
                                }

                                @Override
                                public boolean onDatasetDeleted(Dataset dataset, String datasetName) {
                                    if (debug) Log.d("TAG", "synchronize onDatasetDeleted  : ");
                                    return false;
                                }

                                @Override
                                public boolean onDatasetsMerged(Dataset dataset, List<String> datasetNames) {
                                    if (debug) Log.d("TAG", "synchronize onDatasetsMerged  : ");
                                    return false;
                                }

                                @Override
                                public void onFailure(DataStorageException dse) {
                                    //if(debug) Log.d("TAG", "synchronize onFailure  : " + dse.toString());
                                    onSyncFailed();
                                }
                            });
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.CONF_SAVE) {
            Constants.mainSecurePreferences.edit().putBoolean(getResources().getString(R.string.CONF_PAY_PIN_REQUIRED_KEY), !Boolean.parseBoolean(ussdSpinner.getSelectedItem().toString())).commit();
            Constants.USSD_PAY_PIN_REQUIRED = Constants.mainSecurePreferences.getBoolean(getResources().getString(R.string.CONF_PAY_PIN_REQUIRED_KEY), false);
            if (generalFlageET.getText() != null){
                serverConfs.setGENERALFLAGS(generalFlageET.getText().toString());
            }
            ApplicationActivity.configurationVariablesModel = serverConfs;
            Constants.mainSecurePreferences.edit().putString(getResources().getString(R.string.CONF_AWS_TERMINAL_KEY), serverConfs.getServerSourceJSON()).commit();
            Toast.makeText(mContext, "Configurations saved", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    private ArrayList<String> getBooleanValues(boolean defVal) {
        ArrayList<String> list = new ArrayList();
        if (!defVal) {
            list.add(Boolean.TRUE.toString());
            list.add(Boolean.FALSE.toString());
        } else {
            list.add(Boolean.FALSE.toString());
            list.add(Boolean.TRUE.toString());
        }


        return list;
    }
}
