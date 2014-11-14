package com.yikego.market.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.yikego.android.rom.sdk.bean.Coupon;
import com.yikego.android.rom.sdk.bean.CouponListInfo;
import com.yikego.android.rom.sdk.bean.OrderProductInfo;
import com.yikego.android.rom.sdk.bean.PostProductType;
import com.yikego.android.rom.sdk.bean.PostUserCouponInfo;
import com.yikego.android.rom.sdk.bean.ProductListInfo;
import com.yikego.market.R;
import com.yikego.market.yikegoApplication;
import com.yikego.market.activity.MarketListAdapter.ViewHolder;
import com.yikego.market.model.GoodsData;
import com.yikego.market.model.Image2;
import com.yikego.market.model.LoadingDialog;
import com.yikego.market.utils.CachedThumbnails;
import com.yikego.market.utils.Constant;
import com.yikego.market.utils.GlobalUtil;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MarketCouponListActivity extends ListActivity implements
		OnItemClickListener {
	private Context mContext;
	private Request mCurrentRequest;
	private ThemeService mThemeService;
	private Handler mHandler;
	private ListView mListView;
	private int nowPage, pageCount;
	private boolean isEnd;
	private boolean bBusy;
	private int mUserId;
	private AbsListView.OnScrollListener mScrollListener;
	private CouponListAdapter mCouponListAdapter;
	private static final int ACTION_NETWORK_ERROR = 0;
	private static final int ACTION_COUPON_LIST = 1;
	private LoadingDialog mProgressDialog;
	public static final String ACTION_COUPON_VERIFY = "coupon_verify";
	private final BroadcastReceiver mApplicationsReceiver;
	private boolean verifyFlag;
	public MarketCouponListActivity() {
		isEnd = false;
		bBusy = false;
		verifyFlag = false;
		nowPage = 1;
		pageCount = 10;
		mApplicationsReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Log.v("mApplicationsReceiver", "mApplicationsReceiver intent="+intent.getAction());
				verifyFlag = true;
				GetCouponList();
			}
		};
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coupon_list);
		mContext = this;
		mProgressDialog = new LoadingDialog(mContext);
		mThemeService = ThemeService.getServiceInstance(mContext);
		yikegoApplication.getInstance().addActivity(this);
		mUserId = GlobalUtil.getUserId(mContext);
		initView();
		registerIntentReceivers();
	}
	private void registerIntentReceivers() {
		// TODO Auto-generated method stub
		IntentFilter intentFilter = new IntentFilter(MarketCouponListActivity.ACTION_COUPON_VERIFY);
		registerReceiver(mApplicationsReceiver, intentFilter);		
	}
	
	private void unregisterIntentReceivers() {
		// TODO Auto-generated method stub
		unregisterReceiver(mApplicationsReceiver); 
	}
	private void initView() {
		mListView = getListView();
		mListView.setOnScrollListener(mScrollListener);
		mListView.setOnItemClickListener(this);
		findViewById(R.id.back_home).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(mContext, TicketVerifyActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.market_detail_back).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						finish();
					}
				});
		initHandler();
		initListener();
		GetCouponList();
	}

	public void onBackButton(View v) {
		finish();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterIntentReceivers();
	}
	private void initListener() {
		// TODO Auto-generated method stub
		mScrollListener = new AbsListView.OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				return;
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				case SCROLL_STATE_IDLE:
					bBusy = false;
					if (!isEnd) {
						GetCouponList();
					}
					break;
				case SCROLL_STATE_TOUCH_SCROLL:
				case SCROLL_STATE_FLING:
					// if (!bBusy) {
					// clearPendingThumbRequest();
					// bBusy = true;
					// }
				default:
					break;
				}
			}
		};
	}

	private void GetCouponList() {
		// TODO Auto-generated method stub
		if (isEnd) {
			return;
		}
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		Request request = new Request(0, Constant.TYPE_POST_COUPON_LIST);
		PostUserCouponInfo mPostUserCouponInfo = new PostUserCouponInfo();
		mPostUserCouponInfo.userId = mUserId;
		mPostUserCouponInfo.nowPage = nowPage;
		mPostUserCouponInfo.pageCount = pageCount;
		request.setData(mPostUserCouponInfo);
		request.addObserver(new Observer() {

			@Override
			public void update(Observable observable, Object data) {
				// TODO Auto-generated method stub
				if (data != null) {
					Message msg = Message.obtain(mHandler, ACTION_COUPON_LIST,
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

	private void initHandler() {
		// TODO Auto-generated method stub
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case ACTION_COUPON_LIST:
					CouponListInfo couponListInfo = (CouponListInfo) msg.obj;
					if (couponListInfo != null) {
						ArrayList<Coupon> marketCouponList = new ArrayList<Coupon>();
						for (int i = 0; i < couponListInfo.couponlist.size(); i++) {
							marketCouponList.add(couponListInfo.couponlist
									.get(i));
						}

						if (verifyFlag || mCouponListAdapter == null) {
							mCouponListAdapter = new CouponListAdapter(
									mContext, marketCouponList);
							mListView.setAdapter(mCouponListAdapter);
						} else {
							for (int i = 0; i < marketCouponList.size(); i++) {
								mCouponListAdapter.add(marketCouponList.get(i));
							}
							mCouponListAdapter.notifyDataSetChanged();
							nowPage += 1;
							if (couponListInfo.nowPage == couponListInfo.pageCount) {
								isEnd = true;
							}
						}
						verifyFlag = false;
						mListView.setAdapter(mCouponListAdapter);
					}
					mListView.setVisibility(View.VISIBLE);

					break;
				default:
					break;
				}
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
		};
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	private class CouponListAdapter extends ArrayAdapter<Coupon> {
		private ViewHolder viewHolder = null;
		private LayoutInflater mLayoutInflater;

		public CouponListAdapter(Context context, List<Coupon> objects) {
			// TODO Auto-generated constructor stub
			super(context, 0, objects);
			mContext = context;
			mLayoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Coupon coupon = null;
			Log.v("asd", "position =" + position);
			if (position >= 0) {
				coupon = getItem(position);
			}
			if (convertView == null) {
				// convertView = mLayoutInflater.inflate(R.layout.app_list_item,
				// null);
				convertView = mLayoutInflater.inflate(
						R.layout.coupon_list_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.mCouponId = (TextView) convertView
						.findViewById(R.id.coupon_id);
				viewHolder.mValid = (TextView) convertView
						.findViewById(R.id.coupon_valid);
				viewHolder.mCost = (TextView) convertView
						.findViewById(R.id.coupon_cost);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (coupon != null) {
				viewHolder.mCost.setText("￥ " + coupon.couponCash);
				viewHolder.mCouponId.setText(coupon.couponNo);
				viewHolder.mValid.setText(coupon.validTime);
			}

			return convertView;
		}

		class ViewHolder {
			TextView mCouponId;
			TextView mValid;
			TextView mCost;
		}
	}
}