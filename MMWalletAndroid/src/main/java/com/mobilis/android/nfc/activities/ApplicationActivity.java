package com.mobilis.android.nfc.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.domain.AccountBalance;
import com.mobilis.android.nfc.domain.INTENT;
import com.mobilis.android.nfc.fragments.BankSendMoneyFragment;
import com.mobilis.android.nfc.fragments.BillPaymentsListFragment;
import com.mobilis.android.nfc.fragments.BuyElectronicVoucherFragment;
import com.mobilis.android.nfc.fragments.CableTVFragment;
import com.mobilis.android.nfc.fragments.CashOutFragment;
import com.mobilis.android.nfc.fragments.CashOutVoucherFragment;
import com.mobilis.android.nfc.fragments.CashinFragment;
import com.mobilis.android.nfc.fragments.ChangePinFragment;
import com.mobilis.android.nfc.fragments.CustomerCreationFirstFragment;
import com.mobilis.android.nfc.fragments.CustomerCreationSecondFragment;
import com.mobilis.android.nfc.fragments.CustomerCreationThirdFragment;
import com.mobilis.android.nfc.fragments.DepositFundsFromCreditCardFragment;
import com.mobilis.android.nfc.fragments.ElectronicVoucherFragment;
import com.mobilis.android.nfc.fragments.GenerateTokenFragment;
import com.mobilis.android.nfc.fragments.MerchantServicesFragment;
import com.mobilis.android.nfc.fragments.OtherOperatorsSendMoneyFragment;
import com.mobilis.android.nfc.fragments.ReceiveMoneyFragment;
import com.mobilis.android.nfc.fragments.RedeemVoucherFragment;
import com.mobilis.android.nfc.fragments.SendMoneyFragment;
import com.mobilis.android.nfc.fragments.SendMoneyListFragment;
import com.mobilis.android.nfc.fragments.ServerConfigurationFragment;
import com.mobilis.android.nfc.fragments.TagRegistrationFragment;
import com.mobilis.android.nfc.fragments.TopUpFragment;
import com.mobilis.android.nfc.fragments.UtilitiesFragment;
import com.mobilis.android.nfc.interfaces.A2ACallback;
import com.mobilis.android.nfc.model.BanksCodeModel;
import com.mobilis.android.nfc.model.BillPaymentCodesModel;
import com.mobilis.android.nfc.model.Customer;
import com.mobilis.android.nfc.model.CustomerRegistration;
import com.mobilis.android.nfc.model.EVDCodes;
import com.mobilis.android.nfc.model.LastTransactions;
import com.mobilis.android.nfc.model.LoginBalanceInternal;
import com.mobilis.android.nfc.model.TerminalConfigurationVariablesModel;
import com.mobilis.android.nfc.model.TopUpCreditDestinationCodes;
import com.mobilis.android.nfc.model.TopupAirtimeDestinationCodes;
import com.mobilis.android.nfc.model.TransferCode;
import com.mobilis.android.nfc.model.UtilityCodes;
import com.mobilis.android.nfc.slidemenu.utils.LoginResponseConstants;
import com.mobilis.android.nfc.tabsfragments.AccountFragment;
import com.mobilis.android.nfc.tabsfragments.MenuFragment;
import com.mobilis.android.nfc.tabsfragments.QuickLinkFragment;
import com.mobilis.android.nfc.util.Constants;
import com.mobilis.android.nfc.util.ConversionUtil;
import com.mobilis.android.nfc.util.NFCForegroundUtil;
import com.mobilis.android.nfc.util.ObjectMapperUtil;
import com.mobilis.android.nfc.util.RootUtil;
import com.mobilis.android.nfc.util.SecurePreferences;
import com.mobilis.android.nfc.widget.AndroidToAndroidNFCActivityLowerVersions;

import java.util.Arrays;
import java.util.Locale;

public class ApplicationActivity extends Activity implements A2ACallback , View.OnClickListener{

    private final String TAG = ApplicationActivity.class.getSimpleName();
    public static String loginClientId;
    public static String loginClientPin;
    public static boolean isRefreshRequired;
    public static YoutapReader youReader;
    public static AccountBalance ACCOUNT_BALANCE = null;
    FragmentStatePagerAdapter mSectionsPagerAdapter;
    static ViewPager mViewPager;
    public static TerminalConfigurationVariablesModel configurationVariablesModel;// = new TerminalConfigurationVariablesModel();
    LinearLayout accountLL;
    LinearLayout quickLinkLL;
    LinearLayout menuLL;
    TextView actionbarTitleTV;
    ImageView accountIV;
    ImageView quickLinkIV;
    ImageView menuIV;
    TextView accountTitle;
    TextView quickLinkTitle;
    TextView menuTitle;
    LinearLayout tabsLinearLayout;

    BroadcastReceiver broadcastReceiver;
    ActionBar actionBar;
    //public LoyaltyCardReader mLoyaltyCardReader;
    static boolean isInSubMenu;
    static String firstExtraFragmentName;
    static String secondExtraFragmentName;
    static boolean receivedCustCreationIntent;
    static int fragmentSelection;

    private int VIEW_PAGER_HEIGHT = 180;
    private Customer newCustomer;
    private int variableNumberOfFragments;
    private int staticNumberOfFragment;
    private boolean activityHasBeenDestroyed;

    private TextView CustomerRegistrationResultTV;
    private ProgressBar CustomerRegistrationProgressBar;
    private Button CustomerRegistrationRegisterButton;
    public static String currentTransactionId="";
    public static boolean isDevice=false;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        if(useReader()) {
            unregisterReceiver(mHeadsetPlugReceiver);
            Log.d(this.getClass().getSimpleName(), "Stopping reader");
            stopReader();
        }
    }

    private static final String SAMPLE_LOYALTY_CARD_AID = "F222222222";
    // ISO-DEP command HEADER for selecting an AID.
    // Format: [Class | Instruction | Parameter 1 | Parameter 2]
    private static final String SELECT_APDU_HEADER = "00A40400";
    // "OK" status word sent in response to SELECT AID command (0x9000)
    private static final byte[] SELECT_OK_SW = {(byte) 0x90, (byte) 0x00};
    public static byte[] BuildSelectApdu(String aid) {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return HexStringToByteArray(SELECT_APDU_HEADER + String.format("%02X", aid.length() / 2) + aid);
    }
    public static byte[] HexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    private String readHCE(final Tag tag){
        String terminalId="";
            Log.d(TAG, "readHCE() is called");
            Log.d(TAG, "readHCE() is called Lewis "+tag);
            try {
                Log.d(TAG, "readHCE() IsoDep  ");
                IsoDep isoDep = IsoDep.get(tag);
                Log.d(TAG, "readHCE() IsoDep  "+isoDep);
                if (isoDep != null) {
                    try {
                        // Connect to the remote NFC device
                        isoDep.connect();
                        // Build SELECT AID command for our loyalty card service.
                        // This command tells the remote device which service we wish to communicate with.
                        Log.i(TAG, "readHCE Requesting remote AID: " + SAMPLE_LOYALTY_CARD_AID);
                        byte[] command = BuildSelectApdu(SAMPLE_LOYALTY_CARD_AID);
                        // Send command to remote device
                        Log.i(TAG, "readHCE Sending: " + ByteArrayToHexString(command));
                        byte[] result = isoDep.transceive(command);
                        // If AID is successfully selected, 0x9000 is returned as the status word (last 2
                        // bytes of the result) by convention. Everything before the status word is
                        // optional payload, which is used here to hold the account number.
                        int resultLength = result.length;
                        byte[] statusWord = {result[resultLength-2], result[resultLength-1]};
                        byte[] payload = Arrays.copyOf(result, resultLength - 2);
                        if (Arrays.equals(SELECT_OK_SW, statusWord)) {
                            // The remote NFC device will immediately respond with its stored account number
                            String accountNumber = new String(payload, "UTF-8");
                            Log.i(TAG, "readHCE Received: Response String" + accountNumber);

                            if (accountNumber.contains("|")) {
                                String data[] = accountNumber.split("\\|");
                                if (data.length > 0) {
                                    Log.i(TAG, "readHCE Received: Tag Id " + data[0]);
                                    Log.i(TAG, "readHCE Received: Tag Id " + data[0]);
                                    terminalId = data[0].toUpperCase(Locale.US);
                                    if(data.length>1){
                                        currentTransactionId = data[1];
                                        isDevice =true;
                                    }

                                }
                            }

                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error communicating with card: " + e.toString());
                    }
                }
            }catch(Exception e){
                Log.e(TAG,"trying to read a phone error "+e.toString());
            }
        return terminalId;
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(ApplicationActivity.class.getSimpleName(), "onNewIntent() is called");
        if(NFCForegroundUtil.isNFCTaggingPresent()) {
            if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED) || intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if (tag != null) {
                    String hceTagId = readHCE(tag);
                    Log.d(TAG, "tag.getClass() hceTagId " + hceTagId);
                    if (hceTagId.equalsIgnoreCase("")) {
                        Log.d(TAG, "tag.getClass().getSimpleName(): " + tag.getClass().getSimpleName());
                        String tagId = ConversionUtil.bytesToHexString(tag.getId());
                        Log.d(TAG, "tag.getId() id source" + tag.getId());
                        Log.d(TAG, "tag.getId() String " + tagId);
                        Log.d(TAG, "tag.getId() getTechList " + tag.getTechList());
                        Log.d(TAG, "tag.getId() toString " + tag.toString());
                        Log.d(TAG, "tag.getId()describeContents " + tag.describeContents());
                        finishedA2ACommunication(tagId);
                    } else {
                        finishedA2ACommunication(hceTagId);
                    }
                }
            } else {
                int buildVersion = Build.VERSION.SDK_INT;
                Log.d(TAG, "onNewIntent() is called in ApplicationActivity");
                Log.d(TAG, "buildVersion is:" + buildVersion);
                if (buildVersion <= 18) {
                    Log.d(TAG, "will get an instance if the Tag now and get its nfcId");
                    // This will support NFC scan read for devices with SDK build less than 18
                    if (intent != null) {
                        Tag intentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                        if (intentTag != null) {
                            String nfcId = AndroidToAndroidNFCActivityLowerVersions.onTagDiscovered(intentTag, this).toUpperCase(Locale.US);
                            Log.d(TAG, "nfcId: " + nfcId);
                            Log.d(TAG, "calling finishedA2ACommunication()");
                            finishedA2ACommunication(nfcId);
                        }
                    }
                }
            }
        }
    }

    // This will support NFC scan read for devices with SDK build less than 18 - 18 and above will be supported in NFCFragment
    @Override
    public void finishedA2ACommunication(String scannedId) {
        Log.d(TAG,"ApplicationActivity finishedA2ACommunication() is called");


        Log.d(TAG,"finishedA2ACommunication() in ApplicationActivity is called.");
        scannedId = scannedId.toUpperCase(Locale.US);
        Log.d(TAG,"scannedId: "+scannedId);
        Log.d(TAG, "Sending NFC_SCANNED intent now");

        Intent intent = new Intent(INTENT.NFC_SCANNED.toString());
        intent.putExtra(INTENT.EXTRA_NFC_ID.toString(), scannedId);
        Log.d(TAG, "sending " + INTENT.EXTRA_NFC_ID.toString() + " intent now...");
        playTap();
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        newCustomer = new Customer();
        startReader();
        if(hasNFCFeature()) {
          //  mLoyaltyCardReader = new LoyaltyCardReader(this);
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
          //  nfcAdapter.enableReaderMode(this, mLoyaltyCardReader, READER_FLAGS, null);
        }
    }

    private void setTabTextSize(TextView accountTitle, TextView quickPayTitle, TextView otherTitle) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int textSize = 0;
        if(metrics.heightPixels > 600)
            textSize = 17;
        else
            textSize = 14;
        accountTitle.setTextSize(textSize);
        quickPayTitle.setTextSize(textSize);
        otherTitle.setTextSize(textSize);
    }

    private void setTabSize() {
        LinearLayout.LayoutParams imageParams = (LinearLayout.LayoutParams)accountIV.getLayoutParams();//new LinearLayout.LayoutParams(250,20);
        if(Constants.getScreenHeight(this) > 600)
            imageParams.height = 170;
        else
            imageParams.height = 20;
        imageParams.topMargin = 10;
        imageParams.gravity = Gravity.CENTER_HORIZONTAL;
        accountIV.setLayoutParams(imageParams);
        quickLinkIV.setLayoutParams(imageParams);
        menuIV.setLayoutParams(imageParams);
    }
    private static boolean headSetPluggedIn = false;
    private final BroadcastReceiver mHeadsetPlugReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(this.getClass().getSimpleName(),"Headset "+intent.getAction());
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {

                boolean plugged = (intent.getIntExtra("state", 0) == 1);
                headSetPluggedIn = plugged;
                Log.d(this.getClass().getSimpleName(), "Headset plugged" + plugged);
                /* Mute the audio output if the reader is unplugged. */

                //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager
                 //       .getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_VIBRATE);
                stopReader();
                if(plugged) {
                    displayReaderProgress(View.VISIBLE);
                }
                youReader = YoutapReader.getInstance(mActivity);
                youReader.setStatus(!plugged);
            }
        }
    };
    private static  RelativeLayout All_Fragment_Reader_Progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_application);
        Log.d(TAG, "Constants.mainSecurePreferences  " + Constants.mainSecurePreferences );
        if(Constants.mainSecurePreferences == null){
            Constants.mainSecurePreferences = new SecurePreferences(this);
        }
        Log.d(TAG, "Constants.mainSecurePreferences  " + Constants.mainSecurePreferences );
        Log.d(TAG, "getResources().getString(R.string.CONF_PAY_PIN_REQUIRED_KEY) " + getResources().getString(R.string.CONF_PAY_PIN_REQUIRED_KEY));
        Log.d(TAG, "contains key  " +Constants.mainSecurePreferences.contains(getResources().getString(R.string.CONF_PAY_PIN_REQUIRED_KEY)));
        if(Constants.mainSecurePreferences.contains(getResources().getString(R.string.CONF_PAY_PIN_REQUIRED_KEY))){
            Constants.USSD_PAY_PIN_REQUIRED = Constants.mainSecurePreferences.getBoolean(getResources().getString(R.string.CONF_PAY_PIN_REQUIRED_KEY),false);
        }else{
            Constants.mainSecurePreferences.edit().putBoolean(getResources().getString(R.string.CONF_PAY_PIN_REQUIRED_KEY),getResources().getBoolean(R.bool.PAY_PIN_REQUIRED)).commit();
        }
        if(Constants.mainSecurePreferences.contains(getResources().getString(R.string.CONF_AWS_TERMINAL_KEY))){
            String savedConfigurations= Constants.mainSecurePreferences.getString(getResources().getString(R.string.CONF_AWS_TERMINAL_KEY),"");
            ObjectMapper objectMapper = ObjectMapperUtil.getObjectMapper();
            try {
                configurationVariablesModel = objectMapper.readValue(savedConfigurations, TerminalConfigurationVariablesModel.class);
            } catch (Exception e) {
                configurationVariablesModel= new TerminalConfigurationVariablesModel();
            }
        }else{
            Constants.mainSecurePreferences.edit().putString(getResources().getString(R.string.CONF_AWS_TERMINAL_KEY), "").commit();
            configurationVariablesModel= new TerminalConfigurationVariablesModel();
        }

        Log.d(TAG, "Constants.mainSecurePreferences  " + Constants.mainSecurePreferences );
        Log.d(TAG, "Constants.mainSecurePreferences contains key  " + Constants.mainSecurePreferences.contains(getResources().getString(R.string.CONF_PAY_PIN_REQUIRED_KEY)));
        mActivity = this;
        ACCOUNT_BALANCE = (AccountBalance)getIntent().getSerializableExtra(INTENT.EXTRA_ACCOUNT_BALANCE.toString());
        All_Fragment_Reader_Progress = (RelativeLayout)findViewById(R.id.All_Fragment_Reader_Progress);
        actionBar = getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);


        LayoutInflater inflater = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.actionbar_view, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.TOP|Gravity.FILL_VERTICAL|Gravity.CLIP_VERTICAL;
        actionBar.setCustomView(customView, params);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM );

        ImageView homeIV = (ImageView)actionBar.getCustomView().findViewById(R.id.Actionbar_ImageView_Home);
        homeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });
        actionbarTitleTV = (TextView)actionBar.getCustomView().findViewById(R.id.Actionbar_TextView_Title);
        tabsLinearLayout = (LinearLayout)findViewById(R.id.Linear);
        accountLL = (LinearLayout) findViewById(R.id.Activity_Application_LinearLayout_Tab_Account);
        quickLinkLL = (LinearLayout) findViewById(R.id.Activity_Application_LinearLayout_Tab_QuickPay);
        menuLL = (LinearLayout) findViewById(R.id.Activity_Application_LinearLayout_Tab_OTHER);

        accountIV = (ImageView) findViewById(R.id.Activity_Application_ImageView_Tab_Account);
        quickLinkIV = (ImageView) findViewById(R.id.Activity_Application_ImageView_Tab_QuickLink);
        menuIV = (ImageView) findViewById(R.id.Activity_Application_ImageView_Tab_Other);
        accountTitle = (TextView) findViewById(R.id.Activity_Application_TextView_Tab_Account);
        quickLinkTitle = (TextView) findViewById(R.id.Activity_Application_TextView_Tab_QuickPay);
        menuTitle = (TextView) findViewById(R.id.Activity_Application_TextView_Tab_Other);
        staticNumberOfFragment = getNumberOfFragmentsAtLogin();
        variableNumberOfFragments = staticNumberOfFragment;
        setTabSize();
        if(!LoginResponseConstants.accountTabAvailable)
            accountLL.setVisibility(View.GONE);
        if(!LoginResponseConstants.quickLinkTabAvailable)
            quickLinkLL.setVisibility(View.GONE);
        if(!isMenuTabAvailable())
            menuLL.setVisibility(View.GONE);

        accountLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
                LocalBroadcastManager.getInstance(ApplicationActivity.this).sendBroadcast(new Intent(INTENT.APPLICATION_ACCOUNT_TAB_REFRESH_LIST.toString()));
                updateTransactionsBalance();
            }
        });
        quickLinkLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginResponseConstants.accountTabAvailable)
                    mViewPager.setCurrentItem(1);
                else
                    mViewPager.setCurrentItem(0);
/*                if(youReader !=null) {
                    youReader.reset();
                }*/
            }
        });
        menuLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFullTabs())
                    mViewPager.setCurrentItem(2);
                else if((LoginResponseConstants.accountTabAvailable && !LoginResponseConstants.quickLinkTabAvailable) ||
                        (!LoginResponseConstants.accountTabAvailable && LoginResponseConstants.quickLinkTabAvailable) )
                    mViewPager.setCurrentItem(1);
                else
                    mViewPager.setCurrentItem(0);
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mSectionsPagerAdapter = new CustomFragmentStatePagerAdapter(ApplicationActivity.this.getFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(getCurrentFocus()!=null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                if(position == 0 && LoginResponseConstants.accountTabAvailable) {
/*                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           // Log.d(ApplicationActivity.class.getSimpleName(), "sending UPDATE_ACCOUNTS_BALANCES intent now...");
                            //LocalBroadcastManager.getInstance(ApplicationActivity.this).sendBroadcast(new Intent(INTENT.UPDATE_ACCOUNTS_BALANCES.toString()));
                        }
                    },500);*/
                }
                if(getNumberOfFragmentsAtLogin() == 3)
                {
                    if(mViewPager.getCurrentItem() >= 2)
                        updatePageSelectionCircles(2);
                    else if(mViewPager.getCurrentItem() == 1)
                        updatePageSelectionCircles(1);
                    else if(mViewPager.getCurrentItem() == 0)
                        updatePageSelectionCircles(0);
                }
                else if(getNumberOfFragmentsAtLogin() == 2)
                {
                    if(mViewPager.getCurrentItem() >= 1)
                        updatePageSelectionCircles(1);
                    else if(mViewPager.getCurrentItem() == 0)
                        updatePageSelectionCircles(0);
                }
                else if(getNumberOfFragmentsAtLogin() == 1){
                    updatePageSelectionCircles(0);
                }
                updateTabsSelection(position);
            }
        });
        mViewPager.setCurrentItem(0);
        updateTabsSelection(0);

        if(LoginResponseConstants.accountTabAvailable)
            actionbarTitleTV.setText(getString(R.string.TAB_1));
        else if(LoginResponseConstants.quickLinkTabAvailable)
            actionbarTitleTV.setText(getString(R.string.TAB_2));
        else
            actionbarTitleTV.setText(getString(R.string.TAB_3));

        //Set reader()
        mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);

        if(useReader()) {
            Log.d(mActivity.getClass().getSimpleName(), " user reader ");
            if(!hasNFCFeature()) {
                if (RootUtil.isDeviceRooted()) {
                    Toast.makeText(mActivity, "Device seems to be rooted, reader may not work as expected.", Toast.LENGTH_LONG).show();
                }
            }
            youReader = YoutapReader.getInstance(mActivity);
        }
        setNfcAdapter();
        pendingIntent= PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        Log.d(TAG, " starting loading ");
        Log.d(TAG," starting loading "+getResources().getBoolean(R.bool.LOAD_TRANSFERCODES_FROM_SERVER));
        if(getResources().getBoolean(R.bool.LOAD_TRANSFERCODES_FROM_SERVER)) {
            if (LoginResponseConstants.walletOptions.isBillPayments()) {
                BillPaymentCodesModel bpc = new BillPaymentCodesModel(this);
                bpc.getConnTaskManager().startBackgroundTask();
                UtilityCodes utc = new UtilityCodes(this);
                utc.getConnTaskManager().startBackgroundTask();
            }
            if (LoginResponseConstants.walletOptions.isMakePayment()) {
                //C2CW
                BanksCodeModel bcm = new BanksCodeModel(this);
                bcm.getConnTaskManager().startBackgroundTask();
            }
            if (LoginResponseConstants.walletOptions.isSendTopup()) {
                //C2CA
                TopupAirtimeDestinationCodes tadc = new TopupAirtimeDestinationCodes(this);
                tadc.getConnTaskManager().startBackgroundTask();
                //C2ET
                TopUpCreditDestinationCodes tcdc = new TopUpCreditDestinationCodes(this);
                tcdc.getConnTaskManager().startBackgroundTask();
            }
            if (LoginResponseConstants.walletOptions.isElectronicVouchersAvailable()) {
                //TODO Nothing is returned from server
                EVDCodes evd = new EVDCodes(this);
                evd.setOwner(  ApplicationActivity.loginClientId);
                evd.getConnTaskManager().startBackgroundTask();
            }
        }else{
            if (LoginResponseConstants.walletOptions.isMakePayment()) {
                //C2CW
                BanksCodeModel bcm = new BanksCodeModel(this);
                bcm.getConnTaskManager().startBackgroundTask();
            }
            if (LoginResponseConstants.walletOptions.isElectronicVouchersAvailable()) {
                //TODO Nothing is returned from server
                EVDCodes evd = new EVDCodes(this);
                evd.setOwner(  ApplicationActivity.loginClientId);
                evd.getConnTaskManager().startBackgroundTask();
            }
                              /* if (LoginResponseConstants.walletOptions.isBillPayments()) {
                    getBillPaymentCodes();
                    getUtilityCodes();
                }
                if (LoginResponseConstants.walletOptions.isSendTopup()) {
                    //C2CA
                    getTopupAirTimeDestinationCodes();
                    //C2ET
                    getTopupCreditDestinationCodes();
                }*/
        }
    }
    private PendingIntent pendingIntent;
    private NfcAdapter nfcAdapter;
    private void setNfcAdapter(){
        if(hasNFCFeature()){
            nfcAdapter = NfcAdapter.getDefaultAdapter(mActivity);
        }
    }
    private boolean hasNFCFeature(){
        boolean hasNfc = false;
        try{
            hasNfc =mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC);
        }catch (Exception e){

        }
        return hasNfc;
    }
    //private static AudioManager audioManager;
    public static void displayReaderProgress(int status){
        All_Fragment_Reader_Progress.setVisibility(status);
    }
    private static MediaPlayer mediaPlayer;
    public static void playTap(){
        //mReader.setMute(true);
        //mReader.piccPowerOff();
        //audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        // audioManager.setSpeakerphoneOn(true);
        try {
            mediaPlayer = MediaPlayer.create(mActivity, R.raw.chime_up);
            mediaPlayer.setVolume(1.0f, 1.0f);
            mediaPlayer.setAudioStreamType(AudioManager.MODE_IN_COMMUNICATION);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mp.release();

                    // mReader.start();//.reset(mResetCompleteListener);

                }
            });
            mediaPlayer.start();
        }catch (Exception e){}
    }

/*    public static void alertTap(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                playTap();
            }
        };
        new Timer().schedule(timerTask, 100);
    *//*    mReader.setMute(true);
        mReader.piccPowerOff();
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(true);
        mediaPlayer = MediaPlayer.create(mActivity, R.raw.chime_up);
        mediaPlayer.setVolume(1.0f,1.0f);
        mediaPlayer.setAudioStreamType(AudioManager.MODE_IN_COMMUNICATION);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                if(headSetPluggedIn) {
                    audioManager.setMode(AudioManager.MODE_NORMAL);
                    audioManager.setSpeakerphoneOn(false);
                    mReader.piccPowerOff();
                    mReader.stop();
                    mReader.start();
                    mReader.setMute(false);
                    mResetCompleteListener = new OnResetCompleteListener();
                    mReader.reset(mResetCompleteListener);
                }
                // mReader.start();//.reset(mResetCompleteListener);

            }
        });
        mediaPlayer.start();*//*
    }*/
/*    private void playReaderAlert(){
        mReader.setMute(true);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(true);
        mediaPlayer = MediaPlayer.create(mActivity, R.raw.chime_up);
        mediaPlayer.setVolume(1.0f,1.0f);
        mediaPlayer.setAudioStreamType(AudioManager.MODE_IN_COMMUNICATION);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(false);
                mReader.setMute(false);
                mResetCompleteListener = new OnResetCompleteListener();
                mReader.reset(mResetCompleteListener);
                //mReader.reset(mResetCompleteListener);
            }
        });
        mediaPlayer.start();
    }*/

    public static boolean both =false;
    public static boolean useReader(){
        boolean useReader;
        if("both".equalsIgnoreCase(mActivity.getString(R.string.ONBOARD_NFC))) {
            useReader = true;
            both =true;
        }else{
            useReader= !Boolean.parseBoolean(mActivity.getString(R.string.ONBOARD_NFC)) ;
        }
        return useReader;
    }


    //private static OnResetCompleteListener mResetCompleteListener;



/*
    private static boolean powerOn(){
        return mReader.piccPowerOn(piccTimeOut, mPiccCardType);
    }

    private void sendOffNFCEvent(){
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 80);
        toneG.startTone(ToneGenerator.TONE_PROP_BEEP2, 500); // 200 is duration in ms
        String scannedId = ConversionUtil.bytesToHexString(Arrays.copyOfRange(mPiccResponseApdu, 0, mPiccResponseApdu.length - 2));
        Intent intent = new Intent(INTENT.NFC_SCANNED.toString());
        intent.putExtra(INTENT.EXTRA_NFC_ID.toString(), scannedId.toUpperCase());
        Log.d(mActivity.getClass().getSimpleName(), "sendOffNFCEvent mPiccResponseApduReady " + scannedId);
        Log.d(mActivity.getClass().getSimpleName(), "sendOffNFCEvent " + INTENT.EXTRA_NFC_ID.toString() + " intent now...");
        LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);

    }
*/



    private AudioManager mAudioManager;
    public static Activity mActivity;




    private void startReader(){
        if(youReader != null){
            youReader.startReader();
        }
/*        if(mReader !=null){
            mReader.start();
        }*/
    }
    private void stopReader(){
        Log.d("TAG", "Stopping reader");
        if(youReader != null){
            youReader.stopReader();
        }
/*        if(mReader !=null){
            mReader.stop();
            Log.d("TAG", "reader stopped ");
            mReader = null;
        }*/
    }
    @Override
    protected void onStart() {
        super.onStart();
        startReader();
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Log.d(this.getClass().getSimpleName(),"ApplicationActivity called onPause reader is stopped ");
        // disabledPicc();
        // stopReader();
        //  Log.d(this.getClass().getSimpleName(),"ApplicationActivity onPause");
        if(hasNFCFeature()) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                nfcAdapter.disableReaderMode(this);
                nfcAdapter.disableForegroundDispatch(this);
            }else{
                nfcAdapter.disableForegroundDispatch(this);
            }
//            nfcAdapter.disableReaderMode(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(this.getClass().getSimpleName(), "ApplicationActivity called onStop reader is stopped onStop");
        //disabledPicc();
        stopReader();
        Log.d(this.getClass().getSimpleName(), "ApplicationActivity called onStop reader is stopped onStop");
    }
    public static void triggerPicc(){
    youReader.triggerPicc();

    }
    public static void disabledPicc(){
        youReader.disabledPicc();
    }
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(ApplicationActivity.this.getFragmentManager() == null)
                    return;
                final FragmentManager fragmentManager = ApplicationActivity.this.getFragmentManager();
                String action = intent.getAction();
                Log.d(ApplicationActivity.class.getSimpleName(), "received intent: " + intent.getAction());

                if(action.equalsIgnoreCase(INTENT.UPDATE_ACTION_BAR_TITLE.toString())){
                    if(actionbarTitleTV == null)
                        return;
                    actionbarTitleTV.setText(intent.getStringExtra(INTENT.EXTRA_TITLE.toString()));
                }
                else if(action.equalsIgnoreCase(INTENT.DECREASE_VIEW_PAGER_HEIGHT.toString())){
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    if(metrics.heightPixels > 500)
                        VIEW_PAGER_HEIGHT = 290;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, convertDpToPixel(VIEW_PAGER_HEIGHT, context));
                    mViewPager.setLayoutParams(params);
                }
                else if(action.equalsIgnoreCase(INTENT.RESET_FRAGMENTS.toString()) && !activityHasBeenDestroyed){
                    tabsLinearLayout.setVisibility(View.VISIBLE);
                    resetViewPagerSize();
                    destroyCustomerRegistrationComponents();
                    receivedCustCreationIntent = false;
                    ApplicationActivity.hideCustomerRegistrationControl(ApplicationActivity.this);
                    if(variableNumberOfFragments <= staticNumberOfFragment)
                        return;
                    final int position = intent.getIntExtra(INTENT.EXTRA_POS.toString(), staticNumberOfFragment-1);
                    variableNumberOfFragments = staticNumberOfFragment;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSectionsPagerAdapter = new CustomFragmentStatePagerAdapter(fragmentManager);
                            mViewPager.setAdapter(mSectionsPagerAdapter);
                            mViewPager.setCurrentItem(position);
                        }
                    }, 100);
                }
                else if(action.equalsIgnoreCase(INTENT.NEW_FRAGMENT.toString())){

                    tabsLinearLayout.setVisibility(View.GONE);
                    variableNumberOfFragments = getNumberOfFragmentsAtLogin()+1;//intent.getIntExtra("NUM", staticNumberOfFragment);
                    firstExtraFragmentName = intent.getStringExtra(INTENT.EXTRA_FRAG_NAME.toString());
                    fragmentSelection = variableNumberOfFragments;
                    Log.d(ApplicationActivity.class.getSimpleName(), "variableNumberOfFragments: "+ variableNumberOfFragments);
                    Log.d(ApplicationActivity.class.getSimpleName(), "fragmentSelection: "+fragmentSelection);
                    Log.d(ApplicationActivity.class.getSimpleName(), "firstExtraFragmentName : "+firstExtraFragmentName);

                    mSectionsPagerAdapter = new CustomFragmentStatePagerAdapter(fragmentManager);
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                    mViewPager.setCurrentItem(fragmentSelection, true);

                    return;

                }
                else if(action.equalsIgnoreCase(INTENT.NEW_NEW_FRAGMENT.toString())){
                    variableNumberOfFragments = getNumberOfFragmentsAtLogin()+2;//intent.getIntExtra("NUM", staticNumberOfFragment+1);
                    secondExtraFragmentName = intent.getStringExtra(INTENT.EXTRA_FRAG_NAME.toString());
                    fragmentSelection = variableNumberOfFragments;
                    mSectionsPagerAdapter = new CustomFragmentStatePagerAdapter(fragmentManager);
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                    mViewPager.setCurrentItem(fragmentSelection, true);
                }


                else if(action.equalsIgnoreCase(INTENT.CUSTOMER_CREATION_SELECTION.toString())){
                    Log.d(ApplicationActivity.class.getSimpleName(), "received intent to create Customer..isQuickLinkTabAvailable ? "+isQuickLinkTabAvailable());
                    if(isQuickLinkTabAvailable()) {
                        variableNumberOfFragments = 6;
                        fragmentSelection = 3;
                    }
                    else {
                        variableNumberOfFragments = 5;
                        fragmentSelection = 2;
                    }
                    Log.d(ApplicationActivity.class.getSimpleName(), "received intent to create Customer..variableNumberOfFragments: "+ variableNumberOfFragments);
                    Log.d(ApplicationActivity.class.getSimpleName(), "received intent to create Customer..fragmentSelection: "+fragmentSelection);
                    receivedCustCreationIntent = true;
                    mSectionsPagerAdapter = new CustomFragmentStatePagerAdapter(fragmentManager);
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                    Log.d(ApplicationActivity.class.getSimpleName(), "received intent to create Customer..setting current Item in mViewPager to: "+fragmentSelection);
                    Log.d(ApplicationActivity.class.getSimpleName(), "-----DONE-----");
                    mViewPager.setCurrentItem(fragmentSelection, true);
                    setUpCustomerRegistrationComponents();
                }

                else if(intent.getAction().equalsIgnoreCase(INTENT.CUSTOMER_REGISTRATION.toString())){
                    if(CustomerRegistrationResultTV == null) // This is to distinguish between CustomerRegistration message coming from QL and Menu Profile options
                        return;
                    String serverResponse = intent.getStringExtra(INTENT.EXTRA_SERVER_RESPONSE.toString());
                    if(validateCustomerRegistrationResponse(serverResponse)){
                        CustomerRegistrationResultTV.setText("Customer "+getNewCustomer().getMSISDN()+" registered successfully.");
                    }
                    else{
                        if(serverResponse != null)
                            CustomerRegistrationResultTV.setText(getMessage(serverResponse));
                    }
                    CustomerRegistrationProgressBar.setVisibility(View.GONE);
                    CustomerRegistrationResultTV.setVisibility(View.VISIBLE);

                }
                else if (intent.getAction().equalsIgnoreCase(INTENT.CUSTOMER_REGISTRATION_FIRST_CIRCLE.toString()))
                    updatePageSelectionCircles(0);
                else if (intent.getAction().equalsIgnoreCase(INTENT.CUSTOMER_REGISTRATION_SECOND_CIRCLE.toString()))
                    updatePageSelectionCircles(1);
                else if (intent.getAction().equalsIgnoreCase(INTENT.CUSTOMER_REGISTRATION_THIRD_CIRCLE.toString()))
                    updatePageSelectionCircles(2);
                else if(action.equalsIgnoreCase(INTENT.UPDATE_BALANCE.toString()) && !activityHasBeenDestroyed && LoginResponseConstants.accountTabAvailable){
                    Log.d(ApplicationActivity.class.getSimpleName(), "received UPDATE_BALANCE intent..");
                    Log.d(ApplicationActivity.class.getSimpleName(), "starting two background processes to update Lasansactions and Balance...");
                    updateTransactionsBalance();
                }
                else if(action.equalsIgnoreCase(INTENT.INTERNET_WIFI_SIGNAL_WEAK.toString())){
                    Toast.makeText(context, "Wifi signal is weak", Toast.LENGTH_SHORT).show();
                }
                else if(action.equalsIgnoreCase(INTENT.INTERNET_GSM_SIGNAL.toString())){
                    Toast.makeText(context, "Mobile data connection is weak", Toast.LENGTH_SHORT).show();
                }
                else if(action.equalsIgnoreCase(INTENT.INTERNET_NO_SIGNAL.toString())){
                    Toast.makeText(context, "Internet connection lost", Toast.LENGTH_SHORT).show();
                }
                else if(action.equalsIgnoreCase(INTENT.INTERNET_REGAINED.toString())){
                    Toast.makeText(context, intent.getStringExtra(INTENT.EXTRA_INTERNET.toString()), Toast.LENGTH_SHORT).show();
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.NEW_FRAGMENT.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.NEW_NEW_FRAGMENT.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.RESET_FRAGMENTS.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.UPDATE_BALANCE.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.INTERNET_WIFI_SIGNAL_WEAK.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.INTERNET_GSM_SIGNAL.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.INTERNET_NO_SIGNAL.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.INTERNET_REGAINED.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.UPDATE_ACTION_BAR_TITLE.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter((INTENT.CUSTOMER_CREATION_SELECTION.toString())));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter((INTENT.DECREASE_VIEW_PAGER_HEIGHT.toString())));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter((INTENT.CUSTOMER_REGISTRATION.toString())));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter((INTENT.CUSTOMER_REGISTRATION_FIRST_CIRCLE.toString())));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter((INTENT.CUSTOMER_REGISTRATION_SECOND_CIRCLE.toString())));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter((INTENT.CUSTOMER_REGISTRATION_THIRD_CIRCLE.toString())));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(INTENT.REFRESH_ACCOUNT.toString()));
        if(useReader()) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_HEADSET_PLUG);
            registerReceiver(mHeadsetPlugReceiver, filter);
        }
    }
    private void updateTransactionsBalance(){
        LastTransactions lastTransactions = new LastTransactions(ApplicationActivity.this);
        lastTransactions.getConnTaskManager().startBackgroundTask();
        LoginBalanceInternal loginBalanceInternal = new LoginBalanceInternal(ApplicationActivity.this);
        loginBalanceInternal.getConnTaskManager().startBackgroundTask();
    }
    private TextView firstCircle ;
    private TextView secondCircle ;
    private TextView thirdCircle ;

    private void setUpCustomerRegistrationComponents(){
        firstCircle = (TextView) findViewById(R.id.Activity_Application_FirstPage);
        secondCircle = (TextView) findViewById(R.id.Activity_Application_SecondPage);
        thirdCircle = (TextView) findViewById(R.id.Activity_Application_ThirdPage);
        CustomerRegistrationRegisterButton = (Button)findViewById(R.id.Activity_Application_Button_Register);
        CustomerRegistrationProgressBar = (ProgressBar) findViewById(R.id.Activity_Application_ProgressBar);
        CustomerRegistrationResultTV = (TextView) findViewById(R.id.Activity_Application_TextView_ResultTV);
        CustomerRegistrationRegisterButton.setOnClickListener(this);
        firstCircle.setOnClickListener(this);
        secondCircle.setOnClickListener(this);
        thirdCircle.setOnClickListener(this);
        if(LoginResponseConstants.idType == null || LoginResponseConstants.idType.isEmpty())
            thirdCircle.setVisibility(View.GONE);
        updatePageSelectionCircles(0);
    }

    private void updatePageSelectionCircles(int index){
        if(firstCircle == null)
            return;
        firstCircle.setBackgroundResource(R.drawable.cus_reg_cell_shape);
        secondCircle.setBackgroundResource(R.drawable.cus_reg_cell_shape);
        thirdCircle.setBackgroundResource(R.drawable.cus_reg_cell_shape);
        if(index == 0)
            firstCircle.setBackgroundResource(R.drawable.cus_reg_cell_shape_fill);
        else if(index == 1)
            secondCircle.setBackgroundResource(R.drawable.cus_reg_cell_shape_fill);
        else if(index == 2)
            thirdCircle.setBackgroundResource(R.drawable.cus_reg_cell_shape_fill);

    }

    @Override
    public void onClick(View v) {
        int fragmentIndex = 0;
        if(isQuickLinkTabAvailable())
            fragmentIndex = 3;
        else
            fragmentIndex = 2;
        switch (v.getId()) {
            case R.id.Activity_Application_FirstPage:
                updatePageSelectionCircles(0);
                mViewPager.setCurrentItem(fragmentIndex);
                break;
            case R.id.Activity_Application_SecondPage:
                updatePageSelectionCircles(1);
                fragmentIndex++;
                mViewPager.setCurrentItem(fragmentIndex);
                break;
            case R.id.Activity_Application_ThirdPage:
                updatePageSelectionCircles(2);
                fragmentIndex = fragmentIndex+2;
                mViewPager.setCurrentItem(fragmentIndex);
                break;
            case R.id.Activity_Application_Button_Register:
                int customerCreationFirstFragmentIndex = 0;
                if(isQuickLinkTabAvailable())
                    customerCreationFirstFragmentIndex = 3;
                else
                    customerCreationFirstFragmentIndex = 2;
                if(!validateCustomerRegistrationRequest().equalsIgnoreCase("OK"))
                {
                    if(mViewPager.getCurrentItem() != customerCreationFirstFragmentIndex)
                        mViewPager.setCurrentItem(customerCreationFirstFragmentIndex);
//                    Toast.makeText(v.getContext(), validateCustomerRegistrationRequest(), Toast.LENGTH_SHORT).show();
                    return;
                }
                CustomerRegistrationResultTV.setVisibility(View.GONE);
                CustomerRegistrationProgressBar.setVisibility(View.VISIBLE);
                CustomerRegistration customerRegistration = new CustomerRegistration(ApplicationActivity.this);
                customerRegistration.setCustomer(getNewCustomer());
                customerRegistration.getConnTaskManager().startBackgroundTask();
                break;
        }
    }

    private void destroyCustomerRegistrationComponents(){
        firstCircle = null;
        secondCircle = null;
        thirdCircle = null;
        CustomerRegistrationRegisterButton = null;
        CustomerRegistrationProgressBar = null;
        CustomerRegistrationResultTV = null;
    }

    private void resetViewPagerSize() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mViewPager.setLayoutParams(params);
        mViewPager.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login_actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.Item_logout)
        {
            activityHasBeenDestroyed = true;
            this.finish();
        }
        if(item.getItemId() == R.id.Item_About)
        {
            Dialog aboutDialog = new Dialog(this);
            aboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            aboutDialog.setContentView(R.layout.about_dialog);
            LinearLayout aboutLinearLayout = (LinearLayout)aboutDialog.findViewById(R.id.Dialog_About_TextView_Content_LinearLayout);
            PackageManager manager = this.getPackageManager();
            PackageInfo info = null;
            TextView appVersionView = ((TextView)aboutDialog.findViewById(R.id.Dialog_About_TextView_Content));
            if(info != null){
                appVersionView.setText("APP Version "+info.versionName);
            }else{
                appVersionView.setText("APP Version "+getString(R.string.APP_VERSION_INTERNAL));
            }
            if(getString(R.string.app_name).toUpperCase(Locale.US).contains("ASSOTEL"))
            {
                appVersionView.setTextColor(this.getResources().getColor(R.color.LOGIN_TEXT_COLOR));
            }
            aboutLinearLayout.setOnClickListener(new View.OnClickListener() {
                int taps =0;
                @Override
                public void onClick(View v) {
                    taps++;
                    if(taps>5){
                        String details = PlaceholderFragment.context.getString(R.string.SERVER_IP);
                        details+= "\n "+ PlaceholderFragment.context.getString(R.string.SERVER_PORT);
                        //Toast.makeText(PlaceholderFragment.context,details, Toast.LENGTH_LONG).show();
                        taps=0;
                        Intent changeConfiguration = new Intent(ApplicationActivity.this, ConfigurationActivity.class);
                        startActivityForResult(changeConfiguration,CHANGE_CONFIGURATION);
                    }
                }
            });
            aboutDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ApplicationActivity.CHANGE_CONFIGURATION) {
            if(Build.VERSION.SDK_INT>=15) {
                try {
                    menuLL.callOnClick();
                }catch(Exception e){}
            }
        }
    }
    public static final int CHANGE_CONFIGURATION = 1;
    private void updateTabsSelection(int position){

        TextView accountTitle = (TextView) findViewById(R.id.Activity_Application_TextView_Tab_Account);
        TextView quickLinkTitle = (TextView) findViewById(R.id.Activity_Application_TextView_Tab_QuickPay);
        TextView menuTitle = (TextView) findViewById(R.id.Activity_Application_TextView_Tab_Other);
        accountTitle.setTextColor(getResources().getColor(R.color.TAB_PRESSED));
        quickLinkTitle.setTextColor(getResources().getColor(R.color.TAB_PRESSED));
        menuTitle.setTextColor(getResources().getColor(R.color.TAB_PRESSED));
        setTabTextSize(accountTitle, quickLinkTitle, menuTitle);

        if(position == 0) {
            if (LoginResponseConstants.accountTabAvailable)
                updateUIComponentsWithFocus(accountLL, accountTitle, accountIV, position);
            else if (LoginResponseConstants.quickLinkTabAvailable)
                updateUIComponentsWithFocus(quickLinkLL, quickLinkTitle, quickLinkIV, position);
            else
                updateUIComponentsWithFocus(menuLL, menuTitle, menuIV, position);
        }
        else if(position == 1) {
            // in case Account Tab is Available and QuickLink Tab is Available
            if (LoginResponseConstants.accountTabAvailable && LoginResponseConstants.quickLinkTabAvailable)
                updateUIComponentsWithFocus(quickLinkLL, quickLinkTitle, quickLinkIV, position);
            else
                updateUIComponentsWithFocus(menuLL, menuTitle, menuIV, position);
        }
        else {
            // in case Account Tab is Available and QuickLink is Available
            // in case Account Tab is NOT Available and QuickLink is Available
            // in case Account Tab is Available and QuickLink is NOT Available
            // in case Account Tab is NOT Available and QuickLink is NOT Available
            updateUIComponentsWithFocus(menuLL, menuTitle, menuIV, position);
        }
    }


    private void updateUIComponentsWithFocus(LinearLayout pressedLayout, TextView pressedTextView, ImageView pressedImageView, int position){
        exists = false;
        accountLL.setBackgroundResource(R.drawable.tab_normal);
        quickLinkLL.setBackgroundResource(R.drawable.tab_normal);
        menuLL.setBackgroundResource(R.drawable.tab_normal);
        //accountIV.setImageResource(R.drawable.account_pressed);
        //quickLinkIV.setImageResource(R.drawable.quicklink_pressed);
        //menuIV.setImageResource(R.drawable.menu_pressed);
        actionbarTitleTV.setTextColor(getResources().getColor(R.color.ACTIONBAR_TITLE_TEXT_COLOR));

        switch (pressedLayout.getId()){
            case R.id.Activity_Application_LinearLayout_Tab_Account:
                actionbarTitleTV.setText(getString(R.string.TAB_1));
                pressedImageView.setImageResource(R.drawable.account_pressed);

                quickLinkLL.setBackgroundResource(R.drawable.tab_normal);
                menuLL.setBackgroundResource(R.drawable.tab_normal);

                quickLinkIV.setImageResource(R.drawable.quicklink_normal);
                menuIV.setImageResource(R.drawable.menu_normal);

                accountTitle.setTextColor(getResources().getColor(R.color.TAB_PRESSED));
                quickLinkTitle.setTextColor(getResources().getColor(R.color.TAB_NORMAL));
                menuTitle.setTextColor(getResources().getColor(R.color.TAB_NORMAL));
                break;
            case R.id.Activity_Application_LinearLayout_Tab_QuickPay:
                actionbarTitleTV.setText(getString(R.string.TAB_2));
                pressedImageView.setImageResource(R.drawable.quicklink_pressed);

                accountLL.setBackgroundResource(R.drawable.tab_normal);
                menuLL.setBackgroundResource(R.drawable.tab_normal);

                accountIV.setImageResource(R.drawable.account_normal);
                menuIV.setImageResource(R.drawable.menu_normal);

                quickLinkTitle.setTextColor(getResources().getColor(R.color.TAB_PRESSED));
                accountTitle.setTextColor(getResources().getColor(R.color.TAB_NORMAL));
                menuTitle.setTextColor(getResources().getColor(R.color.TAB_NORMAL));
                break;
            case R.id.Activity_Application_LinearLayout_Tab_OTHER:
                actionbarTitleTV.setText(getString(R.string.TAB_3));
                pressedImageView.setImageResource(R.drawable.menu_pressed);

                accountLL.setBackgroundResource(R.drawable.tab_normal);
                quickLinkLL.setBackgroundResource(R.drawable.tab_normal);

                accountIV.setImageResource(R.drawable.account_normal);
                quickLinkIV.setImageResource(R.drawable.quicklink_normal);

                menuTitle.setTextColor(getResources().getColor(R.color.TAB_PRESSED));
                accountTitle.setTextColor(getResources().getColor(R.color.TAB_NORMAL));
                quickLinkTitle.setTextColor(getResources().getColor(R.color.TAB_NORMAL));
                break;

        }
        pressedLayout.setBackgroundResource(R.drawable.tab_pressed);
        pressedTextView.setTextColor(getResources().getColor(R.color.TAB_PRESSED));
        if(mViewPager.getCurrentItem() < getNumberOfFragmentsAtLogin())
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(INTENT.RESET_FRAGMENTS.toString()).putExtra(INTENT.EXTRA_POS.toString(), position));
    }

    private boolean isFullTabs(){
        if(LoginResponseConstants.accountTabAvailable && LoginResponseConstants.quickLinkTabAvailable && isMenuTabAvailable())
            return true;
        else
            return false;
    }

  /*  @Override
    public void onAccountReceived(final String hceTagId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"onAccountReceived hceTagId"+hceTagId);
                playTap();
                Log.d(TAG, "onAccountReceived hceTagId" + hceTagId);
                if (hceTagId.contains("|")) {
                    String data[] = hceTagId.split("\\|");
                }

                Log.d(TAG,"onAccountReceived hceTagId"+hceTagId);
               }
        });
    }*/

    public class CustomFragmentStatePagerAdapter extends FragmentStatePagerAdapter{

        public CustomFragmentStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(getApplicationContext(), position);
        }

        @Override
        public int getItemPosition(Object object) {
            return fragmentSelection;
        }

        @Override
        public int getCount() {
            return variableNumberOfFragments;
        }
    }
    boolean exists =false;
    @Override
    public void onBackPressed() {
        if(isInSubMenu) {
            isInSubMenu = false;
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(INTENT.RESET_FRAGMENTS.toString()).putExtra(INTENT.EXTRA_POS.toString(), staticNumberOfFragment - 1));
        }
        else {
            if(exists) {
                this.finish();
            }else{
                Toast.makeText(mActivity, "Press again to exit.", Toast.LENGTH_LONG).show();
                exists = true;
            }

        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment{
        private static Context context;
        private static final String TAG = PlaceholderFragment.class.getSimpleName();

        public static PlaceholderFragment newInstance( Context context, int sectionNumber) {
            PlaceholderFragment.context = context;
            Fragment fragment = null;
            Log.d(ApplicationActivity.class.getSimpleName(),"newInstance() is called isQuickLinkTabAvailable()? "+isQuickLinkTabAvailable());
            Log.d(ApplicationActivity.class.getSimpleName(),"newInstance() is called receivedCustCreationIntent? "+receivedCustCreationIntent);
            Log.d(ApplicationActivity.class.getSimpleName(), "newInstance() is called Constants.quickLinks.size(): " + Constants.quickLinks.size());
            Log.d(ApplicationActivity.class.getSimpleName(),"newInstance() is called sectionNumber "+sectionNumber);
            switch (sectionNumber){
                case 0:
                    if(LoginResponseConstants.accountTabAvailable)
                        fragment = new AccountFragment();
                    else if(LoginResponseConstants.quickLinkTabAvailable)
                        fragment = new QuickLinkFragment();
                    else
                        fragment = new MenuFragment();
                    break;
                case 1:
                    if(LoginResponseConstants.accountTabAvailable && LoginResponseConstants.quickLinkTabAvailable)
                        fragment = new QuickLinkFragment();
                    else if(!LoginResponseConstants.accountTabAvailable && LoginResponseConstants.quickLinkTabAvailable
                            || (LoginResponseConstants.accountTabAvailable && !LoginResponseConstants.quickLinkTabAvailable))
                        fragment = new MenuFragment();
                    else
                    {
                        if(receivedCustCreationIntent)
                            fragment = new CustomerCreationFirstFragment();
                        else
                            fragment = getFragment(context, fragment);
                    }
                    break;
                case 2:
                    if(receivedCustCreationIntent && !LoginResponseConstants.quickLinkTabAvailable)
                        fragment = new CustomerCreationFirstFragment();
                    else if(isMenuTabAvailable() && LoginResponseConstants.accountTabAvailable && LoginResponseConstants.quickLinkTabAvailable)
                        fragment = new MenuFragment();
                    else if(!LoginResponseConstants.accountTabAvailable && !LoginResponseConstants.quickLinkTabAvailable)
                        fragment = getFragmentSecondLevel(context, fragment);
                    else
                        fragment = getFragment(context, fragment);
                    break;
                case 3:
                    if(receivedCustCreationIntent && LoginResponseConstants.quickLinkTabAvailable)
                        fragment = new CustomerCreationFirstFragment();
                    else if(receivedCustCreationIntent && !LoginResponseConstants.quickLinkTabAvailable)
                        fragment = new CustomerCreationSecondFragment();
                    else if(LoginResponseConstants.quickLinkTabAvailable)
                        fragment = getFragment(context, fragment);
                    else
                        fragment = getFragmentSecondLevel(context, fragment);
                    break;
                case 4:
                    if(receivedCustCreationIntent && LoginResponseConstants.quickLinkTabAvailable)
                        fragment = new CustomerCreationSecondFragment();
                    else if(receivedCustCreationIntent && !isQuickLinkTabAvailable())
                        fragment = new CustomerCreationThirdFragment();
                    else
                        fragment = getFragmentSecondLevel(context, fragment);
                    break;
                case 5:
                    fragment = new CustomerCreationThirdFragment();
                    break;
            }
            Log.d(ApplicationActivity.class.getSimpleName(),"returned Fragment is: "+fragment.getClass().getSimpleName());
            Log.d(ApplicationActivity.class.getSimpleName(),"++++++++++++++++++++++++++++++++");

            return (PlaceholderFragment) fragment;
        }

        private static Fragment getFragmentSecondLevel(Context context, Fragment fragment) {
            if(secondExtraFragmentName.equalsIgnoreCase(context.getString(R.string.BillPayment_Cable_TV)))
                fragment = new CableTVFragment();
            else if(secondExtraFragmentName.equalsIgnoreCase(context.getString(R.string.BillPayment_Utilities)))
                fragment = new UtilitiesFragment();
            else if(secondExtraFragmentName.equalsIgnoreCase(context.getString(R.string.app_name)))
                fragment = new SendMoneyFragment();
            else if(secondExtraFragmentName.equalsIgnoreCase(context.getString(R.string.BANKS)))
                fragment = new BankSendMoneyFragment();
            else if(secondExtraFragmentName.equalsIgnoreCase(context.getString(R.string.OTHER_OPERATORS)))
                fragment = new OtherOperatorsSendMoneyFragment();
            LocalBroadcastManager.getInstance(context).sendBroadcast(
                    new Intent(INTENT.UPDATE_ACTION_BAR_TITLE.toString()).putExtra(INTENT.EXTRA_TITLE.toString(),secondExtraFragmentName));
            return fragment;
        }

        private static Fragment getFragment(Context context, Fragment fragment) {
            isInSubMenu = true;
            if(firstExtraFragmentName.equalsIgnoreCase(context.getString(R.string.MAKE_PAYMENT))){
                if(TransferCode.bankCodes.size() == 0 && TransferCode.topupcreditCodes.size() == 0)
                    fragment = new SendMoneyFragment();
                else
                    fragment = new SendMoneyListFragment();

            }
            else if(firstExtraFragmentName.equalsIgnoreCase(context.getString(R.string.RECEIVE_PAYMENT)))
                fragment = new ReceiveMoneyFragment();
            else if(firstExtraFragmentName.equalsIgnoreCase(context.getString(R.string.GIVE_CASH_OUT)))
                fragment = new CashOutFragment();
            else if(firstExtraFragmentName.equalsIgnoreCase(context.getString(R.string.RECEIVE_CASH_IN)))
                fragment = new CashinFragment();
            else if(firstExtraFragmentName.equalsIgnoreCase(context.getString(R.string.RECEIVE_CASH_IN_CC)))
                fragment = new DepositFundsFromCreditCardFragment();
            else if(firstExtraFragmentName.equalsIgnoreCase(context.getString(R.string.REDEEM_VOUCHER)))
                fragment = new RedeemVoucherFragment();
            else if(firstExtraFragmentName.equalsIgnoreCase(context.getString(R.string.TOP_UP)))
                fragment = new TopUpFragment();
            else if(firstExtraFragmentName.equalsIgnoreCase(context.getString(R.string.BillPayments))){
                if(TransferCode.utilityCodes.size() != 0 && TransferCode.topupcreditCodes.size() != 0)
                    fragment = new BillPaymentsListFragment();
                else if(TransferCode.utilityCodes.size() == 0 && TransferCode.topupcreditCodes.size() == 0)
                    fragment = new CableTVFragment();
                else if(TransferCode.billPaymentCodes.size() == 0 && TransferCode.topupcreditCodes.size() == 0)
                    fragment = new UtilitiesFragment();
                else
                    fragment = new CableTVFragment();
 /*               fragment = new UtilitiesFragment();
                if(TransferCode.utilityCodes.size() != 0 && TransferCode.topupcreditCodes.size() != 0)
                    fragment = new BillPaymentsListFragment();
                else if(TransferCode.utilityCodes.size() == 0 && TransferCode.topupcreditCodes.size() == 0)
                    fragment = new CableTVFragment();
                else if(TransferCode.billPaymentCodes.size() == 0 && TransferCode.topupcreditCodes.size() == 0)
                    fragment = new UtilitiesFragment();*/
            }
            else if(firstExtraFragmentName.equalsIgnoreCase(context.getString(R.string.CHANGE_PIN)))
                fragment = new ChangePinFragment();
            else if(firstExtraFragmentName.equalsIgnoreCase(context.getString(R.string.TAG_REGISTRATION)))
                fragment = new TagRegistrationFragment();
            else if(firstExtraFragmentName.equalsIgnoreCase(context.getString(R.string.CONFIG_SERVER)))
                fragment = new ServerConfigurationFragment();
            else if(firstExtraFragmentName.equalsIgnoreCase(context.getString(R.string.SELECT_CURRENCY)))
                fragment = new DefaultCurrencyFragment();
            else if(firstExtraFragmentName.equalsIgnoreCase(context.getString(R.string.CUSTOMER_REGISTRATION))) {
               //fragment = new CustomerLookupFragment();
                fragment = new com.mobilis.android.nfc.fragments.CustomerRegistration();
            }else if(firstExtraFragmentName.equalsIgnoreCase(context.getString(R.string.CASH_OUT_VOUCHER)))
                fragment = new CashOutVoucherFragment();

            else if(firstExtraFragmentName.equalsIgnoreCase(context.getString(R.string.CASH_IN_VOUCHER))) {
                fragment = new ElectronicVoucherFragment();
                ElectronicVoucherFragment.isCashInOperation = true;
            }
            else if(firstExtraFragmentName.equalsIgnoreCase(context.getString(R.string.SELL_VOUCHER))) {
                fragment = new ElectronicVoucherFragment();
                ElectronicVoucherFragment.isCashInOperation = false;
            } else if (firstExtraFragmentName.equals(context.getString(R.string.BUY_VOUCHER))) {
                fragment = new BuyElectronicVoucherFragment();
                BuyElectronicVoucherFragment.isCashInOperation = false;
            } else if (firstExtraFragmentName.equals(context.getString(R.string.BUY_BULK_VOUCHER))) {
                fragment = new BuyElectronicVoucherFragment();
                BuyElectronicVoucherFragment.isCashInOperation = true;
            } else if (firstExtraFragmentName.equals(context.getString(R.string.GENERATE_TOKEN))) {
                fragment = new GenerateTokenFragment();
            } else if (firstExtraFragmentName.equals(context.getString(R.string.COMMISSION_TRANSFER))) {
                fragment = new MerchantServicesFragment();
                Bundle bundle = new Bundle();
                bundle.putString(context.getString(R.string.REQ_MESSAGE_TYPE), context.getString(R.string.REQ_MESSAGE_TYPE_MERCHANT_COMMISSION_TRANSFER));
                fragment.setArguments(bundle);
            } else if (firstExtraFragmentName.equals(context.getString(R.string.MERCHANT_VOUCHER))) {
                fragment = new MerchantServicesFragment();
                Bundle bundle = new Bundle();
                bundle.putString(context.getString(R.string.REQ_MESSAGE_TYPE),context.getString(R.string.REQ_MESSAGE_TYPE_MERCHANT_VOUCHER));
                fragment.setArguments(bundle);
            }
/*            if(youReader !=null) {
                youReader.reset();
            }*/
            return fragment;

        }

    }


    private static boolean isMenuTabAvailable(){

        return (LoginResponseConstants.walletOptions.isMakePayment() ||
                LoginResponseConstants.walletOptions.isReceivePaymentAvailable()  ||
                LoginResponseConstants.walletOptions.isGiveCashOutAvailable() ||
                LoginResponseConstants.walletOptions.isReceiveCashInAvailable() ||
                LoginResponseConstants.walletOptions.isRedeemVoucherAvailable() ||
                LoginResponseConstants.walletOptions.isBillPayments() ||
                LoginResponseConstants.walletOptions.isSendTopup() ||
                LoginResponseConstants.walletOptions.isRegServicesTag() ||
                LoginResponseConstants.walletOptions.isChangePinAvailable() ||
                LoginResponseConstants.walletOptions.isServerConfigurable() ||
                LoginResponseConstants.walletOptions.isSelectCurrencyAvailable());
    }


    public static void hideCustomerRegistrationControl(Activity activity){
        LinearLayout controls = (LinearLayout)activity.findViewById(R.id.Activity_Application_LinearLayout_CustomerCreationControls);
        controls.setVisibility(View.GONE);
    }

    private static boolean isQuickLinkTabAvailable(){
        return Constants.quickLinks.size() != 0;
    }

    public static int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int)(dp * (metrics.densityDpi / 160f));
        return px;
    }

    private String validateCustomerRegistrationRequest(){
        if(getNewCustomer().getMSISDN() == null || getNewCustomer().getMSISDN().isEmpty())
            return "Contact Number is mandatory field.";
        if(getNewCustomer().getGivenName() == null || getNewCustomer().getGivenName().isEmpty())
            return "Given Name is mandatory field.";
        if(getNewCustomer().getSurName() == null || getNewCustomer().getSurName().isEmpty())
            return "Surname mandatory field.";
        if(getNewCustomer().getDOB() == null  || getNewCustomer().getDOB().isEmpty() || getNewCustomer().getDOB().length() < 8)
            return "DOB is mandatory field.";
        return "OK";
    }

    private boolean validateCustomerRegistrationResponse(String serverMessage) {
        if(serverMessage == null)
            return false;
        String[] items = serverMessage.split(",");
        for (String item: items)
        {
            if(item.toUpperCase().startsWith("STATUS"))
            {
                String[] temp = item.split("=");
                if(temp[1].equalsIgnoreCase("0"))
                    return true;
                else
                    return false;
            }
        }
        return true;

    }
    private String getMessage(String response){
        String result = null;
        String[] items = response.split(",");
        for (String item: items)
        {
            if(item.toUpperCase(Locale.US).startsWith("MESSAGE"))
            {
                String[] temp = item.split("=");
                result = temp[1];
                break;
            }
        }
        return result;
    }

    private int getNumberOfFragmentsAtLogin(){
        // YES Account Yes QL Yes Menu
        if(LoginResponseConstants.accountTabAvailable && LoginResponseConstants.quickLinkTabAvailable && isMenuTabAvailable())
            return 3;
            // No Account Yes QL Yes Menu
        else  if(!LoginResponseConstants.accountTabAvailable && LoginResponseConstants.quickLinkTabAvailable && isMenuTabAvailable())
            return 2;
            // Yes Account No QL Yes Menu
        else  if(LoginResponseConstants.accountTabAvailable && !LoginResponseConstants.quickLinkTabAvailable && isMenuTabAvailable())
            return 2;
            // Yes Account YES QL No Menu
        else  if(LoginResponseConstants.accountTabAvailable && LoginResponseConstants.quickLinkTabAvailable && !isMenuTabAvailable())
            return 2;
            // No Account No QL Yes Menu
        else  if(!LoginResponseConstants.accountTabAvailable && !LoginResponseConstants.quickLinkTabAvailable&& isMenuTabAvailable())
            return 1;
            // No Account Yes QL No Menu
        else  if(!LoginResponseConstants.accountTabAvailable && LoginResponseConstants.quickLinkTabAvailable && !isMenuTabAvailable())
            return 1;
            // Yes Account No QL No Menu
        else  if(LoginResponseConstants.accountTabAvailable && !LoginResponseConstants.quickLinkTabAvailable&& !isMenuTabAvailable())
            return 1;
        return 0;
    }

    public static void hideKeyboard(Activity activity){
        if(activity != null && activity.getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity activity, EditText editText) {
        if(activity != null ) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        }
    }

    public Customer getNewCustomer() {
        return newCustomer;
    }

    public void setNewCustomer(Customer newCustomer) {
        this.newCustomer = newCustomer;
    }


}
