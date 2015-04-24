/**
 * Created by moses on 4/23/15.
 */
package org.zpcat.test;

import android.app.Application;
import android.util.Log;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class TLSApplicaton extends Application {

    private final String TAG = "TLSdemo";

    private SSLSocketFactory mDefaultSSF;

    private static TLSApplicaton sApplication;

    public static TLSApplicaton getInstance() {
        return sApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

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

            mDefaultSSF = sslContext.getSocketFactory();
            if (mDefaultSSF == null) {
                Log.e(TAG, "SSLContext getSocketFactory is null");
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        sApplication = this;
    }

    public SSLSocketFactory getDefaultSSLSocketFactory() {
        return mDefaultSSF;
    }
}
