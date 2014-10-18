package com.yikego.market.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.yikego.android.rom.sdk.bean.PaginationStoreListInfo;
import com.yikego.android.rom.sdk.bean.PostUserLocationInfo;
import com.yikego.android.rom.sdk.bean.StoreInfo;
import com.yikego.market.R;
import com.yikego.market.model.Latitude;
import com.yikego.market.utils.Constant;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * show store location and click to start activity for store info
 * 
 */
public class LocationActivity extends Activity {

    private static final String TAG = "LocationActivity";
    private TextView mActionbarTitle;
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;

    private Latitude mLatitude;
    public static List<Latitude> storeLatitude;
    private PaginationStoreListInfo mPaginationStoreListInfo = null;
    private ArrayList<StoreInfo> storeList = null;

	MapView mMapView;
	BaiduMap mBaiduMap;

	// UI相关
	OnCheckedChangeListener radioButtonListener;
	boolean isFirstLoc = true;// 是否首次定位

	BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_location);
	private Marker mMarkerA;

    private InfoWindow mInfoWindow;

    private Context mContext;
    private ThemeService mThemeService;

    public LocationActivity() {
        mContext = this;
        storeLatitude = new ArrayList<Latitude>();
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
        mThemeService = ThemeService.getServiceInstance(mContext);
        mLatitude = new Latitude();
//        initIntent();
		mCurrentMode = LocationMode.NORMAL;

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
//		initOverlay();
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getApplicationContext(), marker.getTitle().toString(), Toast.LENGTH_SHORT).show();
                Bundle bundle = marker.getExtraInfo();
                Button button = new Button(getApplicationContext());
                InfoWindow.OnInfoWindowClickListener listener = null;
                button.setBackgroundResource(R.drawable.popup);
                if (bundle!=null){
                    final StoreInfo store = (StoreInfo) bundle.getSerializable("store");
                    if (store != null){
                        button.setTextColor(getResources().getColor(android.R.color.black));
                        button.setText(store.name);
                        listener = new InfoWindow.OnInfoWindowClickListener() {
                            public void onInfoWindowClick() {
                                Intent intent = new Intent();
                                intent.setClass(LocationActivity.this, MarketDetailActivity.class);
                                intent.putExtra("storeInfo", store);
                                startActivity(intent);
                            }
                        };
                        LatLng ll = marker.getPosition();
                        mInfoWindow = new InfoWindow(button, ll , listener);
                        mBaiduMap.showInfoWindow(mInfoWindow);
                    }
                }
                return true;
            }
        });

        initView();
	}

    private void postUserLocalInfo(LatLng latLng) {
        // TODO Auto-generated method stub

        Request request = new Request(0, Constant.TYPE_POST_USER_LOCAL_INFO);
        // Object[] params = new Object[2];
        PostUserLocationInfo postUserLocationInfo = new PostUserLocationInfo();
        postUserLocationInfo.distance = 25.0f;
        postUserLocationInfo.lat = (float) latLng.latitude;
        postUserLocationInfo.lng = (float) latLng.longitude;
        postUserLocationInfo.name = "";
        postUserLocationInfo.nowPage = 1;
        postUserLocationInfo.pageCount = 100;
        request.setData(postUserLocationInfo);
        request.addObserver(new Observer() {

            @Override
            public void update(Observable observable, Object data) {
                // TODO Auto-generated method stub
                if (data != null) {
                    PaginationStoreListInfo paginationStoreListInfo = (PaginationStoreListInfo) data;
                    mPaginationStoreListInfo = paginationStoreListInfo;
                    storeList = mPaginationStoreListInfo.storelist;
                    initOverlay();
                } else {
                }
            }
        });
        mThemeService.getStoreList(request);
    }

    private void initView() {
        mActionbarTitle = (TextView) findViewById(R.id.actionbar_title);
        mActionbarTitle.setText(getString(R.string.title_map));
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent != null){
            mPaginationStoreListInfo = (PaginationStoreListInfo) intent.getSerializableExtra("StoreInfo");
            storeList = mPaginationStoreListInfo.storelist;
            Log.d(TAG, "storeList lenth : " + storeList.size());
        }
    }

    private void initOverlay() {
		// add marker overlay
        if (storeList.size()<=0){
            return;
        }
        int storeCount = storeList.size();
        for (int i=0 ; i < storeCount ; i++){
            StoreInfo store = storeList.get(i);
            LatLng point = new LatLng(store.lat, store.lng);
            OverlayOptions ooA = new MarkerOptions().position(point).icon(bdA)
                    .zIndex(i).draggable(true).title(store.name);
            mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
            Bundle bundle = new Bundle();
            bundle.putSerializable("store", store);
            mMarkerA.setExtraInfo(bundle);
//            mMarkerA.setTitle(store.name);
        }
/*		LatLng llA = new LatLng(31.159488, 121.579398);
		OverlayOptions ooA = new MarkerOptions().position(llA).icon(bdA)
				.zIndex(9).draggable(true);
		mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));*/
	}
	
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);

			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                postUserLocalInfo(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
		
		// 回收 bitmap 资源
		bdA.recycle();
	}

    public void onBackButton(View v) {
        finish();
    }
}
