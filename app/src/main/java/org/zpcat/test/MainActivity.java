package org.zpcat.test;

import org.zpcat.test.certs.CustomSSLSocketFactory;
import org.zpcat.test.network.HttpClientRequest;
import org.zpcat.test.network.ReqCallback;
import org.zpcat.test.network.UrlConnectionRequst;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends Activity implements View.OnClickListener {

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

    private final String mTurbinePEM = "-----BEGIN CERTIFICATE-----\n" +
            "MIICwDCCAaigAwIBAgIJAL61xCEOr3OFMA0GCSqGSIb3DQEBCwUAMBgxFjAUBgNV\n" +
            "BAMTDVR1cmJpbmVFbmdpbmUwHhcNMTUwNDA5MTQ1MjE2WhcNMjUwNDA2MTQ1MjE2\n" +
            "WjAYMRYwFAYDVQQDEw1UdXJiaW5lRW5naW5lMIIBIjANBgkqhkiG9w0BAQEFAAOC\n" +
            "AQ8AMIIBCgKCAQEAsIzmnTlvEVhKTEamNvOWS0f8bcx3I6se8V9QQRGkhw77b+OD\n" +
            "iU59/f+LCigO+wSujP89PADQdiaWmz+VlM5ijf2hx0wXwPjoU1IvcpLuEhYhKJk8\n" +
            "ejFMplkpfywjp5qOYzqUShuivkDhI2HsYBeeSta6VPsuySiu6alffynM0Oj3qKLM\n" +
            "+XPQF51rU3aEkxbWl8sDwDMJLLdPd0F2sQ0zJ/U6jvGyZPHt9mzAhed0coqqNGcw\n" +
            "jnClESfivKGEkycYCsvaceGLCnyEvwVwc+qv8TZpoEp2QxXDVf2K2hTkNg3AlNZK\n" +
            "CMzW/4d3lzPNQEzLxbP5fW7paLgvvX6yDh82YwIDAQABow0wCzAJBgNVHRMEAjAA\n" +
            "MA0GCSqGSIb3DQEBCwUAA4IBAQAb5PrbTYt+/lFYdrZX6S5ECPuxf8K1rJPkYm7P\n" +
            "4xvuA5a8PMrIFNBAnMmpur4Lm6X+BOWcSuBHIYU4QHGtSX8y+zBsngOqUb/XPzI5\n" +
            "11EztA3wZrFtzfBGFfE/ywrSqsUIJwkLEEMOmq//g5ZvEdpY3a0u28I8JycoFQVE\n" +
            "EyNaSBBQA7M3o9as8ZQSzG1MyjvU3uV2qyreEc4wlpOomULwISit3tC3EKfByLVe\n" +
            "p637YF94EPqPCftM04lbX03v7v4XkMZhLZ6v1es9yCU5wSluqS0eWlGJti2qzz9S\n" +
            "C0Sh9gfLNGqujDPrs4hDJcfX+ujbvl9g4n6n84zSjSK4OgF4\n" +
            "-----END CERTIFICATE-----";

    private final String mClientPEM = "-----BEGIN CERTIFICATE-----\n" +
            "MIID2TCCAcECAQEwDQYJKoZIhvcNAQELBQAwgZYxCzAJBgNVBAYTAkNOMRIwEAYD\n" +
            "VQQIDAlndWFuZ2RvbmcxEjAQBgNVBAcMCXNoZW56aGVuZzEQMA4GA1UECgwHdHVy\n" +
            "YmluZTEQMA4GA1UECwwHdHVyaW5iZTEaMBgGA1UEAwwRdHVyYmluZS56cGNhdC5v\n" +
            "cmcxHzAdBgkqhkiG9w0BCQEWEHN1emgxOTg0QDE2My5jb20wHhcNMTUwNzE0MTQ0\n" +
            "NzI0WhcNMTYwNzEzMTQ0NzI0WjBSMQswCQYDVQQGEwJDTjESMBAGA1UECAwJZ3Vh\n" +
            "bmdkb25nMRIwEAYDVQQHDAlzaGVuemhlbmcxDTALBgNVBAoMBGhvbWUxDDAKBgNV\n" +
            "BAsMA2hvZTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAwadQIAk6qMaL5G/O\n" +
            "XUcDHjax16yaKSN66Nh44whJiaD4rH4jF7BpfG7i2f4gset4ZngQaPtAW+brxSIk\n" +
            "JLUs5LKEKpaJ9r0WKXVIjMaX/ccoTum9X1pGOpzNiLuw8oEJf9frtcGK4KYaAact\n" +
            "Yha/RVME/eP05JyurhqpmeTEtwUCAwEAATANBgkqhkiG9w0BAQsFAAOCAgEAZw6i\n" +
            "WczCo+S/cXxNKkMu7z71FDgVuZSJdq6S3PpUqecuEr6Q7EqEelf6NCIRBpZ3q7uV\n" +
            "2LwUC+pV3tNik9lrpvWP3MErGzqzZch5oHZ/6q+JCPfZEiAMZSU+vOiG5U+7rQ3p\n" +
            "6N124OOjTFeIKv5bUEHCo9OGSA+Abgaerawkn7IcGFpRtWgWEY7w00662a3jxkK5\n" +
            "QeyxhY7koItDTNXGfXZ9LdtdufzFjk1jeDf9Gs20ben/whgZgtMNqEQzqMKOCmto\n" +
            "LcZ2prL+1xnliv99BlshazTz8HSuoaj4o9xi8c9157xWjuMOQvMjmTJlSvtdcRUq\n" +
            "hL/gMi7/h0nfnWkbIEa1Vu+FfcFcOfnMfzTVXphOCQrp5pFXeP4/ocqgN3CDbtl5\n" +
            "YKrLTxgykAx5wfdk3nBhGYffRYMNymrua4X4B4hsa4HH2OXkByDh+GwiH4qxZxMW\n" +
            "5V95UJntrhAEyG1NKwKqD7dd/OzB1S0PLgymugQ6GXoSDzoIENf6FUNh4l0B2p41\n" +
            "UCiHaCOFH7ev7y35F2X+F+caYAvbqsbJMTcfypZqYWipAEy8FUOyKA/vqMfz35U+\n" +
            "YN4ULQj1w6LRNFmT351pafB5hKx0AxBTnbDQb7J6Dymrio/NS2Gpw4IoBUJUy3Ea\n" +
            "xHFi7cF7ddAf7lCNH86kdGnAzntPQVIDTDuJjAQ=\n" +
            "-----END CERTIFICATE-----";
    private final String mClientKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIICXAIBAAKBgQDBp1AgCTqoxovkb85dRwMeNrHXrJopI3ro2HjjCEmJoPisfiMX\n" +
            "sGl8buLZ/iCx63hmeBBo+0Bb5uvFIiQktSzksoQqlon2vRYpdUiMxpf9xyhO6b1f\n" +
            "WkY6nM2Iu7DygQl/1+u1wYrgphoBpy1iFr9FUwT94/TknK6uGqmZ5MS3BQIDAQAB\n" +
            "AoGBAIatX5IIGR2Lh3rFLxW77zUyAJjn1wbJQA9zjQ8Hkmz+4DLGPo2ZCKObUnUc\n" +
            "wYw7IK7SzUbVhbrkbA/bYJuOzLrB5xFbffJje3fL2jpl6n2Jkz3Ld16/0Gekks7k\n" +
            "2yYJzd36W9t9Z+WwxkAhVELgn0yxe0kibxH/tDK4uob1lJX5AkEA7nTXI5q4s+f2\n" +
            "DRTA6YaZrBSl9LEd6CTaC4svs7wQWPHOPgYuj7BYnCqglmSrI/5T9P1mJ801XamO\n" +
            "x6NNZvHJLwJBAM/mpHY1F8Q9ySRc9zZ4ZNuozKF8k/58gZ9A6bKmMvQpIR1f0RTf\n" +
            "6tVleMVFM/jXW4T3DbXuNirSMtTpEi2HjgsCQQDSvBN9dbkR1UeP2+148+lPOJhJ\n" +
            "nVWu2RKR7RnlH6ja0ifQjKoNdm145fsxrhnJH3SXKHJlbmdhNP3n1JwNBFUlAj9K\n" +
            "tphj4rCn1YxzhPVSnfGg/wiLADovOo9aq/lQOmoVVLKR291HozDBf7XnLvd+deZt\n" +
            "4lsLBpBw0xlxalEeC5kCQAkS1Ojt8V+sKo/eqgNGwaYENCAJth/xi0rr43o6OaZ2\n" +
            "haUFCzicCPiTeDlGvj1iygSDA7P6NNaponpdEBqCDQA=\n" +
            "-----END RSA PRIVATE KEY-----";

    private final String mClientKeyPk8 = "-----BEGIN PRIVATE KEY-----\n" +
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMGnUCAJOqjGi+Rv\n" +
            "zl1HAx42sdesmikjeujYeOMISYmg+Kx+IxewaXxu4tn+ILHreGZ4EGj7QFvm68Ui\n" +
            "JCS1LOSyhCqWifa9Fil1SIzGl/3HKE7pvV9aRjqczYi7sPKBCX/X67XBiuCmGgGn\n" +
            "LWIWv0VTBP3j9OScrq4aqZnkxLcFAgMBAAECgYEAhq1fkggZHYuHesUvFbvvNTIA\n" +
            "mOfXBslAD3ONDweSbP7gMsY+jZkIo5tSdRzBjDsgrtLNRtWFuuRsD9tgm47MusHn\n" +
            "EVt98mN7d8vaOmXqfYmTPct3Xr/QZ6SSzuTbJgnN3fpb231n5bDGQCFUQuCfTLF7\n" +
            "SSJvEf+0Mri6hvWUlfkCQQDudNcjmriz5/YNFMDphpmsFKX0sR3oJNoLiy+zvBBY\n" +
            "8c4+Bi6PsFicKqCWZKsj/lP0/WYnzTVdqY7Ho01m8ckvAkEAz+akdjUXxD3JJFz3\n" +
            "Nnhk26jMoXyT/nyBn0DpsqYy9CkhHV/RFN/q1WV4xUUz+NdbhPcNte42KtIy1OkS\n" +
            "LYeOCwJBANK8E311uRHVR4/b7Xjz6U84mEmdVa7ZEpHtGeUfqNrSJ9CMqg12bXjl\n" +
            "+zGuGckfdJcocmVuZ2E0/efUnA0EVSUCP0q2mGPisKfVjHOE9VKd8aD/CIsAOi86\n" +
            "j1qr+VA6ahVUspHb3UejMMF/tecu93515m3iWwsGkHDTGXFqUR4LmQJACRLU6O3x\n" +
            "X6wqj96qA0bBpgQ0IAm2H/GLSuvjejo5pnaFpQULOJwI+JN4OUa+PWLKBIMDs/o0\n" +
            "1qmiel0QGoINAA==\n" +
            "-----END PRIVATE KEY-----";

    private final String mTurbine2Pem = "-----BEGIN CERTIFICATE-----\n" +
            "MIIEHTCCAgUCAQEwDQYJKoZIhvcNAQELBQAwgZYxCzAJBgNVBAYTAkNOMRIwEAYD\n" +
            "VQQIDAlndWFuZ2RvbmcxEjAQBgNVBAcMCXNoZW56aGVuZzEQMA4GA1UECgwHdHVy\n" +
            "YmluZTEQMA4GA1UECwwHdHVyaW5iZTEaMBgGA1UEAwwRdHVyYmluZS56cGNhdC5v\n" +
            "cmcxHzAdBgkqhkiG9w0BCQEWEHN1emgxOTg0QDE2My5jb20wHhcNMTUwNzE0MTQ0\n" +
            "NTU4WhcNMTYwNzEzMTQ0NTU4WjCBlTELMAkGA1UEBhMCQ04xEjAQBgNVBAgMCWd1\n" +
            "YW5nZG9uZzERMA8GA1UEBwwIc2hlbnpoZW4xEDAOBgNVBAoMB3R1cmJpbmUxEDAO\n" +
            "BgNVBAsMB3R1cmlibmUxGjAYBgNVBAMMEXR1cmJpbmUuenBjYXQub3JnMR8wHQYJ\n" +
            "KoZIhvcNAQkBFhBzdXpoMTk4NEAxNjMuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GN\n" +
            "ADCBiQKBgQDHCD+fgJBa9x11XSAWGfDSPSchpaXCtIdCLc3INdmu33Vfb2pESMfF\n" +
            "GWqk3rQHAIxCgpuMsLPZrpEJS35UUpq/MtxgdSNqlGI6OXRmtKU+mIv7GpnOepNS\n" +
            "rhgWIVBr2DbO7wBj8O/sL/J1f0hEGrbNU8w9pQy8eoocb57n0EV4DQIDAQABMA0G\n" +
            "CSqGSIb3DQEBCwUAA4ICAQBh2jFJ9OA7Ye8Neevbr5WRfHp12DcX/uPBFNsFgWAq\n" +
            "xgKr+VP95ZsRAOL40cWD1qef2+jVOFe0NjRqMdxjlJhB+fbrPydRP+hdV2sX9Yxx\n" +
            "pwwKwb3iUnGg/AoQ5jCsemGKP4j3sOKYUyuZ99wJveY/+JFnACFkpJYm+oYj3LL5\n" +
            "U5QSvjye4IetlGXfFRpV9lkP9oC8ZE4NvU6yZ3VyCjM3Vz6n1/VPF7jbJVhZ06m7\n" +
            "dIK0jww/SDAh8OsQhVkRM7Q14YafXtSNWGzylG21Eoy8Tg8KQAvga0iGOtA1lQuc\n" +
            "cyY/dp4tbaooqeAArxZpUoM8rYIU8ovAVMT+J97LjHX4x09URlx3hJq7WCFusGJi\n" +
            "CDpAGs0XpctOpjYojhUuaFaxRmRVI/wDmUPGXycPAAvZ5TfXSsTPNhD20zI1X+n3\n" +
            "h3MPajLhZpfllLmEcVPNm5ljedgZZsq9fhuJRXIanOpnOOZc2AH34STkAKBzIs1+\n" +
            "vEHTfA97VbtHZQOMNTjAzYh4m5JQebxGnykbF5SuRaMi5E8tXOAiIv0x9Wg/DfIQ\n" +
            "Su/qDjhBB7f4JIXSH1zd9Ngyf5klprcMusP2Z0GzoyfMifx/DqPQxEKcYPYmYR26\n" +
            "5iN1cv42eH4PVKzEkOWFMc3FwKGVA9ZuzIE4qYPdVF5fK/GGno6jABAs5fGPuJvn\n" +
            "kQ==\n" +
            "-----END CERTIFICATE-----";

    private final String mTrubine3 = "-----BEGIN CERTIFICATE-----\n" +
            "MIIGATCCA+mgAwIBAgIJAMFWCbh9mSbLMA0GCSqGSIb3DQEBCwUAMIGWMQswCQYD\n" +
            "VQQGEwJDTjESMBAGA1UECAwJZ3Vhbmdkb25nMRIwEAYDVQQHDAlzaGVuemhlbmcx\n" +
            "EDAOBgNVBAoMB3R1cmJpbmUxEDAOBgNVBAsMB3R1cmluYmUxGjAYBgNVBAMMEXR1\n" +
            "cmJpbmUuenBjYXQub3JnMR8wHQYJKoZIhvcNAQkBFhBzdXpoMTk4NEAxNjMuY29t\n" +
            "MB4XDTE1MDcxNDE0NDM1NVoXDTE2MDcxMzE0NDM1NVowgZYxCzAJBgNVBAYTAkNO\n" +
            "MRIwEAYDVQQIDAlndWFuZ2RvbmcxEjAQBgNVBAcMCXNoZW56aGVuZzEQMA4GA1UE\n" +
            "CgwHdHVyYmluZTEQMA4GA1UECwwHdHVyaW5iZTEaMBgGA1UEAwwRdHVyYmluZS56\n" +
            "cGNhdC5vcmcxHzAdBgkqhkiG9w0BCQEWEHN1emgxOTg0QDE2My5jb20wggIiMA0G\n" +
            "CSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQCvtLrU2wWOUUAj3U4meTsVJbTHNGMn\n" +
            "VaN55c3pJBD7iROAOqRTxBe4si9z/LII1/7NrktZVSLivlpdZVYvJY5HqJxMYpJw\n" +
            "1AXxTZRaWZPEm0GKl7I+TjPr6qoJSN9h/Pj4NcOi+h0alDABpvS1kS4Q8ZOMDHdW\n" +
            "1R90ZAY99hfOK+2A0QnW858qhAzRRqEZm3nYrY/Ya2EPZdFfNE9G2+Elz/LiGobm\n" +
            "ky+pFaPbbF6h6wdRWjILisR2cecBTw5b6NUnJtZ7hF+dqLs5keGyzXzGQ4xSw3iK\n" +
            "lpTUZnF67W0+MST9HwRU42P1eaToNZh4Zx5SV1Olcc3rgdpjodV+N/wXDoMlbcss\n" +
            "iAsJ/s7t7aj/5IGiwXQNgv9KvH+/l3ojVN0ITjqGQ/cYjhoYa5KdoE9uIC1N7Gr5\n" +
            "q2zpCkHpCuSi99cdDtXnCWl2wlrIaKEWffi68fIo4V06rtox0abr2hOU6PxeE6xR\n" +
            "iy2slxts8yxz0RCCdu3VvLpqW2BWQTCG0gCTof1jgFAdnQLov3BIbs6/Emka/o/H\n" +
            "pCf2AdtAoMgAyQo7WU5FyagT5ZWxvE7+nsvhyCiehIrAccGbK50yqZp4hUI7/Cmv\n" +
            "MsTows9JzsNjngzItg7l1zNQ+YjYu58e4ojIuwjlVOkDSa0UuWvZH+Y6ld7rod2a\n" +
            "1PT1TVVbcVLiCQIDAQABo1AwTjAdBgNVHQ4EFgQUgITcglnNZNPZXpzuzlTBEd24\n" +
            "8BUwHwYDVR0jBBgwFoAUgITcglnNZNPZXpzuzlTBEd248BUwDAYDVR0TBAUwAwEB\n" +
            "/zANBgkqhkiG9w0BAQsFAAOCAgEAnLxvn7OybTqPrvlca6i8ycYUVsvCuoIU6J5D\n" +
            "5drHo4XRZ9ikP6E4tRrOfAe+17/qFriQc4WuBNLNrZUeLuIJGhBcEt58LPsHrn+x\n" +
            "kGvt36eUJ7jZs16vBK5ST10M2HoQpzdM+IWPGrehgXPgZnpP9Wf4+CLj8r/jtuUz\n" +
            "I1neWoY/yTibFC9Apyj9mhYCCGbmQQyIa8L6JjpfWX5ZwVj2mWVBqkBwHE1lxZTZ\n" +
            "Sa01Bz3ysScL1Ynit4DmOvlDa/nX9npLfw9fwasjqHoR0UmKESQ2Lpo/8hju9VE9\n" +
            "QuD4/Ra9jLleR4l4GNH7Wy9OEk6u3FmaePwbM0ySGSvavQcPkkVFqiqp9gEtfftS\n" +
            "Fl43M2V2bNK19xdzqePSgfUjDFXddk6UZsfgZxiqtucKxhmZuG3GdWgfbr5Cxoyt\n" +
            "bh8sIJ7YqrbtgYts1uZ/HT4zWKzrcEb5IA0+c/PKy6MKFOsSVKxdYgKcU3Dpt+Pb\n" +
            "3SO6VW4AgdTKjib6msttHAhzXn467CAyI4XfcYM1qEzW3I5caBBb1hfNipJg7AeZ\n" +
            "CeUhr3gfQhcelRXZd5xOxNv7rGI1YvdLcyvv4riHRsMCVjXQhyc3Xwp1hLScWpRG\n" +
            "gCUjWEkMoMf2LizzyhdefJ7srftNX2l7K6d10iAIOnC1zBpU2wdTbhiv2H1odQE3\n" +
            "b+1azsk=\n" +
            "-----END CERTIFICATE-----";

    private CustomSSLSocketFactory mCustomSSLSocketFactory;
    private TextView mMsgBoard;
    private Spinner mUrlsSpinner;
    private ArrayAdapter<CharSequence> mUrlAdapater;

    private Spinner mCertsSpinner;
    private ArrayAdapter<CharSequence> mCertsAdapater;

    private Button mUrlConnectionBtn;
    private Button mHttpClientBtn;

    private ReqCallback mUrlConnectionCb = new ReqCallback() {
        @Override
        public void onResult(final String result) {
            Log.e(TAG, "-----------");
            CharSequence msg = mMsgBoard.getText();

            SpannableStringBuilder builder = new SpannableStringBuilder()
                    .append(msg);
            if (msg.length() > 1 && msg.charAt((msg.length() - 1)) != '\n') {
                builder.append('\n');
            }

            if (result != null) {
                Log.e(TAG, result);
                builder.append(formatString(MainActivity.this, result,
                        R.style.MsgBoardTextAppearance));
            } else {
                builder.append(formatString(MainActivity.this, R.string.no_response,
                                R.style.CertErrorTextAppearance));

            }

            msg = builder.subSequence(0, builder.length());
            final CharSequence finalMsg = msg;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMsgBoard.setText(finalMsg);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMsgBoard = (TextView) findViewById(R.id.tv_msg_board);
        mMsgBoard.setMovementMethod(new ScrollingMovementMethod());

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

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        mCertsSpinner = (Spinner) findViewById(R.id.sp_certs);
        mCertsAdapater = ArrayAdapter.createFromResource(this,
                R.array.certs, android.R.layout.simple_spinner_item);
        mCertsAdapater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCertsSpinner.setAdapter(mCertsAdapater);
        mCertsSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position,
                            long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        CharSequence selectedItem = (CharSequence) mUrlsSpinner.getSelectedItem();
        Log.e(TAG, selectedItem.toString());

        mUrlConnectionBtn = (Button) findViewById(R.id.btn_url_connection);
        mHttpClientBtn = (Button) findViewById(R.id.btn_http_client);

        mUrlConnectionBtn.setOnClickListener(this);
        mHttpClientBtn.setOnClickListener(this);
        mCustomSSLSocketFactory = new CustomSSLSocketFactory();
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
        } else if (id == R.id.action_clean_board) {
            mMsgBoard.setText(null);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        SSLSocketFactory sslSocketFactory = getCurrentSSLSocketFactory();
        String url = mUrlsSpinner.getSelectedItem().toString();

        switch (v.getId()) {
            case R.id.btn_url_connection:
                new UrlConnectionRequst().request(url, sslSocketFactory,
                        null, mUrlConnectionCb);
                break;
            case R.id.btn_http_client:
                switch (mCertsSpinner.getSelectedItemPosition()) {
                    case 5:
                        try {
                            InputStream input = getResources().openRawResource(R.raw.gitlab_iboxpay);
                            KeyStore keyStore = KeyStore.getInstance("BKS");
                            keyStore.load(input, "123456".toCharArray());
                            new HttpClientRequest().request(url, null, mUrlConnectionCb,
                                    keyStore, "123456");
                        } catch (KeyStoreException e) {
                            e.printStackTrace();
                        } catch (CertificateException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 7:
                        try {
                            InputStream input = getResources().openRawResource(R.raw.turbine);
                            KeyStore keyStore = KeyStore.getInstance("BKS");
                            keyStore.load(input, "123456".toCharArray());
                            new HttpClientRequest().request(url, null, mUrlConnectionCb,
                                    keyStore, "123456");
                        } catch (KeyStoreException e) {
                            e.printStackTrace();
                        } catch (CertificateException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    default:
                        Log.e(TAG, "http client unsupport this.");
                        CharSequence msg = mMsgBoard.getText();

                        SpannableStringBuilder builder = new SpannableStringBuilder()
                                .append(msg);
                        if (msg.length() > 1 && msg.charAt((msg.length() - 1)) != '\n') {
                            builder.append('\n');
                        }

                        builder.append(formatString(MainActivity.this, R.string.http_client_unsupport,
                                R.style.CertErrorTextAppearance));

                        msg = builder.subSequence(0, builder.length());
                        final CharSequence finalMsg = msg;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMsgBoard.setText(finalMsg);
                            }
                        });

                        break;

                }
                break;
            default:
                break;
        }
    }

    private SSLSocketFactory getCurrentSSLSocketFactory() {
        int i = mCertsSpinner.getSelectedItemPosition();
        SSLSocketFactory sslSocketFactory;
        switch (i) {
            case 0:
                sslSocketFactory = null;
                Log.e(TAG, "0");
                break;
            case 1:
                sslSocketFactory = ((TLSApplicaton)getApplication())
                        .getDefaultSSLSocketFactory();
                Log.e(TAG, "1");
                break;
            case 2:
                sslSocketFactory = mCustomSSLSocketFactory
                        .getSSLSocketFactoryFromPEM(mIboxpayPEMCert);
                Log.e(TAG, "2");
                break;
            case 3:
                sslSocketFactory = mCustomSSLSocketFactory
                        .getSSLSocketFactoryFromPEM(mVeriSignPemCert);
                Log.e(TAG, "3");
                break;
            case 4:
                sslSocketFactory = mCustomSSLSocketFactory
                        .getSSLSocketFactoryFromPEM(mGitlabIboxpayPemCert);
                Log.e(TAG, "4");
                break;
            case 5:
                InputStream input = getResources().openRawResource(R.raw.gitlab_iboxpay);

                sslSocketFactory = mCustomSSLSocketFactory
                        .getSSLSocketFactoryFromBKSKeyStore(input, "123456");
                Log.e(TAG, "5");
                break;
            case 6:
                sslSocketFactory = mCustomSSLSocketFactory
                        .getSSLSocketFactoryFromPEM(mTurbinePEM);
                Log.e(TAG, "6");
                break;
            case 7:
                InputStream turbine = getResources().openRawResource(R.raw.turbine);

                sslSocketFactory = mCustomSSLSocketFactory.getSSLSocketFactoryFromBKSKeyStore(
                        turbine, "123456");
                Log.e(TAG, "7");
                break;
            case 8:
                sslSocketFactory = mCustomSSLSocketFactory.
                        getSSLSocketFactoryWithKeyManagerFromPem(mClientPEM, mClientKeyPk8, mTrubine3);
                break;
            default:
                sslSocketFactory = null;
                Log.e(TAG, "null");
                break;
        }

        return sslSocketFactory;
    }

    private static SpannableString formatString(Context context, int textId, int style) {
        String text = context.getString(textId);
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new TextAppearanceSpan(context, style), 0, text.length(), 0);
        return spannableString;
    }

    private static SpannableString formatString(Context context, String text, int style) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new TextAppearanceSpan(context, style), 0, text.length(), 0);
        return spannableString;
    }
}
