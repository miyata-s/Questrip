package com.example.my_boss.questrip;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by takayayuuki on 2016/10/24.
 */

public class imageBuilderAsync extends AsyncTask<Uri.Builder, Void, Bitmap> {
    private ImageView imageView;

    public imageBuilderAsync(){

    }

    public imageBuilderAsync(ImageView imageView){
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Uri.Builder... params) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        Bitmap bitmap = null;

        try{
            URL url = new URL(params[0].toString());
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            inputStream = connection.getInputStream();

            bitmap = BitmapFactory.decodeStream(inputStream);
        }catch (MalformedURLException exception){

        }catch (IOException exception){

        }finally {
            if (connection != null){
                connection.disconnect();
            }
            try{
                if (inputStream != null){
                    inputStream.close();
                }
            }catch (IOException exception){
            }
        }


        return bitmap;
    }


//    @Override
//    protected void onPostExecute(Bitmap result){
//        // インターネット通信して取得した画像をImageViewにセットする
//        this.imageView.setImageBitmap(result);
//    }

}


