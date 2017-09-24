package com.example.my_boss.questrip;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by takayayuuki on 2016/11/06.
 */

public class questrip_waypoint_goal_Activity extends Activity implements LocationListener{
    //ご当地キャラget用------------------------------
    private String api_key_gotouchi = [GOTOUCHI API KEY];  //ご当地キャラAPI使用に用いるAPIキー
    private getItemDataAsync get_item;                  //ご当地キャラAPIへリクエストを投げるAsynctask
    private imageBuilderAsync image_builder;            //URLをBitmapイメージへ変換するAsynctask
    private String url_getitem;                         //APIリクエスト用のURL
    private Uri.Builder builder[] = new Uri.Builder[50];                        //URL→Bitmap変換のURL
    private String json_getitem;                        //APIから返ってきたJSONデータ格納
    private String image_url[] = new String[50];        //JSONデータからimage部分のみをパースしてここに入れる．その後imageBuilderAsyncへ
    private Bitmap image_of_character[] = new Bitmap[50];                  //ご当地キャラの画像データ

    private LocationManager locationManager;
    private double latitude;
    private double longitude;

    private RecyclerView recyclerView;
    private TextView mascot_counta;
    private ImageView message;
    private ImageView mascot;
    private Button end;
    private TextView point;
    private TextView gotouti;



    private String font="Pixel10.ttf";  //8ビットフォント


    private int mascot_count;

    global_values global;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questrip_waypoint_goal);

        global = (global_values)this.getApplication();

        global.point +=100;

        locationStart();
        image_getter(latitude,longitude);  //image_of_charcterにbitmap格納


        //instagramから取得した画像を表示するRecyclerViewの設定
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        gotouti = (TextView) findViewById(R.id.textView);
        point = (TextView) findViewById(R.id.point);
        message = (ImageView)findViewById(R.id.logo);
        mascot =(ImageView)findViewById(R.id.mascot);
        end = (Button)findViewById(R.id.button);
        setClickListener();


        end.setTypeface(Typeface.createFromAsset(getAssets(), font));
        point.setTypeface(Typeface.createFromAsset(getAssets(), font));
        gotouti.setTypeface(Typeface.createFromAsset(getAssets(), font));


        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL); // ここで横方向に設定
        recyclerView.setLayoutManager(manager);

        ArrayList<Bitmap> list = new ArrayList();

        for(int i=0;i<10;i++) {
            list.add(image_of_character[i]);
            if (image_of_character[i]==null) {
                mascot_count = i+1;
                break;
            }
        }
        recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), list));

        point.setText("取得ポイント:"+global.point);

        message.setAlpha(0);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                setLogo();
            }
        }, 1000);

    }

    void setClickListener() {
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                //インテントの作成
                Intent intent = new Intent();
                intent.setClassName("com.example.my_boss.questrip", "com.example.my_boss.questrip.Instagram_connect_Activity");
                startActivity(intent);
                finish();
            }
        });
    }

    void setLogo(){
        message.setAlpha(255);
        message.startAnimation(AnimationUtils.loadAnimation(this, R.anim.a2));
    }

    void image_getter(Double latitude,Double longitude){
        //-------------ご当地キャラget用のasynctask 2016_10_25 by Takaya-----------------------------
        JSONObject jsonData_getitem = null; //画像取得用Asyncから戻ってきたJsonを格納するオブジェクト
        url_getitem = "http://localchara.jp/services/api//search/location/character?api_key="
                //+global.api_key_gotouchi+"&ll="+latitude+","+longitude+"&counts="+3;
                +api_key_gotouchi+"&ll="+latitude+","+longitude+"&counts="+10;
        get_item = new getItemDataAsync();
            try {
            json_getitem = get_item.execute(url_getitem).get();
            jsonData_getitem = new JSONObject(json_getitem);
            JSONArray datas = jsonData_getitem.getJSONArray("result");

            for (int i = 0; i < datas.length(); i++) {
                JSONObject data = datas.getJSONObject(i);
                // 名前を取得
                image_url[i] = data.getString("image_full");
                builder[i] = Uri.parse(image_url[i]).buildUpon();

                image_builder = new imageBuilderAsync();
                image_of_character[i] = image_builder.execute(builder[i]).get();
            }
        } catch (InterruptedException e) {e.printStackTrace();}
        catch (ExecutionException e) {e.printStackTrace();}
        catch (JSONException e) {e.printStackTrace();}
    }


    private void locationStart() {
        // LocationManager インスタンス生成
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // 最新GPS値取得
        Location nowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // GPS地の更新（第一引数：provider（network または gps），第二引数：minTime（位置情報を取得する最小時間）
        // 第三引数：minDistance（位置情報を取得する最小距離），第四引数：listener（リスナーを実装しているクラス）
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 100, this);

        latitude=nowLocation.getLatitude();       //最新GPS値の緯度を格納
        longitude=nowLocation.getLongitude();     //最新GPS値の経度を格納

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            // GPSを設定するように促す
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug", "gpsEnable, startActivity");
        } else {
            Log.d("debug", "gpsEnabled");
        }
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            Log.d("debug", "checkSelfPermission false");
            return;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
