package com.yikego.market.activity;

import java.net.URL;

import com.yikego.android.rom.sdk.bean.StoreInfo;
import com.yikego.market.R;
import com.yikego.market.model.EnterShoppingCarDialog;
import com.yikego.market.model.GoodsData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MarketGoodsDetailActivity extends Activity{
	private Context mContext;
	private GoodsData mGoodsData;
	public MarketGoodsDetailActivity(){
		mContext = this;
	}
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MarketGoodsDetailActivity", "onCreate");
        setContentView(R.layout.activity_goods_detail);
        mGoodsData = (GoodsData) getIntent().getSerializableExtra("goodsData");
        initView();
    }
	
	private void initView(){
		Button confirm = (Button)findViewById(R.id.btn_confirm);
		confirm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, MarketSettlementActivity.class);
				startActivity(intent);
			}
		});
		ImageView back = (ImageView)findViewById(R.id.goods_detail_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		LinearLayout enterMarket = (LinearLayout)findViewById(R.id.enter_market_area);
		enterMarket.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		RelativeLayout enterShoppingCar = (RelativeLayout)findViewById(R.id.goods_detail_area);
		enterShoppingCar.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			EnterShoppingCarDialog dialog = new EnterShoppingCarDialog(mContext);
             dialog.show();
			}
		});
		
	}
	
}