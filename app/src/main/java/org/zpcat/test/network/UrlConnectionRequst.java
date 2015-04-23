package org.zpcat.test.network;

import org.zpcat.test.TLSApplicaton;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by moses on 4/23/15.
 */
public class UrlConnectionRequst implements AsyncNetRequest {

    private final String TAG = "TLSdemo";

    private ReqCallback mReqCallback;
    private String mUrl;
    private SSLSocketFactory mSSLSocketFactory;

    @Override
    public void request(String url, SSLSocketFactory sslSocketFactory,
            HashMap<String, String> params, ReqCallback callback) {
        mReqCallback = callback;
        mUrl = url;
        Log.e(TAG, "https: " + mUrl);
        mSSLSocketFactory = sslSocketFactory;
        if (mSSLSocketFactory == null) {
            Log.e(TAG, "sslsocketfactory is null");
        }

        new NetAsyncTask().execute(mUrl);
    }

    private class NetAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                char[] buffer = new char[1028];

                URL url = new URL(mUrl);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                //httpsConn.setSSLSocketFactory(TLSApplicaton.getInstance().getDefaultSSLSocketFactory());
                httpConn.setReadTimeout(10000);
                httpConn.setConnectTimeout(15000);
                httpConn.setRequestMethod("GET");
                httpConn.setDoInput(true);
                if ("https".equalsIgnoreCase(url.getProtocol()) && mSSLSocketFactory != null) {
                    Log.e(TAG, "Set sslSocketFactory.");
                    ((HttpsURLConnection) httpConn).setSSLSocketFactory(mSSLSocketFactory);
                    ((HttpsURLConnection) httpConn).setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
                }

                httpConn.connect();

                int response = httpConn.getResponseCode();
                Log.e(TAG, "https response code: " + response);
                InputStream in = httpConn.getInputStream();

                Reader reader = null;
                reader = new InputStreamReader(in, "UTF-8");
                reader.read(buffer);

                return new String(buffer);

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
}
