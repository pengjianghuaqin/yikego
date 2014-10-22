package com.yikego.market;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.yikego.market.activity.MarketBrowser;
import com.yikego.market.activity.SearchGoodActivity;
import com.yikego.market.contentProvider.LoacationHistory.LoacationHistoryColumns;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
	private Context mContext;
	private Handler mHandler;
	private static final int ACTION_NETWORK_ERROR = 0;
	private static final int ACTION_NEW_ACTIVITY = 1;
	private String mStreetName;
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    boolean isFirstLoc = true;// 是否首次定位
    private LatLng mLatLng;

    public SplashActivity() {
		mContext = this;
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		initHandler();
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
//		if (checkNetworkState()) {
//			 mHandler.sendEmptyMessageDelayed(ACTION_NEW_ACTIVITY, 1000);
//		} else {
//			mHandler.sendEmptyMessage(ACTION_NETWORK_ERROR);
//		}
	}

    private void updateDB(ContentValues values) {
        getContentResolver().insert(LoacationHistoryColumns.CONTENT_URI, values);
    }

    private void initHandler() {
		// TODO Auto-generated method stub
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case ACTION_NEW_ACTIVITY:
					Intent intent = new Intent(mContext, MarketBrowser.class);
                    if (msg.obj!=null){
                        LatLng latLng = (LatLng) msg.obj;
                        intent.putExtra("lat", latLng.latitude);
                        intent.putExtra("lng", latLng.longitude);
                        intent.putExtra("street", mStreetName);

                    }else {
                        intent.putExtra("lat", 0f);
                        intent.putExtra("lng", 0f);
                    }
					startActivity(intent);
					finish();
					break;
				case ACTION_NETWORK_ERROR:
//					Intent intent2 = new Intent(mContext, MarketBrowser.class);
//					intent2.putExtra("bManage", true);
//					startActivity(intent2);
//					finish();
//					Toast.makeText(mContext, mContext.getString(R.string.error_network_low_speed), Toast.LENGTH_LONG).show();
					break;
					
				default:
					break;
				}
			}
		};
	}
	private boolean checkNetworkState() {
		// TODO Auto-generated method stub
		ConnectivityManager connectMgr = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectMgr == null) {
			return false;
		}

		NetworkInfo nwInfo = connectMgr.getActiveNetworkInfo();

		if (nwInfo == null || !nwInfo.isAvailable()) {
			return false;
		}
		return true;
	}

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        super.onDestroy();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null){
                Message msg = new Message();
                msg.what = ACTION_NEW_ACTIVITY;
                msg.obj = null;
                mHandler.sendMessageDelayed(msg, 1000);
                return;
            }
            mStreetName = location.getStreet();
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
//            mBaiduMap.setMyLocationData(locData);
            ContentValues values = new ContentValues();
            values.put(LoacationHistoryColumns.STREETNAME, mStreetName);
            values.put(LoacationHistoryColumns.LONGITUDE, location.getLongitude());
            values.put(LoacationHistoryColumns.LATITUDE, location.getLatitude());
            updateDB(values);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                mLatLng = ll;
                Log.d(TAG, "Location : lat " + ll.latitude);
                Log.d(TAG, "Location :  lng : " + ll.longitude);
                Log.d(TAG, "Location :  street : " + mStreetName);
                Log.d(TAG, "Location :  street : " + location.getAddrStr());
                Log.d(TAG, "Location :  street : " + location.getCity());
                Log.d(TAG, "Location :  street : " + location.getProvince());
                Log.d(TAG, "Location :  street : " + location.getDistrict());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
//                mBaiduMap.animateMapStatus(u);
                Message msg = new Message();
                msg.what = ACTION_NEW_ACTIVITY;
                msg.obj = ll;
                mHandler.sendMessageDelayed(msg, 1000);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
}
