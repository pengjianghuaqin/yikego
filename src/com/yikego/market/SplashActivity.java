package com.yikego.market;

import com.yikego.market.activity.MarketBrowser;
import com.yikego.market.activity.SearchGoodActivity;

import android.app.Activity;
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
	private Context mContext;
	private Handler mHandler;
	private static final int ACTION_NETWORK_ERROR = 0;
	private static final int ACTION_NEW_ACTIVITY = 1;
	public SplashActivity() {
		mContext = this;
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		initHandler();
		mHandler.sendEmptyMessageDelayed(ACTION_NEW_ACTIVITY, 1000);
//		if (checkNetworkState()) {
//			 mHandler.sendEmptyMessageDelayed(ACTION_NEW_ACTIVITY, 1000);
//		} else {
//			mHandler.sendEmptyMessage(ACTION_NETWORK_ERROR);
//		}
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
}
