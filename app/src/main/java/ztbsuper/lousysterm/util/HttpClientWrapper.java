package ztbsuper.lousysterm.util;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by tbzhang on 9/13/15.
 */
public class HttpClientWrapper {
    private static HttpClient instance;

    private HttpClientWrapper() {

    }

    public static HttpClient getInstance() {
        if (null == instance) {
            instance = new DefaultHttpClient();
        }
        return instance;
    }

}
