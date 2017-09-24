package com.example.my_boss.questrip;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by takayayuuki on 2016/10/26.
 */

public class questrip_root_Activity extends Activity implements LocationListener{
    private Button start_from_here;
    private Button start_from_there;
    private Button search_around;
    private Button char_collection;

    private LocationManager locationManager;
    public double latitude = 34.802991;             //処理に使う緯度
    public double longitude = 135.771159;           //処理に使う経度
    public double mascotLatitude = 35.6581;         //都道府県庁の緯度（キャラ取得用）
    public double mascotLongitude = 139.701742;     //都道府県庁の経度（キャラ取得用）

    private String area;                //現在位置の都道府県+庁 例:京都府庁

    //ご当地キャラget用------------------------------
    private String api_key_gotouchi = [GOTOUCHI API KEY];  //ご当地キャラAPI使用に用いるAPIキー
    private getItemDataAsync get_item;                  //ご当地キャラAPIへリクエストを投げるAsynctask
    private imageBuilderAsync image_builder;            //URLをBitmapイメージへ変換するAsynctask
    private String url_getitem;                         //APIリクエスト用のURL
    private Uri.Builder builder;                        //URL→Bitmap変換のURL
    private String json_getitem;                        //APIから返ってきたJSONデータ格納
    private String image_url[] = new String[50];        //JSONデータからimage部分のみをパースしてここに入れる．その後imageBuilderAsyncへ
    private Bitmap image_of_character;                  //ご当地キャラの画像データ

    private String font="Pixel10.ttf";  //8ビットフォント

    private Timer mTimer = null;
    private Handler mHandler = null;

    private int button_flag = 0;

    private ImageView logo;           //キャラクター表示のImageView

    global_values global;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questrip_root);

        global = (global_values)this.getApplication();

        start_from_here = (Button)findViewById(R.id.button_fromhere);
        start_from_here.setTypeface(Typeface.createFromAsset(getAssets(), font));
        start_from_there = (Button)findViewById(R.id.button_fromthere);
        start_from_there.setTypeface(Typeface.createFromAsset(getAssets(), font));
        search_around = (Button)findViewById(R.id.button_search_around);
        search_around.setTypeface(Typeface.createFromAsset(getAssets(), font));
        char_collection = (Button)findViewById(R.id.button_char_collection);
        char_collection.setTypeface(Typeface.createFromAsset(getAssets(), font));

        logo = (ImageView)findViewById(R.id.logo);
        logo.setAlpha(0);

        setClickListener();

        //いろんな初期処理 画像取得しないとなのでここ---------------------
        locationStart();            //現在地取得
        onGetAddress();             //現在の位置情報から都道府県を取得
        if(area!=null) onGetMascotLocation();      //都道府県から県庁の位置情報を取得
        image_getter();             //県庁の位置情報から
        //-------------------------------------------------

        global.bitmap = image_of_character;

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                setLogo();
            }
        }, 1000);
    }

    void image_getter(){
        //-------------ご当地キャラget用のasynctask 2016_10_25 by Takaya-----------------------------
        JSONObject jsonData_getitem = null; //画像取得用Asyncから戻ってきたJsonを格納するオブジェクト
        url_getitem = "http://localchara.jp/services/api//search/location/character?api_key="
                +api_key_gotouchi+"&ll="+mascotLatitude+","+mascotLongitude+"&counts="+1;
        get_item = new getItemDataAsync();
        try {
            json_getitem = get_item.execute(url_getitem).get();
            jsonData_getitem = new JSONObject(json_getitem);
            JSONArray datas = jsonData_getitem.getJSONArray("result");

            for (int i = 0; i < datas.length(); i++) {
                JSONObject data = datas.getJSONObject(i);
                // 名前を取得
                image_url[i] = data.getString("image_full");
                builder = Uri.parse(image_url[i]).buildUpon();
            }
            image_builder = new imageBuilderAsync();
            image_of_character = image_builder.execute(builder).get();
        } catch (InterruptedException e) {e.printStackTrace();}
        catch (ExecutionException e) {e.printStackTrace();}
        catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    protected void onRestart(){
        super.onRestart();

        buttonOn();
    }

    void setLogo(){
        logo.setAlpha(255);
        logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.a2));
    }

    void buttonOf(){
        start_from_here.setEnabled(false);
        start_from_there.setEnabled(false);
        search_around.setEnabled(false);
        char_collection.setEnabled(false);
    }

    void buttonOn(){
        start_from_here.setEnabled(true);
        start_from_there.setEnabled(true);
        search_around.setEnabled(true);
        char_collection.setEnabled(true);
        start_from_here.setText("　　ここから冒険をはじめる　　　");
        start_from_there.setText("　　違う場所から冒険をはじめる　");
        search_around.setText("　　周辺のスポットまで冒険する　");
        char_collection.setText("　　ご当地キャラ図鑑をみる　　　");
    }

    void select_mode(final Button button){
        final String str = (String) button.getText();
        mHandler = new Handler();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(button_flag==0) {
                            button.setText("　　　　　　　　　　　　　　　　");
                            button_flag = 1;
                        } else {
                            button.setText(str);
                            button_flag = 0;
                        }
                    }
                });
            }
        }, 0, 200); // 実行したい間隔(ミリ秒)
    }

    void setClickListener(){
        start_from_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.point = 0;
                start_from_here.setText("　＞ここから冒険をはじめる　　　");
                buttonOf();
                select_mode(start_from_here);
                HandlerThread handlerThread = new HandlerThread("dispmessage");
                handlerThread.start();
                new Handler(handlerThread.getLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //インテントの作成
                        Intent intent=new Intent();
                        intent.setClassName("com.example.my_boss.questrip","com.example.my_boss.questrip.questrip_setting_Activity_2");
                        startActivity(intent);
                    }
                }, 1000);
            }
        });

        start_from_there.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.point = 0;
                start_from_there.setText("　＞違う場所から冒険をはじめる　");
                buttonOf();
                select_mode(start_from_there);
                HandlerThread handlerThread = new HandlerThread("dispmessage");
                handlerThread.start();
                new Handler(handlerThread.getLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent=new Intent();
                        intent.setClassName("com.example.my_boss.questrip","com.example.my_boss.questrip.questrip_setting_there_Activity");
                        startActivity(intent);
                    }
                }, 1000);
            }
        });

        search_around.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.point = 0;
                search_around.setText("　＞周辺のスポットまで冒険する　");
                buttonOf();
                select_mode(search_around);
                HandlerThread handlerThread = new HandlerThread("dispmessage");
                handlerThread.start();
                new Handler(handlerThread.getLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent=new Intent();
                        intent.setClassName("com.example.my_boss.questrip","com.example.my_boss.questrip.Instagram_connect_Activity");
                        startActivity(intent);
                    }
                }, 1000);
            }
        });

        char_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char_collection.setText("　＞ご当地キャラ図鑑をみる　　　");
                buttonOf();
                select_mode(char_collection);
                Intent intent=new Intent();
                intent.setClassName("com.example.my_boss.questrip","com.example.my_boss.questrip.questrip_collection_Activity");
                startActivity(intent);
            }
        });
    }

    private void locationStart() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Location nowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1000, this);

        latitude=nowLocation.getLatitude();       //最新GPS値の緯度を格納
        longitude=nowLocation.getLongitude();     //最新GPS値の経度を格納

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
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

    //現在位置情報から都道府県を取得---------------------------------------
    public void onGetAddress() {
        Geocoder gcoder = new Geocoder(this, Locale.getDefault());
        int maxResults = 1;
        List<Address> lstAddr = null;
        try {
            lstAddr = gcoder.getFromLocation(latitude, longitude, maxResults);
        } catch (IOException e) { e.printStackTrace();}

        if (lstAddr != null && lstAddr.size() > 0) {
            Address addr = lstAddr.get(0);
            area = addr.getAdminArea();
        }
        if (area!=null) getMascotLocation();
        else {mascotLatitude=latitude; mascotLongitude=longitude;}
    }

    //都道府県から県庁の位置情報を取得-------------------------------------
    public void onGetMascotLocation() {
        String searchKey = area;
        Geocoder gcoder = new Geocoder(this, Locale.getDefault());
        int maxResults = 1;
        List<Address> lstAddr = null;
        // 位置情報の取得
        try {
            lstAddr = gcoder.getFromLocationName(searchKey, maxResults);
        } catch (IOException e) { e.printStackTrace(); }

        if (lstAddr != null && lstAddr.size() > 0) {
            // 緯度・経度取得
            Address addr = lstAddr.get(0);
            mascotLatitude = addr.getLatitude();
            mascotLongitude = addr.getLongitude();
        }
    }

    //キャラクター用位置情報取得のための例外処理
    void getMascotLocation(){
        switch (area){
            case "兵庫県": area = "神戸市役所"; break;
            case "岡山県": area = "岡山県庁駐車場"; break;
            case "広島県": area = "広島県自治会"; break;
            case "山口県": area = "山口県政資料館"; break;
            case "大阪府": area = "大阪城"; break;
            case "三重県": area = "三重県警察本部"; break;
            case "佐賀県": area = "佐賀県立図書館"; break;
            case "沖縄県": area = "那覇市役所市 民文化部長室真和志支所"; break;
            case "福井県": area = "福井県福井市大手２丁目３−１ 三の丸ビル"; break;
            case "岐阜県": area = "中部管区警察局岐阜県情報通信部"; break;
            case "長野県": area = "信濃毎日新聞社"; break;
            case "千葉県": area = "千葉銀行県庁支店"; break;
            case "東京都": area = "UCC喫茶コーナー 都庁第二本庁舎31階";break;
            case "北海道": area = "ホテルグレイスリー札幌（ワシントンホテルチェーン）"; break;
            case "青森県": area = "青森税務署"; break;
            default: area = area + "庁";
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {}

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}
}
