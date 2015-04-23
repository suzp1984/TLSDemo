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
 * Created by jacobsu on 4/22/15.
 */
public class IboxpayTrustManager implements X509TrustManager {

    private final String TAG = "TLSdemo";

    private X509Certificate mCert;
    private String mPEMCert = "-----BEGIN CERTIFICATE-----\n" +
            "MIIGBTCCBO2gAwIBAgIQbbfmex6qGP/VG1KkUU2xATANBgkqhkiG9w0BAQUFADCB\n" +
            "ujELMAkGA1UEBhMCVVMxFzAVBgNVBAoTDlZlcmlTaWduLCBJbmMuMR8wHQYDVQQL\n" +
            "ExZWZXJpU2lnbiBUcnVzdCBOZXR3b3JrMTswOQYDVQQLEzJUZXJtcyBvZiB1c2Ug\n" +
            "YXQgaHR0cHM6Ly93d3cudmVyaXNpZ24uY29tL3JwYSAoYykwNjE0MDIGA1UEAxMr\n" +
            "VmVyaVNpZ24gQ2xhc3MgMyBFeHRlbmRlZCBWYWxpZGF0aW9uIFNTTCBDQTAeFw0x\n" +
            "NDA0MjEwMDAwMDBaFw0xNTA2MjAyMzU5NTlaMIIBQjETMBEGCysGAQQBgjc8AgED\n" +
            "EwJDTjEaMBgGCysGAQQBgjc8AgECFAlHdWFuZ2RvbmcxGTAXBgsrBgEEAYI3PAIB\n" +
            "ARQIU2hlbnpoZW4xHTAbBgNVBA8TFFByaXZhdGUgT3JnYW5pemF0aW9uMRgwFgYD\n" +
            "VQQFEw80NDAzMDExMDU1NzczODgxCzAJBgNVBAYTAkNOMRIwEAYDVQQIFAlHdWFu\n" +
            "Z0RvbmcxETAPBgNVBAcUCFNoZW5aaGVuMTgwNgYDVQQKFC9TaGVuIFpoZW4gaUJP\n" +
            "WFBBWSBJbmZvcm1hdGlvbiBUZWNobm9sb2d5IENvLmx0ZDEzMDEGA1UECxQqVGVy\n" +
            "bXMgb2YgdXNlIGF0IHd3dy52ZXJpc2lnbi5jb20vcnBhIChjKTA1MRgwFgYDVQQD\n" +
            "FA93d3cuaWJveHBheS5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB\n" +
            "AQCrDaemPC8ylGOqaKtmdP0ntUq3YgEqWwWVdaSo190A9taXIrWA4fYhRZIp92wC\n" +
            "9vDn2HQL0pTWqE/2E4EGF1iL+SF/wr8vCEQp/GNaPULJ/s7cfGGh4nhgrH8PKOAL\n" +
            "iVZyy45PwaB0etjhDoqLwGzVXMVY8IMxv1pRmiMJYcSylLTTm7qyirPD33R+D1D7\n" +
            "SKlRwjfpAVpIBN7NiQHo6MN2rVVTRcMSNDGpW7RGUbD1QtAfC0YpatQM+irBRPZM\n" +
            "mHpw1/VSn0bR8GtGtfeSQ64mv2QzpY6zQpYhFUdvKELpZG8mFFeDbtFrrxm823pO\n" +
            "4/SHhfS049yoYygV6mMv9yU3AgMBAAGjggF6MIIBdjAaBgNVHREEEzARgg93d3cu\n" +
            "aWJveHBheS5jb20wCQYDVR0TBAIwADAOBgNVHQ8BAf8EBAMCBaAwQgYDVR0fBDsw\n" +
            "OTA3oDWgM4YxaHR0cDovL0VWU2VjdXJlLWNybC52ZXJpc2lnbi5jb20vRVZTZWN1\n" +
            "cmUyMDA2LmNybDBEBgNVHSAEPTA7MDkGC2CGSAGG+EUBBxcGMCowKAYIKwYBBQUH\n" +
            "AgEWHGh0dHBzOi8vd3d3LnZlcmlzaWduLmNvbS9jcHMwHQYDVR0lBBYwFAYIKwYB\n" +
            "BQUHAwEGCCsGAQUFBwMCMB8GA1UdIwQYMBaAFPyKULqeuSVae1WFT5UAY4/pWGtD\n" +
            "MHMGCCsGAQUFBwEBBGcwZTAkBggrBgEFBQcwAYYYaHR0cDovL29jc3AudmVyaXNp\n" +
            "Z24uY29tMD0GCCsGAQUFBzAChjFodHRwOi8vRVZTZWN1cmUtYWlhLnZlcmlzaWdu\n" +
            "LmNvbS9FVlNlY3VyZTIwMDYuY2VyMA0GCSqGSIb3DQEBBQUAA4IBAQBf7ka0itD9\n" +
            "qHvU2vSa3/Q6TsWAu3xa7VsjPQj+qHXfVQNfb5Ft271Mwr5Vkse+J8c0FIjuQXbH\n" +
            "aJ+7hZTcxY7S1F2NyA8ab+HmDlH02HDxxNpaBXiE05fEekVMRUItOFOkOAX1Mpm+\n" +
            "YRJXGXapwZcVo2mCjgGcyOXqw+5faMRNcHTLnjPJhIceJrpoQomE2c0B/Eox+QCz\n" +
            "o3OHghtdBZd9n7TDyYn6xmi9LS2gicTKX5yKMMnpw+fQQMDP7KjdNRwBoy3aUOWL\n" +
            "wqI0Jryq3ph6a4vLgBPoto4ZZYA+woQ+qnrBnJYJbqDjahJySLiWyDTbszQNMVIR\n" +
            "rPDw2HHTe8/I\n" +
            "-----END CERTIFICATE-----";

    public IboxpayTrustManager() {
        CertificateFactory cf = null;
        InputStream input =
                new ByteArrayInputStream(mPEMCert.getBytes(StandardCharsets.UTF_8));

        try {
            cf = CertificateFactory.getInstance("X509");
            mCert = (X509Certificate) cf.generateCertificate(input);
            // Log.e(TAG, mCert.toString());

        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
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
