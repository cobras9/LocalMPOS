package com.mobilis.android.nfc.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.ReaderCallback;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.mobilis.android.nfc.activities.ApplicationActivity;
import com.mobilis.android.nfc.interfaces.A2ACallback;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

@TargetApi(19)
public class AndroidToAndroidNFCActivity implements ReaderCallback {

    private final static String TAG = AndroidToAndroidNFCActivity.class.getSimpleName();
	private NfcAdapter nfcAdapter;
	private Activity activity;
	private A2ACallback callback;
	
	public AndroidToAndroidNFCActivity(Activity activity, A2ACallback callback) {
		setNfcAdapter(NfcAdapter.getDefaultAdapter(activity));
		setActivity(activity);
		setCallback(callback);
	}
/*	public interface AccountCallback {
		public void onAccountReceived(String account);
	}*/
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
	public void onTagDiscovered(final Tag tag) {
		
		Log.d(TAG, "onTagDiscovered() is called");
		Log.d(TAG, "onTagDiscovered() is called Lewis "+tag);
		try {
			Log.d(TAG, "onTagDiscovered() IsoDep  ");
			IsoDep isoDep = IsoDep.get(tag);
			Log.d(TAG, "onTagDiscovered() IsoDep  "+isoDep);
			if (isoDep != null) {
				try {
					// Connect to the remote NFC device
					isoDep.connect();
					// Build SELECT AID command for our loyalty card service.
					// This command tells the remote device which service we wish to communicate with.
					Log.i(TAG, "Requesting remote AID: " + SAMPLE_LOYALTY_CARD_AID);
					byte[] command = BuildSelectApdu(SAMPLE_LOYALTY_CARD_AID);
					// Send command to remote device
					Log.i(TAG, "Sending: " + ByteArrayToHexString(command));
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
						Log.i(TAG, "Received: Response String" + accountNumber);

						if (accountNumber.contains("|")) {
							String data[] = accountNumber.split("\\|");
							if (data.length > 0) {
								Log.i(TAG, "Received: Tag Id " + data[0]);
								Log.i(TAG, "Received: Tag Id " + data[0]);
								if(data.length>1){
									ApplicationActivity.currentTransactionId = data[1];
									ApplicationActivity.isDevice =true;
								}
								getCallback().finishedA2ACommunication(data[0].toUpperCase(Locale.US));
								return;
							}
						}

					}
				} catch (IOException e) {
					Log.e(TAG, "Error communicating with card: " + e.toString());
				}
			}
		}catch(Exception e){
			Log.e(TAG,"trying to read a phone error "+e.toString());
		}
		if(isNdefTag(tag))
		{
			String uid = bytesToHexString(tag.getId());
			Log.d(TAG, "This is Ndef Message! sending back ID: "+uid);
			getCallback().finishedA2ACommunication(uid);
			return;
		}
		String type = null;
		if (NfcA.get(tag) != null)
		{
			type="NfcA";
		}
		Log.d(TAG, "Tag of type: "+type);
		if(!type.equalsIgnoreCase("NfcA")){
			Log.d(TAG, "NFC Type is:"+type);
			String uid = bytesToHexString(tag.getId());
			Log.d(TAG, "This is Ndef Message! sending back ID: "+uid);
			getCallback().finishedA2ACommunication(uid);
			return;
		}
		String xUid = bytesToHexString(tag.getId());
		if(xUid.matches(".*[a-z0-9A-Z]+.*")){
			getCallback().finishedA2ACommunication(xUid);
			return;
		}
        byte[] SELECT = {
				(byte)0x00, /* CLA = 00 (first interindustry command set) */
				(byte)0xA4, /* INS = A4 (SELECT) */
				(byte)0x04, /* P1  = 04 (select file by DF name) */
				(byte)0x0C, /* P2  = 0C (first or only file; no FCI) */
				(byte)0x07, /* Lc  = 7  (data/AID has 7 bytes) */
				/* AID = A0000000041010 */
				(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, 
				(byte)0x04, (byte)0x10, (byte)0x10
		};
		

		final IsoDep myCard = IsoDep.get(tag);
		
		try {
			startTypeAChecker(tag);
			
			Log.d(TAG, "connecting to tag");
			myCard.connect();
			connected = true;
			Log.d(TAG, "connected");
			if(myCard.isConnected()){
				
				Log.d(TAG, "sending select statement");
				byte[] resp = myCard.transceive(SELECT);
				Log.d(TAG, "respo array == null ? "+(resp == null));
				Log.d(TAG, "222 response length: "+resp.length);
                if(resp.length < 4)
                {
                    String uid = bytesToHexString(tag.getId());
                    Log.d(TAG, "response length is less than 3 setting UID: "+uid);
                    closeTagConnection(myCard);
                    getCallback().finishedA2ACommunication(uid.toUpperCase(Locale.US));
                    return;
                }
				for (int i = 0; i < resp.length; i++) {
					Log.d(TAG, "resp Byte["+i+"} is: "+resp[i]);
				}
				byte[] hex = new byte[resp.length - 3];
				int counter = 0;
				for (int i = 0; i < resp.length; i++) {
					if(i > 0 && i < (resp.length-2))
					{
						hex[counter] =  resp[i];
						counter++;
					}
				}
				String string = bytesToHexString(hex);
				Log.d(TAG, "got androidId from other device: "+string);
				getCallback().finishedA2ACommunication(string.toString().toUpperCase(Locale.US));
				Log.d(TAG, "Device UID: "+string.toString().toUpperCase(Locale.US));
                closeTagConnection(myCard);
			}		
		} 
		catch (IOException e) {
			Log.d(TAG, "Exception occured");
			e.printStackTrace();
		}
        catch (Exception e){
            String uid = bytesToHexString(tag.getId());
            Log.d(TAG, "Exception occured returning uid: "+uid);
            getCallback().finishedA2ACommunication(uid.toUpperCase(Locale.US));
            e.printStackTrace();
        }

		
	}

    private void closeTagConnection(IsoDep myCard) {
        Log.d(TAG, "closeTagConnection() is called in higher version");
        if(myCard != null && myCard.isConnected())
            try {
                Log.d(TAG, "closing Tag connection");
                myCard.close();
                Log.d(TAG, "Tag is now closed!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        putConnectedStatusBackToNormal();
    }


    private void putConnectedStatusBackToNormal() {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						connected = false;
					}
				}, 500);
			}
		});
	}


	private void startTypeAChecker(final Tag tag) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Log.d(TAG, "connected ?" + connected);
						if (!connected) {
							Log.d(TAG, "Couldn't connect..will send back UID");
							String uid = bytesToHexString(tag.getId());
							getCallback().finishedA2ACommunication(uid.toUpperCase(Locale.US));
							Log.d(TAG, "TAG UID: " + uid.toUpperCase(Locale.US));
						}
					}
				}, 500);
			}
		});
	}
	boolean connected = false;
	private String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("0x");
		if (src == null || src.length <= 0) {
			return null;
		}
		
		char[] buffer = new char[2];
		for (int i = 0; i < src.length; i++) {
			buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);  
			buffer[1] = Character.forDigit(src[i] & 0x0F, 16);  
			stringBuilder.append(buffer);
		}
        Log.d(TAG, "Android2Android bytesToHexString is returning: "+stringBuilder.toString().substring(2));
		return stringBuilder.toString().substring(2);
	}

	private boolean isNdefTag(Tag myTag){
		Ndef ndefTag = Ndef.get(myTag);
		if(ndefTag == null)
			return false;
		NdefMessage ndefMesg = ndefTag.getCachedNdefMessage();
		
		if(ndefMesg == null)
			return false;
		else
			return true;
	}
	
	
	public NfcAdapter getNfcAdapter() {
		return nfcAdapter;
	}

	public void setNfcAdapter(NfcAdapter nfcAdapter) {
		this.nfcAdapter = nfcAdapter;
	}
	public static int READER_FLAGS =
			NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
	public void enableReadMode(){
	    Log.d(TAG, "activity == null? " + (activity == null));
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			nfcAdapter.enableReaderMode(activity, this, READER_FLAGS, null);
		}
	}
	public void disableReadMode(){
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			nfcAdapter.disableReaderMode(activity);
		}
	}
	public Activity getActivity() {
		return activity;
	}
	public void setActivity(Activity activity) {
		this.activity = activity;
	}


	public A2ACallback getCallback() {
		return callback;
	}


	public void setCallback(A2ACallback callback) {
		this.callback = callback;
	}
	


}
