package com.example.my_boss.questrip;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my_boss.questrip.HttpAsync;
import com.example.my_boss.questrip.getItemDataAsync;
import com.example.my_boss.questrip.imageBuilderAsync;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.cast.framework.media.widget.ControlButtonsContainer;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import static android.R.id.list;
import static com.example.my_boss.questrip.R.id.textView;


/* AsyncTaskCallbacks was added by tdomen on 2016/10/22 */
public class Instagram_connect_Activity extends FragmentActivity implements
        OnMapReadyCallback, LocationListener {
    public ImageView imageview;
    public Bitmap gazou[] = new Bitmap[500];
    private Uri.Builder builder[] = new Uri.Builder[50];
    private Uri.Builder gotouchi_builder;

    private int NUM_PIN = 30;

//    private Localization localization = new Localization();     //Localizationオブジェクト

    private LocationManager locationManager;  //GPSデータ取得用のオブジェクト
    private SupportMapFragment mMap;                 //Map表示フラグメント領域のオブジェクト
    public GoogleMap gMap;                    //googleマップ操作用オブジェクト

    private String access_token = [INSTAGRAM API TOKEN];    //instagram APIアクセストークン
    private String api_key_gotouchi = [INSTAGRAM API KEY];      //ご当地キャラAPI使用に用いるAPIキー


    private HttpAsync task_getplace;     //http通信用（GET）Asynctaskオブジェクト
    private getItemDataAsync task_getitem;  //画像取得用Asynctaskオブジェクト
    private imageBuilderAsync task_imageBuild;

    private String url_getplace;       //場所リクエストURL
    private String url_getitem;     //画像取得用リクエストURL


    private String json_getplace;   //AsynvtaskでgetしたJsonオブジェクト
    private String json_getitem;    //画像取得用Asynctaskから取得したJSON
    private long place_id[] = new long[NUM_PIN + 1];      //場所ID
    private String place_name[] = new String[NUM_PIN + 1]; //地名
    private double latitude_fromJson[] = new double[NUM_PIN + 1];     //場所リクエストから取得した緯度
    private double longitude_fromJson[] = new double[NUM_PIN + 1];    //場所リクエストから取得した経度
    private LatLng latlng_fromJson[] = new LatLng[NUM_PIN + 1];       //googlemapにピンを立てる際の（緯度，経度）格納庫

    private String images[] = new String[300];
    public Uri uri[] = new Uri[300];
    private String thumbnail;
    private JSONArray datas;

    private long minTime_GPS = 1000;    //GPSの更新間隔（sec）
    private long minDistance_GPS = 50;  //GPSの更新間隔（distance）

    public double latitude = 35.681588;       //適当な緯度
    public double longitude = 139.76608;       //適当な経度

    public double goal_latitude;
    public double goal_longitude;
    public int goal_hour;
    public int goal_minute;

    public boolean light_version = false;   // 「周囲を探す」モード選択時true
    public boolean time_out = false;
    public Marker goal_marker;
    public Marker relay_marker;

    private RecyclerView recyclerView;
    private RelativeLayout gameframe;
    private RelativeLayout layout;
    private TextView text;
    private LinearLayout botton;
    private int height;

    private JSONArray media_datas;

    private Button go_there;
    private Button go_final;

    private String image_url;
    private Bitmap image_of_character;
    public ProgressDialog progressDialog;

    private TextView message;
    private String font = "Pixel10.ttf";  //8ビットフォント
    //1文字ずつ表示するのに必要な変数----
    private String put_txt;
    private String put_word;
    private String put_text;
    private int i = 0;
    //------------------------------

    private Timer mTimer = null;
    private Handler mHandler = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    global_values global;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_connection_main);

        global = (global_values)this.getApplication();

        //プログレス
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("NOW LOADING");
        progressDialog.hide();

        Intent intent = this.getIntent();
        if (intent.getStringExtra("latitude") != null) {
            goal_latitude = Double.parseDouble(intent.getStringExtra("latitude"));
            goal_longitude = Double.parseDouble(intent.getStringExtra("longitude"));
            goal_hour = Integer.parseInt(intent.getStringExtra("hour"));
            goal_minute = Integer.parseInt(intent.getStringExtra("minute"));
        } else {
            goal_latitude = -1.0;
            goal_longitude = -1.0;
            goal_hour = -1;
            goal_minute = -1;
            light_version = true;
        }

        //20161031追加 by dyamashita
        message = (TextView) findViewById(textView);
        message.setTypeface(Typeface.createFromAsset(getAssets(), font));


        if (image_url != null) {
            image_getter(image_url);
            ImageView image = new ImageView(this);
            image.setImageBitmap(image_of_character);
        }


        go_there = (Button) findViewById(R.id.button_go_there);
        go_final = (Button) findViewById(R.id.button_go_final);

        setClickListener();

        // WindowManagerのインスタンス取得
        WindowManager wm = getWindowManager();
        // Displayのインスタンス取得
        Display disp = wm.getDefaultDisplay();
        height= disp.getHeight();

        layout = (RelativeLayout)findViewById(R.id.activity_main);
        gameframe = (RelativeLayout)findViewById(R.id.mapframe);
        text = (TextView)findViewById(R.id.myboss);
        botton = (LinearLayout)findViewById(R.id.bottonll);

        //instagramから取得した画像を表示するRecyclerViewの設定
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL); // ここで横方向に設定
        recyclerView.setLayoutManager(manager);


        //google mapを表示するにはfragmentViewを使う
        mMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap.getMapAsync(this);     //mapの操作は非同期処理（onMapReadyで）
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public double tapped_marker[] = new double[2];    // 現在タップされているマーカーを表示
    public double tapping_marker[] = new double[2];    // 現在タップされているマーカーを表示
    String tapped_marker_name;

    @Override
    //google mapに関する処理
    public void onMapReady(GoogleMap googleMap) {

        locationStart();    //GPS現在位置推定メソッド

        gMap = googleMap;   //googleMap操作用ハンドラセット
        gMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

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
        gMap.setMyLocationEnabled(true);
        UiSettings settings = gMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);

        // 最終目的地点にピン立て
        // ピン立てをCurrent_Distination_Time()内で行っている
        if (!light_version) {
            LatLng goal_position = new LatLng(goal_latitude, goal_longitude);
            gMap.addMarker(new MarkerOptions().position(goal_position).title("最終目的地")
                    .icon(BitmapDescriptorFactory.defaultMarker(150)));
        }
//        Current_Distination_Time(latitude, longitude, goal_latitude, goal_longitude);

        //url_getplace：場所取得リクエストURL(latitude，longitude：現在地の緯度，経度 access_token：アクセストークン，distance：情報取得の範囲)
        url_getplace = "https://api.instagram.com/v1/locations/search?" + "lat=" + latitude + "&lng=" + longitude + "&distance=" + 100 + "&access_token=" + access_token + "&count=" + NUM_PIN;
        task_getplace = new HttpAsync();    //場所取得用Asyncをnew
        JSONObject jsonData = null;     //場所データ（JSONオブジェクト）の格納変数

        try {
            json_getplace = task_getplace.execute(url_getplace).get();  //場所取得Asynctaskを実行し，doInbackgroundの返り値をjson_getplaceに格納

            //ここから取得したJsonをパース
            jsonData = new JSONObject(json_getplace);
            JSONArray datas = jsonData.getJSONArray("data");
            latlng_fromJson[0] = new LatLng(latitude, longitude);     //latlng_fromJson[0]には現在位置を格納
            place_name[0] = "現在地";

            //latlng_fromJson[1]以降はinstagramから取得した緯度，経度を格納
            for (int i = 0; i < datas.length(); i++) {
                JSONObject data = datas.getJSONObject(i);
                // 地名・店名を取得
                place_name[i+1] = data.getString("name");
                latitude_fromJson[i] = Double.parseDouble(data.getString("latitude"));
                longitude_fromJson[i] = Double.parseDouble(data.getString("longitude"));
                latlng_fromJson[i+1] = new LatLng(latitude_fromJson[i], longitude_fromJson[i]);
                System.out.println("latlng: "+latlng_fromJson[i+1]);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        System.out.println("latlng_fromJson.length：" + latlng_fromJson.length);

        //マップの処理
        if (gMap != null) {
            for (int count = 0; count < latlng_fromJson.length - 1; count++) {
                if (count == 0) {
                    // 現在地へのピン立て
                    gMap.addMarker(new MarkerOptions().position(latlng_fromJson[count]).title(place_name[count])
                            .icon(BitmapDescriptorFactory.defaultMarker(200)));
                } else {
                    // 候補中継地点のピン立て
                    gMap.addMarker(new MarkerOptions().position(latlng_fromJson[count]).title(place_name[count]));
                }
            }
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng_fromJson[0], 18)); //latlngに指定されている場所へカメラ移動
        }


        //マーカがタップされた時の処理
        gMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                final Handler handler = new Handler();
                recyclerView.setAdapter(null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //url_getplace：場所取得リクエストURL(latitude，longitude：現在地の緯度，経度 access_token：アクセストークン，distance：情報取得の範囲)
                        url_getplace = "https://api.instagram.com/v1/locations/search?" + "lat=" + latitude + "&lng=" + longitude + "&distance=" + 100 + "&access_token=" + access_token + "&count=" + NUM_PIN;
                        task_getplace = new HttpAsync();    //場所取得用Asyncをnew
                        JSONObject jsonData = null;     //場所データ（JSONオブジェクト）の格納変数

                        try {
                            json_getplace = task_getplace.execute(url_getplace).get();  //場所取得Asynctaskを実行し，doInbackgroundの返り値をjson_getplaceに格納

                            //ここから取得したJsonをパース
                            jsonData = new JSONObject(json_getplace);
                            JSONArray datas = jsonData.getJSONArray("data");
                            latlng_fromJson[0] = new LatLng(latitude, longitude);     //latlng_fromJson[0]には現在位置を格納

                            //latlng_fromJson[1]以降はinstagramから取得した緯度，経度を格納
                            for (int i = 0; i < datas.length(); i++) {
                                JSONObject data = datas.getJSONObject(i);
                                // 名前を取得
                                place_id[i] = Long.parseLong(data.getString("id"));
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONObject jsonData_getitem = null; //画像取得用Asyncから戻ってきたJsonを格納するオブジェクト
                        final ArrayList<Bitmap> list = new ArrayList();
                        for (int count = 0; count < latlng_fromJson.length - 1; count++) {
                            String id = "m" + count;
                            if (marker.getId().equals("m0")) {
                                System.out.println("tap_lat: "+latitude_fromJson[count]);
                                System.out.println("tap_lng: "+longitude_fromJson[count]);
                                System.out.println("tap_id: "+id);
                                break;
                            } else if (marker.getId().equals(id)) {
                                tapping_marker[0] = latitude_fromJson[count-1];
                                tapping_marker[1] = longitude_fromJson[count-1];
                                tapped_marker_name = place_name[count];
                                System.out.println("入った");
                                System.out.println("tap_lat: "+tapping_marker[0]);
                                System.out.println("tap_lng: "+tapping_marker[1]);
                                System.out.println("tap_name: "+tapped_marker_name);
                                System.out.println("tap_id: "+id);
                                Current_Relay_Time(latitude, longitude, tapping_marker[0], tapping_marker[1]);

                                task_getitem = new getItemDataAsync();  //画像取得用Asynctaskのnew
                                //場所id(place_id)を用いてアイテムデータをJsonオブジェクトで取得
                                url_getitem = "https://api.instagram.com/v1/locations/" + String.valueOf(place_id[count]) + "/media/recent?access_token=" + access_token;// リクエストURL
                                try {
                                    json_getitem = task_getitem.execute(url_getitem).get();     //画像取得Asynctaskを実行し，doInbackgroundの返り値をjson_getitemに格納
                                    //ここから画像取得Asyncで取得したJsonをパース
                                    jsonData_getitem = new JSONObject(json_getitem);
                                    media_datas = jsonData_getitem.getJSONArray("data");
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        //2016_10_24_12:15
                        if (media_datas != null) {
                            try {
                                for (int count_datas = 0; count_datas < media_datas.length(); count_datas++) {
                                    JSONObject data = media_datas.getJSONObject(count_datas);
                                    images[count_datas] = data.getJSONObject("images").getJSONObject("thumbnail").getString("url");
                                    uri[count_datas] = Uri.parse(images[count_datas]);
                                    builder[count_datas] = uri[count_datas].buildUpon();

                                    task_imageBuild = new imageBuilderAsync(imageview);
                                    try {
                                        gazou[count_datas] = task_imageBuild.execute(builder[count_datas]).get();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                    list.add(gazou[count_datas]);
                                }
                            } catch (JSONException e) {
                            }
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), list));
                            }
                        });
                    }
                }).start();
                return false;
            }
        });
    }

    //GPSによる現在位置取得
    private void locationStart() {
        Log.d("debug", "locationStart()");

        // LocationManager インスタンス生成
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //GPS地の更新（第一引数：provider（network または gps），第二引数：minTime（位置情報を取得する最小時間），第三引数：minDistance（位置情報を取得する最小距離），第四引数：listener（リスナーを実装しているクラス）
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime_GPS, minDistance_GPS, this);
        Location nowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);     //最新のGPS値の取得
        latitude = nowLocation.getLatitude();       //最新GPS値の緯度を格納
        longitude = nowLocation.getLongitude();     //最新GPS値の経度を格納
        if (goal_latitude < 0.0) {
            goal_latitude = latitude;
            goal_longitude = longitude;
        }

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
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
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug", "checkSelfPermission true");

                locationStart();
                return;
            } else {
                // 拒否された時の対応
                Toast toast = Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    //GPSの取得位置が変わった時
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
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

    void setClickListener() {
        go_there.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ボタンがクリックされた時に呼び出されます
                //インテントの作成
                Intent intent=new Intent();
                intent.setClassName("com.example.my_boss.questrip","com.example.my_boss.questrip.Guide");

                tapped_marker[0] = tapping_marker[0];
                tapped_marker[1] = tapping_marker[1];

                intent.putExtra("latitude_relaypoint", tapped_marker[0]);
                intent.putExtra("longitude_relaypoint", tapped_marker[1]);
                startActivity(intent);
                finish();
            }
        });

        go_final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClassName("com.example.my_boss.questrip","com.example.my_boss.questrip.Guide");

                tapped_marker[0] = global.latitude_final;
                tapped_marker[1] = global.longitude_final;

                intent.putExtra("latitude_relaypoint", tapped_marker[0]);
                intent.putExtra("longitude_relaypoint", tapped_marker[1]);
                startActivity(intent);
                finish();
            }
        });
    }


    void image_getter(String url) {
        //-------------ご当地キャラget用のasynctask 2016_10_25 by Takaya-----------------------------
        try {
            gotouchi_builder = Uri.parse(url).buildUpon();

            task_imageBuild = new imageBuilderAsync();
            image_of_character = task_imageBuild.execute(gotouchi_builder).get();

            System.out.println(json_getitem);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    //ここからLayout的な処理追加しました by dyamashita------------------------------------------------------------
    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        setView();

        mHandler = new Handler();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String str = "";
                        int random = (int) (Math.random() * 3) + 1;
                        if (random == 1) str = "マーカをタップすると\n下に画像がでるよ";
                        else if (random == 2) str = "どこに行く？";
                        else if (random == 3) str = "目的地が決まったら\n［ここへ行く］を押してね";
                        put_text = "";
                        put_word = "";
                        i = 0;
                        put_txt = str;
                        handler.sendEmptyMessage(1);
                    }
                });
            }
        }, 1000, 10000); // 実行したい間隔(ミリ秒)
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
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

    //viewの配置を決めるメソッド---------------------------------------------
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

    // ルート関連===================================================================================v
    public int time_data[] = new int[2];
    public int h = 0;
    public int m = 0;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    //    GoogleMap mMap;
    private static final int MENU_A = 0;
    private static final int MENU_B = 1;
    private static final int MENU_c = 2;

    public static String posinfo = "";
    public static String info_A = "";
    public static String info_B = "";
    ArrayList<LatLng> markerPoints;

    public static MarkerOptions options;

    public String travelMode = "walking";//default driving

    //    現在地
    public double latitude_CurrentPoint = 34.80058444;       //適当な緯度
    public double longitude_CurrentPoint = 135.76810719;       //適当な経度

    //    中継地点
    public double latitude_RelayPoint = 34.813731;       //適当な緯度
    public double longitude_RelayPoint = 135.771333;       //適当な経度

    //    目的地
    public double latitude_Destination = 34.80542380559208;       //適当な緯度
    public double longitude_Destination = 135.7782707735896;       //適当な経度

    private int flag_of_mapManager = 0;       // ルートの時間探索のときにマップを表示するかのフラグ
    private int routeTime_Current_Distination = 1;       // ルートの時間探索のときにマップを表示するかのフラグ
    private int routeTime_Current_Relay_Distination = 2;// ルートの時間探索のときにマップを表示するかのフラグ
    public int routeTime_Current_Relay = 3;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Instagram_connect_ Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

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

        time_data = MinutesToTime(routeTime_Current_Distination);
        Calendar calendar = Calendar.getInstance();
        h = calendar.get(calendar.HOUR_OF_DAY);
        h = h + time_data[0];
        if(h >= 24) h = h - 24;
        m = calendar.get(calendar.MINUTE);
        m = m + time_data[1];
        if(m >= 60) {
            m = m - 60;
            h = h + 1;
        }
        if(h > goal_hour) time_out = true;
        else if(h == goal_hour && m > goal_minute) time_out = true;
        else time_out = false;
        if(goal_hour < 0) time_out = false;
        if(light_version) time_out = false;

        LatLng goal = new LatLng(goal_latitude, goal_longitude);
        if(!light_version) {
            goal_marker = gMap.addMarker(new MarkerOptions()
                    .position(goal)
                    .title("最終目的地")
                    .snippet("到着予定時刻 " +h+ "時" +m+ "分")
                    .icon(BitmapDescriptorFactory.defaultMarker(150)));
        } else {
            goal_marker = gMap.addMarker(new MarkerOptions()
                    .position(goal)
                    .title("現在地")
                    .icon(BitmapDescriptorFactory.defaultMarker(200)));
        }
        goal_marker.showInfoWindow();
    }

    private void timeManager_Current_Relay_Distination_store(int time){
        routeTime_Current_Relay_Distination = time;
    }

    private void timeManager_Current_Relay_store(int time){
        routeTime_Current_Relay = time;

        time_data = MinutesToTime(routeTime_Current_Relay);
        Calendar calendar = Calendar.getInstance();
        h = calendar.get(calendar.HOUR_OF_DAY);
        h = h + time_data[0];
        if(h >= 24) h = h - 24;
        m = calendar.get(calendar.MINUTE);
        m = m + time_data[1];
        if(m >= 60) {
            m = m - 60;
            h = h + 1;
        }
        if(h > goal_hour) time_out = true;
        else if(h == goal_hour && m > goal_minute) time_out = true;
        else time_out = false;
        if(goal_hour < 0) time_out = false;
        if(light_version) time_out = false;

        LatLng relay = new LatLng(tapping_marker[0], tapping_marker[1]);
        relay_marker = gMap.addMarker(new MarkerOptions()
                .position(relay)
                .title(tapped_marker_name)
                .snippet("到着予定時刻 " +h+ "時" +m+ "分"));
        relay_marker.showInfoWindow();
    }

    public int[] MinutesToTime(int minutes) {
        int time[] = new int[2];
        int hour = 0;
        int minute  = minutes;
        while(true) {
            if(minute < 60) break;
            hour += 1;
            minute = minute - 60;
        }
        time[0] = hour;
        time[1] = minute;
        return time;
    }



    //    以下，経路時間探索処理（トリガー：関数routeSearch()）
    private void routeSearch_Current_Distination
    (double lat_CurrentPoint, double lng_CurrentPoint, double lat_Destination, double lng_Destination){

        // ここに現在地と目的地の経路の座標を求めるところ
        String url = getDirectionsUrl_Current_Distination(lat_CurrentPoint, lng_CurrentPoint, lat_Destination, lng_Destination);

        DownloadTask downloadTask = new DownloadTask();

        downloadTask.execute(url);
    }

    private void routeSearch_Current_Relay_Distination
            (double lat_CurrentPoint, double lng_CurrentPoint, double lat_RelayPoint, double lng_RelayPoint, double lat_Destination, double lng_Destination){
        // ここに現在地と目的地の経路の座標を求めるところ
        String url = getDirectionsUrl_Current_Relay_Dinsination(lat_CurrentPoint, lng_CurrentPoint, lat_RelayPoint, lng_RelayPoint, lat_Destination, lng_Destination);

        DownloadTask downloadTask = new DownloadTask();

        downloadTask.execute(url);
    }

    private void routeSearch_Current_Relay
            (double lat_CurrentPoint, double lng_CurrentPoint, double lat_Relay, double lng_Relay){
        // ここに現在地と目的地の経路の座標を求めるところ
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
        String parameters = str_origin+"&"+str_dest+"&"+sensor + "&language=ja" + "&mode=walking";

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
        String parameters = str_origin+"&"+str_dest+"&"+str_relay+"&"+sensor + "&language=ja" + "&mode=walking";

        //JSON指定
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
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

        private String downloadUrl(String strUrl) throws IOException {
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

            progressDialog.hide();
            return routes_time;
        }
    }

    //    結果が帰ってくる（時間出力，マップ案内）
    /*parse the Google Places in JSON format */
    private class ParserTask_Map extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {
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
                    lineOptions.color(Color.BLUE);
                }
            }else{
                Toast.makeText(getApplicationContext(), "行き先をタップしてください", Toast.LENGTH_LONG).show();
            }
            progressDialog.hide();
        }

    }

    // カスタム情報ウィンドウのためのクラス
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
            if (snippet != null) {
                SpannableString snippetText = new SpannableString(snippet);
                if (!time_out) snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 0, snippet.length(), 0);
                else snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }

        }
    }
}
