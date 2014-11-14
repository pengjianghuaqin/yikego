package com.yikego.market.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.yikego.android.rom.sdk.bean.CommitOrder;
import com.yikego.android.rom.sdk.bean.Coupon;
import com.yikego.android.rom.sdk.bean.CouponCheckInfo;
import com.yikego.android.rom.sdk.bean.CouponListInfo;
import com.yikego.android.rom.sdk.bean.OrderDetail;
import com.yikego.android.rom.sdk.bean.OrderProductInfo;
import com.yikego.android.rom.sdk.bean.OrderResult;
import com.yikego.android.rom.sdk.bean.PaginationStoreListInfo;
import com.yikego.android.rom.sdk.bean.PostUserCouponInfo;
import com.yikego.market.R;
import com.yikego.market.adapter.OrderListAdapter;
import com.yikego.market.model.Image2;
import com.yikego.market.model.Latitude;
import com.yikego.market.model.LoadingDialog;
import com.yikego.market.utils.CachedThumbnails;
import com.yikego.market.utils.Constant;
import com.yikego.market.utils.GlobalUtil;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.yikego.market.yikegoApplication;

public class MarketSubmitOrderActivity extends Activity{
	private static final int ACTIVITY_RESULT_COD = 1000;
	private static final int ACTIVITY_RESULT_DETAIL = 1001;
	private ArrayList<Coupon> mCouponList;
	private Context mContext;
	private ListView mListView;
	private ListView mCouponListView;
	private TextView productCount;
	private TextView priceCount;
	private TextView couponCount, couponMount;
	private OrderListAdapter mAdapter;
	private OrderCouponListAdapter mCouponAdapter;
	private Request mCurrentRequest;
	private ThemeService mThemeService;
	private Handler mHandler;
	private LoadingDialog mProgressDialog;
	private TextView mPlayment_way;
	private TextView mAddress;
	private TextView mPayMoney;
	private TextView mAddCoupon;
	private int mOrderType = -1;
	private static final int ACTION_NETWORK_ERROR = 0;
	private static final int ACTION_SUBMIT_ORDER = 1;
	private static final int ACTION_COUPON_LIST = 2;
	private View mHeaderView;
//	private View mFooterView;
	private View mCouponFooterView;
	private float coutPrice;
	private LinearLayout listHeader;
	private final BroadcastReceiver mApplicationsReceiver;
	public MarketSubmitOrderActivity() {
		mContext = this;
		mCouponList = new ArrayList<Coupon>();
		mApplicationsReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Log.v("mApplicationsReceiver", "mApplicationsReceiver intent="+intent.getAction());
				GetCouponList();
			}
		};
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_submitorder);
		mHeaderView = LayoutInflater.from(mContext).inflate(
				R.layout.submit_order_list_header, null);
//		mFooterView = LayoutInflater.from(mContext).inflate(
//				R.layout.submit_order_list_footer, null);

		mCouponFooterView = LayoutInflater.from(mContext).inflate(
				R.layout.submit_order_list_coupon_footer, null);
		mCouponListView = (ListView) findViewById(R.id.coupon_list);
		
		yikegoApplication.getInstance().addActivity(this);
		mThemeService = ThemeService.getServiceInstance(mContext);
		mProgressDialog = new LoadingDialog(mContext);
		initView();
		initHandler();
		registerIntentReceivers();
	}

	public Drawable getThumbnail(String id) {
		// TODO Auto-generated method stub
		Drawable drawable = CachedThumbnails.getGoodsThumbnail(this, id);
		if (drawable == null) {
			return CachedThumbnails.getGoodsDefaultIcon(this);
		} else {
			return drawable;
		}
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
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterIntentReceivers();
	}
	@Override
	protected void onResume() {
		super.onResume();
		TextView mTel = (TextView) mHeaderView.findViewById(R.id.tel);
		mTel.setText(GlobalUtil.getUserPhone(mContext));
		mPlayment_way = (TextView) mHeaderView.findViewById(R.id.playment_way);
		mAddress = (TextView) mHeaderView.findViewById(R.id.addressee);
		mAddress.setText(GlobalUtil.getUserAddress(this));
		int playment_way = GlobalUtil.getPlaymentWay(this);
		if (playment_way == GlobalUtil.PLAYMENT_COD) {
			mPlayment_way.setText(R.string.text_playment_cod);
		} else {
			mPlayment_way.setText(R.string.text_playment_online);
		}

	}

	private void initView() {
		Button settlement = (Button) findViewById(R.id.btn_submit_order);
		settlement.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PostSubmitOrder();
			}

		});
		findViewById(R.id.market_detail_back).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						finish();
					}
				});
//		mListView = getListView();
		
//		mListView.addFooterView(mFooterView);
		listHeader = (LinearLayout) mHeaderView.findViewById(R.id.list_header);
		// listHeader.addView(mListView);
		// mAdapter = new OrderListAdapter(mContext,
		// MarketDetailActivity.orderDetailList);
		// mListView.setAdapter(mAdapter);
		for (int i = 0; i < MarketDetailActivity.orderDetailList.size(); i++) {
			initOrderList(MarketDetailActivity.orderDetailList.get(i));
		}
		mCouponListView.addHeaderView(mHeaderView);
		mCouponListView.addFooterView(mCouponFooterView);
		productCount = (TextView) mCouponFooterView
				.findViewById(R.id.product_count);
		priceCount = (TextView) mCouponFooterView
				.findViewById(R.id.price_mount);
		couponCount = (TextView) mCouponFooterView
				.findViewById(R.id.coupon_count);
		couponMount = (TextView) mCouponFooterView
				.findViewById(R.id.coupon_mount);
		mAddCoupon = (TextView) mHeaderView.findViewById(R.id.btn_add_coupon);
		mAddCoupon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, TicketVerifyActivity.class);
				startActivityForResult(intent, 1);
			}

		});
		mPayMoney = (TextView) findViewById(R.id.order_pay_money);
		float coutPrice = 0;
		int count = 0;
		for (int i = 0; i < MarketDetailActivity.orderDetailList.size(); i++) {
			if (MarketDetailActivity.orderDetailList.get(i).selectFlag) {
				count += MarketDetailActivity.orderDetailList.get(i).count;
				coutPrice += (MarketDetailActivity.orderDetailList.get(i).price)
						* MarketDetailActivity.orderDetailList.get(i).count;

			}
		}
		BigDecimal b = new BigDecimal(coutPrice);
		coutPrice = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
		this.coutPrice = coutPrice;
		setPriceCount(coutPrice);
		setProductCount(count);
		GetCouponList();
	}

	private void initOrderList(OrderProductInfo orderInfo) {
		if (orderInfo.selectFlag) {
			View convertView = LayoutInflater.from(mContext).inflate(
					R.layout.order_goods_list_item, null);
			TextView mName = (TextView) convertView
					.findViewById(R.id.order_goods_listitem_name);
			TextView mDetail = (TextView) convertView
					.findViewById(R.id.order_goods_listitem_detail);
			TextView mPrice = (TextView) convertView
					.findViewById(R.id.order_goods_listitem_price);
			
			TextView mOrderCout = (TextView) convertView
					.findViewById(R.id.order_goods_item_count);
			ImageView mThumbnail = (ImageView) convertView
					.findViewById(R.id.order_goods_listitem_thumb);
			if (orderInfo != null) {
				mName.setText(orderInfo.name);
				mDetail.setText(orderInfo.name);
				mOrderCout.setText("X " + orderInfo.count);
				BigDecimal b = new BigDecimal(orderInfo.price);
				orderInfo.price = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
				mPrice.setText("￥ "+orderInfo.price);
				mThumbnail
						.setBackgroundDrawable(((MarketSubmitOrderActivity) mContext)
								.getThumbnail(orderInfo.name));
			}
			listHeader.addView(convertView);
		}

	}

	private void setProductCount(int count) {
		productCount.setText(count
				+ getResources().getString(R.string.orderProductCount));
	}

	private void setPriceCount(float coutPrice) {
		priceCount.setText(getResources().getString(R.string.yuan)
				+ String.valueOf(coutPrice));
	}

	private void GetCouponList() {
		// TODO Auto-generated method stub
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		Request request = new Request(0, Constant.TYPE_POST_COUPON_LIST);
		PostUserCouponInfo mPostUserCouponInfo = new PostUserCouponInfo();
		mPostUserCouponInfo.userId = GlobalUtil.getUserId(mContext);
		mPostUserCouponInfo.nowPage = 1;
		mPostUserCouponInfo.pageCount = 30;
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

	private void PostSubmitOrder() {
		// TODO Auto-generated method stub

		Request request = new Request(0, Constant.TYPE_POST_SUBMIT_ORDER);
		// Object[] params = new Object[2];
		CommitOrder commitOrder = new CommitOrder();
		commitOrder.storeId = MarketDetailActivity.storeID;
		commitOrder.orderType = 1;
		mOrderType = commitOrder.orderType;
		if (GlobalUtil.isLogin(this))
			commitOrder.userId = GlobalUtil.getUserId(this);
		else
			commitOrder.userId = 1;
		commitOrder.orderStatus = 0;
		commitOrder.orderDetailList = new ArrayList<OrderDetail>();
		StringBuilder subjectSb = new StringBuilder();
		StringBuilder summarySb = new StringBuilder();
		for (int i = 0; i < MarketDetailActivity.orderDetailList.size(); i++) {
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.productId = MarketDetailActivity.orderDetailList.get(i).productId;
			orderDetail.count = MarketDetailActivity.orderDetailList.get(i).count;
			commitOrder.orderDetailList.add(orderDetail);

			if (subjectSb.length() > 256) {
				continue;
			} else {
				subjectSb = subjectSb
						.append(MarketDetailActivity.orderDetailList.get(i).name);
				if (i != MarketDetailActivity.orderDetailList.size() - 1)
					subjectSb = subjectSb.append("，");
				summarySb = summarySb.append("；");
			}
			if (summarySb.length() > 400) {
				continue;
			} else {
				summarySb = summarySb
						.append(MarketDetailActivity.orderDetailList.get(i).summary);
				if (i != MarketDetailActivity.orderDetailList.size() - 1)
					summarySb = summarySb.append("；");
			}
		}
		commitOrder.couponList = new ArrayList<CouponCheckInfo>();
		for (int i = 0; i < mCouponList.size(); i++) {
			if (mCouponList.get(i).selectFlag) {
				CouponCheckInfo couponCheckInfo = new CouponCheckInfo();
				couponCheckInfo.couponNo = Integer
						.parseInt(mCouponList.get(i).couponNo);
				commitOrder.couponList.add(couponCheckInfo);
			}
		}
		commitOrder.subject = subjectSb.toString();
		commitOrder.body = summarySb.toString();
		request.setData(commitOrder);
		request.addObserver(new Observer() {

			@Override
			public void update(Observable observable, Object data) {
				// TODO Auto-generated method stub
				if (data != null) {
					Message msg = Message.obtain(mHandler, ACTION_SUBMIT_ORDER,
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
						if(mCouponList.size()>0){
							mCouponList.clear();
						}
						for (int i = 0; i < couponListInfo.couponlist.size(); i++) {
							mCouponList.add(couponListInfo.couponlist.get(i));
						}
						mCouponAdapter = new OrderCouponListAdapter(mContext,
								mCouponList);
						Log.v("ACTION_COUPON_LIST",
								"ACTION_COUPON_LIST mCouponAdapter ="
										+ mCouponAdapter);
						mCouponListView.setAdapter(mCouponAdapter);
					}
					mCouponListView.setVisibility(View.VISIBLE);
					if (mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
					setCouponInfo();
					break;
				case ACTION_SUBMIT_ORDER:
					OrderResult orderResult = (OrderResult) msg.obj;
					if (orderResult != null && orderResult.resultCode == 0) {
						showOrderSuccess(orderResult);
					}
					break;

				default:
					break;
				}
			}
		};
	}

	public void setCouponInfo() {
		int size = 0;
		float price = 0;
		for (int i = 0; i < mCouponList.size(); i++) {
			if (mCouponList.get(i).selectFlag) {
				size++;
				price += mCouponList.get(i).couponCash;
			}

		}
		couponCount.setText(size
				+ getResources().getString(R.string.orderCouponCount));
		couponMount.setText(getResources().getString(R.string.yuan)
				+ String.valueOf(price));
		price = coutPrice - price;
		BigDecimal b = new BigDecimal(price);
		price = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
		mPayMoney.setText(getResources().getString(R.string.pay_for)
				+ String.valueOf(price));
	}

	private void showOrderSuccess(OrderResult orderResult) {
		Intent intent = new Intent();
		intent.setClass(this, PaySuccessActivity.class);
		intent.putExtra("orderResult", orderResult);
		intent.putExtra("orderType", mOrderType);
		startActivity(intent);
	}
//
//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position,
//			long id) {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		switch (resultCode) {
		case ACTIVITY_RESULT_COD:

			break;

		case ACTIVITY_RESULT_DETAIL:

			break;
		case 1:
			if (resultCode == RESULT_OK) {
				String tmpNo = intent.getStringExtra("couponNo");
				boolean mat = tmpNo.matches("\\d+");
				if (mat) {
					CouponCheckInfo mCouponCheckInfo = new CouponCheckInfo();
					mCouponCheckInfo.userId = GlobalUtil
							.getUserId(MarketSubmitOrderActivity.this);
					mCouponCheckInfo.couponNo = Integer.parseInt(tmpNo);
					ckeckCoupon(mCouponCheckInfo);
				} else {
					GlobalUtil.showToastString(MarketSubmitOrderActivity.this,
							R.string.coupon_check_null);
				}

			}
		default:
			break;
		}
	}

	private void ckeckCoupon(CouponCheckInfo couponCheckInfo) {
		Request request = new Request(0, Constant.TYPE_POST_COUPON_CHECK);
		request.setData(couponCheckInfo);
		request.addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object data) {
				if (data != null) {
					GetCouponList();
				} else {
					Request request = (Request) observable;
					if (request.getStatus() == Constant.STATUS_ERROR) {
						mHandler.sendEmptyMessage(ACTION_NETWORK_ERROR);
					}
				}
			}
		});
		mThemeService.postCouponCheck(request);
	}

	public void layout_cod_onclick(View v) {
		Intent intent = new Intent();
		intent.setClass(this, PaymentWayActivity.class);
		startActivityForResult(intent, ACTIVITY_RESULT_COD);
	}

	public void layout_detail_onclick(View v) {
		Intent intent = new Intent();
		intent.setClass(this, ConsigneeEditActivity.class);
		startActivityForResult(intent, ACTIVITY_RESULT_DETAIL);
	}

	private class OrderCouponListAdapter extends ArrayAdapter<Coupon> {
		private ViewHolder viewHolder = null;
		private LayoutInflater mLayoutInflater;
		private View.OnClickListener mOnClickListener;

		public OrderCouponListAdapter(Context context, List<Coupon> objects) {
			// TODO Auto-generated constructor stub
			super(context, 0, objects);
			mContext = context;
			mLayoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mOnClickListener = new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.v("onClick", "View =" + v);
					switch (v.getId()) {
					case R.id.coupon_area:
						onOrderFlagClick(v);
						break;
					default:
						break;
					}
				}
			};
		}

		private void onOrderFlagClick(View v) {
			int position = (Integer) v.getTag();
			mCouponList.get(position).selectFlag = !mCouponList.get(position).selectFlag;
			setCouponInfo();
			mCouponAdapter.notifyDataSetChanged();
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
				viewHolder.mCouponArea = (RelativeLayout) convertView
						.findViewById(R.id.coupon_area);
				viewHolder.mCouponArea.setBackgroundColor(0xffffffff);
				viewHolder.mCouponId = (TextView) convertView
						.findViewById(R.id.coupon_id);
				viewHolder.mValid = (TextView) convertView
						.findViewById(R.id.coupon_valid);
				viewHolder.mCost = (TextView) convertView
						.findViewById(R.id.coupon_cost);
				viewHolder.mOrderFlag = (ImageView) convertView
						.findViewById(R.id.order_flag);
				viewHolder.mOrderFlag.setVisibility(View.VISIBLE);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (coupon != null) {
				viewHolder.mCouponArea.setOnClickListener(mOnClickListener);
				viewHolder.mCouponArea.setTag(position);
				viewHolder.mCost.setText("￥ " + coupon.couponCash);
				viewHolder.mCouponId.setText(coupon.couponNo);
				viewHolder.mValid.setText(coupon.validTime);
				Drawable orderFlag;
				if (coupon.selectFlag) {
					orderFlag = mContext.getResources().getDrawable(
							R.drawable.img_flag_selected);
				} else {
					orderFlag = mContext.getResources().getDrawable(
							R.drawable.img_flag_unchecked);
				}
				viewHolder.mOrderFlag.setImageDrawable(orderFlag);
			}

			return convertView;
		}

		class ViewHolder {
			RelativeLayout mCouponArea;
			ImageView mOrderFlag;
			TextView mCouponId;
			TextView mValid;
			TextView mCost;
		}
	}

}