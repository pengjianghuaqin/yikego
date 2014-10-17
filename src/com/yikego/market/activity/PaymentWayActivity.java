package com.yikego.market.activity;

import com.yikego.market.R;
import com.yikego.market.utils.GlobalUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PaymentWayActivity extends Activity implements OnClickListener {
	private TextView actionBar_title;
	private Button mButton_cod;
	private Button mButton_online;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_paymentway);

		actionBar_title = (TextView) findViewById(R.id.actionbar_title);
		actionBar_title.setText(R.string.text_playment_way);
		
		mButton_cod = (Button)findViewById(R.id.button_cod);
		mButton_online = (Button)findViewById(R.id.button_online);
		mButton_cod.setOnClickListener(this);
		mButton_online.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_cod:
			GlobalUtil.setPlaymentWay(this, GlobalUtil.PLAYMENT_COD);
			break;
		case R.id.button_online:
			GlobalUtil.setPlaymentWay(this, GlobalUtil.PLAYMENT_ONLINE);
			break;

		default:
			break;
		}
		finish();
	}
}
