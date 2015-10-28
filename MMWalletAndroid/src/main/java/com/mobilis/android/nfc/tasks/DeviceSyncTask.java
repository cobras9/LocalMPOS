package com.mobilis.android.nfc.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.aws.cognito.ExSSLSocketFactory;
import com.mobilis.android.nfc.aws.cognito.LambdaMicroserviceClient;
import com.mobilis.android.nfc.aws.cognito.SyncDeviceModel;
import com.mobilis.android.nfc.aws.cognito.SyncDeviceResponseModel;
import com.mobilis.android.nfc.util.SecurePreferences;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.security.KeyStore;

/**
 * Created by lewischao on 2/02/15.
 */
public class DeviceSyncTask extends AsyncTask<Object, Void, SyncDeviceResponseModel> {
    private static Context mContext;

    //        String URL = mContext.getResources().getString(R.string.DEVICESYNC_URL);
    @Override
    protected SyncDeviceResponseModel doInBackground(Object... params) {
        String imei = (String) params[0];
        String merchantId = (String) params[1];
        String terminalId = (String) params[2];
        mContext = (Context) params[3];

        String URL = (String) params[4];
        String apiKey = (String) params[5];
        String appVersion = mContext.getString(R.string.app_name) + " " + mContext.getString(R.string.APP_VERSION_INTERNAL);
        Log.d("TAG", "DeviceSyncTask imei " + imei);
        Log.d("TAG", "DeviceSyncTask merchantId " + merchantId);
        Log.d("TAG", "DeviceSyncTask terminalId " + terminalId);
        Log.d("TAG", "DeviceSyncTask URL " + URL);
        Log.d("TAG", "DeviceSyncTask apiKey " + apiKey);
        Log.d("TAG", "DeviceSyncTask appVersion " + appVersion);


        return sendDeviceInfo(imei, merchantId, terminalId, URL, apiKey, appVersion);
        //  return null;
    }

    public static HttpClient createHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new ExSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(
                    ExSSLSocketFactory.DO_NOT_VERIFY);
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            Log.e("TAG", "error creating http client");
            return new DefaultHttpClient();
        }
    }

    private SyncDeviceResponseModel sendDeviceInfo(String imei, String merchantId, String terminalId, String URL, String apiKey, String appVersion) {

        SyncDeviceResponseModel myResponseModel = null;
        try {
            ApiClientFactory factory = new ApiClientFactory();
            factory.apiKey(apiKey);
            final LambdaMicroserviceClient client = factory.build(LambdaMicroserviceClient.class);
            SyncDeviceModel dbm = new SyncDeviceModel();
            dbm.setAppVersion(appVersion);
            dbm.setImei(imei);
            dbm.setGlobalMerchantId(merchantId);
            dbm.setTerminalId(terminalId);
            myResponseModel = client.syncDevicePost(dbm);
            Log.d("TAG", " myResponseModel " + myResponseModel);
            Log.d("TAG", " myResponseModel.getInconsistent() " + myResponseModel.getInconsistent());
            Log.d("TAG", "myResponseModel.getSync() " + myResponseModel.getSync());
        }catch(Exception e){}
        return myResponseModel;
    }

    //Apache httpclient
   /* private DeviceSyncModel sendDeviceInfo(String imei, String merchantId, String terminalId, String URL, String apiKey, String appVersion) {
        HttpClient client = createHttpClient();

        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;
        JSONObject json = new JSONObject();
        DeviceSyncModel myResponseModel = null;
        try {
            HttpPost post = new HttpPost(URL);
            post.setHeader("Accept", "application/json");
            post.addHeader("x-api-key", apiKey);
            post.addHeader("Content-Type","application/json");
            json.put("imei", imei);
            json.put("globalMerchantId", merchantId);
            json.put("terminalId", terminalId);
            json.put("geo", "NOT YET");
            json.put("appVersion", appVersion);
            StringEntity se = new StringEntity(json.toString());
            //se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(se);
            response = client.execute(post);

                    //Checking response
            if (response != null) {
                //InputStream in = response.getEntity().getContent(); //Get the data in the entity
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    for (String line = null; (line = reader.readLine()) != null; ) {
                        builder.append(line).append("\n");
                    }
                    Log.d("TAG", "DeviceSyncTask response" + builder.toString());
                    Gson gson = new Gson(); // Or use new GsonBuilder().create();
                    myResponseModel = gson.fromJson(builder.toString(), DeviceSyncModel.class);
                    Log.d("TAG", "DeviceSyncTask isInconsistent" + myResponseModel.isInconsistent());
                    Log.d("TAG", "DeviceSyncTask getSync" + myResponseModel.getSync());
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG", "DeviceSyncTask response" + e.toString());
        }
        return myResponseModel;
    }*/

    @Override
    protected void onPostExecute(SyncDeviceResponseModel result) {
        try {
            SecurePreferences mainSecurePreferences = new SecurePreferences(mContext);
            if (result != null) {
                boolean serverSynced = !result.getInconsistent().booleanValue();
                Log.d("TAG", "DeviceSyncTask onPostExecute synced" + serverSynced);
                if (serverSynced) {
                    mainSecurePreferences.edit().putBoolean("DeviceSynced", true).commit();
                } else {
                    mainSecurePreferences.edit().putBoolean("DeviceSynced", false).commit();
                }
            } else {
                mainSecurePreferences.edit().putBoolean("DeviceSynced", false).commit();
            }
        }catch(Exception e){}
    }
}
