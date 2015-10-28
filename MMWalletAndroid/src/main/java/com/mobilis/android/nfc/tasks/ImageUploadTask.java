package com.mobilis.android.nfc.tasks;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.activities.LoginActivity;
import com.mobilis.android.nfc.util.SecurePreferences;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lewischao on 21/09/15.
 */
public class ImageUploadTask extends AsyncTask<Object, Void, Void> {
    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private String customerId;
    private String terminalId;
    //private String mobMonPin;
    private String fileUrl;



    @Override
    protected Void doInBackground(Object... params) {

        mContext = (Context) params[0];
        customerId = (String) params[1];
        terminalId = (String) params[2];
        //mobMonPin = (String) params[3];
        fileUrl = (String) params[3];
        HttpURLConnection conn = null;
        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String appVersionCode = mContext.getResources().getString(R.string.app_name) + pInfo.versionName;
        String serverIp =  LoginActivity.mainSecurePreferences.getString(appVersionCode + SecurePreferences.KEY_SERVER_IP, mContext.getString(R.string.SERVER_IP));//mContext.getString(R.string.IMAGE_UPLOAD_IP);
        String port =  mContext.getString(R.string.IMAGE_UPLOAD_PORT);//LoginActivity.mainSecurePreferences.getString(appVersionCode + SecurePreferences.KEY_SERVER_PORT, mContext.getString(R.string.SERVER_PORT));
        String preUrl = mContext.getString(R.string.IMAGE_URL);
        String suffixUrl = mContext.getString(R.string.IMAGE_URL_SUFFIX);
        try {

            String finalUrl = "http://" + serverIp + ":" + port + preUrl + customerId + suffixUrl;
            String encodedImage = Base64.encodeToString(convertBytes(fileUrl), Base64.DEFAULT);
            Log.d(TAG, "Request finalUrl " + finalUrl);
            Log.d(TAG, "Request terminalId " + terminalId);
            Log.d(TAG, "Request customerId " + customerId);
            //Log.d(TAG, "Request mobMonPin " + mobMonPin);
            Log.d(TAG, "Request getHTTPAuth " + getHTTPAuth());
            //Log.d(TAG, "Request photoData " + encodedImage);

            URL url = new URL(finalUrl);
            JSONObject data = new JSONObject();
            data.put("terminalId", terminalId);
            data.put("mime", "image/png");
            // data.put("mobMonPin", mobMonPin);
            data.put("photoData", encodedImage);
            data.put("type", "");
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Authorization", "Basic " + getHTTPAuth());
            conn.connect();
            OutputStream os = conn.getOutputStream();
            os.write(data.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK || conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {

            } else {
                Log.d(TAG, "Server responded with status code: " + conn.getResponseMessage());
            }
        } catch (Exception ex) {
            Log.e(TAG, "Failed to send HTTP request due to: " + ex);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
    private byte[] convertBytes(String filePath) {
        byte[] imageBytes = new byte[0];
        try {
            Bitmap bm = BitmapFactory.decodeFile(filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            imageBytes = baos.toByteArray();
        } catch (Exception e) {
            Log.e(TAG, "convertBytes error " + e.toString());
        }
        return imageBytes;
    }

    private String getHTTPAuth() {
        String encodedUserPassword = "";
        try {
            String userPassword = mContext.getString(R.string.IMAGE_UPLOAD_USER) + ":" + mContext.getString(R.string.IMAGE_UPLOAD_PASSWORD);
            encodedUserPassword = Base64.encodeToString(userPassword.getBytes(), Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "error getHTTPAuth " + e.toString());
        }
        return encodedUserPassword;
    }
/*
    @Override
    protected void onPostExecute(Void obj) {
        Log.d(TAG, "Request onPostExecute " + sendOffRequest);
        if (sendOffRequest) {
            Log.d(TAG, "Request onPostExecute commandOff " + commandOff);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Request onPostExecute " + sendOffRequest);
                    ImageUploadTask request = new ImageUploadTask();
                    request.execute(mContext, commandOn, commandOff, delay, Boolean.FALSE.booleanValue());
                }
            }, delay);
        }
*//*        Intent intent = new Intent(LocalIntent.LOAD_ALBUMS.toString());
        Bundle albumBundles = new Bundle();
        albumBundles.putSerializable(LocalIntent.DATA.toString(), result);
        intent.putExtras(albumBundles);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);*//*
    }*/
}
