package com.yikego.market.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.yikego.android.rom.sdk.bean.MarketGoodsInfo;
import com.yikego.android.rom.sdk.bean.MarketGoodsInfoListData;
import com.yikego.android.rom.sdk.bean.OrderProductInfo;
import com.yikego.android.rom.sdk.bean.PaginationStoreListInfo;
import com.yikego.android.rom.sdk.bean.PostUserLocationInfo;
import com.yikego.android.rom.sdk.bean.StoreId;
import com.yikego.android.rom.sdk.bean.StoreInfo;
import com.yikego.market.R;
import com.yikego.market.activity.MarketListAdapter.ViewHolder;
import com.yikego.market.utils.CachedThumbnails;
import com.yikego.market.utils.Constant;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.zip.Inflater;

/**
 * Created by wll on 14-8-16.
 */
public class MarketDetailActivity extends Activity implements
		AdapterView.OnItemClickListener {

	private GridView mGridView;
	private ImageView mBack;
	private TextView mSearch;
	private GridAdapter mGridAdapter;
	public static StoreInfo storeInfo;
	private Context mContext;
	private Request mCurrentRequest;
	private ThemeService mThemeService;
	private static final int ACTION_NETWORK_ERROR = 0;
	private static final int ACTION_STORE_INFO = 1;
	private Handler mHandler;
	public static int storeID;
	public static ArrayList<OrderProductInfo> orderDetailList;

	public MarketDetailActivity() {
		mContext = this;
		orderDetailList = new ArrayList<OrderProductInfo>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_market_detail);
		storeInfo = (StoreInfo) getIntent().getSerializableExtra("storeInfo");
		storeID = storeInfo.storeId;
		mThemeService = ThemeService.getServiceInstance(mContext);
		initHandler();
		initViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// initActionBar();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(orderDetailList!=null){
			orderDetailList.clear();
		}
		// initActionBar();
	}
	
	public Drawable getThumbnail(int id) {
		// TODO Auto-generated method stub
		
		Drawable drawable = CachedThumbnails.getThumbnail(this, id);
		if (drawable == null) {
			return CachedThumbnails.getDefaultIcon(this);
		} else {
			return drawable;
		}
	}
	private void initViews() {
		mGridView = (GridView) findViewById(R.id.market_detail_grid);
		mGridView.setOnItemClickListener(this);
		mBack = (ImageView) findViewById(R.id.market_detail_back);
		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mSearch = (TextView) findViewById(R.id.market_search);
		mSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MarketDetailActivity.this,
						SearchGoodActivity.class);
				startActivity(intent);
			}
		});
		ImageView marketIcon = (ImageView) findViewById(R.id.market_detail_image);
		marketIcon.setBackgroundDrawable(getThumbnail(storeInfo.storeId));
		TextView marketName = (TextView) findViewById(R.id.market_detail_name);
		marketName.setText(storeInfo.name);
		TextView marketDistance = (TextView) findViewById(R.id.market_detail_distance);
		marketDistance.setText("距离 :"+storeInfo.aboutDistance+"米");
		TextView marketWorkTime = (TextView) findViewById(R.id.market_detail_work_time);
		String openTime,closeTime;
		if (storeInfo.openMinute < 10) {
			openTime = "0" + storeInfo.openMinute;
		} else {
			openTime = "" + storeInfo.openMinute;
		}
		if (storeInfo.closeMinute < 10) {
			closeTime = "0" + storeInfo.closeMinute;
		} else {
			closeTime = "" + storeInfo.closeMinute;
		}
		marketWorkTime.setText("营业时间" + storeInfo.openHour + ":"
				+ openTime + "-" + storeInfo.closeHour + ":" + closeTime);TextView marketAddress = (TextView) findViewById(R.id.market_detail_address);
		marketAddress.setText(storeInfo.address);
		TextView marketTel = (TextView) findViewById(R.id.market_detail_tel);
		marketTel.setText(storeInfo.storeTel);
		TextView marketPhone = (TextView) findViewById(R.id.market_detail_phone);
		marketPhone.setText(storeInfo.storePhone);
		GetStoreInfo();
	}

	private void initHandler() {
		// TODO Auto-generated method stub
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case ACTION_STORE_INFO:
					MarketGoodsInfoListData marketGoodsInfoList = (MarketGoodsInfoListData) msg.obj;
					
					if (marketGoodsInfoList != null) {
						if (marketGoodsInfoList.productTypeList != null) {
							mGridAdapter = new GridAdapter(mContext,
									marketGoodsInfoList.productTypeList);
							Log.v("ACTION_STORE_INFO", "productTypeList ="+marketGoodsInfoList.productTypeList.size());
						}
						mGridView.setAdapter(mGridAdapter);
					}
					mGridView.setVisibility(View.VISIBLE);

					break;

				default:
					break;
				}
			}
		};
	}

	private void GetStoreInfo() {
		// TODO Auto-generated method stub

		Request request = new Request(0, Constant.TYPE_GET_GOODS_TYPE_INFO);
		StoreId mStoreId = new StoreId();
		mStoreId.storeId = storeInfo.storeId;
		request.setData(mStoreId);
		request.addObserver(new Observer() {

			@Override
			public void update(Observable observable, Object data) {
				// TODO Auto-generated method stub
				if (data != null) {
					Message msg = Message.obtain(mHandler, ACTION_STORE_INFO,
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
		mThemeService.getStoreList(request);
	}

	private class GridAdapter extends ArrayAdapter<MarketGoodsInfo> {
		private Context mContext;
		private ViewHolder viewHolder = null;
		private LayoutInflater mLayoutInflater;

		public GridAdapter(Context context, ArrayList<MarketGoodsInfo> objects) {
			super(context, 0, objects);
			// TODO Auto-generated constructor stub
			mContext = context;
			mLayoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			MarketGoodsInfo marketGoodsInfo = new MarketGoodsInfo();
			if (position >= 0) {
				marketGoodsInfo = getItem(position);
			}
			Log.v("asd", "marketGoodsInfo =" + marketGoodsInfo);
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(
						R.layout.market_detail_grid_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.mName = (TextView) convertView
						.findViewById(R.id.grid_item_title);
				viewHolder.detail = (TextView) convertView
						.findViewById(R.id.grid_item_detail);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (marketGoodsInfo != null) {
				viewHolder.mName.setText(marketGoodsInfo.name);
				if(marketGoodsInfo.shortDescription!=null){
					viewHolder.detail.setText(marketGoodsInfo.shortDescription);
				}
			}
			return convertView;
		}

		public class ViewHolder {
			TextView mName;
			TextView detail;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		MarketGoodsInfo marketGoodsInfo= mGridAdapter.getItem(position);
		Intent intent = new Intent(mContext,
				MarketGoodsListActivity.class);
		intent.putExtra("productTypeId",marketGoodsInfo.productTypeId);
		startActivity(intent);
	}
}
