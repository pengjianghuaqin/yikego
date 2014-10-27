package com.yikego.market.activity;

import android.widget.TextView;
import com.yikego.market.R;
import com.yikego.market.utils.GlobalUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.yikego.market.yikegoApplication;


public class ConsigneeEditActivity extends Activity {
	private EditText mEditText;
    private TextView mSure;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_consignee_edit);
        yikegoApplication.getInstance().addActivity(this);
		mEditText = (EditText) findViewById(R.id.edit);
        mSure = (TextView) findViewById(R.id.confirm);
        mSure.setVisibility(View.VISIBLE);
        mSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAddressInfo();
            }
        });
        findViewById(R.id.market_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
	
	public void onConfirmClick(View v) {
        saveAddressInfo();
	}
	
	private void saveAddressInfo(){
		String editText = mEditText.getText().toString();
		if(editText != null) {
			GlobalUtil.setUserAddress(this, editText);
			finish();
		} else {
			Toast.makeText(this, "地址不能为空，请重新输入！", Toast.LENGTH_SHORT).show();
		}
	}
}
