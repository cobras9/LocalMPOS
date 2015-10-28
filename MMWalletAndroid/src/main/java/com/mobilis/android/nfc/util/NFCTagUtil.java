package com.mobilis.android.nfc.util;

import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.util.Log;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by lewischao on 6/05/15.
 */
public class NFCTagUtil {
    private static String TAG = NFCTagUtil.class.getSimpleName();
    private static boolean debug = false;
    public static boolean isNdefTag(Tag myTag){
        Ndef ndefTag = Ndef.get(myTag);
        if(ndefTag == null)
            return false;
        NdefMessage ndefMesg = ndefTag.getCachedNdefMessage();

        if(ndefMesg == null)
            return false;
        else
            return true;
    }
    public static void closeTagConnection(IsoDep myCard) {
        if(debug)Log.d(TAG, "closeTagConnection() is called in lower version");
        if(myCard != null && myCard.isConnected())
            try {
                if(debug)Log.d(TAG, "closing Tag connection");
                myCard.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public static String findTagId(Tag tag){
        if(debug)Log.d(TAG, "Got it!!!");
        StringBuilder output = new StringBuilder();

        if(NFCTagUtil.isNdefTag(tag))
        {
            String uid = ConversionUtil.bytesToHexString(tag.getId());
            if(debug)Log.d(TAG, "This is Ndef Message! sending back ID: "+uid);
            return uid;
        }

        String type = "NfcB";
        if (NfcA.get(tag) != null)
        {
            type="NfcA";
        }
        if(debug)Log.d(TAG, "Tag of type: "+type);
        if(debug)Log.d(TAG, "NFC Type is:"+type);
        String uid = ConversionUtil.bytesToHexString(tag.getId());
        if(debug)Log.d(TAG, "This is Ndef Message! sending back ID: "+uid);
        if(uid.matches(".*[a-zA-Z]+.*"))
            return uid;

        byte[] SELECT = {
                (byte)0x00, // CLA = 00 (first interindustry command set)
                (byte)0xA4, /// INS = A4 (SELECT)
                (byte)0x04, // P1  = 04 (select file by DF name)
                (byte)0x0C, /// P2  = 0C (first or only file; no FCI)
                (byte)0x07, /// Lc  = 7  (data/AID has 7 bytes)
                // AID = A0000000041010
                (byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x04, (byte)0x10, (byte)0x10
        };


        final IsoDep myCard = IsoDep.get(tag);

        try {
            if(debug)Log.d(TAG, "connecting to tag");
            myCard.connect();
            //connected = true;
            if(debug)Log.d(TAG, "connected");
            if(myCard.isConnected()){

                if(debug)Log.d(TAG, "sending select statement");
                byte[] resp = myCard.transceive(SELECT);
                if(debug)Log.d(TAG, "respo array == null ? "+(resp == null));
                if(debug)Log.d(TAG, "222 response length: "+resp.length);
                if(resp.length < 4)
                {
                    uid = ConversionUtil.bytesToHexString(tag.getId());
                    if(debug)Log.d(TAG, "response length is less than 3 setting UID: "+uid);
                    //getCallback().finishedA2ACommunication(uid.toUpperCase(Locale.US));
                    NFCTagUtil.closeTagConnection(myCard);
                    return uid;
                }
                for (int i = 0; i < resp.length; i++) {
                    if(debug)Log.d(TAG, "resp Byte["+i+"} is: "+resp[i]);
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
                String string = ConversionUtil.bytesToHexString(hex);
                output.append(string);
                if(debug)Log.d(TAG, "(LowerVersion) got androidId from other device: "+output);
                if(debug)Log.d(TAG, "Device UID: "+output.toString().toUpperCase(Locale.US));
                //putConnectedStatusBackToNormal();
            }
        }
        catch (IOException e) {
            if(debug)Log.d(TAG, "Exception occured");
            e.printStackTrace();
        }
        finally {

            if(debug)Log.d(TAG, "Finally clause is called");
            if(myCard != null && myCard.isConnected())
                try {
                    if(debug)Log.d(TAG, "Finally clause..closing Tag connection");
                    myCard.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return output.toString().toUpperCase(Locale.US);

    }
}
