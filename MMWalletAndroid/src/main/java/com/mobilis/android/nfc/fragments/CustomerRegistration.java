package com.mobilis.android.nfc.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.activities.ApplicationActivity;
import com.mobilis.android.nfc.activities.MerchantRegistrationActivity;
import com.mobilis.android.nfc.domain.INTENT;
import com.mobilis.android.nfc.model.Customer;
import com.mobilis.android.nfc.model.CustomerAddress;
import com.mobilis.android.nfc.slidemenu.utils.CustomerType;
import com.mobilis.android.nfc.slidemenu.utils.IDType;
import com.mobilis.android.nfc.slidemenu.utils.LoginResponseConstants;
import com.mobilis.android.nfc.tasks.ImageUploadTask;
import com.mobilis.android.nfc.tasks.LookupCustomerTask;
import com.mobilis.android.nfc.util.CustomerLookupUtil;
import com.mobilis.android.nfc.view.utils.ClearErrorTextWatcher;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by lewischao on 13/10/15.
 */
public class CustomerRegistration extends ApplicationActivity.PlaceholderFragment implements View.OnClickListener, CustomerLookupUtil.Callable {
    private View rootView;
    private Context mContext;
    private Activity mActivity;
    private String TAG = this.getClass().getSimpleName();
    private com.mobilis.android.nfc.model.CustomerRegistration customerRegistrationModel;

    private DecimalFormat mFormat;
    private CustomerLookupUtil customerUtil;
    private RegistrationHandler mHandler = new RegistrationHandler(this);
    private BroadcastReceiver broadcastReceiver;
    private int year, month, day;
    final static int SHOW_SERVER_RESPONSE = 1;
    final static int SHOW_TOAST = 2;
    final static int PERFORM_REGISTRATION = 3;
    final static int DATE_DIALOG = 999;

    private EditText accessNumberId;
    private RelativeLayout detailViews;
    private Button submitButton;
    //private EditText pin;
    //private EditText confirmPin;
    private EditText givenName;
    private EditText surname;
    private EditText email;
    private EditText dob;
    private Button lookupCustomerBtn;
    private RelativeLayout detailsLayout;
    private RelativeLayout progressLayout;
    private Button cancelButton;
    private ScrollView masterScrollView;
    private EditText business;
    private EditText address;
    private EditText city;
    private EditText state;
    private Spinner country;
    private Spinner verificationSpinner;
    private InputMethodManager inputMethodManager;
    final ArrayList<String> countryListNames = new ArrayList<String>();
    final ArrayList<Locale> countryList = new ArrayList<Locale>();
    private Spinner customerTypeSpinner;
    private RelativeLayout Customer_PhotoLayout;
    private Customer model;
    @SuppressWarnings("deprecation")
    //Image related 
    private ImageView Customer_Registration_PhotoId;
    private TextView Customer_Registration_PhotoIdLabel;
    private Button Customer_Registration_PhotoBTN;
    public static final int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath = "";
    /*    customerRegistration.setCustomer(getNewCustomer());
        customerRegistration.getConnTaskManager().startBackgroundTask();*/
    private boolean test = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_customer_registration, container, false);
        accessNumberId = (EditText) rootView.findViewById(R.id.Customer_Registration_AccessNumberIdET);
        Customer_Registration_PhotoId = (ImageView) rootView.findViewById(R.id.Customer_Registration_PhotoId);
        Customer_Registration_PhotoIdLabel = (TextView) rootView.findViewById(R.id.Customer_Registration_PhotoIdLabel);
        Customer_Registration_PhotoBTN =(Button)rootView.findViewById(R.id.Customer_Registration_PhotoBTN);
        detailViews = (RelativeLayout) rootView.findViewById(R.id.Customer_Registration_Details_layout);
        submitButton = (Button) rootView.findViewById(R.id.Customer_Registration_SubmitBtn);
        // pin = (EditText) rootView.findViewById(R.id.Customer_Registration_PinET);
        //confirmPin = (EditText) rootView.findViewById(R.id.Customer_Registration_Confirm_PinET);
        givenName = (EditText) rootView.findViewById(R.id.Customer_Registration_Given_NameET);
        surname = (EditText) rootView.findViewById(R.id.Customer_Registration_SurnameET);
        email = (EditText) rootView.findViewById(R.id.Customer_Registration_EmailET);
        dob = (EditText) rootView.findViewById(R.id.Customer_Registration_DOBET);
        lookupCustomerBtn = (Button) rootView.findViewById(R.id.Customer_Registration_LookUp_Customer_Btn);
        detailsLayout = (RelativeLayout) rootView.findViewById(R.id.Customer_Registration_Details_layout);
        progressLayout = (RelativeLayout) rootView.findViewById(R.id.ProgressBar_layout);
        cancelButton = (Button) rootView.findViewById(R.id.Customer_Registration_CancelBtn);
        masterScrollView = (ScrollView) rootView.findViewById(R.id.Customer_Registration_ScrollView);
        business = (EditText) rootView.findViewById(R.id.Customer_Registration_BusinessNameET);
        address = (EditText) rootView.findViewById(R.id.Customer_Registration_AddressET);
        city = (EditText) rootView.findViewById(R.id.Customer_Registration_CityET);
        state = (EditText) rootView.findViewById(R.id.Customer_Registration_StateET);
        country = (Spinner) rootView.findViewById(R.id.Customer_Registration_CountryET);
        customerTypeSpinner = (Spinner) rootView.findViewById(R.id.Customer_CustomerType);
        verificationSpinner = (Spinner) rootView.findViewById(R.id.Customer_IdVerification_Spinner);
        Customer_PhotoLayout = (RelativeLayout) rootView.findViewById(R.id.Customer_PhotoLayout);
        mActivity = getActivity();
        mContext = getActivity().getApplicationContext();
        inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        model = new Customer();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupPage();
    }

    private boolean dateDialogShowing = false;
/*    private DatePickerDialog.OnCancelListener myDateCancelListener = new DatePickerDialog.OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
            dateDialogShowing = false;
        }
    };*/
    private DatePickerDialog.OnDateSetListener myDateListener
            = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            setDob(arg1, arg2 + 1, arg3);
            dateDialogShowing = false;
        }
    };

    /*    @SuppressWarnings("deprecation")
        public Dialog onCreateDialog(int id) {
            // TODO Auto-generated method stub
            if (id == DATE_DIALOG) {
                return new DatePickerDialog(mActivity, DatePickerDialog.THEME_DEVICE_DEFAULT_DARK, myDateListener, year, month, day);
            }
            return null;
        }*/
    private void setDob(int year, int month, int day) {
        dob.setText(new StringBuilder().append(year).append("/").append(mFormat.format(month)).append("/").append(mFormat.format(day)));
    }

    private void setupPage() {
        customerRegistrationModel = new com.mobilis.android.nfc.model.CustomerRegistration(mActivity);

        mFormat = new DecimalFormat("00");
        disableDetails();
        if (customerUtil == null) {
            customerUtil = new CustomerLookupUtil(mActivity, this);
        }

        setFieldsBasics();
        setupPhotoEvents();
        setUpCustomerTypeSpinner();
        setUpIdSpinner();

    }
    @Override
    public void onResume() {
        super.onResume();
        setUpReceiver();
    }
    private void setUpIdSpinner() {
        final ArrayList<String> spinnerList = new ArrayList<String>();
        spinnerList.add("NONE");
        for (IDType type : LoginResponseConstants.idType) {
            spinnerList.add(type.getIDType().toString());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, R.id.Spinner_TextView, spinnerList);
        verificationSpinner.setAdapter(spinnerAdapter);
        verificationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(mContext.getResources().getColor(R.color.COUNTRY_SPINNER_COLOR));
                String idType = spinnerList.get(position);
                model.setCustomerVerificationId(idType);
                if (idType.equalsIgnoreCase("NONE")) {
                    Customer_PhotoLayout.setVisibility(View.GONE);
                    model.setiDVerified(false);

                    model.setIdType(null);
                } else if (idType.equalsIgnoreCase(IDType.TYPE.PHOTO_ID.toString())) {
                    model.setiDVerified(true);
                    model.setIdType(spinnerList.get(position));
                    Customer_PhotoLayout.setVisibility(View.VISIBLE);
                } else {
                    Customer_PhotoLayout.setVisibility(View.GONE);
                    model.setiDVerified(true);
                    model.setIdType(spinnerList.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        verificationSpinner.setSelection(0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.Customer_Registration_LookUp_Customer_Btn) {
            if (accessNumberId.getText().length() < 1) {
                createMessage(SHOW_SERVER_RESPONSE, getString(R.string.CONTACT_NUMBER_IS_MANDATORY));
            } else {
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                progressStatus(View.VISIBLE);
                LookupCustomerTask task = new LookupCustomerTask();
                task.execute(mActivity, this, customerUtil, accessNumberId.getText().toString());
            }

        } else if (v.getId() == R.id.Customer_Registration_SubmitBtn) {
            progressStatus(View.VISIBLE);
            performRegistration();
        } else if (v.getId() == R.id.Customer_Registration_CancelBtn) {
            restart();
            //mActivity.finish();
            //LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(INTENT.RESET_FRAGMENTS.toString()).putExtra(INTENT.EXTRA_POS.toString(), staticNumberOfFragment - 1));
        }
    }

    private void restart() {
        disableDetails();
        accessNumberId.setEnabled(true);
        accessNumberId.setFocusable(true);
        accessNumberId.setFocusableInTouchMode(true);
        accessNumberId.setText("");
        model = new Customer();
    }

    private void performRegistration() {
        if (checkPin()) {
            if (validateFields()) {
                //Log.d(TAG, " pin" + pin.getText().toString());

                model.setMSISDN(accessNumberId.getText().toString());
                //model.setMobMonPin(pin.getText().toString());
                model.setPhoneNumber(accessNumberId.getText().toString());
                model.setGivenName(givenName.getText().toString());
                model.setSurName(surname.getText().toString());
                model.setBusinessName(business.getText().toString());
                model.setEmailAddress(email.getText().toString());
                model.setDOB(dob.getText().toString().replaceAll("/", ""));
                CustomerAddress customerAddress = new CustomerAddress();
                customerAddress.setAddress1(address.getText().toString());
                customerAddress.setCity(city.getText().toString());
                customerAddress.setState(state.getText().toString());
                customerAddress.setCountry(((Locale) countryList.get(country.getSelectedItemPosition())).getISO3Country());
                model.setCustomerAddress(customerAddress);
                model.setCustomerType(customerTypeSpinner.getSelectedItem().toString());
                customerRegistrationModel.setCustomer(model);
                createMessage(PERFORM_REGISTRATION);
            } else {
                progressStatus(View.GONE);
                createMessage(SHOW_TOAST, getString(R.string.check_mandatory_field));
            }
        } else {
            progressStatus(View.GONE);
        }


    }

    private void setUpCustomerTypeSpinner() {
        if (LoginResponseConstants.customerTypes.size() == 0) {
            customerTypeSpinner.setVisibility(View.GONE);
            return;
        }

        final ArrayList<String> customerTypesList = new ArrayList<String>();
        final ArrayList<CustomerType> customerTypesObjList = new ArrayList<CustomerType>();

        for (CustomerType customerType : LoginResponseConstants.customerTypes) {
            customerTypesList.add(customerType.getCustomerType().getLabel());
            customerTypesObjList.add(customerType);
        }

        ArrayAdapter<String> customerTypeSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, R.id.Spinner_TextView, customerTypesList);
        customerTypeSpinner.setAdapter(customerTypeSpinnerAdapter);
        customerTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //setCustomerType(customerTypesObjList.get(position).getCustomerType().getValue());
                ((TextView) parent.getChildAt(0)).setTextColor(mContext.getResources().getColor(R.color.COUNTRY_SPINNER_COLOR));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        customerTypeSpinner.setSelection(0);
    }

    private boolean validateFields() {
        int childCount;
        View childView;
        if (accessNumberId.getText().length() == 0) {
            accessNumberId.setError(getString(R.string.mandatory_field));
            return false;
        } else {
            accessNumberId.setError(null);
        }
        boolean verified = true;
        childCount = detailViews.getChildCount();
        for (int i = 0; i < childCount; i++) {
            childView = detailViews.getChildAt(i);
            if (childView instanceof EditText && childView.getTag() != null && "validate".equals(childView.getTag().toString())) {
                if (((EditText) childView).getText().length() == 0) {
                    ((EditText) childView).setError(getString(R.string.mandatory_field));
                    verified = false;
                } else {
                    ((EditText) childView).setError(null);
                }
                Log.d(this.getClass().getSimpleName(), " child value " + ((EditText) childView).getText().toString());
            }
        }
        if (country.getSelectedItemPosition() == 0) {
            ((TextView) country.getSelectedView()).setError(getString(R.string.mandatory_field));
            verified = false;
        } else {
            ((TextView) country.getSelectedView()).setError(null);
        }
        if (model.getIdType()!=null && model.getIdType().equalsIgnoreCase(IDType.TYPE.PHOTO_ID.toString())) {
            if (mCurrentPhotoPath.equalsIgnoreCase("")) {
                verified = false;
                Customer_Registration_PhotoIdLabel.setError(getString(R.string.mandatory_field));
            }
        }
        return verified;
    }

    private boolean checkPin() {
/*        if (pin.getText().length() == 0) {
            createMessage(SHOW_SERVER_RESPONSE, getString(R.string.pin_cannot_be_empty));
            pin.requestFocus();
            return false;
        } else if (confirmPin.getText().length() == 0) {
            createMessage(SHOW_SERVER_RESPONSE, getString(R.string.pin_confirm_cannot_be_empty));
            confirmPin.requestFocus();
            return false;
        } else if (!pin.getText().toString().equals(confirmPin.getText().toString())) {
            createMessage(SHOW_SERVER_RESPONSE, getString(R.string.pin_does_not_match));
            confirmPin.requestFocus();
            return false;
        } else if (pin.getText().length() < getResources().getInteger(R.integer.MINIMUM_PIN_LENGTH)) {
            createMessage(SHOW_SERVER_RESPONSE, getString(R.string.pin_minimum_length_pref) + " " + getResources().getInteger(R.integer.MINIMUM_PIN_LENGTH) + " " + getString(R.string.pin_minimum_length_suf));
            pin.requestFocus();
            return false;
        }
        return true;*/
        return true;
    }

    private void setFieldsBasics() {
        masterScrollView.setOnTouchListener(new View.OnTouchListener() {
            // to solve focus problem on scrolling
            public boolean onTouch(View v, MotionEvent event) {

                IBinder windowToken = null;
                if (accessNumberId.hasFocus()) {
                    accessNumberId.clearFocus();
                    windowToken = accessNumberId.getWindowToken();
                }
                if (windowToken != null) {
                    inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
                }
                // masterScrollView.requestFocusFromTouch();
                return false;
            }
        });
        int childCount;
        childCount = detailViews.getChildCount();
        View childView;
        EditText actualET;
        for (int i = 0; i < childCount; i++) {
            childView = detailViews.getChildAt(i);
            if (childView instanceof EditText && childView.getTag() != null && "validate".equals(childView.getTag().toString())) {
                actualET = (EditText) childView;
                actualET.addTextChangedListener(new ClearErrorTextWatcher(actualET));
                actualET.setError(getString(R.string.mandatory_field));
            }
        }
        String[] locales = Locale.getISOCountries();
        countryList.add(null);
        countryListNames.add(mContext.getResources().getString(R.string.select_country_label));
        for (String countryCode : locales) {
            Locale locale = new Locale("", countryCode);
            countryList.add(locale);
            countryListNames.add(locale.getDisplayCountry());
        }
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(mActivity,
                android.R.layout.simple_spinner_item, countryListNames);
        countryAdapter.setDropDownViewResource(R.layout.spinner_layout);
        country.setAdapter(countryAdapter);
        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(mContext.getResources().getColor(R.color.COUNTRY_SPINNER_COLOR));
                if (position != 0) {
                    ((TextView) parent.getChildAt(0)).setError(null);
                } else {
                    ((TextView) parent.getChildAt(0)).setError(getString(R.string.mandatory_field));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ((TextView) parent.getChildAt(0)).setError(null);
            }
        });
        lookupCustomerBtn.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR) - 20;
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DATE);
        dob.setOnTouchListener(new View.OnTouchListener() {
                                   public boolean onTouch(View v, MotionEvent event) {
                                       if (!dateDialogShowing) {
                                           dateDialogShowing = true;
                                           showDateDialog();
                                       }
                                       return true;
                                   }
                               }
        );
/*        dob.setOnFocusChangeListener(new View.OnFocusChangeListener()

                                     {
                                         public void onFocusChange(View v, boolean gainFocus) {
                                             //onFocus
                                             if (gainFocus) {
                                                 mActivity.showDialog(DATE_DIALOG);
                                             }
                                             //onBlur
                                             else {
                                                 mActivity.dismissDialog(DATE_DIALOG);
                                             }
                                         }
                                     }

        );*/
        if (test) {

            //pin.setText("1234");
            //confirmPin.setText("1234");
            givenName.setText("Lewis");
            surname.setText("Chao");
            email.setText("cobras9@gmail.com");
            business.setText("Youtap");
            address.setText("3739 ireland st");
            city.setText("AKL");
            state.setText("AKL");

        }
    }

    DatePickerDialog dateDialog;

    private void showDateDialog() {
        if (dateDialog != null) {
            if (dateDialog.isShowing()) {
                dateDialog.dismiss();
            }
        }
        dateDialog = new DatePickerDialog(mActivity, DatePickerDialog.THEME_DEVICE_DEFAULT_DARK, myDateListener, year, month, day);
        dateDialog.show();
        dateDialog.setCanceledOnTouchOutside(false);
        dateDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    dateDialog.hide();
                    dateDialogShowing = false;
                }
            }
        });
       // dateDialog.setOnCancelListener(myDateCancelListener);
    }

    private void setUpReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(this.getClass().getSimpleName(), "onReceive  broadcastReceiver " + broadcastReceiver);
                progressStatus(View.GONE);
                if (intent.getAction().equalsIgnoreCase(INTENT.SERVER_COMM_TIME_OUT.toString())) {
                    showServerResponseDialog(intent.getStringExtra(INTENT.EXTRA_ERROR.toString()));
                } else if (intent.getAction().equalsIgnoreCase(INTENT.CUSTOMER_REGISTRATION.toString())) {
                    String serverResponse = intent.getStringExtra(INTENT.EXTRA_SERVER_RESPONSE.toString());
                    String resp = customerRegistrationModel.getMessageFromServerResponse(serverResponse);
                    Log.d(this.getClass().getSimpleName(), "registration serverResponse " + serverResponse);
                    Log.d(this.getClass().getSimpleName(), "registration resp " + resp);
                    if (resp.equalsIgnoreCase("OK")) {
                        //Start sending photo to server TODO
                        createMessage(SHOW_TOAST, String.format(getString(R.string.customer_regsistered), model.getMSISDN()));
                        Log.d(this.getClass().getSimpleName(), "registration mCurrentPhotoPath " + mCurrentPhotoPath);
                        if (!mCurrentPhotoPath.equalsIgnoreCase("")) {
                            ImageUploadTask iut = new ImageUploadTask();
                            iut.execute(mContext, model.getMSISDN(), customerRegistrationModel.getAndroidId(mContext), mCurrentPhotoPath);
                        }
                        restart();
                    } else {
                        createMessage(SHOW_SERVER_RESPONSE, resp);
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.CUSTOMER_REGISTRATION.toString()));
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.SERVER_COMM_TIME_OUT.toString()));
    }

    @Override
    public void onPause() {
        super.onPause();
        //cleanUp();
        if (customerUtil != null) {
            customerUtil.unregisterReceiver();
            customerUtil = null;
        }
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(broadcastReceiver);
        broadcastReceiver = null;
    }

    private void disableDetails() {
        progressStatus(View.GONE);
        detailsLayout.setVisibility(View.GONE);
        lookupCustomerBtn.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
    }

    private void progressStatus(int status) {
        progressLayout.setVisibility(status);
    }

    static class RegistrationHandler extends Handler {
        WeakReference<CustomerRegistration> registrationFragment;

        RegistrationHandler(CustomerRegistration rFragment) {
            registrationFragment = new WeakReference<CustomerRegistration>(rFragment);
        }

        @Override
        public void handleMessage(Message message) {
            CustomerRegistration registrationHandler = registrationFragment.get();
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
                        registrationHandler.customerRegistrationModel.getConnTaskManager().startBackgroundTask();
                        break;
                }
            }
        }
    }

    private void showToast(String message, boolean close) {
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
/*        if (close) {
            getActivity().finish();
        }*/

    }

    private void showServerResponseDialog(String message) {
        final Dialog messageDialog = new Dialog(mActivity);
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

    @Override
    public void finishedExtractingCustomers(ArrayList<Customer> customers) {
        Log.d(TAG, "In merchantRegistation finishedExtract customers");
        for (Customer c : customers) {
            Log.d(TAG, "Index: " + c.getIndex());
            Log.d(TAG, "GiveName: " + c.getGivenName());
            Log.d(TAG, "SurName: " + c.getSurName());
            Log.d(TAG, "MSISDN: " + c.getMSISDN());
            Log.d(TAG, "DOB: " + c.getDOB());
            Log.d(TAG, "CustomerId: " + c.getCustomerId());
        }
        disableDetails();
        createMessage(SHOW_SERVER_RESPONSE, getString(R.string.FOUND_CUSTOMER_MESSAGE));
    }

    @Override
    public void finishedUpdatingCustomer(String response) {

    }

    @Override
    public void error(String errorMessage, int status) {
        Log.d(MerchantRegistrationActivity.class.getSimpleName(), "No merchant found " + errorMessage + "..." + status);
        enableDetails();
        createMessage(SHOW_TOAST, getString(R.string.valid_merchant_continue_registration));

    }

    private void enableDetails() {
        progressStatus(View.GONE);
        detailsLayout.setVisibility(View.VISIBLE);
        lookupCustomerBtn.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        accessNumberId.setEnabled(false);
        accessNumberId.setFocusable(false);
        accessNumberId.setFocusableInTouchMode(false);

    }


    //Image related
    private void setupPhotoEvents() {
        Customer_Registration_PhotoId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "change user icon");
                dispatchTakePictureIntent();
            }
        });
        Customer_Registration_PhotoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "change user icon");
                dispatchTakePictureIntent();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode " + requestCode);
        Log.d(TAG, "resultCode " + resultCode);
        Log.d(TAG, "data " + data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult mCurrentPhotoPath " + mCurrentPhotoPath);
            galleryAddPic();
            ExifInterface exif = null;
            int orientation =ExifInterface.ORIENTATION_NORMAL;
            try {
                exif = new ExifInterface(mCurrentPhotoPath);
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
            } catch (IOException e) {
                e.printStackTrace();
            }
            setPic(Customer_Registration_PhotoId, orientation);
            Customer_Registration_PhotoIdLabel.setError(null);
           // ImageUploadTask iut = new ImageUploadTask();
            //iut.execute(mContext, accessNumberId.getText().toString(), customerRegistrationModel.getAndroidId(mContext), mCurrentPhotoPath);
        }
    }

    private void setPic(ImageView mImageView,int orientation) {
        try {
            // Get the dimensions of the View
            int targetW = mImageView.getWidth();
            int targetH = mImageView.getHeight();
            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            // Determine how much to scale down the image
            int scaleFactor = 5;
            if (targetW > 0 && targetH > 0) {
                scaleFactor = Math.min(photoW / targetW, photoH / targetH);
            }
            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    break;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                default:
                    break;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            //  Bitmap bm = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), mCurrentPhotoPath);

            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            mImageView.setImageBitmap(bmRotated);//.setBackground(d);
        }catch(Exception e){
            Log.e(TAG,"error while rotating image"+e.toString());
            int targetW = mImageView.getWidth();
            int targetH = mImageView.getHeight();
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            // Determine how much to scale down the image
            int scaleFactor = 5;
            if (targetW > 0 && targetH > 0) {
                scaleFactor = Math.min(photoW / targetW, photoH / targetH);
            }
            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            mImageView.setImageBitmap(bitmap);//.setBackground(d);
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mActivity.sendBroadcast(mediaScanIntent);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d(TAG, "dispatchTakePictureIntent ");
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "YOUTAP_PNG" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getCanonicalPath();
        Log.d(TAG, "file path " + mCurrentPhotoPath);
        return image;
    }
}
