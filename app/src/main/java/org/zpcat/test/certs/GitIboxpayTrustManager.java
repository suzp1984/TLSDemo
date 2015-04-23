package org.zpcat.test.certs;

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

import javax.net.ssl.X509TrustManager;

/**
 * Created by moses on 4/23/15.
 */
public class GitIboxpayTrustManager implements X509TrustManager {

    private final String TAG = "TLSdemo";
    private X509Certificate mCert;

    private final String mPemCert = "-----BEGIN CERTIFICATE-----\n"
            + "MIID/zCCAuegAwIBAgIJAI87+17ijWe6MA0GCSqGSIb3DQEBBQUAMIGVMQswCQYD\n"
            + "VQQGEwJDTjESMBAGA1UECAwJR3VhbmdEb25nMREwDwYDVQQHDAhTaGVuemhlbjEQ\n"
            + "MA4GA1UECgwHaWJveHBheTEQMA4GA1UECwwHaWJveHBheTEYMBYGA1UEAwwPZ2l0\n"
            + "Lmlib3hwYXkuY29tMSEwHwYJKoZIhvcNAQkBFhJHaXRMYWJAaWJveHBheS5jb20w\n"
            + "HhcNMTQxMTA4MTYwMzQyWhcNMjQwODA3MTYwMzQyWjCBlTELMAkGA1UEBhMCQ04x\n"
            + "EjAQBgNVBAgMCUd1YW5nRG9uZzERMA8GA1UEBwwIU2hlbnpoZW4xEDAOBgNVBAoM\n"
            + "B2lib3hwYXkxEDAOBgNVBAsMB2lib3hwYXkxGDAWBgNVBAMMD2dpdC5pYm94cGF5\n"
            + "LmNvbTEhMB8GCSqGSIb3DQEJARYSR2l0TGFiQGlib3hwYXkuY29tMIIBIjANBgkq\n"
            + "hkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvFVGEyr4CvNWbr6z8ZrfEMYTVHZwQGHB\n"
            + "PIFmRkxznxmoNE+b1z5zgHTA2rYoJbwlaMg2X77jEt9xPrtaCFtXxoW4zgNcTLjM\n"
            + "S5uSNVwkkvwL3+w3xQaburTK934xR86EOgQs/iZYTyiRhywkzauPaJbNFG2pBwgE\n"
            + "GgDBWwUZhtz2go7H8FEYKFUFRgA0xUXLNXE66yJicFX3jDPO7MHA1+25Si4rksGe\n"
            + "HMUg6/TAp/VW/yKnKaReZjs0EA7S0R3tDAWl8eop/k5RBpCPIDTWcg97pvc3ZYDA\n"
            + "DWPqJehy260S0YpvrvjHqS/qK9XcltKuCRG4f15CUC1VUIlwccfHzQIDAQABo1Aw\n"
            + "TjAdBgNVHQ4EFgQUgo1hZ8wB4M+4AQ8UZh8blZxOESAwHwYDVR0jBBgwFoAUgo1h\n"
            + "Z8wB4M+4AQ8UZh8blZxOESAwDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOC\n"
            + "AQEAWbKMIpl6hya82YAXYdcGU1coONuja6CwYKcpU/Q2ngQ5J1JEDZFFGM4VGwBb\n"
            + "tBL9o+LsDIBVAGGHSsrUhBlh4Rc7joBJmCfwb9Dkx3YKwUvZbq40QBiayBdVK5BQ\n"
            + "wlNdmdLDAAQMJj3mXRKGivzfTaKfpw5xv3vSWRU7JNQAAZBTUftWstNilRuRYq71\n"
            + "PUTfMo7k6WthGtl36SnQyYj8h8NT9C1fIaqiiEcQBmWxHT5gTO5OkP95jJnvU6aB\n"
            + "SLrk7FfaVbWUU13Iofx0NfoN1gp2jMLHdsLE3nx+5vjV7XCgADVbGL8Bhn7j+G/o\n"
            + "4TyN9ykMa4Nde6nxTT9o8dWw1A==\n"
            + "-----END CERTIFICATE-----";

    public GitIboxpayTrustManager() {
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
        Log.e(TAG, "check server trusted: auth type - " + authType);
        boolean ok = false;

        for(X509Certificate cert : chain) {
            Log.e(TAG, "sigAlgName: " + cert.getSigAlgName() + "; SigAlgOID: " + cert.getSigAlgOID());
            try {
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
