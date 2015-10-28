package com.mobilis.android.nfc.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.acs.audiojack.AesTrackData;
import com.acs.audiojack.AudioJackReader;
import com.acs.audiojack.DukptReceiver;
import com.acs.audiojack.DukptTrackData;
import com.acs.audiojack.Result;
import com.acs.audiojack.Status;
import com.acs.audiojack.Track1Data;
import com.acs.audiojack.Track2Data;
import com.acs.audiojack.TrackData;
import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.domain.INTENT;
import com.mobilis.android.nfc.util.ConversionUtil;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by lewischao on 23/06/15.
 */
public class YoutapReader {
    private AudioJackReader mReader;
    private AudioManager mAudioManager;
    private Result mResult;
    private int mSleepTimeout = 60;
    private Object mResponseEvent = new Object();
    private byte[] mPiccAtr;
    private boolean mPiccAtrReady = false;
    private boolean mResultReady = false;
    private int piccTimeOut = 10;
    private int mPiccCardType;
    private String cardType = "8F";
    private static final String SAMPLE_LOYALTY_CARD_AID = "F222222222";
    private static final String SELECT_APDU_HEADER = "00A40400";
    private byte[] mPiccCommandApdu = {(byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    //Selecting youTap virtual card application 00A404000EF0000000014D505041592E535953
    private byte[] youtapMobileDeviceCommandApdu;/* ={(byte) 0x00,
            (byte) 0xA4,(byte) 0x04,(byte) 0x00,(byte) 0x0E,(byte) 0xF0,(byte) 0x00,(byte) 0x00,
            (byte) 0x00,(byte) 0x01,(byte) 0x4D,(byte) 0x50,(byte) 0x50,
            (byte) 0x41,(byte) 0x59,(byte) 0x2E,(byte) 0x53,(byte) 0x59,(byte) 0x53};*/
    private boolean mPiccResponseApduReady = false;
    private byte[] mPiccResponseApdu;
    private boolean mFirmwareVersionReady;
    private String mFirmwareVersion;
    private Activity mActivity;
    private boolean onBoardNFCUsed = false;
    private String TAG = this.getClass().getSimpleName();
    private static YoutapReader youReader = null;
    private boolean mStatusReady;
    private Status mStatus;
    private byte[] mIpek = new byte[16];
    public static final String DEFAULT_AES_KEY_STRING = "4E 61 74 68 61 6E 2E 4C 69 20 54 65 64 64 79 20";
    public static final String DEFAULT_IKSN_STRING = "FF FF 98 76 54 32 10 E0 00 00";
    public static final String DEFAULT_IPEK_STRING = "6A C2 92 FA A1 31 5B 4D 85 8A B3 A3 D7 D5 93 3A";

    public static YoutapReader getInstance(Activity mActivity) {
        if (youReader == null) {
            youReader = new YoutapReader(mActivity);
        }
        return youReader;
    }

    private YoutapReader(Activity mActivity) {
        Log.d(TAG, " youtap reader constructor");
        this.mActivity = mActivity;
        mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_VIBRATE);
        mReader = new AudioJackReader(mAudioManager, true);
        mReader.setOnPiccAtrAvailableListener(new OnPiccAtrAvailableListener());
        mReader.setOnPiccResponseApduAvailableListener(new OnPiccResponseApduAvailableListener());
        mReader.setOnResultAvailableListener(new OnResultAvailableListener());
        //  mReader.setOnRawDataAvailableListener(new OnRawDataAvailableListener());
        mReader.setOnFirmwareVersionAvailableListener(new OnFirmwareVersionAvailableListener());
        mReader.setOnTrackDataAvailableListener(new OnTrackDataAvailableListener());
        //mReader.setOnStatusAvailableListener(new OnStatusAvailableListener());
        mReader.setSleepTimeout(mSleepTimeout);
                /* Set the key serial number. */
        mDukptReceiver.setKeySerialNumber(mIksn);

        /* Load the initial key. */
        mDukptReceiver.loadInitialKey(mIpek);
        String aesKeyString = DEFAULT_AES_KEY_STRING;
        if ((aesKeyString == null) || aesKeyString.equals("")
                || (toByteArray(aesKeyString, mAesKey) != 16)) {

            aesKeyString = DEFAULT_AES_KEY_STRING;
            toByteArray(aesKeyString, mAesKey);
        }

        String iksnString = DEFAULT_IKSN_STRING;
        if ((iksnString == null) || iksnString.equals("")
                || (toByteArray(iksnString, mIksn) != 10)) {

            iksnString = DEFAULT_IKSN_STRING;
            toByteArray(iksnString, mIksn);
        }

        mReader.start();
        Log.d(TAG, " youtap reader constructor set");

        youtapMobileDeviceCommandApdu = HexStringToByteArray(SELECT_APDU_HEADER + String.format("%02X", SAMPLE_LOYALTY_CARD_AID.length() / 2) + SAMPLE_LOYALTY_CARD_AID);
        Log.d(TAG, " youtap reader constructor set"+youtapMobileDeviceCommandApdu);
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
    public static byte[] HexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
/*    public boolean isSleep() {
        Log.d(TAG, " youtap reader  mReader.getCardState(1) " + mReader.getCardState(1)); ;

        if(!mReader.getStatus()){
            return true;
        }else {
            return showStatus();
        }
    }*/

    public void startReader() {
        mReader.start();
    }

    public void stopReader() {
        mReader.stop();
        youReader = null;
    }

/*    private boolean showStatus() {
        boolean status ;
        synchronized (mResponseEvent) {
            *//* Wait for the status. *//*
            while (!mStatusReady && !mResultReady) {
                try {
                    mResponseEvent.wait(2000);
                } catch (InterruptedException e) {
                }
                break;
            }
            Log.d(TAG, "youtap reader isSleep showStatus " + mStatus);
            if (mStatusReady) {
                Log.d(TAG, "youtap reader isSleep showStatus  getBatteryLevel" + mStatus.getBatteryLevel());

                status =  false;
            } else if (mResultReady) {
                Log.d(TAG, "youtap reader isSleep showStatus  mResultReady" + mResultReady);
                status =  true;
            } else {
                status =  true;
            }

        }
        mStatusReady = false;
        mResultReady = false;
        return status;
    }*/

    private class OnStatusAvailableListener implements
            AudioJackReader.OnStatusAvailableListener {

        @Override
        public void onStatusAvailable(AudioJackReader reader, Status status) {

            synchronized (mResponseEvent) {

                /* Store the status. */
                mStatus = status;

                /* Trigger the response event. */
                mStatusReady = true;
                mResponseEvent.notifyAll();
            }
        }
    }

    public class OnResetCompleteListener implements
            AudioJackReader.OnResetCompleteListener {
        @Override
        public void onResetComplete(AudioJackReader reader) {
            Log.d(this.getClass().getSimpleName(), " youtap onResetComplete  check reader status ");
            // if (!mReader.setSleepTimeout(mSleepTimeout)) {
            //     Log.d(this.getClass().getSimpleName(), " youtap  onResetComplete  failed failed");
            // }else {
            mFirmwareVersionReady = false;
            mResultReady = false;
            if (!mReader.getFirmwareVersion()) {
                Log.d(this.getClass().getSimpleName(), " youtap  onResetComplete  not getting firmware");
            } else {
                showFirmWare();
/*                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mReader.reset();

                        }
                    });*/
            }
            // }
        }
    }

    private void showFirmWare() {
        synchronized (mResponseEvent) {

            /* Wait for the firmware version. */
            while (!mFirmwareVersionReady && !mResultReady) {

                try {
                    mResponseEvent.wait(10000);
                } catch (InterruptedException e) {
                }

                break;
            }
            Log.d(TAG, "youtap showFirmWare mFirmwareVersionReady " + mFirmwareVersionReady);
            Log.d(TAG, "youtap showFirmWare mResultReady " + mResultReady);
            if (mFirmwareVersionReady) {
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Log.d(TAG, "showFirmWare mFirmwareVersionReady " + mFirmwareVersion);
                        Toast.makeText(mActivity, "Reader ready." + mFirmwareVersion, Toast.LENGTH_SHORT).show();
                    }
                });

            } else if (mResultReady) {
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Log.d(TAG, "showFirmWare mResultReady " + mFirmwareVersion);
                        Toast.makeText(mActivity,
                                toErrorCodeString(mResult.getErrorCode()) + "   " + mFirmwareVersion,
                                Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                        /* Show the timeout. */
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "The operation timed out.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ApplicationActivity.displayReaderProgress(View.GONE);
                }
            });
            mFirmwareVersionReady = false;
            mResultReady = false;

        }
    }

    private static String toErrorCodeString(int errorCode) {

        String errorCodeString = null;

        switch (errorCode) {
            case Result.ERROR_SUCCESS:
                errorCodeString = "The operation completed successfully.";
                break;
            case Result.ERROR_INVALID_COMMAND:
                errorCodeString = "The command is invalid.";
                break;
            case Result.ERROR_INVALID_PARAMETER:
                errorCodeString = "The parameter is invalid.";
                break;
            case Result.ERROR_INVALID_CHECKSUM:
                errorCodeString = "The checksum is invalid.";
                break;
            case Result.ERROR_INVALID_START_BYTE:
                errorCodeString = "The start byte is invalid.";
                break;
            case Result.ERROR_UNKNOWN:
                errorCodeString = "The error is unknown.";
                break;
            case Result.ERROR_DUKPT_OPERATION_CEASED:
                errorCodeString = "The DUKPT operation is ceased.";
                break;
            case Result.ERROR_DUKPT_DATA_CORRUPTED:
                errorCodeString = "The DUKPT data is corrupted.";
                break;
            case Result.ERROR_FLASH_DATA_CORRUPTED:
                errorCodeString = "The flash data is corrupted.";
                break;
            case Result.ERROR_VERIFICATION_FAILED:
                errorCodeString = "The verification is failed.";
                break;
            default:
                errorCodeString = "Error communicating with reader.";
                break;
        }

        return errorCodeString;
    }

    private class OnPiccAtrAvailableListener implements
            AudioJackReader.OnPiccAtrAvailableListener {

        @Override
        public void onPiccAtrAvailable(AudioJackReader reader, byte[] atr) {

            synchronized (mResponseEvent) {

                /* Store the PICC ATR. */
                mPiccAtr = new byte[atr.length];
                System.arraycopy(atr, 0, mPiccAtr, 0, atr.length);

                /* Trigger the response event. */
                mPiccAtrReady = true;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(mActivity, "Card read,processing request.", Toast.LENGTH_SHORT).show();
                    }
                });
                mResponseEvent.notifyAll();
            }
        }
    }

    private class OnPiccResponseApduAvailableListener implements
            AudioJackReader.OnPiccResponseApduAvailableListener {

        @Override
        public void onPiccResponseApduAvailable(AudioJackReader reader,
                                                byte[] responseApdu) {

            synchronized (mResponseEvent) {

                /* Store the PICC response APDU. */
                mPiccResponseApdu = new byte[responseApdu.length];
                System.arraycopy(responseApdu, 0, mPiccResponseApdu, 0,
                        responseApdu.length);

                /* Trigger the response event. */
                mPiccResponseApduReady = true;
                mResponseEvent.notifyAll();
            }
        }
    }

    private class OnResultAvailableListener implements
            AudioJackReader.OnResultAvailableListener {

        @Override
        public void onResultAvailable(AudioJackReader reader, Result result) {

            synchronized (mResponseEvent) {

                /* Store the result. */
                mResult = result;
                Log.d(this.getClass().getSimpleName(), " OnResultAvailableListener " + mResult.getErrorCode());
                /* Trigger the response event. */
                mResultReady = true;
                mResponseEvent.notifyAll();
            }
        }
    }

    private class OnRawDataAvailableListener implements
            AudioJackReader.OnRawDataAvailableListener {

        private String mHexString;

        @Override
        public void onRawDataAvailable(AudioJackReader reader, byte[] rawData) {

            mHexString = ConversionUtil.bytesToHexString(rawData);

            Log.d(mActivity.getClass().getSimpleName(), "onRawDataAvailable mHexString " + mHexString);
            // if (reader.verifyData(rawData) && mHexString.startsWith("2300875") && !mPiccResponseApduReady) {
            if (reader.verifyData(rawData) && mHexString.startsWith("2300875") && !mPiccResponseApduReady) {

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "Card swiped,processing request.", Toast.LENGTH_SHORT).show();
                    }
                });
                String cardId = toHexString(rawData);
                Log.d(mActivity.getClass().getSimpleName(), "cardId " + cardId);
                Intent intent = new Intent(INTENT.NFC_SCANNED.toString());
                intent.putExtra(INTENT.EXTRA_NFC_ID.toString(), cardId.toUpperCase());
                Log.d(mActivity.getClass().getSimpleName(), "onRawDataAvailable rawData " + cardId);
                Log.d(mActivity.getClass().getSimpleName(), "onRawDataAvailable " + INTENT.EXTRA_NFC_ID.toString() + " intent now...");
                LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
            }
            mHexString = null;
            mPiccAtr=null;
        }
    }

    private String toHexString(byte[] buffer) {

        String bufferString = "";

        if (buffer != null) {

            for (int i = 0; i < buffer.length; i++) {

                String hexChar = Integer.toHexString(buffer[i] & 0xFF);
                if (hexChar.length() == 1) {
                    hexChar = "0" + hexChar;
                }
                if (!hexChar.equals("00") && i > 3) {
                    bufferString += hexChar.toUpperCase(Locale.US);
                }

            }
        }

        return bufferString;
    }

    private class OnFirmwareVersionAvailableListener implements
            AudioJackReader.OnFirmwareVersionAvailableListener {

        @Override
        public void onFirmwareVersionAvailable(AudioJackReader reader,
                                               String firmwareVersion) {

            synchronized (mResponseEvent) {

                /* Store the firmware version. */
                mFirmwareVersion = firmwareVersion;
                Log.d(TAG, "onFirmwareVersionAvailable mFirmwareVersion " + firmwareVersion);
                Log.d(TAG, "onFirmwareVersionAvailable mFirmwareVersion " + mFirmwareVersion);
                /* Trigger the response event. */
                mFirmwareVersionReady = true;
                mResponseEvent.notifyAll();
            }
        }
    }

    /////////////////////////////////////////////
    /////////////////////////////////////////////
    public synchronized void triggerPicc() {
/*        if(mReader !=null){
            Log.d(mActivity.getClass().getSimpleName(),  "triggerPicc mReader.getStatus() "+mReader.getStatus());
            if(!mReader.getStatus()){
                mReader.start();
            }
        }*/
        onBoardNFCUsed = false;
        int i = 0;
        Log.d(mActivity.getClass().getSimpleName(), "triggerPicc ");
        String piccCardTypeString = (String) cardType;
        byte[] cardType = new byte[1];
        ConversionUtil.toByteArray(piccCardTypeString, cardType);
        mPiccCardType = cardType[0] & 0xFF;
        if (mPiccCardType == 0) {
            mPiccCardType = 0x8F;
        }
        displayPower();

    }

    private void displayPower() {
        new Thread(new PowerOn()).start();
    }

    private class PowerOn implements Runnable {
        @Override
        public void run() {
            if (mReader != null) {
                String piccCardTypeString = (String) cardType;
                byte[] cardType = new byte[1];
                ConversionUtil.toByteArray(piccCardTypeString, cardType);
                mPiccCardType = cardType[0] & 0xFF;
                if (mPiccCardType == 0) {
                    mPiccCardType = 0x8F;
                }
                mPiccAtrReady = false;
                mResultReady = false;
                if (!mReader.piccPowerOn(piccTimeOut, mPiccCardType)) {
                    Log.d(mActivity.getClass().getSimpleName(), "PowerOn card error restart triggerPiccss ");
                    triggerPicc();
                } else {
                    showPiccAtr();
                }
            }
        }
    }
  /*  private boolean isCard(){
        boolean isCard = false;

        Log.d(mActivity.getClass().getSimpleName(), " isCard  " + ConversionUtil.bytesToHexString(mPiccAtr));
        for(int i =0;i< mPiccAtr.length;i++){
           // Log.d(mActivity.getClass().getSimpleName(), "isCard  showPiccAtr isCard i " + i + " ... " + mPiccAtr[i]);
            String oneByteString =ConversionUtil.bytesToHexString(mPiccAtr[i]);
            //Log.d(mActivity.getClass().getSimpleName(), "isCard  showPiccAtr oneByteString i "+i+" ... "+ oneByteString);
            int value = Integer.parseInt(oneByteString, 16);
           // Log.d(mActivity.getClass().getSimpleName(), "isCard  showPiccAtr value i "+i+" ... "+ value);
            if(i==13){
                Log.d(mActivity.getClass().getSimpleName(), "isCard  showPiccAtr value i "+i+" ... "+ value);
                oneByteString +=ConversionUtil.bytesToHexString(mPiccAtr[i+1]);
                Log.d(mActivity.getClass().getSimpleName(), "isCard  showPiccAtr value i "+i+" ... "+ oneByteString);
                if(oneByteString.equalsIgnoreCase("003B")){
                    isCard =  true;
                    break;
                }
            }
        }
        Log.d(mActivity.getClass().getSimpleName(), " isCard  " + isCard);
        return isCard;
    }*/
    private void showPiccAtr() {
        synchronized (mResponseEvent) {
            /* Wait for the PICC ATR. */
            while (!mPiccAtrReady && !mResultReady) {
                try {
                    mResponseEvent.wait(10000);
                } catch (InterruptedException e) {
                }
                break;
            }
            if (mPiccAtrReady) {
                Log.d(mActivity.getClass().getSimpleName(), " showPiccAtr mPiccAtr " + ConversionUtil.bytesToHexString(mPiccAtr));
                Log.d(mActivity.getClass().getSimpleName(), "Lewis mPiccAtr.length " + mPiccAtr.length);
                if(mPiccAtr != null && mPiccAtr.length<=5){
                    new Thread(new Transmit(true)).start();
                }else {
                    new Thread(new Transmit(true)).start();
/*                    if (isCard()) {
                        Log.d(mActivity.getClass().getSimpleName(), "Lewis PowerOn " + mPiccAtr.length);
                        mPiccResponseApdu = null;
                        new Thread(new PowerOn()).start();
                    } else {
                        new Thread(new Transmit(true)).start();
                    }*/

                }
            } else {
                Log.d(mActivity.getClass().getSimpleName(), " showPiccAtr onBoardNFCUsed " + onBoardNFCUsed);
                Log.d(mActivity.getClass().getSimpleName(), " showPiccAtr mPiccAtrReady " + mPiccAtrReady);
                Log.d(mActivity.getClass().getSimpleName(), " showPiccAtrmResultReady " + mResultReady);
                //disabledPicc();
                Log.d(mActivity.getClass().getSimpleName(), " showPiccAtr onBoardNFCUsed triggerPicc " + !onBoardNFCUsed);
                if (!onBoardNFCUsed) {
                    Log.d(mActivity.getClass().getSimpleName(), " showPiccAtr restarting piccc ");
                    triggerPicc();
                }
            }
            mPiccAtrReady = false;
            mResultReady = false;
        }
    }

    private class Transmit implements Runnable {
        private boolean deviceCommand;

        public Transmit(boolean deviceCommand) {
            this.deviceCommand = deviceCommand;
        }

        @Override
        public void run() {
                /* Transmit the command APDU. */
            Log.d(TAG,"Lewis start transmit "+  this.deviceCommand);
            mPiccResponseApduReady = false;
            mResultReady = false;
            byte[] actualCommand = null;
            if (deviceCommand) {
                actualCommand = youtapMobileDeviceCommandApdu;
                Log.d(TAG,"Lewis start transmit youtap command ");
            } else {
                actualCommand = mPiccCommandApdu;
                Log.d(TAG,"Lewis start transmit normal command ");
            }
            Log.d(TAG,"Lewis start transmit "+ByteArrayToHexString(actualCommand));
            if (!mReader.piccTransmit(piccTimeOut, actualCommand)) {
                //TODO retransmit
            } else {
                showPiccResponseApdu(deviceCommand);
            }
        }
    }

    public void setStatus(boolean mute) {
        Log.d(TAG, " youtap reader setStatus setStatus " + mute);
        mReader.setMute(mute);// = new AudioJackReader(mAudioManager,true);
        Log.d(TAG, " youtap reader setStatus unmute reader " + !mReader.isMute());
        if (!mReader.isMute()) {
            mReader.reset(new OnResetCompleteListener());
        }
    }

    public void disabledPicc() {
        if (mReader != null) {
            mReader.piccPowerOff();
        }
        onBoardNFCUsed = true;
        sendYoutapCommandCnt =0;
    }

    public void reset() {
        mReader.reset();
    }

    private void showPiccResponseApdu(boolean deviceCommand) {
        synchronized (mResponseEvent) {

            /* Wait for the PICC response APDU. */
            while (!mPiccResponseApduReady && !mResultReady) {
                Log.d(mActivity.getClass().getSimpleName(), " showPiccResponseApdu mPiccResponseApduReady " + mPiccResponseApduReady);
                try {
                    mResponseEvent.wait(10000);
                } catch (InterruptedException e) {
                    Log.d(mActivity.getClass().getSimpleName(), "showPiccResponseApdu error " + e.toString());
                }

                break;
            }
            Log.d(mActivity.getClass().getSimpleName(), " showPiccResponseApdu mResultReady " + mResultReady);
            Log.d(mActivity.getClass().getSimpleName(), " showPiccResponseApdu mPiccResponseApduReady " + mPiccResponseApduReady);
            Log.d(mActivity.getClass().getSimpleName(), " showPiccResponseApdu mPiccResponseApdu string " +  ConversionUtil.bytesToHexString(mPiccResponseApdu));
            Log.d(mActivity.getClass().getSimpleName(), " showPiccResponseApdu deviceCommand " + deviceCommand);
            if (mPiccResponseApduReady || mPiccResponseApdu != null) {
                // alertTap();


                String scannedId;
                if (deviceCommand) {
                    scannedId = ConversionUtil.bytesToHexString(Arrays.copyOfRange(mPiccResponseApdu, 0, mPiccResponseApdu.length - 2));
                    if(scannedId != null) {
                        Log.d(mActivity.getClass().getSimpleName(), " YoutapWallet scannedId " + scannedId);
                        try {
                            String result = new String(mPiccResponseApdu, "ASCII");
                            Log.d(mActivity.getClass().getSimpleName(), " YoutapWallet result " + result);
                            if (result.contains("|")) {
                                String data[] = result.split("\\|");
                                if (data.length > 0) {
                                    Log.i(TAG, "readHCE Received: Tag Id " + data[0]);
                                    Log.i(TAG, "readHCE Received: Tag Id " + data[0]);
                                    scannedId = data[0].toUpperCase(Locale.US);
                                    if(data.length>1){
                                        ApplicationActivity.currentTransactionId = data[1];
                                        ApplicationActivity.isDevice =true;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            scannedId = null;
                        }
                    }
                } else {
                    scannedId = ConversionUtil.bytesToHexString(Arrays.copyOfRange(mPiccResponseApdu, 0, mPiccResponseApdu.length - 2));
                }
                if(scannedId !=null) {
                    Intent intent = new Intent(INTENT.NFC_SCANNED.toString());
                    intent.putExtra(INTENT.EXTRA_NFC_ID.toString(), scannedId.toUpperCase());
                    Log.d(mActivity.getClass().getSimpleName(), "showPiccResponseApdu mPiccResponseApduReady " + scannedId);
                    Log.d(mActivity.getClass().getSimpleName(), "showPiccAtr " + INTENT.EXTRA_NFC_ID.toString() + " intent now...");
                    LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
                }else{
                    if(sendYoutapCommandCnt<3) {
                        sendYoutapCommandCnt++;
                        new Thread(new Transmit(true)).start();
                    }else{
                        new Thread(new Transmit(false)).start();
                    }
                }

            }
            mPiccResponseApduReady = false;
            mResultReady = false;
        }
    }
    private int sendYoutapCommandCnt=0;
    private byte[] mAesKey = new byte[16];
    private byte[] mIksn = new byte[10];


    private class OnTrackDataAvailableListener implements
            AudioJackReader.OnTrackDataAvailableListener {

        private Track1Data mTrack1Data;
        private Track2Data mTrack2Data;
        private Track1Data mTrack1MaskedData;
        private Track2Data mTrack2MaskedData;
        private String mTrack1MacString;
        private String mTrack2MacString;
        private String mKeySerialNumberString;
        private int mErrorId;

        @Override
        public void onTrackDataAvailable(AudioJackReader reader,
                                         TrackData trackData) {

            mTrack1Data = new Track1Data();
            mTrack2Data = new Track2Data();
            mTrack1MaskedData = new Track1Data();
            mTrack2MaskedData = new Track2Data();
            mTrack1MacString = "";
            mTrack2MacString = "";
            mKeySerialNumberString = "";

            if ((trackData.getTrack1ErrorCode() != TrackData.TRACK_ERROR_SUCCESS)
                    && (trackData.getTrack2ErrorCode() != TrackData.TRACK_ERROR_SUCCESS)) {
                mErrorId = R.string.message_track_data_error_corrupted;
            } else if (trackData.getTrack1ErrorCode() != TrackData.TRACK_ERROR_SUCCESS) {
                mErrorId = R.string.message_track1_data_error_corrupted;
            } else if (trackData.getTrack2ErrorCode() != TrackData.TRACK_ERROR_SUCCESS) {
                mErrorId = R.string.message_track2_data_error_corrupted;
            }

            /* Show the track error. */
            if ((trackData.getTrack1ErrorCode() != TrackData.TRACK_ERROR_SUCCESS)
                    || (trackData.getTrack2ErrorCode() != TrackData.TRACK_ERROR_SUCCESS)) {

                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showMessageDialog(mErrorId);
                    }
                });
            }
            Log.d(mActivity.getClass().getSimpleName(), "youtapreader onTrackDataAvailable trackData getTrack1Length " + trackData.getTrack1Length());
            Log.d(mActivity.getClass().getSimpleName(), "youtapreader onTrackDataAvailable trackData getTrack2Length " + trackData.getTrack2Length());
            /* Show the track data. */
            if (trackData instanceof AesTrackData) {
                Log.d(mActivity.getClass().getSimpleName(), "youtapreader onTrackDataAvailable AesTrackData " + trackData);
                showAesTrackData((AesTrackData) trackData);
            } else if (trackData instanceof DukptTrackData) {
                Log.d(mActivity.getClass().getSimpleName(), "youtapreader onTrackDataAvailable DukptTrackData " + trackData);
                showDukptTrackData((DukptTrackData) trackData);
            }
        }

        /**
         * Shows the AES track data.
         *
         * @param trackData the AES track data.
         */
        private void showAesTrackData(AesTrackData trackData) {

            byte[] decryptedTrackData = null;

            /* Decrypt the track data. */
            try {

                decryptedTrackData = aesDecrypt(mAesKey,
                        trackData.getTrackData());

            } catch (GeneralSecurityException e) {

                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showMessageDialog(
                                R.string.message_track_data_error_decrypted);
                    }
                });

                /* Show the track data. */
                //  showTrackData();
                return;
            }

            /* Verify the track data. */
            if (!mReader.verifyData(decryptedTrackData)) {

                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showMessageDialog(
                                R.string.message_track_data_error_checksum);
                    }
                });

                /* Show the track data. */
                //showTrackData();
                return;
            }

            /* Decode the track data. */
            mTrack1Data.fromByteArray(decryptedTrackData, 0,
                    trackData.getTrack1Length());
            mTrack2Data.fromByteArray(decryptedTrackData, 79,
                    trackData.getTrack2Length());

            /* Show the track data. */
            showTrackData();
        }

        /**
         * Shows the DUKPT track data.
         *
         * @param trackData the DUKPT track data.
         */
        private void showDukptTrackData(DukptTrackData trackData) {

            int ec = 0;
            int ec2 = 0;
            byte[] track1Data = null;
            byte[] track2Data = null;
            String track1DataString = null;
            String track2DataString = null;
            byte[] key = null;
            byte[] dek = null;
            byte[] macKey = null;
            byte[] dek3des = null;

            mKeySerialNumberString = toHexString(trackData.getKeySerialNumber());
            mTrack1MacString = toHexString(trackData.getTrack1Mac());
            mTrack2MacString = toHexString(trackData.getTrack2Mac());
            mTrack1MaskedData.fromString(trackData.getTrack1MaskedData());
            mTrack2MaskedData.fromString(trackData.getTrack2MaskedData());

            /* Compare the key serial number. */
            if (!DukptReceiver.compareKeySerialNumber(mIksn,
                    trackData.getKeySerialNumber())) {

                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showMessageDialog(
                                R.string.message_track_data_error_ksn);
                    }
                });

                /* Show the track data. */
                showTrackData();
                return;
            }

            /* Get the encryption counter from KSN. */
            ec = DukptReceiver.getEncryptionCounter(trackData
                    .getKeySerialNumber());

            /* Get the encryption counter from DUKPT receiver. */
            ec2 = mDukptReceiver.getEncryptionCounter();

            /*
             * Load the initial key if the encryption counter from KSN is less
             * than the encryption counter from DUKPT receiver.
             */
            if (ec < ec2) {

                mDukptReceiver.loadInitialKey(mIpek);
                ec2 = mDukptReceiver.getEncryptionCounter();
            }

            /*
             * Synchronize the key if the encryption counter from KSN is greater
             * than the encryption counter from DUKPT receiver.
             */
            while (ec > ec2) {

                mDukptReceiver.getKey();
                ec2 = mDukptReceiver.getEncryptionCounter();
            }

            if (ec != ec2) {

                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showMessageDialog(
                                R.string.message_track_data_error_ec);
                    }
                });

                /* Show the track data. */
                showTrackData();
                return;
            }

            key = mDukptReceiver.getKey();
            if (key == null) {

                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        /* Show the timeout. */
                        Toast.makeText(
                                mActivity,
                                "The maximum encryption count had been reached.",
                                Toast.LENGTH_LONG).show();
                    }
                });

                /* Show the track data. */
                showTrackData();
                return;
            }

            dek = DukptReceiver.generateDataEncryptionRequestKey(key);
            macKey = DukptReceiver.generateMacRequestKey(key);
            dek3des = new byte[24];

            /* Generate 3DES key (K1 = K3) */
            System.arraycopy(dek, 0, dek3des, 0, dek.length);
            System.arraycopy(dek, 0, dek3des, 16, 8);

            try {

                if (trackData.getTrack1Data() != null) {

                    /* Decrypt the track 1 data. */
                    track1Data = tripleDesDecrypt(dek3des,
                            trackData.getTrack1Data());

                    /* Generate the MAC for track 1 data. */
                    mTrack1MacString += " ("
                            + toHexString(DukptReceiver.generateMac(macKey,
                            track1Data)) + ")";

                    /* Get the track 1 data as string. */
                    track1DataString = new String(track1Data, 1,
                            trackData.getTrack1Length(), "US-ASCII");

                    /* Divide the track 1 data into fields. */
                    mTrack1Data.fromString(track1DataString);
                }

                if (trackData.getTrack2Data() != null) {

                    /* Decrypt the track 2 data. */
                    track2Data = tripleDesDecrypt(dek3des,
                            trackData.getTrack2Data());

                    /* Generate the MAC for track 2 data. */
                    mTrack2MacString += " ("
                            + toHexString(DukptReceiver.generateMac(macKey,
                            track2Data)) + ")";

                    /* Get the track 2 data as string. */
                    track2DataString = new String(track2Data, 1,
                            trackData.getTrack2Length(), "US-ASCII");

                    /* Divide the track 2 data into fields. */
                    mTrack2Data.fromString(track2DataString);
                }

            } catch (GeneralSecurityException e) {

                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showMessageDialog(
                                R.string.message_track_data_error_decrypted);
                    }
                });

            } catch (UnsupportedEncodingException e) {
            }

            /* Show the track data. */
            showTrackData();
        }

        /**
         * Shows the track data.
         */
        private void showTrackData() {

            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    String cardId = concatString(mTrack2Data.getPrimaryAccountNumber(), mTrack2MaskedData.getPrimaryAccountNumber());
                    Log.d(TAG, " showTrackData " + cardId);
                    Toast.makeText(mActivity, "Card swiped,processing request.", Toast.LENGTH_SHORT).show();
                    Log.d(mActivity.getClass().getSimpleName(), "cardId " + cardId);
                    Intent intent = new Intent(INTENT.NFC_SCANNED.toString());
                    intent.putExtra(INTENT.EXTRA_NFC_ID.toString(), cardId.toUpperCase());
                    Log.d(mActivity.getClass().getSimpleName(), "showTrackData " + INTENT.EXTRA_NFC_ID.toString() + " intent now...");
                    LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
                }

            });
        }

        /**
         * Concatenates two strings with carriage return.
         *
         * @param string1 the first string.
         * @param string2 the second string.
         * @return the combined string.
         */
        private String concatString(String string1, String string2) {

            String ret = string1;

            if ((string1.length() > 0) && (string2.length() > 0)) {
                ret += "\n";
            }

            ret += string2;

            return ret;
        }
    }

    private DukptReceiver mDukptReceiver = new DukptReceiver();

    private void showMessageDialog(int text) {
        Toast.makeText(mActivity, mActivity.getString(text), Toast.LENGTH_SHORT).show();
    }

    private byte[] aesDecrypt(byte key[], byte[] input)
            throws GeneralSecurityException {

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        return cipher.doFinal(input);
    }

    private byte[] tripleDesDecrypt(byte[] key, byte[] input)
            throws GeneralSecurityException {

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "DESede");
        Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[8]);

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(input);
    }

    private int toByteArray(String hexString, byte[] byteArray) {

        char c = 0;
        boolean first = true;
        int length = 0;
        int value = 0;
        int i = 0;
        for (i = 0; i < hexString.length(); i++) {
            c = hexString.charAt(i);
            if ((c >= '0') && (c <= '9')) {
                value = c - '0';
            } else if ((c >= 'A') && (c <= 'F')) {
                value = c - 'A' + 10;
            } else if ((c >= 'a') && (c <= 'f')) {
                value = c - 'a' + 10;
            } else {
                value = -1;
            }
            if (value >= 0) {
                if (first) {
                    byteArray[length] = (byte) (value << 4);
                } else {
                    byteArray[length] |= value;
                    length++;
                }
                first = !first;
            }
            if (length >= byteArray.length) {
                break;
            }
        }
        return length;
    }
}
