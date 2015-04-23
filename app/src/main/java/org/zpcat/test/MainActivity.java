package org.zpcat.test;

import org.zpcat.test.network.HttpClientRequest;
import org.zpcat.test.network.ReqCallback;
import org.zpcat.test.network.UrlConnectionRequst;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends Activity implements View.OnClickListener {

    private final String TAG = "TLSdemo";
    private final String IBOXPAY_LOGIN = "https://www.iboxpay.com/cashbox/login.htm";
    private final String SELF_SIGN_OWN_CLOUD = "https://172.30.60.165/owncloud/";

    private Spinner mUrlsSpinner;
    private ArrayAdapter<CharSequence> mUrlAdapater;
    private String mUrl;

    private Spinner mCertsSpinner;
    private ArrayAdapter<CharSequence> mCertsAdapater;
    private String mCert;

    private Button mUrlConnectionBtn;
    private Button mHttpClientBtn;

    private ReqCallback mUrlConnectionCb = new ReqCallback() {
        @Override
        public void onResult(String result) {
            Log.e(TAG, "-----------");
            if (result != null) {
                Log.e(TAG, result);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUrlsSpinner = (Spinner) findViewById(R.id.sp_urls);
        mUrlAdapater = ArrayAdapter.createFromResource(this,
                R.array.urls, android.R.layout.simple_spinner_item);
        mUrlAdapater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUrlsSpinner.setAdapter(mUrlAdapater);
        mUrlsSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position,
                            long id) {
                        /*CharSequence chars = mUrlAdapater.getItem(position);
                        mUrl = chars.toString();*/
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        mCertsSpinner = (Spinner) findViewById(R.id.sp_certs);
        mCertsAdapater = ArrayAdapter.createFromResource(this,
                R.array.certs, android.R.layout.simple_spinner_item);
        mCertsAdapater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCertsSpinner.setAdapter(mCertsAdapater);
        mCertsSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position,
                            long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        CharSequence selectedItem = (CharSequence) mUrlsSpinner.getSelectedItem();
        Log.e(TAG, selectedItem.toString());

        mUrlConnectionBtn = (Button) findViewById(R.id.btn_url_connection);
        mHttpClientBtn = (Button) findViewById(R.id.btn_http_client);

        mUrlConnectionBtn.setOnClickListener(this);
        mHttpClientBtn.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        SSLSocketFactory sslSocketFactory = getCurrentSSLSocketFactory();
        String url = ((CharSequence) mUrlsSpinner.getSelectedItem()).toString();

        switch (v.getId()) {
            case R.id.btn_url_connection:
                new UrlConnectionRequst().request(url, sslSocketFactory,
                        null, mUrlConnectionCb);
                break;
            case R.id.btn_http_client:
                new HttpClientRequest().request(url, sslSocketFactory,
                        null, mUrlConnectionCb);
                break;
            default:
                break;
        }
    }

    private SSLSocketFactory getCurrentSSLSocketFactory() {
        int i = mCertsSpinner.getSelectedItemPosition();
        SSLSocketFactory sslSocketFactory;
        switch (i) {
            case 0:
                sslSocketFactory = null;
                Log.e(TAG, "0");
                break;
            case 1:
                sslSocketFactory = ((TLSApplicaton)getApplication())
                        .getDefaultSSLSocketFactory();
                Log.e(TAG, "1");
                break;
            case 2:
                sslSocketFactory = ((TLSApplicaton)getApplication())
                        .getIboxpaySSLSocketFactory();
                Log.e(TAG, "2");
                break;
            case 3:
                sslSocketFactory = ((TLSApplicaton)getApplication())
                        .getVeriSignSSLSocketFactory();
                Log.e(TAG, "3");
                break;
            case 4:
                sslSocketFactory = ((TLSApplicaton)getApplication())
                        .getGitIboxpaySSLSocketFactory();
                Log.e(TAG, "4");
                break;
            default:
                sslSocketFactory = null;
                Log.e(TAG, "null");
                break;
        }

        return sslSocketFactory;
    }
}
