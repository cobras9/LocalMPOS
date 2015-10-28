package com.mobilis.android.nfc.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobilis.android.nfc.R;
import com.mobilis.android.nfc.aws.cognito.ExSSLSocketFactory;
import com.mobilis.android.nfc.domain.INTENT;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by lewischao on 21/09/15.
 */
public class PinSyncTask extends AsyncTask<Object, Void, PINModel> {
    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private String txlId;
    private int counter;

    private String getHTTPAuth() {
        String encodedUserPassword = "";
        try {
            String userPassword ="x-api-key"+ ":" +  mContext.getString(R.string.PIN_SYNC_X_API_KEY);
            encodedUserPassword = Base64.encodeToString(userPassword.getBytes(), Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "error getHTTPAuth " + e.toString());
        }
        return encodedUserPassword;
    }

    @Override
    protected PINModel doInBackground(Object... params) {
        mContext = (Context) params[0];
        txlId = (String) params[1];
        counter = (int)params[2];
        HttpsURLConnection conn = null;
        PINModel onePin = null;
        try {

            String finalUrl = mContext.getString(R.string.PIN_SYNC_SERVER_URL) + txlId;
            Log.d(TAG, "Request finalUrl " + finalUrl);
            Log.d(TAG, "Request txlId " + txlId);
            if(!"".equalsIgnoreCase(txlId)){


            URL url = new URL(finalUrl);
            conn = (HttpsURLConnection) url.openConnection();
             conn.setRequestProperty("x-api-key", mContext.getString(R.string.PIN_SYNC_X_API_KEY));
            InputStream serverResponse = conn.getInputStream();
            BufferedReader br=null;
            StringBuilder sb = new StringBuilder();
            String line="";
            br = new BufferedReader(new InputStreamReader(serverResponse));
            while((line=br.readLine()) != null){
                sb.append(line);

            }
            Log.d(TAG, "Server responded onePin getId" + sb.toString());
            serverResponse.close();
            conn.disconnect();
            if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                Log.d(TAG, "Server responded getResponseMessage" + conn.getResponseMessage());
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JavaType type = objectMapper.getTypeFactory().constructType(PINModel.class);
                onePin = objectMapper.readValue(sb.toString(), type);
                Log.d(TAG, "Server responded onePin getId" + onePin.getId());
                Log.d(TAG, "Server responded onePin getEntry" + onePin.getEntry());

            } else {
                Log.d(TAG, "Server responded with status code: " + conn.getResponseCode());
                Log.d(TAG, "Server responded with status getResponseMessage: " + conn.getResponseMessage());
            }
            }else{
                onePin = new PINModel();
                onePin.setEntry("-2");
            }
        } catch (Exception ex) {
            Log.e(TAG, "Failed to send HTTP request due to: " + ex);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        if(onePin !=null && !"".equalsIgnoreCase(onePin.getEntry())){
            return onePin;
        }else{
            return null;
        }

    }

/*
    @Override
    protected PINModel doInBackground(Object... params) {
        mContext = (Context) params[0];
        txlId = (String) params[1];
        counter = (int)params[2];
        PINModel onePin = null;
        try {
            String finalUrl = mContext.getString(R.string.PIN_SYNC_SERVER_URL) + txlId;
            Log.d(TAG, "Server responded onePin finalUrl " + finalUrl);
            Log.d(TAG, "Server responded onePin getId " + txlId);
            HttpClient client = HttpClients.createDefault();

            HttpGet method = new HttpGet(finalUrl);
            method.setHeader("x-api-key",mContext.getString(R.string.PIN_SYNC_X_API_KEY));
            HttpResponse resp = client.execute(method);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JavaType type = objectMapper.getTypeFactory().constructType(PINModel.class);
            onePin = objectMapper.readValue(resp.getEntity().getContent(), type);
            Log.d(TAG, "Server responded onePin getId" + onePin.getId());
            Log.d(TAG, "Server responded onePin getEntry" + onePin.getEntry());

        } catch (Exception e) {
            Log.e(TAG, "Failed to send HTTP request due to: " + e);
        }
        return onePin;
    }
*/



















    /*@Override
    protected PINModel doInBackground(Object... params) {
        mContext = (Context) params[0];
        txlId = (String) params[1];
        HttpURLConnection conn = null;
        PINModel onePin = null;
        try {
            ApiClientFactory factory = new ApiClientFactory();
            factory.apiKey(mContext.getString(R.string.PIN_SYNC_X_API_KEY));
            final LambdaPinMicroserviceClient client = factory.build(LambdaPinMicroserviceClient.class);
           // HttpClient httpclient = createHttpClient();
            String finalUrl = mContext.getString(R.string.PIN_SYNC_SERVER_URL) + txlId;
            onePin = client.getPin(onePin);
            Log.d(TAG, "Server responded onePin getId" + onePin.getId());
            Log.d(TAG, "Server responded onePin getEntry" + onePin.getEntry());
            //  HttpGet request = new HttpGet(finalUrl);
            //request.addHeader("x-api-key", mContext.getString(R.string.PIN_SYNC_X_API_KEY));
            //request.addHeader("Content-Type", "application/json; charset=UTF-8");
           // request.addHeader("Accept", "application/json");
           // HttpResponse response = httpclient.execute(request);
*//*            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Log.d(TAG, "Server responded getResponseMessage" + response.getEntity().getContent().toString());

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JavaType type = objectMapper.getTypeFactory().constructType(PINModel.class);
                onePin = objectMapper.readValue(response.getEntity().getContent(), type);
                Log.d(TAG, "Server responded onePin getId" + onePin.getId());
                Log.d(TAG, "Server responded onePin getEntry" + onePin.getEntry());
            } else {
                Log.d(TAG, "Server responded with status code: " + response.getStatusLine().getReasonPhrase());
            }*//*
        } catch (Exception e) {
            Log.e(TAG, "Failed to send HTTP request due to: " + e);
        }
        return onePin;
    }
*/
/*    public  HttpClient createHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(
                    ExSSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
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
    }*/
/*    public class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("sslv3");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            SSLSocket S = (SSLSocket) sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
            S.setEnabledProtocols(new String[]{"SSLv3"});
            return S;
        }

        @Override
        public Socket createSocket() throws IOException {
            SSLSocket S = (SSLSocket) sslContext.getSocketFactory().createSocket();
            S.setEnabledProtocols(new String[]{"SSLv3"});
            return S;
        }
    }*/
/*private getSSLContext()
{
    KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
    trustStore.load(null, null);

    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()); //optional
    tmf.init(keyStore); //optional

    //This is the important line, specifying the cipher to use and cipher provider
    SSLContext sslContext = SSLContext.getInstance("TLSv1","AndroidOpenSSL");
    ctx.init(null, tmf.getTrustManagers(), null); //if trustmanager not used pass null as the second parameter
    return sslContext;
}*/


    @Override
    protected void onPostExecute(final PINModel onePinModel) {
        super.onPostExecute(onePinModel);
        if (onePinModel != null) {
            Intent foundPinIntent = new Intent(INTENT.DEMO_WALLET_WAIT_FOR_PIN.toString());
            foundPinIntent.putExtra(INTENT.DEMO_WALLET_WAIT_FOR_PIN_VALUE.toString(), onePinModel.getEntry());
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(foundPinIntent);
        } else {
                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if(counter >10){
                                if (onePinModel != null) {

                                    Intent foundPinIntent = new Intent(INTENT.DEMO_WALLET_WAIT_FOR_PIN.toString());
                                    foundPinIntent.putExtra(INTENT.DEMO_WALLET_WAIT_FOR_PIN_VALUE.toString(), onePinModel.getEntry());
                                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(foundPinIntent);
                                }else{
                                    Intent foundPinIntent = new Intent(INTENT.DEMO_WALLET_WAIT_FOR_PIN.toString());
                                    foundPinIntent.putExtra(INTENT.DEMO_WALLET_WAIT_FOR_PIN_VALUE.toString(), "-1");
                                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(foundPinIntent);
                                }
                            }else {
                               //new PinSyncTask().execute(mContext, txlId, 20);
                                new PinSyncTask().execute(mContext, txlId, ++counter);
                            }
                        }
                    }
                };
                t.start();
/*            try {
                Thread.currentThread();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

        }

    }


    public static HttpClient createHttpClient() {
        try {

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new ExSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(
                    ExSSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
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
}
