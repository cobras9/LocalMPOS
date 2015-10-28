package com.mobilis.android.nfc.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
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

import com.google.inject.Inject;
import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.domain.INTENT;
import com.mobilis.android.nfc.model.AbstractModel;
import com.mobilis.android.nfc.model.Customer;
import com.mobilis.android.nfc.model.MerchantRegistration;
import com.mobilis.android.nfc.tasks.ImageUploadTask;
import com.mobilis.android.nfc.tasks.LookupCustomerTask;
import com.mobilis.android.nfc.util.CustomerLookupUtil;
import com.mobilis.android.nfc.util.LocationUtils;
import com.mobilis.android.nfc.util.SecurePreferences;
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

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.merchant_registration)
public class MerchantRegistrationActivity extends RoboActivity implements OnClickListener, CustomerLookupUtil.Callable{
    @InjectView(R.id.Merchant_Registration_RootView)
    RelativeLayout rootView;
    @InjectView(R.id.Merchant_Registration_Details_layout)
    RelativeLayout detailViews;

    @InjectView(R.id.Merchant_Registration_SubmitBtn)
    Button submitButton;
    @InjectView(R.id.Merchant_Registration_AccessNumberIdET)
    EditText accessNumberId;
    @InjectView(R.id.Merchant_Registration_MerchantIdET)
    EditText merchantId;

    @InjectView(R.id.Merchant_Registration_PinET)
    EditText pin;
    @InjectView(R.id.Merchant_Registration_Confirm_PinET)
    EditText confirmPin;
    @InjectView(R.id.Merchant_Registration_Given_NameET)
    EditText givenName;
    @InjectView(R.id.Merchant_Registration_SurnameET)
    EditText surname;
    @InjectView(R.id.Merchant_Registration_EmailET)
    EditText email;
    @InjectView(R.id.Merchant_Registration_DOBET)
    EditText dob;
    @InjectView(R.id.Merchant_Registration_LookUp_Merchant_Btn)
    Button lookupMerchantBtn;
    @InjectView(R.id.Merchant_Registration_Details_layout)
    RelativeLayout detailsLayout;
    @InjectView(R.id.ProgressBar_layout)
    RelativeLayout progressLayout;
    @InjectView(R.id.Merchant_Registration_CancelBtn)
    Button cancelButton;

    @InjectView(R.id.Merchant_Registration_ScrollView)
    ScrollView masterScrollView;
    @InjectView(R.id.Merchant_Registration_BusinessNameET)
    EditText business;
    @InjectView(R.id.Mechant_Registration_AddressET)
    EditText address;

    @InjectView(R.id.Mechant_Registration_CityET)
    EditText city;
    @InjectView(R.id.Mechant_Registration_StateET)
    EditText state;
    @InjectView(R.id.Mechant_Registration_CountryET)
    Spinner country;
    @Inject
    Context mContext;
    @Inject
    LocationManager locationManager;
    @InjectView(R.id.Merchant_Registration_PhotoIdLabel)
    TextView photoLabel;
    @InjectView(R.id.Merchant_Registration_PhotoId)
    ImageView photoIdImage;
    private String TAG =this.getClass().getSimpleName();
    public static final int REQUEST_TAKE_PHOTO = 1;

    LocationListener locationListener;
    private RegistrationHandler mHandler = new RegistrationHandler(this);
    private CustomerLookupUtil customerUtil;
    private MerchantRegistration model;
    private BroadcastReceiver broadcastReceiver;
    private int year, month, day;
    final static int SHOW_SERVER_RESPONSE = 1;
    final static int SHOW_TOAST = 2;
    final static int PERFORM_REGISTRATION = 3;
    final static int DATE_DIALOG = 999;
    private DecimalFormat mFormat;
    @Inject
    InputMethodManager inputMethodManager;
    @Inject
    Activity mActivity;

    LocationUtils location;// = new LocationUtils(this);
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setUpPage();
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, location);

    /*    locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(this.getClass().getSimpleName(),"onLocationChanged location "+location);
                accessNumberId.setText(location.getLatitude()+"____"+location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(this.getClass().getSimpleName(),"onStatusChanged provider "+provider);
                Log.d(this.getClass().getSimpleName(),"onStatusChanged status "+status);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(this.getClass().getSimpleName(),"onProviderEnabled "+provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(this.getClass().getSimpleName(),"onProviderDisabled "+provider);
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
*/    }

    @Override
    public void onResume() {
        super.onResume();

    }


    private void setUpPage() {
        location = new LocationUtils(this);
        mFormat = new DecimalFormat("00");
        model = new MerchantRegistration(this);
        disableDetails();
        if (customerUtil == null) {
            customerUtil = new CustomerLookupUtil(this, this);
        }

        // merchantId.setText("0279269700");
/*        accessNumberId.setText("642792697111");
        email.setText("cobras9@gmail.com");
        pin.setText("1234");
        confirmPin.setText("1234");
        //phoneNumber.setText("66291204");
        givenName.setText("64279269711");
        surname.setText("64279269711");
        address.setText("Harding Mount Wellington");
        city.setText("Auckland");
        state.setText("AucklandState");*/
/*        if(location != null && location.canGetLocation()) {
            accessNumberId.setText(location.getLatitude()+"..."+location.getLongitude());
        }*/
        setUpReceiver();
        setFieldsBasics();
        setupPhotoEvents();
        setUpServerIp();
    }
    private void setUpServerIp(){
        String appVersionCode = String.valueOf(model.getAppVersionCode(model.getActivity()));
        String SERVER_PORT = model.getActivity().getString(R.string.SERVER_PORT);
        String SERVER_IP = model.getActivity().getString(R.string.SERVER_IP);
        if(LoginActivity.mainSecurePreferences.contains(appVersionCode + SecurePreferences.KEY_SERVER_IP)) {
            SERVER_IP = LoginActivity.mainSecurePreferences.getString(appVersionCode + SecurePreferences.KEY_SERVER_IP, SERVER_IP);
        }else{
            LoginActivity.mainSecurePreferences.edit().putString(appVersionCode + SecurePreferences.KEY_SERVER_IP, SERVER_IP);
            LoginActivity.mainSecurePreferences.edit().commit();
        }
        if(LoginActivity.mainSecurePreferences.contains(appVersionCode + SecurePreferences.KEY_SERVER_PORT)) {
            SERVER_PORT =  LoginActivity.mainSecurePreferences.getString(appVersionCode + SecurePreferences.KEY_SERVER_PORT, SERVER_PORT);
        }else{
            LoginActivity.mainSecurePreferences.edit().putString(appVersionCode + SecurePreferences.KEY_SERVER_PORT, SERVER_PORT);
            LoginActivity.mainSecurePreferences.edit().commit();
        }
        model.saveIPAddress(SERVER_IP);
        model.savePort(SERVER_PORT);
    }
    String mCurrentPhotoPath;
    private void setupPhotoEvents(){
        photoIdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "change user icon");
                dispatchTakePictureIntent();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode " + requestCode);
        Log.d(TAG, "resultCode " + resultCode);
        Log.d(TAG, "data " + data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult mCurrentPhotoPath " + mCurrentPhotoPath);
            galleryAddPic();
            setPic(photoIdImage);
            ImageUploadTask iut = new ImageUploadTask();
            iut.execute(mContext, accessNumberId.getText().toString(),model.getAndroidId(mContext),pin.getText().toString(),mCurrentPhotoPath);
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "YOUTAP_PNG" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES );
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
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d(TAG, "dispatchTakePictureIntent ");
        // Log.d(TAG, "dispatchTakePictureIntent "+  takePictureIntent.resolveActivity(getPackageManager()) );
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
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
    private void setPic(ImageView mImageView) {
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

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        Matrix matrix = new Matrix();
/*        imageView.setScaleType(ImageView.ScaleType.MATRIX);   //required
        matrix.postRotate((float) angle, pivotX, pivotY);
        imageView.setImageMatrix(matrix);*/
        Drawable d = new BitmapDrawable(getResources(), bitmap);

        mImageView.setImageBitmap(bitmap);//.setBackground(d);
       // mImageView.setRotation(90f);
        //bitmap.recycle();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    private DatePickerDialog.OnDateSetListener myDateListener
            = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            setDob(arg1, arg2 + 1, arg3);
        }
    };
    /*    class ClearErrorTextWatcher implements TextWatcher{
            EditText currentET;
            public ClearErrorTextWatcher(EditText editText){
                currentET = editText;
            }
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(!s.equals("") )
                {
                    currentET.setError(null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        };
        */
    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == DATE_DIALOG) {
            return new DatePickerDialog(this, DatePickerDialog.THEME_DEVICE_DEFAULT_DARK, myDateListener, year, month, day);
        }
        return null;
    }

    private void setDob(int year, int month, int day) {
        dob.setText(new StringBuilder().append(year).append("/").append(mFormat.format(month)).append("/").append(mFormat.format(day)));
    }

    private void setUpReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                progressStatus(View.GONE);
                if (intent.getAction().equalsIgnoreCase(INTENT.SERVER_COMM_TIME_OUT.toString())) {
                    showServerResponseDialog(intent.getStringExtra(INTENT.EXTRA_ERROR.toString()));
                } else if (intent.getAction().equalsIgnoreCase(INTENT.MERCHANT_REGISTRATION.toString())) {
                    String serverResponse = intent.getStringExtra(INTENT.EXTRA_RESPONSE.toString());
                    String resp = model.getMessageFromServerResponse(serverResponse);
                    Log.d(this.getClass().getSimpleName(), "registration serverResponse " + serverResponse);
                    Log.d(this.getClass().getSimpleName(), "registration resp " + resp);
                    if (model != null) {
                        model.saveMerchantId(accessNumberId.getText().toString());
                    }
                    if (AbstractModel.getStatusFromServerResponse(serverResponse) == getResources().getInteger(R.integer.NEW_PIN_STATUS_CODE)) {
                        startActivity(new Intent(MerchantRegistrationActivity.this, ChangePinActivity.class));
                    } else if (resp.equalsIgnoreCase("OK")) {
                        //Start sending photo to server TODO

                        createMessage(SHOW_TOAST, getString(R.string.merchant_registered));
                        mActivity.finish();
                    } else {
                        createMessage(SHOW_SERVER_RESPONSE, resp);
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.MERCHANT_REGISTRATION.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.SERVER_COMM_TIME_OUT.toString()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //cleanUp();
        if (customerUtil != null) {
            customerUtil.unregisterReceiver();
            customerUtil = null;
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        broadcastReceiver = null;
        if(locationManager != null){
            locationManager.removeUpdates(location);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.Merchant_Registration_LookUp_Merchant_Btn) {
            if(accessNumberId.getText().length()<1){
                createMessage(SHOW_SERVER_RESPONSE, getString(R.string.customer_number_cannont_be_empty));
            }else{
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                progressStatus(View.VISIBLE);
                LookupCustomerTask task = new LookupCustomerTask();
                task.execute(this.getApplicationContext(), this, customerUtil, accessNumberId.getText().toString());
            }

        } else if (v.getId() == R.id.Merchant_Registration_SubmitBtn) {
            progressStatus(View.VISIBLE);
            performRegistration();
        } else if (v.getId() == R.id.Merchant_Registration_CancelBtn) {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanUp();
    }

    private void cleanUp() {
        if (customerUtil != null) {
            customerUtil.unregisterReceiver();
            customerUtil = null;
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        broadcastReceiver = null;
        if(locationManager != null){
            locationManager.removeUpdates(location);
        }
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cleanUp();
    }

    private boolean checkPin() {
        if (pin.getText().length() == 0) {
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
        }else if (pin.getText().length()<getResources().getInteger(R.integer.MINIMUM_PIN_LENGTH)){
            createMessage(SHOW_SERVER_RESPONSE, getString(R.string.pin_minimum_length_pref)+" "+ getResources().getInteger(R.integer.MINIMUM_PIN_LENGTH)+" "+getString(R.string.pin_minimum_length_suf));
            pin.requestFocus();
            return false;
        }
        return true;
    }
    final ArrayList<String> countryListNames = new ArrayList<String>();
    final ArrayList<Locale> countryList = new ArrayList<Locale>();
    @SuppressWarnings("deprecation")
    private void setFieldsBasics(){
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
        accessNumberId.requestFocus();
        accessNumberId.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(accessNumberId, InputMethodManager.SHOW_FORCED);
            }
        }, 30);
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
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,countryListNames);
        countryAdapter.setDropDownViewResource( R.layout.spinner_layout);
        country.setAdapter(countryAdapter);
        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(mContext.getResources().getColor(R.color.COUNTRY_SPINNER_COLOR));
                if(position != 0) {
                    ((TextView) parent.getChildAt(0)).setError(null);
                }else{
                    ((TextView) parent.getChildAt(0)).setError(getString(R.string.mandatory_field));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ((TextView) parent.getChildAt(0)).setError(null);
            }
        });
        lookupMerchantBtn.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR)-20;
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DATE);
        dob.setOnTouchListener(new View.OnTouchListener() {
                                   public boolean onTouch(View v, MotionEvent event) {
                                       showDialog(DATE_DIALOG);
                                       return true;
                                   }
                               }
        );
        dob.setOnFocusChangeListener(new View.OnFocusChangeListener()

                                     {
                                         public void onFocusChange(View v, boolean gainFocus) {
                                             //onFocus
                                             if (gainFocus) {
                                                 showDialog(DATE_DIALOG);
                                             }
                                             //onBlur
                                             else {
                                                 dismissDialog(DATE_DIALOG);
                                             }
                                         }
                                     }

        );
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
        if(country.getSelectedItemPosition()==0){
            ((TextView)country.getSelectedView()).setError(getString(R.string.mandatory_field));
            verified = false;
        }else{
            ((TextView)country.getSelectedView()).setError("");
        }
        return verified;
    }

    private void performRegistration() {
        if (checkPin()) {
            if (validateFields()) {
                Log.d(MerchantRegistrationActivity.class.getSimpleName(), " pin" + pin.getText().toString());
                model.setMsisdn(accessNumberId.getText().toString());
                model.setMobMonPin(pin.getText().toString());
                model.setPhone(accessNumberId.getText().toString());
                model.setGivenName(givenName.getText().toString());
                model.setSurName(surname.getText().toString());
                model.setBusinessName(business.getText().toString());
                model.setEmail(email.getText().toString());
                model.setDob(dob.getText().toString().replaceAll("/", ""));
                model.setAddressLine(address.getText().toString());
                model.setCity(city.getText().toString());
                model.setState(state.getText().toString());
                model.setCountry(((Locale) countryList.get(country.getSelectedItemPosition())).getISO3Country());
                if(location != null && location.canGetLocation()) {
                    model.setGeoLocation(location.getLatLong());
                }
                createMessage(PERFORM_REGISTRATION);
            } else {
                progressStatus(View.GONE);
                createMessage(SHOW_TOAST, getString(R.string.check_mandatory_field));
            }
        } else {
            progressStatus(View.GONE);
        }


    }

    private void progressStatus(int status) {
        progressLayout.setVisibility(status);
    }

    private void enableDetails() {
        progressStatus(View.GONE);
        detailsLayout.setVisibility(View.VISIBLE);
        lookupMerchantBtn.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);

    }

    private void disableDetails() {
        progressStatus(View.GONE);
        detailsLayout.setVisibility(View.GONE);
        lookupMerchantBtn.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
    }

    private void showToast(String message, boolean close) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        if (close) {
            this.finish();
        }

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

    @Override
    public void finishedExtractingCustomers(ArrayList<Customer> customers) {
        Log.d(MerchantRegistrationActivity.class.getSimpleName(), "In merchantRegistation finishedExtract customers");
        for (Customer c : customers) {
            Log.d(MerchantRegistrationActivity.class.getSimpleName(), "Index: " + c.getIndex());
            Log.d(MerchantRegistrationActivity.class.getSimpleName(), "GiveName: " + c.getGivenName());
            Log.d(MerchantRegistrationActivity.class.getSimpleName(), "SurName: " + c.getSurName());
            Log.d(MerchantRegistrationActivity.class.getSimpleName(), "MSISDN: " + c.getMSISDN());
            Log.d(MerchantRegistrationActivity.class.getSimpleName(), "DOB: " + c.getDOB());
            Log.d(MerchantRegistrationActivity.class.getSimpleName(), "CustomerId: " + c.getCustomerId());
        }
        disableDetails();
        createMessage(SHOW_SERVER_RESPONSE, getString(R.string.access_number_already_exists));
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
    public void finishedUpdatingCustomer(String response) {

    }

    @Override
    public void error(String errorMessage, int status) {
        Log.d(MerchantRegistrationActivity.class.getSimpleName(), "No merchant found " + errorMessage + "..." + status);
        enableDetails();
        createMessage(SHOW_TOAST, getString(R.string.valid_merchant_continue_registration));

    }

    static class RegistrationHandler extends Handler {
        WeakReference<MerchantRegistrationActivity> registrationActivity;

        RegistrationHandler(MerchantRegistrationActivity rActivity) {
            registrationActivity = new WeakReference<MerchantRegistrationActivity>(rActivity);
        }

        @Override
        public void handleMessage(Message message) {
            MerchantRegistrationActivity registrationHandler = registrationActivity.get();
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
                        registrationHandler.model.getConnTaskManager().startBackgroundTask();
                        break;
                }
            }
        }
    }
}
