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

public class MarketSettlementActivity extends ListActivity implements
		OnItemClickListener {
	private Context mContext;

	public MarketSettlementActivity() {
		mContext = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settlement);
		initView();
	}

	private void initView() {
		Button cod = (Button)findViewById(R.id.btn_cod);
		cod.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, CodActivity.class);
				startActivity(intent);
			}
		});
		Button payment = (Button)findViewById(R.id.btn_payment);
		payment.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, PaymentActivity.class);
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