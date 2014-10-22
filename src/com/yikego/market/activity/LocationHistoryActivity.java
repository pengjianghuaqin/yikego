package com.yikego.market.activity;

import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.yikego.android.rom.sdk.bean.LocationHistoryList;
import com.yikego.market.R;
import com.yikego.market.SplashActivity.MyLocationListenner;
import com.yikego.market.yikegoApplication;
import com.yikego.market.adapter.LocHistoryAdapter;
import com.yikego.market.adapter.UserOrderAdapter;
import com.yikego.market.contentProvider.LoacationHistory.LoacationHistoryColumns;
import com.yikego.market.utils.GlobalUtil;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Message;
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
	private String mStreetName;
	private EditText mSearchName;
	private RelativeLayout mLayout_locat_cur;
	private TextView mTitle_map;
	private LocHistoryAdapter mLocHistoryAdapter;
    private List<LocationHistoryList> mLocHistoryLists;
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private yikegoApplication mApplication;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("LocationHistoryActivity", "onCreate");
        yikegoApplication.getInstance().addActivity(this);
		// 定位初始化
		mLocClient = new LocationClient(LocationHistoryActivity.this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		
		setContentView(R.layout.activity_location_history);
		mActionBarText = (TextView) findViewById(R.id.actionbar_title);
		mActionBarText.setText(R.string.location_history_actionbar_title);
		mTitle_map = (TextView) findViewById(R.id.title_map);
		mLocHistoryAdapter = new LocHistoryAdapter(this);
		mApplication = (yikegoApplication) yikegoApplication.getInstance();
		mLocHistoryLists = mApplication.getLocationHistoryList();
		mLocHistoryAdapter.setLocationHistoryLists(mLocHistoryLists);
		initView();
	}

	private void initView() {
		mLayout_locat_cur = (RelativeLayout) findViewById(R.id.layout_location_cur);
		mLayout_locat_cur.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mLocClient.start();
			}
		});
		mListView = (ListView) findViewById(android.R.id.list);
		mListView.setAdapter(mLocHistoryAdapter);
		// mListView.setOnScrollListener(mScrollListener);
		mListView.setOnItemClickListener(this);
		TextView map = (TextView) findViewById(R.id.text_define);
		map.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mLocationName = mSearchName.getText().toString();
				GlobalUtil.setCurrentStreet(LocationHistoryActivity.this,
						mLocationName);
				finish();
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

    private void updateDB(ContentValues values) {
        getContentResolver().insert(LoacationHistoryColumns.CONTENT_URI, values);
    }

    /**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null) {
				return;
			}
			mStreetName = location.getStreet();
			if (mStreetName != null) {
				mTitle_map.setText(mStreetName);
			} else {
				mTitle_map.setText(R.string.location_history_location_hint);
			}
			if (mStreetName != null) {
				ContentValues values = new ContentValues();
				values.put(LoacationHistoryColumns.STREETNAME, mStreetName);
				values.put(LoacationHistoryColumns.LONGITUDE,
						location.getLongitude());
				values.put(LoacationHistoryColumns.LATITUDE,
						location.getLatitude());
				updateDB(values);
			}
			List<LocationHistoryList> locHistoryLists = mApplication.getLocationHistoryList();
			mLocHistoryAdapter.setLocationHistoryLists(locHistoryLists);
			mLocHistoryAdapter.notifyDataSetChanged();
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub

		}
	}
}
