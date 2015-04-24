package org.zpcat.test.certs;

import javax.net.ssl.TrustManager;

/**
 * Created by moses on 4/24/15.
 */
public class CustomTrustManagerFactory {

    public static TrustManager getTrustManagerFromPEM(String pems) {
        return new PEMTrustManager(pems);
    }

}
