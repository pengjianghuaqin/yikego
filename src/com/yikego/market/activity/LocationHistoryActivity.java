package com.yikego.market.activity;

import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.widget.*;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.yikego.android.rom.sdk.bean.LocationHistoryList;
import com.yikego.market.R;
import com.yikego.market.SplashActivity.MyLocationListenner;
import com.yikego.market.contentProvider.LoacationHistory;
import com.yikego.market.contentProvider.LoacationHistoryProvider;
import com.yikego.market.utils.DBHelper;
import com.yikego.market.yikegoApplication;
import com.yikego.market.adapter.LocHistoryAdapter;
import com.yikego.market.adapter.UserOrderAdapter;
import com.yikego.market.contentProvider.LoacationHistory.LoacationHistoryColumns;
import com.yikego.market.model.Latitude;
import com.yikego.market.utils.GlobalUtil;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class LocationHistoryActivity extends Activity implements
		AdapterView.OnItemClickListener, OnGetGeoCoderResultListener {
    private static final String TAG = "LocationHistoryActivity";
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private Latitude mLatitude;
	private TextView mActionBarText;
	private ListView mListView;
    private ImageView mDeleteHistory;
	private String mLocationName;
	private String mStreetName;
	private EditText mSearchName;
	private RelativeLayout mLayout_locat_cur;
	private TextView mTitle_map;
    private TextView mSure;
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
        option.setAddrType("all");// 返回的定位结果包含地址信息
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(LocationHistoryActivity.this);
		mLocClient.setLocOption(option);
		mLatitude = new Latitude();
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

	@Override
	protected void onDestroy() {
        mSearch.destroy();
        mLocClient.stop();
		super.onDestroy();
	}
	
	
	private void initView() {
		mLayout_locat_cur = (RelativeLayout) findViewById(R.id.layout_location_cur);
		mLayout_locat_cur.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mLocClient.start();
			}
		});
        mDeleteHistory = (ImageView) findViewById(R.id.delete_location);
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
        mDeleteHistory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getContentResolver().delete(LoacationHistoryColumns.CONTENT_URI,null,null);
//                getApplicationContext().deleteDatabase(LoacationHistoryProvider.TABLE_NAME);
                mLocHistoryAdapter.setLocationHistoryLists(null);
                mLocHistoryAdapter.notifyDataSetChanged();
            }
        });
		mSearchName = (EditText) findViewById(R.id.search_edittext);
        mSure = (TextView) findViewById(R.id.text_define);
        mSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = null;
                search = mSearchName.getText().toString();
                if (search !=null&&!search.equals("")){
                    searchHistory(search);
                }
            }
        });

	}

    private void searchHistory(String search) {
        String where = LoacationHistoryColumns.STREETNAME + " LIKE '%"+search+"%'";
        Cursor cursor = getContentResolver().query(LoacationHistoryColumns.CONTENT_URI,
                new String[]{where}, null, null, LoacationHistoryColumns.STREETNAME + " ASC ");
        Log.d(TAG, "search : " + cursor.getCount());
        try {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                LocationHistoryList locHistory = new LocationHistoryList();
                String streetName = cursor
                        .getString(cursor
                                .getColumnIndex(LoacationHistory.LoacationHistoryColumns.STREETNAME));
                String longitude = cursor
                        .getString(cursor
                                .getColumnIndex(LoacationHistory.LoacationHistoryColumns.LONGITUDE));
                String latitude = cursor
                        .getString(cursor
                                .getColumnIndex(LoacationHistory.LoacationHistoryColumns.LATITUDE));
                locHistory.setStreetName(streetName);
                locHistory.setStreetLongitude(longitude);
                locHistory.setStreetLatitude(latitude);
                List<LocationHistoryList> locHistoryLists = mApplication.getLocationHistoryList();
                locHistoryLists.add(locHistory);
                mLocHistoryAdapter.setLocationHistoryLists(locHistoryLists);
                mLocHistoryAdapter.notifyDataSetChanged();
            }
        } finally {
            cursor.close();
        }

    }

    public void onBackButton(View v) {
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
        final LocationHistoryList orderList = mLocHistoryLists.get(arg2);
        Intent intent = new Intent(this, MarketBrowser.class);
        intent.putExtra("lat", orderList.getStreetLatitude());
        intent.putExtra("lng", orderList.getStreetLongitude());
        intent.putExtra("street", orderList.getStreetName());
        this.startActivity(intent);
        finish();
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
            Log.d(TAG, "Location : lat " + location.getLatitude());
            Log.d(TAG, "Location :  lng : " + location.getLongitude());
            Log.d(TAG, "Location :  street : " + mStreetName);
            mLatitude.lat = (float) location.getLatitude();
            mLatitude.lng = (float) location.getLongitude();
            if (mStreetName != null) {
                mTitle_map.setText(mStreetName);
            } else {
                mTitle_map.setText(R.string.location_history_location_hint);
                LatLng ptCenter = new LatLng((location.getLatitude()), (location.getLongitude()));
                // 反Geo搜索
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
            }
            ContentValues values = new ContentValues();
            values.put(LoacationHistoryColumns.STREETNAME, mStreetName);
            values.put(LoacationHistoryColumns.LONGITUDE, mLatitude.lng);
            values.put(LoacationHistoryColumns.LATITUDE, mLatitude.lat);
            DBHelper.updateDB(getContentResolver(), values);

			List<LocationHistoryList> locHistoryLists = mApplication.getLocationHistoryList();
            if (mLocClient!=null)
                mLocClient.stop();
            mLocHistoryAdapter.setLocationHistoryLists(locHistoryLists);
            mLocHistoryAdapter.notifyDataSetChanged();
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
		if (reverseGeoCodeResult == null
				|| reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(LocationHistoryActivity.this, "定位失败", Toast.LENGTH_LONG)
					.show();
			return;
		}
		Toast.makeText(LocationHistoryActivity.this, reverseGeoCodeResult.getAddress(),
				Toast.LENGTH_LONG).show();
		Log.d(TAG,
				"LocationHistoryActivity:   onGetReverseGeoCodeResult : "
						+ reverseGeoCodeResult.getAddress());
		Log.d(TAG,
				"LocationHistoryActivity:   onGetReverseGeoCodeResult : "
						+ reverseGeoCodeResult.getBusinessCircle());
		mStreetName = reverseGeoCodeResult.getAddress();
		if (mStreetName != null) {
			mTitle_map.setText(mStreetName);
			ContentValues values = new ContentValues();
			values.put(LoacationHistoryColumns.STREETNAME, reverseGeoCodeResult.getAddress());
			values.put(LoacationHistoryColumns.LONGITUDE, mLatitude.lng);
			values.put(LoacationHistoryColumns.LATITUDE, mLatitude.lat);
            DBHelper.updateDB(getContentResolver(), values);
            mLocHistoryAdapter.notifyDataSetChanged();
		} else {
			mTitle_map.setText(R.string.location_history_location_hint);
		}
	}
}
