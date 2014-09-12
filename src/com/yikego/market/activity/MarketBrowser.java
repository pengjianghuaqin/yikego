package com.yikego.market.activity;

import java.util.ArrayList;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.yikego.market.R;
import com.yikego.market.fragment.SlidingMenuFragment;
import com.yikego.market.model.MarketData;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MarketBrowser extends SlidingFragmentActivity{
	private ListView mListView;
	private MarketListAdapter mMarketListAdapter;
	private Context mContext;
	private String TAG = "MarketBrowser";
	public MarketBrowser(){
		Log.v(TAG, "MarketBrowser");
		mContext = this;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		setContentView(R.layout.activity_market_browser);
		Log.v(TAG, "setContentView");
		initView();
        initSlidingMenu();
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
		mListView = (ListView) findViewById(android.R.id.list);

        ArrayList<MarketData> marketList = new ArrayList<MarketData>();
		marketList.add(new MarketData());
		marketList.add(new MarketData());
		marketList.add(new MarketData());
		mMarketListAdapter = new MarketListAdapter(mContext,marketList);
		
		mListView.setAdapter(mMarketListAdapter);
	}
}