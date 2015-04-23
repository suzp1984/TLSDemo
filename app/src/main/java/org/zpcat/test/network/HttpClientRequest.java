package org.zpcat.test.network;

import java.util.HashMap;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by moses on 4/23/15.
 */
public class HttpClientRequest implements AsyncNetRequest {

    private ReqCallback mReqCallback;
    String mUrl;

    @Override
    public void request(String url, SSLSocketFactory sslSocketFactory, HashMap<String, String> params, ReqCallback callback) {
        mReqCallback = callback;
        mUrl = url;
    }
}
