package com.anthonycr.base47.sample;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.anthonycr.base47.Base47;
import com.anthonycr.base47.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private final HandlerThread mHandlerThread = new HandlerThread("encoding-thread");
    private Handler mBackgroundHandler;

    private volatile Runnable mPostRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textViewEncoded = (TextView) findViewById(R.id.textEncoded);
        final TextView textViewDecoded = (TextView) findViewById(R.id.textDecoded);
        EditText editText = (EditText) findViewById(R.id.editText);

        mHandlerThread.start();
        mBackgroundHandler = new Handler(mHandlerThread.getLooper());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                postOrQueueRunnable(new Runnable() {
                    @Override
                    public void run() {
                        encode(editable.toString(), textViewEncoded, textViewDecoded);

                    }
                });
            }
        });

        String text = "Hello World \uD83D\uDE0E";

        editText.setText(text);
    }

    private void postOrQueueRunnable(@NonNull final Runnable encodeRunnable) {

        Runnable wrapperRunnable = new Runnable() {
            @Override
            public void run() {
                encodeRunnable.run();
                if (!this.equals(mPostRunnable) && mPostRunnable != null) {
                    mPostRunnable.run();
                } else {
                    mPostRunnable = null;
                }
            }
        };

        if (mPostRunnable != null) {
            mPostRunnable = wrapperRunnable;
        } else {
            mBackgroundHandler.post(wrapperRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
            mHandlerThread.quitSafely();
        } else {
            mHandlerThread.quit();
        }
    }

    @WorkerThread
    private static void encode(@NonNull String textToEncode, final TextView encodeView,
                               final TextView decodeView) {
        final String encoded = Base47.encode(textToEncode.getBytes());
        // Log.d(TAG, "ENCODED: " + encoded);

        final String decoded = new String(Base47.decode(encoded));
        // Log.d(TAG, "DECODED: " + decoded);

        MAIN.post(new Runnable() {
            @Override
            public void run() {
                Context context = encodeView.getContext();
                encodeView.setText(context.getString(R.string.encoded, encoded));
                decodeView.setText(context.getString(R.string.decoded, decoded));
            }
        });
    }

}
