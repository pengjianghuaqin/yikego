package com.yikego.market.activity;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.widget.ImageView;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.yikego.android.rom.sdk.bean.MessageRecord;
import com.yikego.android.rom.sdk.bean.UserLoginInfo;
import com.yikego.market.R;
import com.yikego.market.fragment.SlidingMenuFragment;
import com.yikego.market.model.MarketData;
import com.yikego.market.utils.Constant;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MarketBrowser extends SlidingFragmentActivity{
	private ListView mListView;
	private MarketListAdapter mMarketListAdapter;
    private ImageView mMenuButton;
	private Context mContext;
	private Request mCurrentRequest;
	private ThemeService mThemeService;
	private String TAG = "MarketBrowser";
	private static final int ACTION_NETWORK_ERROR = 0;
	private static final int ACTION_USER_LOGIN = 1;
	private Handler mHandler;
	public MarketBrowser(){
		Log.v(TAG, "MarketBrowser");
		mContext = this;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		setContentView(R.layout.activity_market_browser);
		mThemeService = ThemeService.getServiceInstance(mContext);
		initView();
        initSlidingMenu();
        initHandler();
        userLogin();
	}

    private void initSlidingMenu() {
        // 设置滑动菜单打开后的视图界面
        setBehindContentView(R.layout.menu_frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new SlidingMenuFragment()).commit();

        // 设置滑动菜单的属性值
        SlidingMenu sm = getSlidingMenu();
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.25f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
    }

    private void initView(){
        mMenuButton = (ImageView) findViewById(R.id.btn_menu);
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        mListView = (ListView) findViewById(android.R.id.list);

        ArrayList<MarketData> marketList = new ArrayList<MarketData>();
		marketList.add(new MarketData());
		marketList.add(new MarketData());
		marketList.add(new MarketData());
		mMarketListAdapter = new MarketListAdapter(mContext,marketList);
		
		mListView.setAdapter(mMarketListAdapter);
	}
    
    
    private void userLogin() {
		// TODO Auto-generated method stub
		
		Request request = new Request(0, Constant.TYPE_POST_USER_LOGIN);
//		Object[] params = new Object[2];
        UserLoginInfo userLoginInfo = new UserLoginInfo();
        userLoginInfo.messageRecordType = "1";
        userLoginInfo.userPhone = "13816412339";
//		params[0] = "1";
//		params[1] = "13827658345";
//		request.setData(params);
        request.setData(userLoginInfo);
		request.addObserver(new Observer() {

			@Override
			public void update(Observable observable, Object data) {
				// TODO Auto-generated method stub
				if (data != null) {
					Message msg = Message.obtain(mHandler, ACTION_USER_LOGIN,
							data);
					mHandler.sendMessage(msg);
				} else {
					Request request = (Request) observable;
					if (request.getStatus() == Constant.STATUS_ERROR) {
						mHandler.sendEmptyMessage(ACTION_NETWORK_ERROR);
					}
				}
			}
		});
		mCurrentRequest = request;
		mThemeService.getThemeList(request);
	}
    
    private void initHandler() {
		// TODO Auto-generated method stub
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case ACTION_USER_LOGIN:
                    MessageRecord messageRecord = (MessageRecord) msg.obj;
                    Log.d(TAG, "messageRecorde : " + messageRecord.messageRecordId);
					break;

				default:
					break;
				}
			}
		};
	}
}