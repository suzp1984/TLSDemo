package org.zpcat.test.network;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
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

    private class NetAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();

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
}
