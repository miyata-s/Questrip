package com.example.my_boss.questrip;

import android.app.Activity;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.my_boss.questrip.R;

/**
 * Created by daisuke on 16/10/22.
 */

public class input extends Activity {

    private InputMethodManager inputMethodManager;
    private LinearLayout mainLayout;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input);

    }
}
