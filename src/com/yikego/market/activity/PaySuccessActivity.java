package com.yikego.market.activity;

import com.yikego.market.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PaySuccessActivity extends Activity {

	private TextView actionBar_title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.activity_pay_success);
        
		actionBar_title = (TextView) findViewById(R.id.actionbar_title);
		actionBar_title.setText(R.string.text_play_success);
	}
	
	public void onBackButton(View v) {
		finish();
	}
}
