package com.yikego.market.activity;

import com.yikego.market.R;
import com.yikego.market.utils.GlobalUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;



public class ConsigneeEditActivity extends Activity {
	private EditText mEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_consignee_edit);
		mEditText = (EditText) findViewById(R.id.edit);
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
