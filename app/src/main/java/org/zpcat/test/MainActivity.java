package org.zpcat.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


public class MainActivity extends Activity {

    private final String TAG = "SSLTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            Log.e(TAG, "TrustManagerFacotry default algorithm: " + tmfAlgorithm);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init((KeyStore) null);
            TrustManager[] tms = tmf.getTrustManagers();

            for (TrustManager tm : tms) {
                if (tm instanceof X509TrustManager) {
                    X509Certificate[] certs = ((X509TrustManager) tm).getAcceptedIssuers();

                    /*for (X509Certificate cert : certs) {
                        Log.e(TAG, "--------------");
                        Log.e(TAG, cert.toString());
                        Log.e(TAG, "--------------");
                    }*/
                }
            }

            String kmfAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
            Log.e(TAG, "KeyManagerFactory default algorithm: " + kmfAlgorithm);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(kmfAlgorithm);
            kmf.init(null, null);
            KeyManager[] kms = kmf.getKeyManagers();

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tms, null);

            Log.e(TAG, "Keystore algorithm: " + KeyStore.getDefaultType());

            SSLSocketFactory ssf = sslContext.getSocketFactory();
            PersonalTrustManager personal = new PersonalTrustManager();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
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
}
