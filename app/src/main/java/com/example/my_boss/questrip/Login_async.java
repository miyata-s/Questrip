package com.example.my_boss.questrip;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by takayayuuki on 2016/10/25.
 */

public class Login_async extends AsyncTask<String ,Void ,String> {

    private questrip_Login_Activity _main;
    private JSONObject jsonObject = new JSONObject();

    public Login_async(){

    }



    public Login_async(questrip_Login_Activity main) {
        super();
        _main = main;
    }




    @Override
    protected String doInBackground(String... params) {
        String  request_url = params[0];
        String  username = params[1];
        String  password = params[2];


        BufferedReader reader = null;
        OutputStream os = null;
        HttpURLConnection urlCon = null;

        try {

            URL url = new URL(request_url);

            urlCon = (HttpURLConnection) url.openConnection();
            urlCon.setReadTimeout(10000);
            urlCon.setConnectTimeout(20000);
            urlCon.setRequestMethod("POST");
            urlCon.setDoOutput(true);
            urlCon.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlCon.setUseCaches(false);

            urlCon.connect();


            jsonObject.put("username", username);
            jsonObject.put("password", password);




            // データを送信する
            os = urlCon.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(String.valueOf(jsonObject));
            writer.flush();
            writer.close();
            os.close();

            int status = urlCon.getResponseCode();

            switch (status) {
                case HttpURLConnection.HTTP_OK:
                    InputStream is = urlCon.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(is));

                    String httpSource = new String();
                    String str;
                    while (null != (str = reader.readLine())) {
                        httpSource = httpSource + str;
                    }

                    is.close();
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (os != null) {
                    os.close();
                }
                if (urlCon != null) {
                    urlCon.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    return String.valueOf(jsonObject);
    }

    @Override
    protected void onPostExecute(String result) {
//        _main.result_job(result);
    }

}


