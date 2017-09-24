package com.example.my_boss.questrip;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by takayayuuki on 2016/11/05.
 */

public class post_server_Async extends AsyncTask<String,Integer,String>{

    @Override
    protected String doInBackground(String... params) {
        String  request_url = params[0];
        String  json_string = params[1];
//        String  userID = params[2];


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







            // データを送信する
            os = urlCon.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(String.valueOf(json_string));
//            writer.write(String.valueOf(userID));
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
        return String.valueOf(json_string);
    }





    public String readInputStream(InputStream in) throws IOException, UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        String st = "";

        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        while((st = br.readLine()) != null)
        {
            sb.append(st);
        }
        try
        {
            in.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return sb.toString();
    }

}
