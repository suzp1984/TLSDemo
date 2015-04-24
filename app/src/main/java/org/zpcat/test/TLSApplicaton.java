/**
 * Created by moses on 4/23/15.
 */
package org.zpcat.test;

import org.zpcat.test.certs.CustomTrustManagerFactory;

import android.app.Application;
import android.util.Log;

import java.io.InputStream;
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
    private final String mGitlabIboxpayPemCert = "-----BEGIN CERTIFICATE-----\n"
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

    private String mIboxpayPEMCert = "-----BEGIN CERTIFICATE-----\n" +
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

    private String mVeriSignPemCert = "-----BEGIN CERTIFICATE-----\n"
            + "MIIF5DCCBMygAwIBAgIQW3dZxheE4V7HJ8AylSkoazANBgkqhkiG9w0BAQUFADCB\n"
            + "yjELMAkGA1UEBhMCVVMxFzAVBgNVBAoTDlZlcmlTaWduLCBJbmMuMR8wHQYDVQQL\n"
            + "ExZWZXJpU2lnbiBUcnVzdCBOZXR3b3JrMTowOAYDVQQLEzEoYykgMjAwNiBWZXJp\n"
            + "U2lnbiwgSW5jLiAtIEZvciBhdXRob3JpemVkIHVzZSBvbmx5MUUwQwYDVQQDEzxW\n"
            + "ZXJpU2lnbiBDbGFzcyAzIFB1YmxpYyBQcmltYXJ5IENlcnRpZmljYXRpb24gQXV0\n"
            + "aG9yaXR5IC0gRzUwHhcNMDYxMTA4MDAwMDAwWhcNMTYxMTA3MjM1OTU5WjCBujEL\n"
            + "MAkGA1UEBhMCVVMxFzAVBgNVBAoTDlZlcmlTaWduLCBJbmMuMR8wHQYDVQQLExZW\n"
            + "ZXJpU2lnbiBUcnVzdCBOZXR3b3JrMTswOQYDVQQLEzJUZXJtcyBvZiB1c2UgYXQg\n"
            + "aHR0cHM6Ly93d3cudmVyaXNpZ24uY29tL3JwYSAoYykwNjE0MDIGA1UEAxMrVmVy\n"
            + "aVNpZ24gQ2xhc3MgMyBFeHRlbmRlZCBWYWxpZGF0aW9uIFNTTCBDQTCCASIwDQYJ\n"
            + "KoZIhvcNAQEBBQADggEPADCCAQoCggEBAJjboFXrnP0XeeOabhQdsVuYI4cWbod2\n"
            + "nLU4O7WgerQHYwkZ5iqISKnnnbYwWgiXDOyq5BZpcmIjmvt6VCiYxQwtt9citsj5\n"
            + "OBfH3doxRpqUFI6e7nigtyLUSVSXTeV0W5K87Gws3+fBthsaVWtmCAN/Ra+aM/EQ\n"
            + "wGyZSpIkMQht3QI+YXZ4eLbtfjeubPOJ4bfh3BXMt1afgKCxBX9ONxX/ty8ejwY4\n"
            + "P1C3aSijtWZfNhpSSENmUt+ikk/TGGC+4+peGXEFv54cbGhyJW+ze3PJbb0S/5tB\n"
            + "Ml706H7FC6NMZNFOvCYIZfsZl1h44TO/7Wg+sSdFb8Di7Jdp91zT91ECAwEAAaOC\n"
            + "AdIwggHOMB0GA1UdDgQWBBT8ilC6nrklWntVhU+VAGOP6VhrQzASBgNVHRMBAf8E\n"
            + "CDAGAQH/AgEAMD0GA1UdIAQ2MDQwMgYEVR0gADAqMCgGCCsGAQUFBwIBFhxodHRw\n"
            + "czovL3d3dy52ZXJpc2lnbi5jb20vY3BzMD0GA1UdHwQ2MDQwMqAwoC6GLGh0dHA6\n"
            + "Ly9FVlNlY3VyZS1jcmwudmVyaXNpZ24uY29tL3BjYTMtZzUuY3JsMA4GA1UdDwEB\n"
            + "/wQEAwIBBjARBglghkgBhvhCAQEEBAMCAQYwbQYIKwYBBQUHAQwEYTBfoV2gWzBZ\n"
            + "MFcwVRYJaW1hZ2UvZ2lmMCEwHzAHBgUrDgMCGgQUj+XTGoasjY5rw8+AatRIGCx7\n"
            + "GS4wJRYjaHR0cDovL2xvZ28udmVyaXNpZ24uY29tL3ZzbG9nby5naWYwKQYDVR0R\n"
            + "BCIwIKQeMBwxGjAYBgNVBAMTEUNsYXNzM0NBMjA0OC0xLTQ3MD0GCCsGAQUFBwEB\n"
            + "BDEwLzAtBggrBgEFBQcwAYYhaHR0cDovL0VWU2VjdXJlLW9jc3AudmVyaXNpZ24u\n"
            + "Y29tMB8GA1UdIwQYMBaAFH/TZafC3ey78DAJ80M5+gKvMzEzMA0GCSqGSIb3DQEB\n"
            + "BQUAA4IBAQCWovp/5j3t1CvOtxU/wHIDX4u6FpAl98KD2Md1NGNoElMMU4l7yVYJ\n"
            + "p8M2RE4O0GJis4b66KGbNGeNUyIXPv2s7mcuQ+JdfzOE8qJwwG6Cl8A0/SXGI3/t\n"
            + "5rDFV0OEst4t8dD2SB8UcVeyrDHhlyQjyRNddOVG7wl8nuGZMQoIeRuPcZ8XZsg4\n"
            + "z+6Ml7YGuXNG5NOUweVgtSV1LdlpMezNlsOjdv3odESsErlNv1HoudRETifLriDR\n"
            + "fip8tmNHnna6l9AW5wtsbfdDbzMLKTB3+p359U64drPNGLT5IO892+bKrZvQTtKH\n"
            + "qQ2mRHNQ3XBb7a1+Srwi1agm5MKFIA3Z\n"
            + "-----END CERTIFICATE-----";

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
        TrustManager personalTrustManager =
                CustomTrustManagerFactory.getTrustManagerFromPEM(mIboxpayPEMCert);

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
        TrustManager veriSignTrustManager =
                CustomTrustManagerFactory.getTrustManagerFromPEM(mVeriSignPemCert);
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
        TrustManager gitIboxpayTrustManager =
                CustomTrustManagerFactory.getTrustManagerFromPEM(mGitlabIboxpayPemCert);
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

    public SSLSocketFactory getGitlabIboxpayBKSSocketFactory(InputStream input, String passwd) {
        TrustManager gitlabTrustManager =
                CustomTrustManagerFactory.getTrustManagerFromKeyStore(input, passwd);
        TrustManager[] tms = new TrustManager[1];
        tms[0] = gitlabTrustManager;

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
