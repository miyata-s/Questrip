package com.example.my_boss.questrip;

//styles.xml にNo action bar を記述

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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
import java.util.Date;

import android.app.ProgressDialog;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
//=======================================================================================================================^

public class Localization extends FragmentActivity implements
        LocationListener,
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        View.OnClickListener{

    private LocationManager locationManager;  //GPSデータ取得用のオブジェクト

    private long minTime_GPS = 1000;
    private long minDistance_GPS = 50;

//    ===================================================================================v
//        private MapFragment mMap;
//    ===================================================================================^

    // カスタム情報ウィンドウ v
    /** Demonstrates customizing the info window and/or its contents. */
    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        // These are both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;

        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 12, 0);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }

        }
    }
    // カスタム情報ウィンドウ ^


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

    public ProgressDialog progressDialog;

    public String travelMode = "walking";//default driving

    //    現在地
    public double latitude_CurrentPoint = 34.80058444;       //適当な緯度
    public double longitude_CurrentPoint = 135.76810719;       //適当な経度

    //    中継地点（すきや）
    public double latitude_RelayPoint = 34.813731;       //適当な緯度
    public double longitude_RelayPoint = 135.771333;       //適当な経度

    //    中継地点（しおん）
//    public double latitude_RelayPoint = 34.813731;       //適当な緯度
//    public double longitude_RelayPoint = 135.771333;       //適当な経度

    //    目的地 34.802556297454004,135.53884506225586
    public double latitude_Destination = 34.80542380559208;       //適当な緯度
    public double longitude_Destination = 135.7782707735896;       //適当な経度

    private int flag_of_mapManager = 0;       // ルートの時間探索のときにマップを表示するかのフラグ
    private int routeTime_Current_Distination = 0;       // ルートの時間探索のときにマップを表示するかのフラグ
    private int routeTime_Current_Relay_Distination = 0;       // ルートの時間探索のときにマップを表示するかのフラグ

//    直線距離から到着したか判定
    private float goal_Judge = 10;
    private boolean tapFragment_help = true;
    private boolean tapFragment_relay = true;
    private boolean tapFragment_dist = true;
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
                case R.id.help:
                    // クリック処理
                    Toast.makeText(this, "HELP", Toast.LENGTH_LONG).show();
                    if(tapFragment_help == true){
                        //                    時間計算
//                      Current_Distination_Time(latitude_CurrentPoint, longitude_CurrentPoint, latitude_Destination, longitude_Destination);
//                      Current_Distination_Map(latitude_CurrentPoint, longitude_CurrentPoint, latitude_Destination, longitude_Destination);

//                    マップ出力
//                      Current_Relay_Distination_Time(latitude_CurrentPoint, longitude_CurrentPoint,latitude_RelayPoint, longitude_RelayPoint, latitude_Destination, longitude_Destination);
                        Current_Relay_Distination_Map(latitude_CurrentPoint, longitude_CurrentPoint,latitude_RelayPoint, longitude_RelayPoint, latitude_Destination, longitude_Destination);
                    }
                    break;

                case R.id.relay:
                    // クリック処理
                    Toast.makeText(this, "RELAY", Toast.LENGTH_LONG).show();
                    if(tapFragment_relay == true){

                    }
                    break;

                case R.id.dist:
                    // クリック処理
                    Toast.makeText(this, "DIST", Toast.LENGTH_LONG).show();
                    if(tapFragment_dist == true){
                        Current_Relay_Distination_Map(latitude_CurrentPoint, longitude_CurrentPoint,latitude_RelayPoint, longitude_RelayPoint, latitude_Destination, longitude_Destination);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private Marker mJapan;
    private Marker start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_location);

//    ===================================================================================v
////        google mapを表示するにはfragmentViewを使う
//        mMap = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//        mMap.getMapAsync(this);     //mapの操作は非同期処理（onMapReadyで）

//        MapFragment mapFragment =
//                (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//        mMap = mapFragment.getMapAsync(this);

        //プログレス
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("CALCULATION");
        progressDialog.hide();

        //初期化
        markerPoints = new ArrayList<LatLng>();

        //座標配列の初期化
        LatLng location = new LatLng(latitude_CurrentPoint, longitude_CurrentPoint);
        LatLng location_CurrentPoint = new LatLng(latitude_CurrentPoint, longitude_CurrentPoint);
        LatLng location_RelayPoint = new LatLng(latitude_RelayPoint, longitude_RelayPoint);
        LatLng location_Distination = new LatLng(latitude_Destination, longitude_Destination);

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

        findViewById(R.id.help).setOnClickListener(this);
        findViewById(R.id.relay).setOnClickListener(this);
        findViewById(R.id.dist).setOnClickListener(this);
//    ===================================================================================^

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // カスタム情報ウインドウ v
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // カスタム情報ウインドウ ^
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
        Toast.makeText(this, +latitude_CurrentPoint+"", Toast.LENGTH_LONG).show();
        Toast.makeText(this, +longitude_CurrentPoint+"", Toast.LENGTH_LONG).show();
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

        RelayPointPointing(mMap);
        DistinationPointing(mMap);

        // カスタム情報ウインドウ v
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        // カスタム情報ウインドウ ^
//
//    ===================================================================================^

//        LatLng latlng = new LatLng(latitude, longitude);    //緯度と経度格納（将来的には配列化してinstagramから取得した緯度，経度情報を格納？）

//    ===================================================================================v
//        if (googleMap != null) {
//            googleMap.addMarker(new MarkerOptions().position(latlng).title("Skytree"));     //マップ上にピンを追加
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));            //latlngに指定されている場所へカメラ移動
//        }

        if (mMap != null) {
            // タップ時のイベントハンドラ登録
            mMap.setOnMapClickListener(new OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {

//                    // TODO Auto-generated method stub
//                    mMap.clear();
//                    mMap.addMarker(new MarkerOptions().position(point));
//                    Toast.makeText(getApplicationContext(), "タップ位置\n緯度：" + point.latitude + "\n経度:" + point.longitude, Toast.LENGTH_LONG).show();

                }
            });

            // 長押し時のイベントハンドラ登録
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng point) {
                    // TODO Auto-generated method stub
//                    mMap.clear();
//                    mMap.addMarker(new MarkerOptions().position(point));
                    Toast.makeText(getApplicationContext(), "長押し位置\n緯度：" + point.latitude + "\n経度:" + point.longitude, Toast.LENGTH_LONG).show();

//                    時間計算
//                    Current_Distination_Time(latitude_CurrentPoint, longitude_CurrentPoint, latitude_Destination, longitude_Destination);
//                    Current_Distination_Map(latitude_CurrentPoint, longitude_CurrentPoint, latitude_Destination, longitude_Destination);

//                    マップ出力
//                    Current_Relay_Distination_Time(latitude_CurrentPoint, longitude_CurrentPoint,latitude_RelayPoint, longitude_RelayPoint, latitude_Destination, longitude_Destination);
                    Current_Relay_Distination_Map(latitude_CurrentPoint, longitude_CurrentPoint,latitude_RelayPoint, longitude_RelayPoint, latitude_Destination, longitude_Destination);
                }
            });
        }

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

    /**
     * Demonstrates converting a {@link Drawable} to a {@link BitmapDescriptor},
     * for use as a marker icon.
     */
    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
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
        latitude_CurrentPoint = location.getLatitude();
        longitude_CurrentPoint = location.getLongitude();

        //    ===================================================================================v
        float[] results = new float[1];

//        最終地点との距離
//        Location.distanceBetween(latitude_CurrentPoint, longitude_CurrentPoint, latitude_RelayPoint, longitude_RelayPoint, results);

//        中継地点との距離
        Location.distanceBetween(latitude_CurrentPoint, longitude_CurrentPoint, latitude_RelayPoint, longitude_RelayPoint, results);
        System.out.println("*******************************************");
        System.out.println(results[0]);
        System.out.println("*******************************************");

        if(results[0] < goal_Judge){
            Toast.makeText(getApplicationContext(), "GOAL！！", Toast.LENGTH_LONG).show();
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
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        com.google.android.gms.appindexing.AppIndex.AppIndexApi.start(client, getIndexApiAction());
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
    // 緯度経度を入れて経路を検索（使用しない）
    private void pointSearch(){
//        出発座標
        String src_lat = "34.81002";
        String src_ltg = "135.76780";
//        到着座標
        String des_lat = "35.684752";
        String des_ltg = "139.707937";

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

//        なぜか徒歩検索（指定できるところがわからん）
        intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
//        intent.setData(Uri.parse("http://maps.google.com/maps?saddr="+src_lat+","+src_ltg+"&daddr="+des_lat+","+des_ltg));
        startActivity(intent);
    }

    // 地名を入れて経路を検索（使用しない）
    private void nameSearch(){
        String start = "東京駅";
        String destination = "スカイツリー";

        // 電車:r
        String dir = "r";
        // 車:d
        //String dir = "d";
        // 歩き:w
        //String dir = "w";

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
        intent.setData(Uri.parse("http://maps.google.com/maps?saddr="+start+"&daddr="+destination+"&dirflg="+dir));
        startActivity(intent);
    }
//    ===================================================================================^



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

    private void mapManager_store(int flag){
        flag_of_mapManager = flag;
    }

    private int mapManager_output(){
        return flag_of_mapManager;
    }

    int time_data[] = new int[2];
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
        System.out.println(routeTime_Current_Relay_Distination);

        // カスタム情報ウィンドウ v
        time_data = MinutesToTime(routeTime_Current_Relay_Distination);
        DateFormat df = new SimpleDateFormat("hh:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, time_data[0]);
        calendar.add(Calendar.MINUTE, time_data[1]);
        String date = df.format(calendar.getTime());
        LatLng start_pos = new LatLng(latitude_Destination, longitude_Destination);
        start = mMap.addMarker(new MarkerOptions()
                .position(start_pos)
                .title("最終目的地")
                .snippet("到着予定時刻 " +date+ " 所要時間 " +routeTime_Current_Relay_Distination)
                .icon(vectorToBitmap(R.drawable.ic_android, Color.parseColor("#A4C639"))));
        // カスタム情報ウィンドウ ^
    }

    // カスタム情報ウィンドウ v
    public int[] MinutesToTime(int minutes) {
        int time[] = new int[2];
        int hour = 0;
        int min  = minutes;
        for(int i = 0;; i++) {
            if(min < 60) break;
            hour += 1;
            min = minutes - 60;
        }
        time[0] = hour;
        time[1] = min;

        return time;
    }
    // カスタム情報ウィンドウ ^

//    ===================================================================================^


//以下，経路時間探索処理（トリガー：関数routeSearch()）
//    ===================================================================================v
    private void routeSearch_Current_Distination
    (double lat_CurrentPoint, double lng_CurrentPoint, double lat_Destination, double lng_Destination){
        progressDialog.show();

//        ここに現在地と目的地の経路の座標を求めるところ
        String url = getDirectionsUrl_Current_Distination(lat_CurrentPoint, lng_CurrentPoint, lat_Destination, lng_Destination);

        DownloadTask downloadTask = new DownloadTask();

        downloadTask.execute(url);

    }

    private void routeSearch_Current_Relay_Distination
            (double lat_CurrentPoint, double lng_CurrentPoint, double lat_RelayPoint, double lng_RelayPoint, double lat_Destination, double lng_Destination){
        progressDialog.show();

//        ここに現在地と目的地の経路の座標を求めるところ
        String url = getDirectionsUrl_Current_Relay_Dinsination(lat_CurrentPoint, lng_CurrentPoint, lat_RelayPoint, lng_RelayPoint, lat_Destination, lng_Destination);

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

            progressDialog.hide();
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

            progressDialog.hide();
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
            progressDialog.hide();

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