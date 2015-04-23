package org.zpcat.test;

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


public class MainActivity extends Activity implements View.OnClickListener {

    private final String TAG = "TLSdemo";
    private final String IBOXPAY_LOGIN = "https://www.iboxpay.com/cashbox/login.htm";
    private final String SELF_SIGN_OWN_CLOUD = "https://172.30.60.165/owncloud/";

    private Spinner mUrlsSpinner;
    private ArrayAdapter<CharSequence> mUrlAdapater;
    private String mUrl;

    private Button mIboxpayUrlBtn;
    private Button mSelfSignedBtn;
    private Button mUrlConnectinSelfSign;
    private Button mUrlConnectionVeriSign;
    private Button mOwnCloudIboxpaySign;

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
                        CharSequence chars = mUrlAdapater.getItem(position);
                        mUrl = chars.toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        mIboxpayUrlBtn = (Button) findViewById(R.id.btn_iboxpay_urlcon);
        mIboxpayUrlBtn.setOnClickListener(this);

        mSelfSignedBtn = (Button) findViewById(R.id.btn_selfsigned);
        mSelfSignedBtn.setOnClickListener(this);

        mUrlConnectinSelfSign = (Button) findViewById(R.id.btn_urlconnection_personalsign);
        mUrlConnectinSelfSign.setOnClickListener(this);

        mUrlConnectionVeriSign = (Button) findViewById(R.id.btn_iboxpay_verisign);
        mUrlConnectionVeriSign.setOnClickListener(this);

        mOwnCloudIboxpaySign = (Button) findViewById(R.id.btn_owncloud_with_iboxpay_cert);
        mOwnCloudIboxpaySign.setOnClickListener(this);
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
        switch (v.getId()) {
            case R.id.btn_iboxpay_urlcon:
                new UrlConnectionRequst().request(IBOXPAY_LOGIN,
                        ((TLSApplicaton)getApplication()).getDefaultSSLSocketFactory(),
                        null, mUrlConnectionCb);
                break;
            case R.id.btn_selfsigned:
                new UrlConnectionRequst().request(SELF_SIGN_OWN_CLOUD,
                        ((TLSApplicaton)getApplication()).getDefaultSSLSocketFactory(),
                        null, mUrlConnectionCb);
                break;
            case R.id.btn_urlconnection_personalsign:
                new UrlConnectionRequst().request(IBOXPAY_LOGIN,
                        ((TLSApplicaton)getApplication()).getIboxpaySSLSocketFactory(),
                        null, mUrlConnectionCb);
                break;
            case R.id.btn_iboxpay_verisign:
                new UrlConnectionRequst().request(IBOXPAY_LOGIN,
                        ((TLSApplicaton)getApplication()).getIssuerSSLSocketFactory(),
                        null, mUrlConnectionCb);
                break;
            case R.id.btn_owncloud_with_iboxpay_cert:
                new UrlConnectionRequst().request(SELF_SIGN_OWN_CLOUD,
                        ((TLSApplicaton)getApplication()).getIboxpaySSLSocketFactory(),
                        null, mUrlConnectionCb);
                break;
            default:
                break;
        }
    }
}
