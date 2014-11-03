package com.yikego.market.activity;

import java.math.BigDecimal;
import java.util.List;

import com.yikego.android.rom.sdk.bean.OrderProductInfo;
import com.yikego.market.R;
import com.yikego.market.model.GoodsData;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.yikego.market.utils.CachedThumbnails;
import com.yikego.market.yikegoApplication;

public class MarketShoppingCarActivity extends ListActivity {
	private Context mContext;
	private ListView mListView;
	private TextView mCoutPrice;
	private OrderListAdapter mAdapter;
	private boolean loginFlag;

	public MarketShoppingCarActivity() {
		mContext = this;
		loginFlag = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_car);
        yikegoApplication.getInstance().addActivity(this);
		initView();
		SharedPreferences mSharePreferences = this.getSharedPreferences(
				"userInfo", MODE_PRIVATE);

		String userId = mSharePreferences.getString("userId", null);
		if (userId != null) {
			loginFlag = true;
		}
	}

	private void initView() {
		Button settlement = (Button) findViewById(R.id.btn_settlement);
        findViewById(R.id.market_detail_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
		settlement.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (loginFlag) {
					Intent intent = new Intent(mContext,
							MarketSubmitOrderActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(mContext,
							mContext.getString(R.string.non_login),
							Toast.LENGTH_LONG).show();
				}

			}

		});
		mCoutPrice = (TextView) findViewById(R.id.selected_settlement_amount);
		mCoutPrice.setText(getResources().getString(R.string.goods_cout_price)
				+ getCoutPrice());
		mListView = getListView();
		mAdapter = new OrderListAdapter(mContext,
				MarketDetailActivity.orderDetailList);
		mListView.setAdapter(mAdapter);
	}
	public Drawable getThumbnail( String id) {
		// TODO Auto-generated method stub
		Drawable drawable = CachedThumbnails.getGoodsThumbnail(this, id);
		if (drawable == null) {
				return CachedThumbnails.getGoodsDefaultIcon(this);
		} else {
			return drawable;
		}
	}
	private float getCoutPrice() {
		float coutPrice = 0;
		for (int i = 0; i < MarketDetailActivity.orderDetailList.size(); i++) {
			if (MarketDetailActivity.orderDetailList.get(i).selectFlag) {
				coutPrice += (MarketDetailActivity.orderDetailList.get(i).price)
						* (MarketDetailActivity.orderDetailList.get(i).count);
			}
		}
		BigDecimal b = new BigDecimal(coutPrice);
		coutPrice = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
		Log.v("getCoutPrice", "coutPrice =" + coutPrice);
		return coutPrice;
	}

	private class OrderListAdapter extends ArrayAdapter<OrderProductInfo> {
		private ViewHolder viewHolder = null;
		private LayoutInflater mLayoutInflater;
		private View.OnClickListener mOnClickListener;

		public OrderListAdapter(Context context, List<OrderProductInfo> objects) {
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
					case R.id.order_goods_plus:
						onPlusClick(v);
						break;
					case R.id.order_goods_subtract:
						onSubtractClick(v);
						break;
					case R.id.order_flag:
						onOrderFlagClick(v);
						break;
					default:
						break;
					}
				}
			};
		}

		private void onPlusClick(View v) {
			int position = (Integer) v.getTag();
			MarketDetailActivity.orderDetailList.get(position).count += 1;
			mCoutPrice.setText(getResources().getString(
					R.string.goods_cout_price)
					+ getCoutPrice());
			mAdapter.notifyDataSetChanged();
		}

		private void onSubtractClick(View v) {
			int position = (Integer) v.getTag();
			if (MarketDetailActivity.orderDetailList.get(position).count > 0) {
				MarketDetailActivity.orderDetailList.get(position).count -= 1;
				mAdapter.notifyDataSetChanged();
				mCoutPrice.setText(getResources().getString(
						R.string.goods_cout_price)
						+ getCoutPrice());
			}
		}

		private void onOrderFlagClick(View v) {
			int position = (Integer) v.getTag();
			ImageView orderFlag = (ImageView) v;
			MarketDetailActivity.orderDetailList.get(position).selectFlag = !MarketDetailActivity.orderDetailList
					.get(position).selectFlag;
			mAdapter.notifyDataSetChanged();
			mCoutPrice.setText(getResources().getString(
					R.string.goods_cout_price)
					+ getCoutPrice());
		}

		@SuppressWarnings("deprecation")
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
				viewHolder.mPlus = (ImageView) convertView
						.findViewById(R.id.order_goods_plus);
				viewHolder.mSubtract = (ImageView) convertView
						.findViewById(R.id.order_goods_subtract);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.mOrderFlag.setOnClickListener(mOnClickListener);
			viewHolder.mOrderFlag.setTag(position);
			viewHolder.mPlus.setOnClickListener(mOnClickListener);
			viewHolder.mPlus.setTag(position);
			viewHolder.mSubtract.setOnClickListener(mOnClickListener);
			viewHolder.mSubtract.setTag(position);
			viewHolder.mThumbnail.setBackgroundDrawable(((MarketShoppingCarActivity)mContext).getThumbnail(orderInfo.name));
			if (orderInfo != null) {
				viewHolder.mName.setText(orderInfo.name);
				viewHolder.mDetail.setText(orderInfo.name);
				float price = orderInfo.price;
				BigDecimal b = new BigDecimal(price);
				price = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
				viewHolder.mPrice.setText("￥  " + price);
				viewHolder.mOrderCout.setText("" + orderInfo.count);
				Drawable orderFlag;
				if (orderInfo.count == 0) {
					orderFlag = mContext.getResources().getDrawable(
							R.drawable.img_flag_unchecked);
				} else {
					orderFlag = mContext.getResources().getDrawable(
							R.drawable.img_flag_selected);
				}
				if (orderInfo.selectFlag) {
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
			ImageView mThumbnail;
			// TextView mAuthor;
			TextView mName;
			TextView mDetail;
			TextView mPrice;
			ImageView mOrderFlag;
			ImageView mPlus;
			ImageView mSubtract;
			TextView mOrderCout;
		}
	}
}
