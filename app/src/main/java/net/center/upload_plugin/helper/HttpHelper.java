package net.center.upload_plugin.helper;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class HttpHelper {


    public static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).build();
    }

}
