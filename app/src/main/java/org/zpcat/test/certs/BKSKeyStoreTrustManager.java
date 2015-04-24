package org.zpcat.test.certs;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by moses on 4/24/15.
 */
public class BKSKeyStoreTrustManager implements X509TrustManager {

    X509TrustManager mX509TrustManagerImp;

    public BKSKeyStoreTrustManager(InputStream input, String passwd) {
        mX509TrustManagerImp = fetchTrustManager(input, passwd);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        if (mX509TrustManagerImp != null) {
            mX509TrustManagerImp.checkClientTrusted(chain, authType);
        } else {
            throw new CertificateException();
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        if (mX509TrustManagerImp != null) {
            mX509TrustManagerImp.checkServerTrusted(chain, authType);
        } else {
            throw new CertificateException();
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        if (mX509TrustManagerImp != null) {
            return mX509TrustManagerImp.getAcceptedIssuers();
        } else {
            return null;
        }
    }

    private X509TrustManager fetchTrustManager(InputStream in, String passwd) {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("BKS");
            keyStore.load(in, passwd.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(keyStore);

            TrustManager[] tms = tmf.getTrustManagers();

            for (TrustManager tm : tms) {
                if (tm instanceof X509TrustManager) {
                    return (X509TrustManager) tm;
                }
            }
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
