package com.example.my_boss.questrip;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by takayayuuki on 2016/10/22.
 */

public class getItemDataAsync extends AsyncTask <String, Void, String>{
    private String readSt ;


    @Override
    protected String doInBackground(String ... param) {
        HttpURLConnection con = null;
        URL url = null;
        String urlSt = param[0];

        try {
            // URLの作成
            url = new URL(urlSt);

            // 接続用HttpURLConnectionオブジェクト作成
            con = (HttpURLConnection)url.openConnection();

            // リクエストメソッドの設定
            con.setRequestMethod("GET");

            // リダイレクトを自動で許可しない設定
            con.setInstanceFollowRedirects(false);

            // URL接続からデータを読み取る場合はtrue
            con.setDoInput(true);


            // URL接続にデータを書き込む場合はtrue
//            con.setDoOutput(true);

            // 接続
            con.connect(); // ①

            InputStream in = con.getInputStream();
            readSt = readInputStream(in);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return param[0];
        return readSt;
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
