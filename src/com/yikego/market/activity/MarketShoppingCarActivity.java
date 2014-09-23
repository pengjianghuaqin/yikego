package com.yikego.market.activity;

import com.yikego.market.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

public class MarketShoppingCarActivity extends ListActivity implements
		OnItemClickListener {
	private Context mContext;

	public MarketShoppingCarActivity() {
		mContext = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_car);
		initView();
	}

	private void initView() {
		Button settlement = (Button) findViewById(R.id.btn_settlement);
		settlement.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, MarketSettlementActivity.class);
				startActivity(intent);
			}
			
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}
}