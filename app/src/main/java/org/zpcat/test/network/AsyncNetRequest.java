package org.zpcat.test.network;

import java.util.HashMap;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by moses on 4/23/15.
 */
public interface AsyncNetRequest {
    void request(String url, SSLSocketFactory sslSocketFactory, HashMap<String, String> params, ReqCallback callback);
}
