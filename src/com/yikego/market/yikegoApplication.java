package com.yikego.market;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import cn.sharesdk.framework.ShareSDK;
import com.baidu.mapapi.SDKInitializer;
import com.yikego.android.rom.sdk.bean.LocationHistoryList;
import com.yikego.market.contentProvider.LoacationHistory;
import com.yikego.market.contentProvider.LoacationHistory.LoacationHistoryColumns;

import android.app.Application;
import android.database.Cursor;

public class yikegoApplication extends Application {
	private List<LocationHistoryList> mLocationHistoryList;
	private static yikegoApplication mApplication;
    private List<Activity> activityList=new LinkedList<Activity>();

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		mLocationHistoryList = new ArrayList<LocationHistoryList>();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
        //ShareSDK init
        ShareSDK.initSDK(this);
	}

	public static synchronized yikegoApplication getInstance() {
		return mApplication;
	}
	
	public List<LocationHistoryList> getLocationHistoryList() {
		mLocationHistoryList.clear();
		Cursor cursor = getContentResolver().query(
				LoacationHistory.LoacationHistoryColumns.CONTENT_URI, null,
				null, null, LoacationHistoryColumns.STREETNAME + " ASC ");
		try {
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
				mLocationHistoryList.add(locHistory);
			}
		} finally {
			cursor.close();
		}

		return mLocationHistoryList;
	}

    //添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    //遍历所有Activity并finish
    public void exit() {
        for(Activity activity:activityList) {
            activity.finish();
        }
        System.exit(0);
    }
}
