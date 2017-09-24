package com.example.my_boss.questrip;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class questrip_setting_there_Activity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback,LocationListener {

    private TimePickerDialog timePick;  //時間入力のためのDialog
    private String name;                //ユーザ名（どちらかといえばニックネーム）
    private String destination;         //目的地
    private int hour;                   //目標到達時間 何時か
    private int minute;                 //目標到達時間 何分か
    private String area;                //現在位置の都道府県+庁 例:京都府庁

    private FrameLayout mascotFrame;    //画像表示のFrameLayout
    private FrameLayout mapFrame;       //map表示のFrameLayout
    private RelativeLayout layout;      //いろいろ全部入ってるLayout
    private TextView text;              //メッセージ表示View
    private EditText edt;               //解答入力テキストボックス
    private ImageView mascot;           //キャラクター表示のImageView
    private Button yes;                 //「はい」ボタン
    private Button no;                  //「いいえ」ボタン
    private MapFragment mapFragment;    //マップ表示のFragment

    private int step = -2;              //質問ステップを管理

    private String font="Pixel10.ttf";  //8ビットフォント

    //1文字ずつ表示するのに必要な変数----
    private String put_txt;
    private String put_word;
    private String put_text;
    private int i = 0;
    //------------------------------

    //MapFragmentとかとかで必要なやつら
    private GoogleApiClient client;
    private LocationManager locationManager;
    private GoogleMap mMap;
    //------------------------------

    //緯度経度たち---------------------------
    public double latitude = 34.802991;             //処理に使う緯度
    public double longitude = 135.771159;           //処理に使う経度
    public double startLatitude = 35.6581;          //現在地の緯度
    public double startLongitude = 139.701742;      //現在地の経度
    public double mascotLatitude = 35.6581;         //都道府県庁の緯度（キャラ取得用）
    public double mascotLongitude = 139.701742;     //都道府県庁の経度（キャラ取得用）

    //ご当地キャラget用------------------------------
    private String api_key_gotouchi = [GOTOUCHI API KEY];  //ご当地キャラAPI使用に用いるAPIキー
    private getItemDataAsync get_item;                  //ご当地キャラAPIへリクエストを投げるAsynctask
    private imageBuilderAsync image_builder;            //URLをBitmapイメージへ変換するAsynctask
    private String url_getitem;                         //APIリクエスト用のURL
    private Uri.Builder builder;                        //URL→Bitmap変換のURL
    private String json_getitem;                        //APIから返ってきたJSONデータ格納
    private String image_url[] = new String[50];        //JSONデータからimage部分のみをパースしてここに入れる．その後imageBuilderAsyncへ
    private Bitmap image_of_character;                  //ご当地キャラの画像データ

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        //全部が入ってるLayout
        layout = (RelativeLayout)findViewById(R.id.ll);

        //キャラ表示用のFrameLayout
        mascotFrame = (FrameLayout) findViewById(R.id.mascotframe);

        //キャラ表示用のFrameLayout
        mapFrame = (FrameLayout) findViewById(R.id.mapframe);

        //ボタンの設定---------------------
        yes = (Button) findViewById(R.id.YES);
        yes.setText("はい");
        yes.setVisibility(View.INVISIBLE);  //最初は非表示
        yes.setTypeface(Typeface.createFromAsset(getAssets(), font));
        no = (Button) findViewById(R.id.NO);
        no.setText("いいえ");
        no.setVisibility(View.INVISIBLE);   //最初は非表示
        no.setTypeface(Typeface.createFromAsset(getAssets(), font));
        //-------------------------------

        //ボタンを押した時に呼び出されるメソッド--
        yes.findViewById(R.id.YES).setOnClickListener(yesClickListener);
        no.findViewById(R.id.NO).setOnClickListener(noClickListener);
        //-------------------------------

        //入力用のテキストボックスと入力後の呼び出しメソッド------------
        edt = (EditText) findViewById(R.id.editText);
        edt.setVisibility(View.INVISIBLE);
        edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (step == -2){
                    destination = edt.getText().toString();
                    step += 1; interaction();
                }else if (step == 1) {
                    name = edt.getText().toString();
                    step += 1; interaction();
                } else if (step == 3) {
                    destination = edt.getText().toString();
                    step += 1; interaction();
                } else if (step == 5) {
                    step += 1; interaction();
                } return false;
            }
        });//-------------------------------------------------

        //メッセージ表示View----------------
        text = (TextView) findViewById(R.id.message);
        text.setTypeface(Typeface.createFromAsset(getAssets(), font));
        //-------------------------------

        //いろんな初期処理 画像取得しないとなのでここ---------------------
        locationStart();            //現在地取得
        //-------------------------------------------------

        // 時間選択ダイアログの生成 目標到達時刻設定に使用--------
        timePick= new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                // 設定 ボタンクリック時の処理
                hour = hourOfDay;
                minute = min;
                step+=1; interaction();
            }
        }, hour, minute, true);
        timePick.setButton(DialogInterface.BUTTON_NEGATIVE,
                "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        interaction();}
                });
        //-------------------------------------------------

        //現在地とか目標地をMapで表示するためのFragment
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFrame.setVisibility(View.INVISIBLE);    //最初は隠す
        // ↓ Map表示にいるやつ
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
        interaction();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    //設定時の質疑応答の流れ-------------------------------------
    void interaction() {
        switch (step) {
            case -2:
                setView_map();
                //mascot.setAlpha(0);
                display_message("\n.......", 2000);
                display_message("\n冒険をはじめる場所は？", 4000);
                setEditText("スタート地点を入力", 7000);
                //view_visible(7000);
                break;
            case -1:
                onGetLocation();
                mapFragment.getMapAsync(this);
                edt.setText("");
                edt.setVisibility(View.INVISIBLE);
                display_message("\nここでいいのかな？", 1000);
                view_visible(4000);
                break;
            case 0:
                display_message("\n.....やあ", 2000);
                display_message("君も冒険の旅を\nはじめるんだね", 4000);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        step+=1; interaction(); } }, 6000);
                break;
            case 1:
                if(name!=null) edt.setText(name);
                display_message("\n君の名前は？", 1000);
                setEditText("名前を入力", 3000);
                break;
            case 2:
                edt.setText("");
                edt.setVisibility(View.INVISIBLE);
                display_message(name + "...\nよろしくね", 1000);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        step+=1; interaction(); } }, 3000);
                break;
            case 3:
                display_message(name + "にとって\n最終目的地はどこ？", 1000);
                setEditText("最終目的地を入力", 4000);
                break;
            case 4:
                onGetLocation();
                mapFragment.getMapAsync(this);
                edt.setText("");
                edt.setVisibility(View.INVISIBLE);
                display_message("\nここでいいのかな？", 1000);
                view_visible(4000);
                break;
            case 5:
                edt.setText("");
                edt.setVisibility(View.INVISIBLE);
                display_message("いつまでに\n辿り着きたいの？", 1000);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timePick.show(); }
                }, 4000);
                break;
            case 6:
                edt.setText("");
                edt.setVisibility(View.INVISIBLE);
                display_message(hour+"時"+minute+"分までに\n"
                        +destination+"へ...\nこれでいいんだね？", 1000);
                view_visible(6000);
                break;
            default:
                step = 1; interaction();
        }
    }

    //viewの配置を決めるメソッド---------------------------------------------
    void setView_map(){
        text.setId(1); yes.setId(2); edt.setId(3);

        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,  RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams lp5 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams lp6 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp1.addRule(RelativeLayout.CENTER_IN_PARENT);
        lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp2.addRule(RelativeLayout.BELOW,1);
        lp2.addRule(RelativeLayout.ABOVE,2);
        lp3.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp3.addRule(RelativeLayout.ABOVE,1);
        lp4.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp4.addRule(RelativeLayout.ALIGN_LEFT,3);
        lp5.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp5.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp5.addRule(RelativeLayout.ALIGN_RIGHT,3);
        lp6.addRule(RelativeLayout.ABOVE,1);

        lp1.setMargins(0,50,0,50);
        lp2.setMargins(0,0,0,0);
        lp3.setMargins(0,400,0,400);
        lp4.setMargins(0,50,0,10);
        lp5.setMargins(0,50,0,10);

        layout.removeAllViews();
        layout.addView(text,lp1);
        layout.addView(mascotFrame,lp2);
        layout.addView(edt,lp3);
        layout.addView(yes,lp4);
        layout.addView(no,lp5);
        layout.addView(mapFrame,lp6);
    }

    //一定時間後に文字列を位置文字ずつ表示するメソッド（引数 表示メッセージ 遅らせる時間）
    void display_message(final String message, int ms) {
        HandlerThread handlerThread = new HandlerThread("dispmessage");
        handlerThread.start();
        new Handler(handlerThread.getLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                put_text = ""; put_word = "";
                i = 0; put_txt = message;
                handler.sendEmptyMessage(1);
            }
        }, ms);
    }

    //マップとボタンを表示------------------------------------
    void view_visible(int ms) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(step!=6) mapFrame.setVisibility(View.VISIBLE);
                yes.setVisibility(View.VISIBLE);
                no.setVisibility(View.VISIBLE);
            }
        }, ms);
    }

    //入力を要求するタイミングで呼びだされてEditTextを表示-------------
    void setEditText(final String message, int ms) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                edt.setHint(message);
                edt.setVisibility(View.VISIBLE);
                edt.requestFocus();
                InputMethodManager inputMethodManager
                        = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(edt, 0);
            }
        }, ms);
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
                    text.setText(put_text);//(5)
                    handler.sendEmptyMessageDelayed(1, 120);
                    i++;
                } else { super.dispatchMessage(msg); }
            }
        }
    };

    //yesボタン（はい）が押された時のリスナー------------------------
    View.OnClickListener yesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(step==6) {
                yes.setVisibility(View.INVISIBLE);
                no.setVisibility(View.INVISIBLE);
                display_message("それじゃあ\n冒険のはじまりだよ...", 1000);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // インテントの生成
                        Intent intent=new Intent();
                        intent.setClassName("com.example.my_boss.questrip","com.example.my_boss.questrip.Instagram_connect_Activity");
                        intent.putExtra("latitude",String.valueOf(latitude));
                        intent.putExtra("longitude",String.valueOf(longitude));
                        intent.putExtra("hour",String.valueOf(hour));
                        intent.putExtra("minute",String.valueOf(minute));

                        intent.setClassName("com.example.my_boss.questrip","com.example.my_boss.questrip.Instagram_connect_Activity");
                        intent.putExtra("keyword",image_url);
                        // SubActivity の起動
                        startActivity(intent); finish(); } }, 5000);
            }
            else if(step==-1){
                text.setText("");
                yes.setVisibility(View.INVISIBLE);
                no.setVisibility(View.INVISIBLE);
                mapFrame.setVisibility(View.INVISIBLE);

                onGetAddress();                            //現在の位置情報から都道府県を取得
                if(area!=null) onGetMascotLocation();      //都道府県から県庁の位置情報を取得
                image_getter();                            //県庁の位置情報から

                // WindowManagerのインスタンス取得
                WindowManager wm = getWindowManager();
                // Displayのインスタンス取得
                Display disp = wm.getDefaultDisplay();

                //キャラクター画像表示（上下にふわふわ）-
                int width = image_of_character.getWidth();      //サイズ直す用
                int height = image_of_character.getHeight();    //サイズ直す用
                // ↓ 画像の大きさを統一
                Bitmap bitmap = Bitmap.createScaledBitmap(image_of_character,
                        (disp.getHeight()/5)*width/height, disp.getHeight()/5, false);
                mascot = (ImageView) findViewById(R.id.character);  //imageViewにセット
                mascot.setImageBitmap(bitmap);                      //画像を登録
                animetion();
                mascot.setAlpha(0);
                //-------------------------------

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mascotFrame.setBackgroundResource(R.drawable.spotlight);
                        layout.setBackgroundResource(R.drawable.backblock); } }, 300);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mascot.setAlpha(5); } }, 1000);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mascot.setAlpha(30); } }, 1500);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mascot.setAlpha(255); } }, 2000);
                step += 1; interaction();
            }
            else if (step==4) {
                text.setText("");
                yes.setVisibility(View.INVISIBLE);
                no.setVisibility(View.INVISIBLE);
                mapFrame.setVisibility(View.INVISIBLE);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        step += 1; interaction(); } }, 1000);
            }
        }
    };

    void animetion(){
        mascot.startAnimation(AnimationUtils.loadAnimation(this, R.anim.a1));
    }

    //noボタン（いいえ）が押された時のリスナー------------------------
    View.OnClickListener noClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(step==6) {
                yes.setVisibility(View.INVISIBLE);
                no.setVisibility(View.INVISIBLE);
                step = 1; interaction();
            }else if(step==-1){
                text.setText("");
                yes.setVisibility(View.INVISIBLE);
                no.setVisibility(View.INVISIBLE);
                mapFrame.setVisibility(View.INVISIBLE);
                display_message("GPSの設定を確かめて\nまたきてください...",1000);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() { finish(); } }, 5000);
            }else if (step==4) {
                text.setText("");
                yes.setVisibility(View.INVISIBLE);
                no.setVisibility(View.INVISIBLE);
                mapFrame.setVisibility(View.INVISIBLE);
                step = 3; interaction();
            }
        }
    };

    //現在地取得-----------------------------------------------------------------------------
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
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            Log.d("debug", "checkSelfPermission false");
            return;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latlng= new LatLng(latitude,longitude);
        mMap.addMarker(new MarkerOptions().position(latlng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
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

    //文字列から検索してマップ上に反映----------------------------------
    public void onGetLocation() {
        String searchKey = edt.getText().toString();
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
            latitude = addr.getLatitude();
            longitude = addr.getLongitude();
            if(step==-1) {
                startLatitude = addr.getLatitude();
                startLongitude = addr.getLongitude();
            }
        }
    }

    //キャラクター用位置情報取得のための例外処理
    void getMascotLocation(){
        switch (area){
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
    public void onMapLoaded() {}

    @Override
    public void onLocationChanged(Location location) {}

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("set Page") // TODO: Define a title for the content shown.
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
}
