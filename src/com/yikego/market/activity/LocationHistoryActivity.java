package com.yikego.market.activity;

import com.yikego.market.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LocationHistoryActivity extends Activity implements
		AdapterView.OnItemClickListener {
	private TextView mActionBarText;
	private ListView mListView;
	private String mLocationName;
	private EditText mSearchName;
	private RelativeLayout mLayout_locat_cur;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("LocationHistoryActivity", "onCreate");

		setContentView(R.layout.activity_location_history);
		mActionBarText = (TextView) findViewById(R.id.actionbar_title);
		mActionBarText.setText(R.string.location_history_actionbar_title);

		initView();
	}

	private void initView() {
		mLayout_locat_cur = (RelativeLayout) findViewById(R.id.layout_location_cur);
		mLayout_locat_cur.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

			}
		});
		mListView = (ListView) findViewById(android.R.id.list);
		// mListView.setOnScrollListener(mScrollListener);
		mListView.setOnItemClickListener(this);
		TextView map = (TextView) findViewById(R.id.text_define);
		map.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mLocationName = mSearchName.getText().toString();

			}
		});
		mSearchName = (EditText) findViewById(R.id.search_edittext);

	}

	public void onBackButton(View v) {
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}
}
