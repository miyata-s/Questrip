package com.example.my_boss.questrip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by takayayuuki on 2016/11/05.
 */

public class questrip_collection_Activity extends Activity {
    private int IMAGE_NUM = 20;

    private List<Bitmap> imgList = new ArrayList<Bitmap>();  //ご当地キャラ画像を格納するList
    private GridView gridview;  //gridviewオブジェクト

    private String url_get_server;  //サーバへgetリクエストするためのURL
    private String charID[] = new String[IMAGE_NUM];  //サーバからgetしたキャラIDの格納配列
    private String charID_fromserver;   //サーバからgetしたJSON

    private String url_get_char_fromID = new String();  //ご当地キャラAPIへ送るURL
    private String api_key_gotouchi = [GOTOUCHI API KEY];  //ご当地キャラAPI使用に用いるAPIキー
    private String string_char;     //ご当地キャラAPIから返って来たJSON


    //AsyncTaskオブジェクト生成
    private getItemDataAsync get_itemData;
    private imageBuilderAsync img_builder;
    private get_server_Async get_server_async;


    private String img_url = new String();        //JSONデータからimage部分のみをパースしてここに入れる．その後imageBuilderAsyncへ
    private Bitmap image_of_character;                  //ご当地キャラの画像データ
    private Uri.Builder builder;                        //URL→Bitmap変換のURL

    global_values global;   //global変数使うためのオブジェクト

    private Button button_top;  //Topへ戻るボタン


    private String userID ="takaya";








    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questrip_collection);
        button_top = (Button)findViewById(R.id.button_top);
        button_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ボタンがクリックされた時に呼び出されます
                Intent intent=new Intent();
                intent.setClassName("com.example.my_boss.questrip","com.example.my_boss.questrip.questrip_root_Activity");
                startActivity(intent);
            }
        });

        //グローバル変数を取得
        global = (global_values)this.getApplication();



//        userID = global.user;
//
//        charID[0] = "3891";
//        charID[1] = "3893";
//        charID[2] = "2050";


        //ここでサーバからキャラID取得
//        url_get_server = "http://192.168.20.23:3000/zukan/:"+global.user;
        url_get_server = "http://192.168.20.32:3000/zukan/:"+userID;
//        url_get_server = "http://192.168.12.11:3000/zukan/:"+userID;

        get_server_async = new get_server_Async();
        try {
            charID_fromserver = get_server_async.execute(url_get_server).get();
            JSONObject json = new JSONObject(charID_fromserver);
            for(int count = 0;count < IMAGE_NUM;count++) {
                if(json.getString("id"+count) == null){
                    break;
                }
                charID[count]=json.getString("id" + count);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Toast toast2 = Toast.makeText(getApplicationContext(), global.user, Toast.LENGTH_SHORT);
//        toast2.show();



        //キャラIDからご当地キャラAPIへ→画像取得


        for(int count = 0;count<charID.length;count++){
            if (charID[count] == null){
                break;
            }
            JSONObject jsonData_char = null;

            url_get_char_fromID = "http://localchara.jp/services/api/info/character?api_key="
                    + api_key_gotouchi + "&id=" + charID[count];


            get_itemData = new getItemDataAsync();

            try {
                string_char = get_itemData.execute(url_get_char_fromID).get();
                jsonData_char = new JSONObject(string_char);
                JSONObject data = jsonData_char.getJSONObject("result");

                img_url = data.getString("image");
                builder = Uri.parse(img_url).buildUpon();

                img_builder = new imageBuilderAsync();
                image_of_character = img_builder.execute(builder).get();

                //imglistにbitmap格納
                imgList.add(image_of_character);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        BitmapAdapter adapter = new BitmapAdapter(getApplicationContext(), R.layout.list_item, imgList);

        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(adapter);

    }



    public class BitmapAdapter extends ArrayAdapter<Bitmap> {

        private int resourceId;

        public BitmapAdapter(Context context, int resource, List<Bitmap> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(resourceId, null);
            }

            ImageView view = (ImageView) convertView;
            view.setImageBitmap(getItem(position));

            return view;
        }
    }
}
