package com.partymaker.cinaitaren;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by vladimir on 29.10.16.
 */

public class YandexApi {
    public interface OnYandexListener{
        void onSuccess(JSONObject jsonObject);
        void onError();
    }

    public static void getOrganizations(Context context, OnYandexListener onYandexListener){
        String url = "https://search-maps.yandex.ru/v1/?ll="+AppPreferences.getMyLong(context)+","+ AppPreferences.getMyLat(context)+"&spn=0.005069,0.0050552&lang=ru_RU&apikey=36113da8-9ad0-47b8-a37a-4463ae4611c6&results=10";
       // L.d("url = " + url);
//        String url = "https://search-maps.yandex.ru/v1/?ll=30.250473,60.014499&spn=0.005069,0.0050552&lang=ru_RU&apikey=f1ce6492-2534-40d4-8c25-67c2e229e446&results=10";

        sendGETRequest(url,onYandexListener);
    }
    public static void sendGETRequest(final String url, final OnYandexListener onYandexListener){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                onYandexListener.onSuccess(sendRequest(url,"GET"));
            }
        });
        thread.start();
    }

    private static JSONObject sendRequest(String urlStr, String Method){

        final Handler handler = new Handler(Looper.getMainLooper());
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod(Method);
            connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
            connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");

            int responseCode = connection.getResponseCode();



            L.d("url = " + url + " responseCode = " + responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator") + "Type " + "GET");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();

                final JSONObject responseJson = new JSONObject(responseOutput.toString());
                L.d("responseJson = " + responseJson);

               return responseJson;
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
