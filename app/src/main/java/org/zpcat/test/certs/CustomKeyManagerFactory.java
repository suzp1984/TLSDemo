package org.zpcat.test.certs;

import java.io.InputStream;

import javax.net.ssl.KeyManager;

/**
 * Created by moses on 7/14/15.
 */
public class CustomKeyManagerFactory {

    public static KeyManager getKeyManagerFromFile(String pem, String privKey) {

        return new PEMKeyManager(pem, privKey);
    }

    public static KeyManager[] getKeyManagerFromKeyStore(InputStream input, String passwd) {
        return null;
    }
}
