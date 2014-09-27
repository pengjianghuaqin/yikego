package com.yikego.market.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.yikego.android.rom.sdk.bean.PaginationStoreListInfo;
import com.yikego.android.rom.sdk.bean.PostUserLocationInfo;
import com.yikego.android.rom.sdk.bean.StoreId;
import com.yikego.android.rom.sdk.bean.StoreInfo;
import com.yikego.market.R;
import com.yikego.market.activity.MarketListAdapter.ViewHolder;
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
public class MarketDetailActivity extends Activity{

	private GridView mGridView;
	private ImageView mBack;
	private TextView mSearch;
	private GridAdapter mGridAdapter;
	private int storeId;
	private Context mContext;
	private Request mCurrentRequest;
	private ThemeService mThemeService;
	private static final int ACTION_NETWORK_ERROR = 0;
	private static final int ACTION_STORE_INFO = 1;
	private Handler mHandler;

	public MarketDetailActivity() {
		mContext = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_market_detail);
		storeId = getIntent().getIntExtra("storeId", 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initViews();
		// initActionBar();
	}

	private void initViews() {
		mGridView = (GridView) findViewById(R.id.market_detail_grid);
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
						if (marketGoodsInfoList.marketGoodsInfoList != null) {
							mGridAdapter = new GridAdapter(mContext,
									marketGoodsInfoList.marketGoodsInfoList);

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

		Request request = new Request(0, Constant.TYPE_POST_USER_LOCAL_INFO);
		StoreId mStoreId = new StoreId();
		mStoreId.storeId = storeId;
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

			MarketGoodsInfo marketGoodsInfo = null;
			Log.v("asd", "position =" + position);
			if (position >= 0) {
				marketGoodsInfo = getItem(position);
			}
			convertView = mLayoutInflater.inflate(
					R.layout.market_detail_grid_item, null);
			if (convertView == null) {
				// convertView = mLayoutInflater.inflate(R.layout.app_list_item,
				// null);
				convertView = mLayoutInflater.inflate(
						R.layout.market_list_item, parent, false);
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
				viewHolder.detail.setText(marketGoodsInfo.shortDescription);
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext,
							MarketGoodsListActivity.class);
					startActivity(intent);
				}
			});
			return convertView;
		}

		public class ViewHolder {
			TextView mName;
			TextView detail;
		}
	}
}
