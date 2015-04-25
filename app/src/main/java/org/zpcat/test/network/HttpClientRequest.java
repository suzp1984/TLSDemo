package org.zpcat.test.network;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by moses on 4/23/15.
 */
public class HttpClientRequest implements AsyncNetRequest {

    private final String TAG = "TLSdemo";

    private ReqCallback mReqCallback;
    private String mUrl;
    private Map<String, String> mParms;
    private SSLSocketFactory mSSLSocketFactory;

    private KeyStore mKeyStore;
    private String mKeyStorePasswd;

    @Override
    public void request(String url,
                        SSLSocketFactory sslSocketFactory,
                        HashMap<String, String> params,
                        ReqCallback callback) {
        mReqCallback = callback;
        mUrl = url;
        mParms = params;
        mSSLSocketFactory = sslSocketFactory;

        new NetAsyncTask().execute();
    }

    public void request(String url, Map<String, String> params, ReqCallback callback,
                        KeyStore keystore, String passwd) {

        mReqCallback = callback;
        mUrl = url;
        mParms = params;
        mKeyStore = keystore;
        mKeyStorePasswd = passwd;

        new NetAsyncTask().execute();
    }

    private class NetAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            AndroidHttpClient httpClient = AndroidHttpClient.newInstance("android");
            if (mKeyStore != null) {
                Scheme https = null;
                try {
                    https = new Scheme("https", new AdapterSSLSocketFactory(mKeyStore,
                            mKeyStorePasswd), 443);
                    Log.e(TAG, "initialize https scheme");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (UnrecoverableKeyException e) {
                    e.printStackTrace();
                }

               /* List<String> schemes =
                        httpClient.getConnectionManager().getSchemeRegistry().getSchemeNames();
                for (String name : schemes) {
                    httpClient.getConnectionManager().getSchemeRegistry().unregister(name);
                }*/

                httpClient.getConnectionManager().getSchemeRegistry().register(https);
            }

            HttpGet httpGet = new HttpGet(mUrl);
            HttpResponse response;

            try {
                response = httpClient.execute(httpGet);
                Log.e(TAG, "reponse statusline: " + response.getStatusLine().toString());
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    InputStream in = entity.getContent();
                    String result = convertStreamToString(in);
                    in.close();

                    return result;
                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String ret) {
            super.onPostExecute(ret);

            if (mReqCallback != null) {
                mReqCallback.onResult(ret);
            }
        }
    }


    private String convertStreamToString(InputStream input) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    // direct implementation of apache's SSLSocketFactory

    class AdapterSSLSocketFactory extends org.apache.http.conn.ssl.SSLSocketFactory {


        public AdapterSSLSocketFactory(KeyStore keystore, String keystorePassword)
                throws NoSuchAlgorithmException, KeyManagementException,
                        KeyStoreException, UnrecoverableKeyException {
            super(keystore, keystorePassword);
        }
    }
}
