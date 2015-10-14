package com.example.proxymeister.antonsskafferi;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.example.proxymeister.antonsskafferi.model.ApiInterface;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class Utils {

    public static String toString(InputStream in) throws IOException
    {
        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        while (true) {
            byte b = (byte)bis.read();
            if (b == -1)
                break;
            buf.write(b);
        }

        return buf.toString();
    }

    //http://46.254.15.8/api/http://10.0.2.2:8080/web-app/api/
    public static ApiInterface getApi(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String username = sp.getString("username", null);
        String password = sp.getString("password", null);
        String BASE_URL = "http://46.254.15.8/api/";



        final String basicAuth = "Basic " + new String(Base64.encode((username + ":" + password).getBytes(), Base64.NO_WRAP));

        System.out.println(basicAuth);


        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Customize the request
                Request request = original.newBuilder()
                        .header("Authorization", basicAuth)
                        .method(original.method(), original.body())
                        .build();

                Response response = chain.proceed(request);

                // Customize or return the response
                return response;
            }
        });


        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ApiInterface.class);
    }
}
