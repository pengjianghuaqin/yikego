package com.yikego.market.activity;

import com.yikego.market.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class PaymentActivity extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("PaymentActivity", "onCreate");
        setContentView(R.layout.activity_playment);
    }
}
