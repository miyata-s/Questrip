package com.example.my_boss.questrip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.id;

public class questrip_Login_Activity extends AppCompatActivity {
    private Login_async login_async;
    private String root_url = "http://172.20.11.112:3000";
    private String str_email;
    private String str_pass;

    private EditText edit_email;
    private EditText edit_pass;


    private questrip_Login_Activity main;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questrip__login_);

        edit_email = (EditText) findViewById(R.id.editText1);
        edit_pass = (EditText) findViewById(R.id.editText2);
        Button button = (Button) findViewById(R.id.button);

        root_url = root_url + "/login";

//
//        str_email = edit_email.getText().toString();
//        str_pass = edit_pass.getText().toString();
//


//        login_async = new Login_async(this);
//        login_async.execute(to_login_async);


//
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ボタンがクリックされた時に呼び出されます
                login_async = new Login_async();
                login_async.execute(root_url,edit_email.getText().toString(),edit_pass.getText().toString());
//                Toast.makeText(getApplicationContext(), edit_email.getText().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void result_job(String result){
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }
}
