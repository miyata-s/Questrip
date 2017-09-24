// 共通変数を創る（最終目的地）
// 中継地点につきました
// ゴールしました
// 大輔：UIのデザイン（ご当地キャラだしたりとか）
//ホンダ：マーカーのポップアップを出す

//・マニフェスト追記
//・レイアウト追加
//・Instagram_connect_Activity の遷移部分追記
//・


//        ゴールアクティビティ
//        セリフを考える
//        ルートの色変更
//        図鑑

package com.example.my_boss.questrip;

//styles.xml にNo action bar を記述
//プログレス通知はすべて消す


import android.*;
import android.Manifest;
//import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;

//=======================================================================================================================v
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
//=========================================v
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.common.GooglePlayServicesUtil;

//import android.app.ProgressDialog;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
//=======================================================================================================================^

public class Guide extends FragmentActivity implements
        LocationListener,
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        View.OnClickListener{

    private LocationManager locationManager;  //GPSデータ取得用のオブジェクト

    private long minTime_GPS = 1000;
    private long minDistance_GPS = 5;
//    public double latitude = 35.710065;       //適当な緯度
//    public double longitude = 139.8107;       //適当な経度

//    ===================================================================================v
//        private MapFragment mMap;
//    ===================================================================================^

    //    ===================================================================================v

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    GoogleMap mMap;
    private static final int MENU_A = 0;
    private static final int MENU_B = 1;
    private static final int MENU_c = 2;

    public static String posinfo = "";
    public static String info_A = "";
    public static String info_B = "";
    ArrayList<LatLng> markerPoints;

    public static MarkerOptions options;

//    public ProgressDialog progressDialog;

    public String travelMode = "walking";//default driving

    //    現在地
    public double latitude_CurrentPoint = 34.80058444;       //適当な緯度
    public double longitude_CurrentPoint = 135.76810719;       //適当な経度

    //    中継地点（すきや）
    public double latitude_RelayPoint = 34.69428345331;       //適当な緯度
    public double longitude_RelayPoint = 135.19914027303;       //適当な経度

    //    中継地点（しおん）
//    public double latitude_RelayPoint = 34.813731;       //適当な緯度
//    public double longitude_RelayPoint = 135.771333;       //適当な経度

    //    目的地 34.802556297454004,135.53884506225586
//    public double latitude_Destination = 34.68756965238;       //適当な緯度
//    public double longitude_Destination = 135.1965938508;       //適当な経度
    public double latitude_Destination ;       //適当な緯度
    public double longitude_Destination ;       //適当な経度

//    tapped_marker[0] = 34.69162119;
//    tapped_marker[1] = 135.192208036;
//    中継地点と同じ
//    public double latitude_Destination = 34.69428345331;
//    public double longitude_Destination = 135.19914027303;

    private int flag_of_mapManager = 0;       // ルートの時間探索のときにマップを表示するかのフラグ
    private int routeTime_Current_Distination = 0;       // ルートの時間探索のときにマップを表示するかのフラグ
    private int routeTime_Current_Relay_Distination = 0;       // ルートの時間探索のときにマップを表示するかのフラグ
    private int routeTime_Current_Relay = 0;

    //    直線距離から到着したか判定
    private float goal_JudgeDistance = 10;
    private boolean goal_Judge = false; //到着したらtrue


    private Timer mTimer = null;
    private Handler mHandler = null;
    private Timer mmTimer = null;
    private Handler mmHandler = null;

    //1文字ずつ表示するのに必要な変数----
    private String put_txt;
    private String put_word;
    private String put_text;
    private int i = 0;
    //------------------------------


    //2016_11_05_19_40 crated by TAKAYA
    private String url_post_server = [DB URL];
    private String url_getitem;
    private String api_key_gotouchi = [GOTOUCHI API KEY];
    private getItemDataAsync get_gotouchi;
    private String json_postitem;
    private post_server_Async post_server_async;

    global_values global;
    private String userID;

    private RelativeLayout recyclerView;
    private RelativeLayout gameframe;
    private RelativeLayout layout;
    private TextView text;
    private TextView message;
    private LinearLayout botton;
    private ImageView mascot;
    private int height;
    private int width;
    private String font = "Pixel10.ttf";  //8ビットフォント





//    ===================================================================================^

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private GoogleApiClient client;

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.button_route_update:
                    // クリック処理
//                    Toast.makeText(this, "UPDATE", Toast.LENGTH_LONG).show();
//                        現在地を更新
                    currentLocation();

                    mMap.clear();
//                        現在から目的地
//                      Current_Distination_Time(latitude_CurrentPoint, longitude_CurrentPoint, latitude_Destination, longitude_Destination);
//                      Current_Distination_Map(latitude_CurrentPoint, longitude_CurrentPoint, latitude_Destination, longitude_Destination);

//                        中継地経由で目的地
//                      Current_Relay_Distination_Time(latitude_CurrentPoint, longitude_CurrentPoint,latitude_RelayPoint, longitude_RelayPoint, latitude_Destination, longitude_Destination);
//                        Current_Relay_Distination_Map(latitude_CurrentPoint, longitude_CurrentPoint,latitude_RelayPoint, longitude_RelayPoint, latitude_Destination, longitude_Destination);

//                        中継地まで
//                      Current_Relay_Time(latitude_CurrentPoint, longitude_CurrentPoint,latitude_RelayPoint, longitude_RelayPoint);
                    Current_Relay_Map(latitude_CurrentPoint, longitude_CurrentPoint,latitude_RelayPoint, longitude_RelayPoint);

                    RelayPointPointing(mMap);
                    DistinationPointing(mMap);

                    break;

                case R.id.button_go_distination:
                    // クリック処理
//                    Toast.makeText(this, "DIST", Toast.LENGTH_LONG).show();

                    //OKボタンが押された時の処理（遷移）
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    // アラートダイアログのタイトルを設定します
//                    alertDialogBuilder.setTitle("確認");
                    // アラートダイアログのメッセージを設定します
                    alertDialogBuilder.setMessage("目的地選択画面に移動しますか？");
                    // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
                    alertDialogBuilder.setPositiveButton("移動する",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent=new Intent();
                                    intent.setClassName("com.example.my_boss.questrip","com.example.my_boss.questrip.Instagram_connect_Activity");
                                    startActivity(intent);
                                    finish();

                                }
                            });
                    // アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
                    alertDialogBuilder.setNegativeButton("キャンセル",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    // アラートダイアログのキャンセルが可能かどうかを設定します
//                    alertDialogBuilder.setCancelable(true);
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // アラートダイアログを表示します
                    alertDialog.show();


//                        現在地を更新
//                    currentLocation();

//                    mMap.clear();

//                        現在から目的地
//                      Current_Distination_Time(latitude_CurrentPoint, longitude_CurrentPoint, latitude_Destination, longitude_Destination);
//                        Current_Distination_Map(latitude_CurrentPoint, longitude_CurrentPoint, latitude_Destination, longitude_Destination);

//                        中継地経由で目的地
//                      Current_Relay_Distination_Time(latitude_CurrentPoint, longitude_CurrentPoint,latitude_RelayPoint, longitude_RelayPoint, latitude_Destination, longitude_Destination);
//                        Current_Relay_Distination_Map(latitude_CurrentPoint, longitude_CurrentPoint,latitude_RelayPoint, longitude_RelayPoint, latitude_Destination, longitude_Destination);

//                        中継地まで
//                      Current_Relay_Time(latitude_CurrentPoint, longitude_CurrentPoint,latitude_RelayPoint, longitude_RelayPoint);
//                        Current_Relay_Map(latitude_CurrentPoint, longitude_CurrentPoint,latitude_RelayPoint, longitude_RelayPoint);

//                    RelayPointPointing(mMap);
//                    DistinationPointing(mMap);
                    break;

                default:
                    break;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        //グローバル変数を取得(2016_11_05_19_40 TAKAYA)
        global = (global_values)this.getApplication();
//        userID = global.user;
        userID = "ytakaya";
        latitude_Destination = global.latitude_final;
        longitude_Destination = global.longitude_final;
        //TAKAYAここまで


        // WindowManagerのインスタンス取得
        WindowManager wm = getWindowManager();
        // Displayのインスタンス取得
        Display disp = wm.getDefaultDisplay();
        height= disp.getHeight();
        width = disp.getWidth();

        layout = (RelativeLayout)findViewById(R.id.activity_main);
        gameframe = (RelativeLayout)findViewById(R.id.mapframe);
        text = (TextView)findViewById(R.id.myboss);
        botton = (LinearLayout)findViewById(R.id.bottonll);
        mascot = (ImageView)findViewById(R.id.mascot);
        recyclerView = (RelativeLayout)findViewById(R.id.recyclerview);
        message = (TextView)findViewById(R.id.message);
        message.setTypeface(Typeface.createFromAsset(getAssets(), font));

        int width = global.bitmap.getWidth();      //サイズ直す用
        int height = global.bitmap.getHeight();    //サイズ直す用
        // ↓ 画像の大きさを統一
        Bitmap bitmap = Bitmap.createScaledBitmap(global.bitmap,
                disp.getWidth()/3,(disp.getWidth()/3)*height/width,  false);
        mascot.setImageBitmap(bitmap);                      //画像を登録


//        インテント間の受け渡し
        // 現在のintentを取得する
        Intent intent = getIntent();
        // intentから指定キーのDoubleを取得する
        latitude_RelayPoint = intent.getDoubleExtra("latitude_relaypoint",0);
        longitude_RelayPoint = intent.getDoubleExtra("longitude_relaypoint",0);
//        RelayPoint_setting(latitude_RelayPoint,longitude_RelayPoint);
//        Destination_setting(latitude_Destination,longitude_Destination);

//    ===================================================================================v
////        google mapを表示するにはfragmentViewを使う
//        mMap = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//        mMap.getMapAsync(this);     //mapの操作は非同期処理（onMapReadyで）

//        MapFragment mapFragment =
//                (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//        mMap = mapFragment.getMapAsync(this);

        //プログレス
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.setMessage("CALCULATION");
//        progressDialog.hide();

        //初期化
        markerPoints = new ArrayList<LatLng>();

        //座標配列の初期化
//        LatLng location = new LatLng(latitude_CurrentPoint, longitude_CurrentPoint);
//        LatLng location_CurrentPoint = new LatLng(latitude_CurrentPoint, longitude_CurrentPoint);
//        LatLng location_RelayPoint = new LatLng(latitude_RelayPoint, longitude_RelayPoint);
//        LatLng location_Distination = new LatLng(latitude_Destination, longitude_Destination);

        //マップフラグメントの定義
        SupportMapFragment mapfragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapfragment.getMapAsync(this);     //mapの操作は非同期処理（onMapReadyで）

//        MapFragment.getMap()メソッドは非推奨になったため、getMapAsync()を使う
//        http://nvtrlab.jp/blog/penco/mapfragment%E3%81%AE%E3%82%B9%E3%82%AF%E3%83%AD%E3%83%BC%E3%83%AB%E3%82%92%E6%A4%9C%E7%9F%A5%E3%81%99%E3%82%8B-2.html
//        mMap = mapFragment.getMapAsync(this);

        locationStart();

        //関数 locationStart を呼び出してインテント開始
//        pointSearch(); // 地点を基準に検索
//        nameSearch(); // こっちは使わない（地名を引数として検索する）

        findViewById(R.id.button_route_update).setOnClickListener(this);
        findViewById(R.id.button_go_distination).setOnClickListener(this);

//    ===================================================================================^

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //    ===================================================================================v
    private void currentLocation() {
        //    ----------------------------------------------------------------v
        // LocationManager インスタンス生成
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }

        //GPS地の更新（第一引数：provider（network または gps），第二引数：minTime（位置情報を取得する最小時間），第三引数：minDistance（位置情報を取得する最小距離），第四引数：listener（リスナーを実装しているクラス）
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime_GPS, minDistance_GPS, this);

        Location CurrentLocation = locationManager.getLastKnownLocation("gps");     //最新のGPS値の取得
        latitude_CurrentPoint = CurrentLocation.getLatitude();       //最新GPS値の緯度を格納
        longitude_CurrentPoint = CurrentLocation.getLongitude();     //最新GPS値の経度を格納

        System.out.println("=========================================================");
//        Toast.makeText(this, +latitude_CurrentPoint+"    "+longitude_CurrentPoint, Toast.LENGTH_LONG).show();
        System.out.println(latitude_CurrentPoint);
        System.out.println(longitude_CurrentPoint);
        System.out.println("=========================================================");
    }


    private void locationStart() {
        Log.d("debug", "locationStart()");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //GPS地の更新（第一引数：provider（network または gps），第二引数：minTime（位置情報を取得する最小時間），第三引数：minDistance（位置情報を取得する最小距離），第四引数：listener（リスナーを実装しているクラス）
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime_GPS, minDistance_GPS, this);

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            // GPSを設定するように促す
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug", "gpsEnable, startActivity");
        } else {
            Log.d("debug", "gpsEnabled");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }
        //    ----------------------------------------------------------------^
    }
    //    ===================================================================================^

    //    ===================================================================================v
//    中継地点のピン建て
    public void RelayPointPointing(GoogleMap googleMap){
        LatLng relay_palce = new LatLng(latitude_RelayPoint, longitude_RelayPoint);
        mMap.addMarker(new MarkerOptions().position(relay_palce).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    //    到着地点のピン建て
    public void DistinationPointing(GoogleMap googleMap){
        LatLng dist_palce = new LatLng(latitude_Destination, longitude_Destination);
        mMap.addMarker(new MarkerOptions().position(dist_palce).icon(BitmapDescriptorFactory.defaultMarker(150)));
    }
    //    ===================================================================================^


    @Override
    //google mapに関する処理
    public void onMapReady(GoogleMap googleMap) {
//    ===================================================================================v
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);

        currentLocation();
        LatLng latlng_current = new LatLng(latitude_CurrentPoint, longitude_CurrentPoint);    //緯度と経度格納（将来的には配列化してinstagramから取得した緯度，経度情報を格納？）

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng_current, 18)); //latlngに指定されている場所へカメラ移動

        Current_Relay_Map(latitude_CurrentPoint, longitude_CurrentPoint,latitude_RelayPoint, longitude_RelayPoint);
        RelayPointPointing(mMap);
        DistinationPointing(mMap);
//
//    ===================================================================================^



//    ===================================================================================v
//        if (googleMap != null) {
//            googleMap.addMarker(new MarkerOptions().position(latlng).title("Skytree"));     //マップ上にピンを追加
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));            //latlngに指定されている場所へカメラ移動
//        }






//        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% vv
//        if (mMap != null) {
//            // タップ時のイベントハンドラ登録
//            mMap.setOnMapClickListener(new OnMapClickListener() {
//                @Override
//                public void onMapClick(LatLng point) {
//
////                    // TODO Auto-generated method stub
////                    mMap.clear();
////                    mMap.addMarker(new MarkerOptions().position(point));
////                    Toast.makeText(getApplicationContext(), "タップ位置\n緯度：" + point.latitude + "\n経度:" + point.longitude, Toast.LENGTH_LONG).show();
//
//                }
//            });
//
//            // 長押し時のイベントハンドラ登録
//            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//                @Override
//                public void onMapLongClick(LatLng point) {
//                    // TODO Auto-generated method stub
////                    mMap.clear();
////                    mMap.addMarker(new MarkerOptions().position(point));
////                    Toast.makeText(getApplicationContext(), "長押し位置\n緯度：" + point.latitude + "\n経度:" + point.longitude, Toast.LENGTH_LONG).show();
////
////                    RelayPointPointing(mMap);
////                    DistinationPointing(mMap);
////                    時間計算
////                    Current_Distination_Time(latitude_CurrentPoint, longitude_CurrentPoint, latitude_Destination, longitude_Destination);
////                    Current_Distination_Map(latitude_CurrentPoint, longitude_CurrentPoint, latitude_Destination, longitude_Destination);
//
////                    マップ出力
////                    Current_Relay_Distination_Time(latitude_CurrentPoint, longitude_CurrentPoint,latitude_RelayPoint, longitude_RelayPoint, latitude_Destination, longitude_Destination);
////                    Current_Relay_Distination_Map(latitude_CurrentPoint, longitude_CurrentPoint,latitude_RelayPoint, longitude_RelayPoint, latitude_Destination, longitude_Destination);
//                }
//            }
//            );
//        }
//        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ~~

        // 現在地ボタンの表示
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        UiSettings settings = mMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);
//    ===================================================================================^
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug", "checkSelfPermission true");

                locationStart();
                return;

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    //GPSの取得位置が変わった時
    @Override
    public void onLocationChanged(Location location) {
//        judge();
    }

    void setView(){
        gameframe.setId(1); text.setId(2); recyclerView.setId(3); botton.setId(4);

        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, (int)(height*0.51));
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp2.addRule(RelativeLayout.BELOW,1);
        lp3.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp3.addRule(RelativeLayout.BELOW,2);
        lp3.addRule(RelativeLayout.ABOVE,4);
        lp4.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp4.addRule(RelativeLayout.CENTER_HORIZONTAL);

        lp1.setMargins(0,0,0,0);
        lp2.setMargins(0,10,0,80);
        lp3.setMargins(0,0,0,0);
        lp4.setMargins(0,80,0,0);

        layout.removeAllViews();
        layout.addView(gameframe,lp1);
        layout.addView(text,lp2);
        layout.addView(botton,lp4);
        layout.addView(recyclerView,lp3);
    }

    public void judge(){
        //        中継地点との距離
//        Toast.makeText(this, "judge："+goal_Judge, Toast.LENGTH_LONG).show();
        currentLocation();
        //    ===================================================================================v
        float[] results = new float[1];
        Location.distanceBetween(latitude_CurrentPoint, longitude_CurrentPoint, latitude_RelayPoint, longitude_RelayPoint, results);
        System.out.println("*******************************************");
//        if(goal_Judge == false){
//            Toast.makeText(this, "距離："+results[0], Toast.LENGTH_LONG).show();
//        }
        System.out.println("*******************************************");

        System.out.println(latitude_RelayPoint+"   "+latitude_Destination);
        System.out.println(longitude_RelayPoint+"  "+longitude_Destination);


        if(results[0] < goal_JudgeDistance && goal_Judge == false){
            goal_Judge = true;
//            最終目的地か中継地点かの判断（relayの座標とdestinationの座標が同じ場合，最終目的地）
            if(latitude_RelayPoint == latitude_Destination && longitude_RelayPoint == longitude_Destination){
//                Toast.makeText(getApplicationContext(), "最終目的地にGOAL！！", Toast.LENGTH_LONG).show();


                //2016_11_05_19_40 created by TAKAYA
                url_getitem = "http://localchara.jp/services/api//search/location/character?api_key="
                        +api_key_gotouchi+"&ll="+latitude_CurrentPoint+","+longitude_CurrentPoint+"&counts="+30;

                get_gotouchi = new getItemDataAsync();
                try {
                    json_postitem = get_gotouchi.execute(url_getitem).get();

                } catch (InterruptedException e) {e.printStackTrace();}
                catch (ExecutionException e) {e.printStackTrace();}


//                Toast toast = Toast.makeText(getApplicationContext(), json_postitem, Toast.LENGTH_SHORT);
//                toast.show();


                url_post_server = url_post_server + userID;

                post_server_async = new post_server_Async();

                post_server_async.execute(url_post_server,json_postitem);
                //TAKAYAここまで

                Intent intent = new Intent();
                intent.setClassName("com.example.my_boss.questrip", "com.example.my_boss.questrip.questrip_waypoint_goal_Activity");
                startActivity(intent);
                finish();

            }
            else{

//                Toast.makeText(getApplicationContext(), "中継地点にGOAL！！", Toast.LENGTH_LONG).show();


                //2016_11_05_19_40 crated by TAKAYA
                url_getitem = "http://localchara.jp/services/api//search/location/character?api_key="
                        +api_key_gotouchi+"&ll="+latitude_CurrentPoint+","+longitude_CurrentPoint+"&counts="+30;

                get_gotouchi = new getItemDataAsync();
                try {
                    json_postitem = get_gotouchi.execute(url_getitem).get();

                } catch (InterruptedException e) {e.printStackTrace();}
                catch (ExecutionException e) {e.printStackTrace();}

//                Toast toast = Toast.makeText(getApplicationContext(), json_postitem, Toast.LENGTH_SHORT);
//                toast.show();

                url_post_server = "http://192.168.20.23:3000/zukan/:"+userID;

                post_server_async = new post_server_Async();

                post_server_async.execute(url_post_server,json_postitem);
                //TAKAYAここまで

                Intent intent = new Intent();
                intent.setClassName("com.example.my_boss.questrip", "com.example.my_boss.questrip.questrip_waypoint_goal_Activity");
                startActivity(intent);
                finish();

            }
        }
        //    ===================================================================================^

    }

    //providerが変わった時
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    //providerが利用可能になった時
    @Override
    public void onProviderEnabled(String provider) {

    }

    //providerが利用不可になった時
    @Override
    public void onProviderDisabled(String provider) {

    }

//    ===================================================================================v
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public com.google.android.gms.appindexing.Action getIndexApiAction() {
        com.google.android.gms.appindexing.Thing object = new com.google.android.gms.appindexing.Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new com.google.android.gms.appindexing.Action.Builder(com.google.android.gms.appindexing.Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(com.google.android.gms.appindexing.Action.STATUS_TYPE_COMPLETED)
                .build();
    }
//    ===================================================================================^

    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        setView();

        mHandler = new Handler();
        mTimer = new Timer();
//        mTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        judge();
//                    }
//                });
//            }
//        }, 1000, 10000); // 実行したい間隔(ミリ秒)

        //文字表示専用スレッド
        mmHandler = new Handler();
        mmTimer = new Timer();
        mmTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        judge();

                        if(goal_Judge == false){
//                            コンテンツ決定
                            put_text = "";
                            put_word = "";
                            i = 0;
                            put_txt = talkContents();
                            handler.sendEmptyMessage(1);
                        }
                        else{
                            put_text = "";
                            put_word = "";
                            i = 0;
                            put_txt = "到着したようだね。\nお疲れ様．．．";
                            handler.sendEmptyMessage(1);
                        }
//                        String str = "";
//                        int random = (int) (Math.random() * 3) + 1;
//                        if (random == 1) str = "どんどん進もう";
//                        else if (random == 2) str = "少し休憩するかい？";
//                        else if (random == 3) str = "もう少しだ";
//                        put_text = "";
//                        put_word = "";
//                        i = 0;
//                        put_txt = str;
//                        handler.sendEmptyMessage(1);
                    }
                });
            }
        }, 1000, 10000); // 実行したい間隔(ミリ秒)

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
        com.google.android.gms.appindexing.AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    private String talkContents(){
//          現在地取得
//        currentLocation();
//        到着までの時間を取得
//        Current_Relay_Time(latitude_CurrentPoint,longitude_CurrentPoint,latitude_RelayPoint,longitude_RelayPoint);
//        到着までの時間でセリフを変更（routeTime_Current_Relay）
        String serif = "";
        float[] results = new float[1];
        Location.distanceBetween(latitude_CurrentPoint, longitude_CurrentPoint, latitude_RelayPoint, longitude_RelayPoint, results);
        int random = (int) (Math.random() * 3);
        if(results[0] < 50){
            switch(random){
                case 0:
                    serif = "もう少しだ．．．\n";
                    break;
                case 1:
                    serif = "近いな．．．";
                    break;
                case 2:
                    serif = "油断すると道に迷うからね";
                    break;
            }
        }
        else if(results[0] < 300){
            switch(random){
                case 0:
                    serif = "左のボタンで\n経路を再探索できるよ";
                    break;
                case 1:
                    serif = "気をつけて歩くんだよ";
                    break;
                case 2:
                    serif = "景色はどうだ？";
                    break;
            }
        }
        else{
            switch(random){
                case 0:
                    serif = "まだ遠い．．．";
                    break;
                case 1:
                    serif = "右のボタンで\n行き先を変更できるよ";
                    break;
                case 2:
                    serif = "道のりは険しさを増す．．．";
                    break;
            }
        }
        return serif;
    }


    // 文字列を一文字ずつ出力するハンドラ------------------------
    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // 文字列を配列dataに１文字ずつセット
            char data[] = put_txt.toCharArray();//（1）
            if (i < data.length) {//(2)
                if (msg.what == 1) {
                    put_word = String.valueOf(data[i]);//(3)
                    put_text = put_text + put_word;//(4)
                    message.setText(put_text);//(5)
                    handler.sendEmptyMessageDelayed(1, 120);
                    i++;
                } else {
                    super.dispatchMessage(msg);
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }


    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        com.google.android.gms.appindexing.AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

//    ===================================================================================v

    //    時間出力（現在地から目的地）
    private void Current_Distination_Time
    (double lat_CurrentPoint, double lng_CurrentPoint, double lat_Destination, double lng_Destination){
        int time = 0;

        mapManager_store(0);
        routeSearch_Current_Distination(lat_CurrentPoint, lng_CurrentPoint, lat_Destination, lng_Destination);
    }

    //    時間出力とマップに経路表示（現在地から目的地）
    private void Current_Distination_Map
    (double lat_CurrentPoint, double lng_CurrentPoint, double lat_Destination, double lng_Destination){
        int time = 0;

        mapManager_store(1);
        routeSearch_Current_Distination(lat_CurrentPoint, lng_CurrentPoint, lat_Destination, lng_Destination);
    }

    //    時間出力（現在地から中継地点を介して目的地）
    private void Current_Relay_Distination_Time
    (double lat_CurrentPoint, double lng_CurrentPoint, double lat_RelayPoint, double lng_RelayPoint, double lat_Destination, double lng_Destination){
        int time = 0;

        mapManager_store(2);
        routeSearch_Current_Relay_Distination(lat_CurrentPoint, lng_CurrentPoint, lat_RelayPoint, lng_RelayPoint, lat_Destination, lng_Destination);
    }

    //    時間出力とマップに経路表示（現在地から中継地点を介して目的地）
    private void Current_Relay_Distination_Map
    (double lat_CurrentPoint, double lng_CurrentPoint, double lat_RelayPoint, double lng_RelayPoint, double lat_Destination, double lng_Destination){
        int time = 0;

        mapManager_store(3);
        routeSearch_Current_Relay_Distination(lat_CurrentPoint, lng_CurrentPoint, lat_RelayPoint, lng_RelayPoint, lat_Destination, lng_Destination);
    }

    //    時間出力（現在地から中継地点）
    private void Current_Relay_Time
    (double lat_CurrentPoint, double lng_CurrentPoint, double lat_RelayPoint, double lng_RelayPoint){
        int time = 0;

        mapManager_store(4);
        routeSearch_Current_Relay(lat_CurrentPoint, lng_CurrentPoint, lat_RelayPoint, lng_RelayPoint);
    }

    //    時間出力とマップに経路表示（現在地から中継地点）
    private void Current_Relay_Map
    (double lat_CurrentPoint, double lng_CurrentPoint, double lat_RelayPoint, double lng_RelayPoint){
        int time = 0;

        mapManager_store(5);
        routeSearch_Current_Relay(lat_CurrentPoint, lng_CurrentPoint, lat_RelayPoint, lng_RelayPoint);
    }


    private void mapManager_store(int flag){
        flag_of_mapManager = flag;
    }

    private int mapManager_output(){
        return flag_of_mapManager;
    }

    private void timeManager_Current_Distination_initialize(){
        routeTime_Current_Distination = 0;
        System.out.println("====-----====----- Current_Distination_time_initialize ====-----====-----");
        System.out.println(routeTime_Current_Distination);
    }

    private void timeManager_Current_Distination_store(int time){
        routeTime_Current_Distination = time;
        System.out.println("====-----====----- Current_Distination ====-----====-----");
        System.out.println(routeTime_Current_Distination);
    }

    private void timeManager_Current_Relay_Distination_store(int time){
        routeTime_Current_Relay_Distination = time;
        System.out.println("====-----====----- Current_Distination ====-----====-----");
        System.out.println(routeTime_Current_Distination);
    }

    private void timeManager_Current_Relay_store(int time){
        routeTime_Current_Relay = time;
        System.out.println("====-----====----- Current_Relay ====-----====-----");
        System.out.println(routeTime_Current_Relay);
    }

//    ===================================================================================^


    //以下，経路時間探索処理（トリガー：関数routeSearch()）
//    ===================================================================================v
    private void routeSearch_Current_Distination
    (double lat_CurrentPoint, double lng_CurrentPoint, double lat_Destination, double lng_Destination){
//        progressDialog.show();

//        ここに現在地と目的地の経路の座標を求めるところ
        String url = getDirectionsUrl_Current_Distination(lat_CurrentPoint, lng_CurrentPoint, lat_Destination, lng_Destination);

        DownloadTask downloadTask = new DownloadTask();

        downloadTask.execute(url);

    }

    private void routeSearch_Current_Relay_Distination
            (double lat_CurrentPoint, double lng_CurrentPoint, double lat_RelayPoint, double lng_RelayPoint, double lat_Destination, double lng_Destination){
//        progressDialog.show();

//        ここに現在地と目的地の経路の座標を求めるところ
        String url = getDirectionsUrl_Current_Relay_Dinsination(lat_CurrentPoint, lng_CurrentPoint, lat_RelayPoint, lng_RelayPoint, lat_Destination, lng_Destination);

        DownloadTask downloadTask = new DownloadTask();

        downloadTask.execute(url);

    }

    private void routeSearch_Current_Relay
            (double lat_CurrentPoint, double lng_CurrentPoint, double lat_Relay, double lng_Relay){
//        progressDialog.show();

//        ここに現在地と目的地の経路の座標を求めるところ
        String url = getDirectionsUrl_Current_Distination(lat_CurrentPoint, lng_CurrentPoint, lat_Relay, lng_Relay);

        DownloadTask downloadTask = new DownloadTask();

        downloadTask.execute(url);
    }

    private String getDirectionsUrl_Current_Distination
            (double lat_CurrentPoint, double lng_CurrentPoint, double lat_Destination, double lng_Destination){

        String str_origin = "origin="+lat_CurrentPoint+","+lng_CurrentPoint;
        String str_dest = "destination="+lat_Destination+","+lng_Destination;

        String sensor = "sensor=false";

        //パラメータ
        String parameters = str_origin+"&"+str_dest+"&"+sensor + "&language=ja" + "&mode=" + travelMode;

        //JSON指定
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    private String getDirectionsUrl_Current_Relay_Dinsination
            (double lat_CurrentPoint, double lng_CurrentPoint, double lat_RelayPoint, double lng_RelayPoint, double lat_Destination, double lng_Destination){

        String str_origin = "origin="+lat_CurrentPoint+","+lng_CurrentPoint;
        String str_relay = "waypoints="+lat_RelayPoint+","+lng_RelayPoint;
        String str_dest = "destination="+lat_Destination+","+lng_Destination;

        String sensor = "sensor=false";

        //パラメータ
        String parameters = str_origin+"&"+str_dest+"&"+str_relay+"&"+sensor + "&language=ja" + "&mode=" + travelMode;

        //JSON指定
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }



//    以下，クラス ================================================

    private class DownloadTask extends AsyncTask<String, Void, String>{
        //非同期で取得

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
            }
            return data;
        }

        private String downloadUrl(String strUrl) throws IOException{
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try{
                URL url = new URL(strUrl);


                urlConnection = (HttpURLConnection) url.openConnection();


                urlConnection.connect();


                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while( ( line = br.readLine()) != null){
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            }catch(Exception e){
                Log.d("Exception download url", e.toString());
            }finally{
                iStream.close();
                urlConnection.disconnect();
            }

            return data;
        }

        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            int routetime = 0;

            switch (mapManager_output()) {
                case 0:
                    ParserTask_Time parserTask_Time = new ParserTask_Time();
                    routetime = parserTask_Time.get_time(result);
                    System.out.println(routetime);
                    System.out.println("====-----====----- case 0 ====-----====-----");
                    timeManager_Current_Distination_store(routetime);
                    break;
                case 1:
                    //マップまで表示する
                    ParserTask_Map parserTask_Map = new ParserTask_Map();
                    routetime = parserTask_Map.get_time(result);
                    parserTask_Map.execute(result);
                    System.out.println("====-----====----- case 1 ====-----====-----");
                    timeManager_Current_Distination_store(routetime);
                    break;
                case 2:
                    ParserTask_Time parserTask_Time2 = new ParserTask_Time();
                    routetime = parserTask_Time2.get_time(result);
                    System.out.println(routetime);
                    System.out.println("====-----====----- case 2 ====-----====-----");
                    timeManager_Current_Relay_Distination_store(routetime);
                    break;
                case 3:
                    //マップまで表示する
                    ParserTask_Map parserTask_Map2 = new ParserTask_Map();
                    routetime = parserTask_Map2.get_time(result);
                    parserTask_Map2.execute(result);
                    System.out.println("====-----====----- case 3 ====-----====-----");
                    timeManager_Current_Relay_Distination_store(routetime);
                    break;
                case 4:
                    ParserTask_Time parserTask_Time3 = new ParserTask_Time();
                    routetime = parserTask_Time3.get_time(result);
                    System.out.println(routetime);
                    System.out.println("====-----====----- case 4 ====-----====-----");
                    timeManager_Current_Relay_store(routetime);
                    break;
                case 5:
                    //マップまで表示する
                    ParserTask_Map parserTask_Map3 = new ParserTask_Map();
                    routetime = parserTask_Map3.get_time(result);
                    parserTask_Map3.execute(result);
                    System.out.println("====-----====----- case 5 ====-----====-----");
                    timeManager_Current_Relay_store(routetime);
                    break;
                default:

            }
        }
    }

    //    結果が帰ってくる(時間出力のみ)
    /*parse the Google Places in JSON format */
    private class ParserTask_Time {
        // 引数 : [現在地と最終目的地点の緯度・経度の配列]
        int get_time(String... jsonData) {

            JSONObject jObject;
            int routes_time = 0;
            Object value[] = new Object[2];

            try{
                jObject = new JSONObject(jsonData[0]);
                parseJsonpOfDirectionAPI parser = new parseJsonpOfDirectionAPI();

                // parse()により時間とルートを取得
                value = parser.parse(jObject);
                routes_time = (int)value[1];
            }catch(Exception e){
                e.printStackTrace();
            }

//            progressDialog.hide();
            return routes_time;
        }
    }

    //    結果が帰ってくる（時間出力，マップ案内）
    /*parse the Google Places in JSON format */
    private class ParserTask_Map extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // 引数 : [現在地と最終目的地点の緯度・経度の配列]
        int get_time(String... jsonData) {

            JSONObject jObject;
            int routes_time = 0;
            Object value[] = new Object[2];

            try{
                jObject = new JSONObject(jsonData[0]);
                parseJsonpOfDirectionAPI parser = new parseJsonpOfDirectionAPI();

                // parse()により時間とルートを取得
                value = parser.parse(jObject);
                routes_time = (int)value[1];
            }catch(Exception e){
                e.printStackTrace();
            }

//            progressDialog.hide();
            return routes_time;
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            Object value[] = new Object[2];

            try{
                jObject = new JSONObject(jsonData[0]);
                parseJsonpOfDirectionAPI parser = new parseJsonpOfDirectionAPI();

                value = parser.parse(jObject); // タスクを投げる 返り値が結果
            }catch(Exception e){
                e.printStackTrace();
            }

            System.out.println("================------------------------================");
            System.out.println(value[1]);
            System.out.println("================------------------------================");

            return (List<List<HashMap<String, String>>>) value[0];
        }

        //ルート検索で得た座標を使って経路表示
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            if(result.size() != 0){

                for(int i=0;i<result.size();i++){
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();


                    List<HashMap<String, String>> path = result.get(i);


                    for(int j=0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    //ポリライン
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(0x550000ff);
//                    lineOptions.visible(false);

                }

                //描画
                mMap.addPolyline(lineOptions);
            }else{
                mMap.clear();
//                Toast.makeText("ルート情報を取得できませんでした", Toast.LENGTH_LONG).show();
            }
//            progressDialog.hide();

        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        //getMenuInflater().inflate(R.menu.main, menu);
//        menu.add(0, MENU_A,   0, "Info");
//        menu.add(0, MENU_B,   0, "Legal Notices");
//        menu.add(0, MENU_c,   0, "Mode");
//        return true;
//    }

//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch ( item.getItemId() )
//        {
//            case MENU_A:
////                show_mapInfo();
//                Toast.makeText(this, "アイテム A", Toast.LENGTH_LONG).show();
//                return true;
//
//            case MENU_B:
//                Toast.makeText(this, "アイテム B", Toast.LENGTH_LONG).show();
////                //Legal Notices(免責事項)
////
////                String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getApplicationContext());
////                AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(this);
////                LicenseDialog.setTitle("Legal Notices");
////                LicenseDialog.setMessage(LicenseInfo);
////                LicenseDialog.show();
//
//                return true;
//
//            case MENU_c:
//                Toast.makeText(this, "アイテム c", Toast.LENGTH_LONG).show();
//                //show_settings();
//                return true;
//
//        }
//        return false;
//    }

//    //リ･ルート検索
//    private void re_routeSearch(){
//        progressDialog.show();
//
//        LatLng origin = markerPoints.get(0);
//        LatLng dest = markerPoints.get(1);
//
//        //
//        mMap.clear();
//
//        //マーカー
//        //A
//        options = new MarkerOptions();
//        options.position(origin);
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.green));
//        options.title("A");
//        options.draggable(true);
//        mMap.addMarker(options);
//        //B
//        options = new MarkerOptions();
//        options.position(dest);
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.red));
//        options.title("B");
//        options.draggable(true);
//        mMap.addMarker(options);
//
//        String url = getDirectionsUrl(latitude_CurrentPoint, longitude_CurrentPoint, latitude_Destination, longitude_Destination);
//
//        DownloadTask downloadTask = new DownloadTask();
//
//
//        downloadTask.execute(url);
//
//    }

//    ===================================================================================^

}




//    LatLng current_palce = new LatLng(latitude_CurrentPoint, longitude_CurrentPoint);
//    LatLng relay_palce = new LatLng(latitude_RelayPoint, longitude_RelayPoint);
//    LatLng dist_palce = new LatLng(latitude_Destination, longitude_Destination);
//mMap.addMarker(new MarkerOptions().position(current_palce));
//        mMap.addMarker(new MarkerOptions().position(relay_palce).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//        mMap.addMarker(new MarkerOptions().position(dist_palce).icon(BitmapDescriptorFactory.defaultMarker(150)));
