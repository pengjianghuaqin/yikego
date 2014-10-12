package com.yikego.market.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.yikego.android.rom.sdk.bean.CommitOrder;
import com.yikego.android.rom.sdk.bean.OrderDetail;
import com.yikego.android.rom.sdk.bean.OrderProductInfo;
import com.yikego.android.rom.sdk.bean.OrderResult;
import com.yikego.android.rom.sdk.bean.PaginationStoreListInfo;
import com.yikego.android.rom.sdk.bean.PostUserLocationInfo;
import com.yikego.market.R;
import com.yikego.market.model.Latitude;
import com.yikego.market.utils.Constant;
import com.yikego.market.utils.GlobalUtil;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import android.app.Activity;
import android.app.ListActivity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MarketSubmitOrderActivity extends ListActivity implements
		OnItemClickListener {
	private Context mContext;
	private ListView mListView;
	private OrderListAdapter mAdapter;
	private Request mCurrentRequest;
	private ThemeService mThemeService;
	private Handler mHandler;
	private static final int ACTION_NETWORK_ERROR = 0;
	private static final int ACTION_SUBMIT_ORDER = 1;
	public MarketSubmitOrderActivity() {
		mContext = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submitorder);
		mThemeService = ThemeService.getServiceInstance(mContext);
		initView();
		initHandler();
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
		mListView = getListView();
		mAdapter = new OrderListAdapter(mContext,
				MarketDetailActivity.orderDetailList);
		mListView.setAdapter(mAdapter);
	}
	private void PostSubmitOrder() {
		// TODO Auto-generated method stub

		Request request = new Request(0, Constant.TYPE_POST_SUBMIT_ORDER);
		// Object[] params = new Object[2];
		CommitOrder commitOrder = new CommitOrder();
		commitOrder.storeId = MarketDetailActivity.storeID;
        if (GlobalUtil.isLogin(this))
            commitOrder.userId = GlobalUtil.getUserId(this);
        else
            commitOrder.userId = 1;
		commitOrder.orderStatus = 0;
		commitOrder.subject = "product subject";
		commitOrder.body = "subject body";
		commitOrder.orderDetailList = new ArrayList<OrderDetail>();
		for(int i=0;i<MarketDetailActivity.orderDetailList.size();i++){
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.productId = MarketDetailActivity.orderDetailList.get(i).productId;
			orderDetail.count = MarketDetailActivity.orderDetailList.get(i).count;
			commitOrder.orderDetailList.add(orderDetail);
		}
		request.setData(commitOrder);
		request.addObserver(new Observer() {

			@Override
			public void update(Observable observable, Object data) {
				// TODO Auto-generated method stub
				if (data != null) {
					Message msg = Message.obtain(mHandler,
							ACTION_SUBMIT_ORDER, data);
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
				case ACTION_SUBMIT_ORDER:
					OrderResult orderResult = (OrderResult) msg.obj;
					Toast toast = Toast.makeText(mContext,"orderId="+orderResult.orderNo+"/ resultCode="+orderResult.resultCode+"/ totalFee="+orderResult.totalFee, Toast.LENGTH_SHORT);
					toast.show(); 
					break;

				default:
					break;
				}
			}
		};
	}
	private class OrderListAdapter extends ArrayAdapter<OrderProductInfo> {
		private ViewHolder viewHolder = null;
		private LayoutInflater mLayoutInflater;

		public OrderListAdapter(Context context, List<OrderProductInfo> objects) {
			// TODO Auto-generated constructor stub
			super(context, 0, objects);
			mContext = context;
			mLayoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			OrderProductInfo orderInfo = null;
			Log.v("asd", "position =" + position);
			if (position >= 0) {
				orderInfo = getItem(position);
			}
			if (convertView == null) {
				// convertView = mLayoutInflater.inflate(R.layout.app_list_item,
				// null);
				convertView = mLayoutInflater.inflate(R.layout.order_list_item,
						parent, false);
				viewHolder = new ViewHolder();
				viewHolder.mName = (TextView) convertView
						.findViewById(R.id.order_listitem_name);
				viewHolder.mDetail = (TextView) convertView
						.findViewById(R.id.order_listitem_detail);
				viewHolder.mPrice = (TextView) convertView
						.findViewById(R.id.order_listitem_price);
				viewHolder.mThumbnail = (ImageView) convertView
						.findViewById(R.id.order_listitem_icon);
				viewHolder.mOrderFlag = (ImageView) convertView
						.findViewById(R.id.order_flag);
				viewHolder.mOrderCout = (TextView) convertView
						.findViewById(R.id.order_goods_index);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			// convertView.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// Intent intent = new Intent(mContext,
			// MarketGoodsDetailActivity.class);
			// mContext.startActivity(intent);
			// }
			// });
			if (orderInfo != null) {
				viewHolder.mName.setText(orderInfo.name);
				viewHolder.mDetail.setText(orderInfo.name);
				viewHolder.mPrice.setText("��   " + orderInfo.price);
				viewHolder.mOrderCout.setText("" + orderInfo.count);
				Drawable orderFlag;
				if (orderInfo.count == 0) {
					orderFlag = mContext.getResources().getDrawable(
							R.drawable.img_flag_unchecked);
				} else {
					orderFlag = mContext.getResources().getDrawable(
							R.drawable.img_flag_selected);
				}
				viewHolder.mOrderFlag.setImageDrawable(orderFlag);
			}

			return convertView;
		}

		class ViewHolder {
			ImageView mThumbnail;
			// TextView mAuthor;
			TextView mName;
			TextView mDetail;
			TextView mPrice;
			ImageView mOrderFlag;
			TextView mOrderCout;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}
}