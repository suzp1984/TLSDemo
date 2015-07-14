package org.zpcat.test.certs;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509KeyManager;

/**
 * Created by moses on 7/14/15.
 */
public class PEMKeyManager implements X509KeyManager {

    private X509Certificate mCert;
    private PrivateKey mKey;

    public PEMKeyManager(String pem, String privKey) {
        CertificateFactory cf = null;
        InputStream input =
                new ByteArrayInputStream(pem.getBytes(StandardCharsets.UTF_8));

        try {
            cf = CertificateFactory.getInstance("X509");
            mCert = (X509Certificate) cf.generateCertificate(input);
            // Log.e(TAG, mCert.toString());

        } catch (CertificateException e) {
            e.printStackTrace();
        }

        KeyFactory kf = null;
        InputStream keyInput = new ByteArrayInputStream(
                privKey.getBytes(StandardCharsets.UTF_8));

        try {
            kf = KeyFactory.getInstance("RSA");
            String privateKeyPem = privKey.replace("-----BEGIN PRIVATE KEY-----\n", "");
            privateKeyPem = privateKeyPem.replace("-----END PRIVATE KEY-----", "");
            System.out.println(privateKeyPem);

            byte[] encoded = Base64.decode(privateKeyPem, Base64.DEFAULT);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);

            mKey = kf.generatePrivate(keySpec);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        Log.e("chooseClientAlias", keyType.toString());
        for (String key : keyType) {
            Log.e("---", key);
        }

        return "abd";
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        Log.e("chooseServerAlias", keyType);
        return null;
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return new X509Certificate[]{mCert};
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        Log.e("getClientAliaes", keyType);
        return new String[0];
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        Log.e("getServerAliases", keyType);
        return new String[0];
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return mKey;
    }
}
