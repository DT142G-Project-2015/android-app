package com.example.proxymeister.antonsskafferi;


import android.os.AsyncTask;

import com.example.proxymeister.antonsskafferi.model.ApiInterface;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public static ApiInterface getApi() {
        String BASE_URL = "http://46.254.15.8/api/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ApiInterface.class);
    }

    // TODO: Remove this class:
    static class FetchURL extends AsyncTask<URL, Void, String> {
        private final Callback callback;

        public interface Callback {
            void onComplete(Object result);
            void onError();
        }

        public FetchURL(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(URL... params) {
            assert(params.length == 1);

            HttpURLConnection conn = null;
            try {

                // Download URL(params[0]) into result
                conn = (HttpURLConnection) params[0].openConnection();
                InputStream inputStream = conn.getInputStream();
                String result = Utils.toString(inputStream);

                return result;
            } catch (IOException e) {
                return null;
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null)
                callback.onComplete(result);
            else
                callback.onError();
        }
    }
}
