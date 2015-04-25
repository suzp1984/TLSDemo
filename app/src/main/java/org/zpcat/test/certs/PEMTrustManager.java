package org.zpcat.test.certs;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by moses on 4/24/15.
 */
public class PEMTrustManager implements X509TrustManager {

    private final String TAG = "TLSdemo";
    private X509Certificate mCert;
    private final String mPemCert;

    public PEMTrustManager(String pem) {
        mPemCert = pem;

        CertificateFactory cf = null;
        InputStream input =
                new ByteArrayInputStream(mPemCert.getBytes(StandardCharsets.UTF_8));

        try {
            cf = CertificateFactory.getInstance("X509");
            mCert = (X509Certificate) cf.generateCertificate(input);
            // Log.e(TAG, mCert.toString());

        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {

        boolean ok = false;

        for(X509Certificate cert : chain) {
            Log.e(TAG,
                    "sigAlgName: " + cert.getSigAlgName()
                            + "; SigAlgOID: " + cert.getSigAlgOID());
            try {
                Log.e(TAG, "public key algorithm: " + mCert.getPublicKey().getAlgorithm()
                            + "; form: " + mCert.getPublicKey().getFormat()
                            + "; key: " + mCert.getPublicKey().toString());
                Log.e(TAG, "public key base64: " + Base64.encodeToString(
                        mCert.getPublicKey().getEncoded(), Base64.DEFAULT));

                cert.verify(mCert.getPublicKey());
                ok = true;
                break;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (SignatureException e) {
                e.printStackTrace();
            }
        }

        if (!ok) {
            throw new CertificateException();
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] certs = new X509Certificate[1];
        certs[0] = mCert;
        return certs;
    }
}
