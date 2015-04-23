/**
 * Created by moses on 4/23/15.
 */
package org.zpcat.test;

import org.zpcat.test.certs.GitIboxpayTrustManager;
import org.zpcat.test.certs.IboxpayTrustManager;
import org.zpcat.test.certs.VeriSignTrustManager;

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
        if (sApplication == null) {
            sApplication = new TLSApplicaton();
        }

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
            // sslContext.init(null, null, null);

            Log.e(TAG, "Keystore algorithm: " + KeyStore.getDefaultType());

            mDefaultSSF = sslContext.getSocketFactory();
            if (mDefaultSSF == null) {
                Log.e(TAG, "SSLContext getSocketFactory is null");
            }
            // PersonalTrustManager personal = new PersonalTrustManager();

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

    public SSLSocketFactory getIboxpaySSLSocketFactory() {
        IboxpayTrustManager personalTrustManager = new IboxpayTrustManager();
        TrustManager[] tms = new TrustManager[1];
        tms[0] = personalTrustManager;

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tms, null);
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }

    public SSLSocketFactory getVeriSignSSLSocketFactory() {
        VeriSignTrustManager veriSignTrustManager = new VeriSignTrustManager();
        TrustManager[] tms = new TrustManager[1];
        tms[0] = veriSignTrustManager;

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tms, null);
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }

    public SSLSocketFactory getGitIboxpaySSLSocketFactory() {
        GitIboxpayTrustManager gitIboxpayTrustManager = new GitIboxpayTrustManager();
        TrustManager[] tms = new TrustManager[1];
        tms[0] = gitIboxpayTrustManager;

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tms, null);
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }
}
