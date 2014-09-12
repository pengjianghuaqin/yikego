package com.yikego.market.activity;

import com.yikego.market.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MarketGoodsDetailActivity extends Activity{
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MarketGoodsDetailActivity", "onCreate");
        setContentView(R.layout.activity_goods_detail);
    }
}