package com.anthonycr.base47.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.anthonycr.base47.Base47;
import com.anthonycr.base47.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.text);
        TextView textView2 = (TextView) findViewById(R.id.text2);

        String text = "Hello World \uD83D\uDE0E";

        String encoded = Base47.encode(text.getBytes());
        Log.d(TAG, "ENCODED: " + encoded);
        textView.setText(encoded);

        String decoded = new String(Base47.decode(encoded));
        Log.d(TAG, "DECODED: " + decoded);
        textView2.setText(decoded);

    }

}
