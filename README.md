# TLS Demo
A sample to show up how to use https client in Android.


# Java Keytool

Java keystore is like a database which store certificates
and private keys and protect them by a password maybe. There
are at least three [formats](http://docs.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html#KeyStore) of that, JKS, PKCS12, JCEKS, and
many more.

But in this tutorial, I only consider two formats of that
JKS which is the Java's default format and BKS which is the
Android's default format.

How to write the certificate to the BKS style KeyStore?
First step was to prepare your certificate at least, the PEM
format and DER format is ok. Then to download
[bcprov-jdk16-146.jar](http://www.bouncycastle.org/fr/download/bcprov-jdk16-146.jar). after that use following command to
import an X590 certificate into the keystore.

```sh
  keytool -importcert -v -trustcacerts -file "my_server_cert.der" -alias key_alias -keystore "my.bks" -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath "bcprov-jdk16-146.jar" -storetype BKS
```

Import an PEM X509 certificate into JKS keystore:

```sh
  keytool -importcert -v -trustcacerts -file "cas.pem" -alias cas_alias -keystore "cas.jks" -storetype JKS
```

The default export format of keytool is der format.

```sh
  keytool -exportcert -alias cas_alias -keystore "cas.jks" > cas.der
  
```

# Certificate formats

The certificate which I mentioned here are all reference to 
X509 format, which is not just the most wildly used, but also
the standard in the PKI industry. The evidence is that there
are Certificate interface in the JDK, but with just only one
child, X509Certificate class.

Now, go back to the point, there are two X509 Certificate formats here,
PEM and CER, they are equivalent same things. PEM is base64 encoded 
format, CER is the binary format.

Using the following cmds to download and convert between each other.

```sh
  #download pem certificate by guntls
  gnutls-cli --print-cert www.youwebsite.com | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p'
  
  #download pem certificate by openssl
  openssl s_client -connect ${HOST}:${PORT} </dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p'
  
  # convert DER binary format certificate to PEM base64 encoded format
  openssl x509 -inform der -in cas.der -outform pem -out cas.pem
  
  # convert PEM base64 encoded format certificate to DER binary format
  openssl x509 -inform pem -in cas.pem -outform der -out cas.der
  
```

# Use Https in Android Client

Before going that, you need to figure out how many http clients in Android.
Basically, there are only two http clients available in Android, HttpUrlConnection
and AndroidHttpClient, which two are provided by the system. While HttpUrlConnection
is the barely the only one left now, because AndroidHttpClient and its sibling, 
DefaultHttpClient and its parent HttpClient are all deprecated in Android 2.3. So
choose HttpUrlConnection if you wanna wrote any http client code.

And the most of the 3rd party Http Clients are developed above them, Volley, and what ever,
but Square's OkHttp, which implements its own http stack.

# Java SSLContext

I think to use SSL programming in java is to understand SSLContext and its 
related classes KeyManager, TrustManager, KeyManagerFactory, TrustManagerFactory,
and SSLSocketFactory.

Here is just a piece of code:

```java

        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("BKS");
            keyStore.load(in, passwd.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(keyStore);

            TrustManager[] tms = tmf.getTrustManagers();

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tms, null);
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


```

While that's not enough, that's why I wrote following demo [TLSDemo](https://github.com/suzp1984/TLSDemo).

# Two-way SSL verfication

SSL support server-side verification, just the reverse of the former ones, at this time the
certificates are stored in the client side, and also a private key. So how to configure
the client side to support those feature.

The key point is to give the Keyanager array to the SSLContext.init method.

```java
    sslContext.init(new KeyManager[]{km}, tms, null);
```

following methods is example of generate a KeyManager from the client certificate and
its related private key, and the factoray class, CustomKeyManagerFactory is the
wrapper class that you can leverage.

Oh, in android platform, it only support pkcs8 format private key.

```java
    public SSLSocketFactory getSSLSocketFactoryWithKeyManagerFromPem(String clientPem,
            String privateKey, String servrePem) {
        ArrayList<TrustManager> tmsList = new ArrayList<>();

        TrustManager tm = CustomTrustManagerFactory.getTrustManagerFromPEM(servrePem);

        tmsList.add(tm);
        TrustManager[] tms = tmsList.toArray(new TrustManager[tmsList.size()]);

        KeyManager km = CustomKeyManagerFactory.getKeyManagerFromFile(clientPem, privateKey);
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(new KeyManager[]{km}, tms, null);
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }
```

# TLSDemo

I wrote this demo here [TLSDemo](https://github.com/suzp1984/TLSDemo) to illustrate how to use SSL client in Android.
It illustrates following puzzles:

- How to import certificates in PEM format.
- How to import certificates in BKS type Keystore.
- How to connect https sites in HttpUrlConnection.

There are still some faults in this demo that is the HttpClient https 
connection is not implemented actually, because of the already deprecated 
api was so incompatible, for example, HttpClient is the apache's http
client interface, so its SSLSocketFactory is not compatible with the
javax.net.SSLSocketFactory.

But except the unfinished HttpClient implementation, [TLSDemo](https://github.com/suzp1984/TLSDemo) is still a 
good reference of how to use https in UrlConnection, and how SSLContext 
works?


# reference resources
- [http://developer.android.com/training/articles/security-ssl.html](http://developer.android.com/training/articles/security-ssl.html)
- [http://ogrelab.ikratko.com/using-android-volley-with-self-signed-certificate/](http://ogrelab.ikratko.com/using-android-volley-with-self-signed-certificate/)
- [http://stackoverflow.com/questions/2138940/import-pem-into-java-key-store](http://stackoverflow.com/questions/2138940/import-pem-into-java-key-store)
- [http://stackoverflow.com/questions/3685548/java-keytool-easy-way-to-add-server-cert-from-url-port](http://stackoverflow.com/questions/3685548/java-keytool-easy-way-to-add-server-cert-from-url-port)

