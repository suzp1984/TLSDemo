package org.zpcat.test.certs;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by moses on 4/24/15.
 */
public class CustomSSLSocketFactory {

    public SSLSocketFactory getSSLSocketFactoryFromPEM(String... PEMS) {
        ArrayList<TrustManager> tmsList = new ArrayList<>();

        for (String pem : PEMS) {
            TrustManager trustManager = CustomTrustManagerFactory.getTrustManagerFromPEM(pem);
            if (trustManager != null) {
                tmsList.add(trustManager);
            }
        }

        TrustManager[] tms = tmsList.toArray(new TrustManager[tmsList.size()]);
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

    public SSLSocketFactory getSSLSocketFactoryWithKeyManagerFromPem(String[] clientPems,
            String[] privateKey, String[] PEMS) {
        ArrayList<TrustManager> tmsList = new ArrayList<>();

        for (String pem : PEMS) {
            TrustManager trustManager = CustomTrustManagerFactory.getTrustManagerFromPEM(pem);
            if (trustManager != null) {
                tmsList.add(trustManager);
            }
        }

        TrustManager[] tms = tmsList.toArray(new TrustManager[tmsList.size()]);
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

    public SSLSocketFactory getSSLSocketFactoryFromBKSKeyStore(InputStream input, String passwd) {
        TrustManager[] tms = fetchTrustManager(input, passwd);
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

    public SSLSocketFactory getSSLSocketFactoryFromTrustManager(TrustManager[] tms) {

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

    private TrustManager[] fetchTrustManager(InputStream in, String passwd) {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("BKS");
            keyStore.load(in, passwd.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(keyStore);

            return tmf.getTrustManagers();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
