package com.anthonycr.base47.sample

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.support.annotation.WorkerThread
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.anthonycr.base47.Base47
import com.anthonycr.base47.R

class MainActivity : AppCompatActivity() {

    private val handlerThread = HandlerThread("encoding-thread")
    private val backgroundHandler by lazy { Handler(handlerThread.looper) }

    @Volatile private var postRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textViewEncoded = findViewById(R.id.textEncoded) as TextView
        val textViewDecoded = findViewById(R.id.textDecoded) as TextView
        val editText = findViewById(R.id.editText) as EditText

        handlerThread.start()

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                postOrQueueRunnable(Runnable { encode(editable.toString(), textViewEncoded, textViewDecoded) })
            }
        })

        editText.setText("Hello World \uD83D\uDE0E")
    }

    private fun postOrQueueRunnable(encodeRunnable: Runnable) {

        val wrapperRunnable = object : Runnable {
            override fun run() {
                encodeRunnable.run()
                val runnable = postRunnable
                if (this != runnable && runnable != null) {
                    runnable.run()
                } else {
                    postRunnable = null
                }
            }
        }

        if (postRunnable != null) {
            postRunnable = wrapperRunnable
        } else {
            backgroundHandler.post(wrapperRunnable)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
            handlerThread.quitSafely()
        } else {
            handlerThread.quit()
        }
    }

    companion object {

        private val TAG = "MainActivity"
        private val MAIN = Handler(Looper.getMainLooper())

        @WorkerThread
        private fun encode(textToEncode: String,
                           encodeView: TextView,
                           decodeView: TextView) {
            val encoded = Base47.encode(textToEncode.toByteArray())
            Log.d(TAG, "ENCODED: " + encoded)

            val decoded = String(Base47.decode(encoded))
            Log.d(TAG, "DECODED: " + decoded)

            MAIN.post {
                val context = encodeView.context
                encodeView.text = context.getString(R.string.encoded, encoded)
                decodeView.text = context.getString(R.string.decoded, decoded)
            }
        }
    }

}